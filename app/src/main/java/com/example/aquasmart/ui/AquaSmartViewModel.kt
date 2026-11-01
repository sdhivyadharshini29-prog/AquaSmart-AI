package com.example.aquasmart.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.aquasmart.data.model.*
import com.example.aquasmart.data.repository.AgroWeatherSnapshot
import com.example.aquasmart.data.repository.AquaSmartRepository
import com.example.aquasmart.domain.calculator.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AquaSmartViewModel(
    private val repository: AquaSmartRepository
) : ViewModel() {

    // Farmer Profile
    val farmerProfile: StateFlow<FarmerProfileEntity?> = repository.farmerProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // All Farms
    val allFarms: StateFlow<List<FarmEntity>> = repository.allFarms
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected Farm ID
    private val _selectedFarmId = MutableStateFlow<Long>(1L)
    val selectedFarmId: StateFlow<Long> = _selectedFarmId.asStateFlow()

    // Active Farm Entity
    val activeFarm: StateFlow<FarmEntity?> = combine(allFarms, _selectedFarmId) { farms, id ->
        farms.find { it.id == id } ?: farms.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Active Crop Cycle for current farm
    val activeCropCycle: StateFlow<CropCycleEntity?> = _selectedFarmId.flatMapLatest { farmId ->
        repository.getActiveCropCycle(farmId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Latest Soil Record for current farm
    val latestSoilRecord: StateFlow<SoilRecordEntity?> = _selectedFarmId.flatMapLatest { farmId ->
        repository.getLatestSoilRecord(farmId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Latest Irrigation Record for current farm
    val latestIrrigationRecord: StateFlow<IrrigationRecordEntity?> = _selectedFarmId.flatMapLatest { farmId ->
        repository.getLatestIrrigationRecord(farmId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // All Observations
    val farmObservations: StateFlow<List<CropObservationEntity>> = _selectedFarmId.flatMapLatest { farmId ->
        repository.getObservationsForFarm(farmId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All Records for History / Reports
    val allIrrigationRecords: StateFlow<List<IrrigationRecordEntity>> = repository.allIrrigationRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSoilRecords: StateFlow<List<SoilRecordEntity>> = repository.allSoilRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allObservations: StateFlow<List<CropObservationEntity>> = repository.allObservations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Weather state
    private val _manualWeather = MutableStateFlow<AgroWeatherSnapshot?>(null)
    val currentWeather: StateFlow<AgroWeatherSnapshot> = combine(activeFarm, _manualWeather) { farm, manual ->
        manual ?: repository.getWeatherForDistrict(farm?.district ?: "Thanjavur")
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        repository.getWeatherForDistrict("Thanjavur")
    )

    // -------------------------------------------------------------
    // Irrigation Calculator State
    // -------------------------------------------------------------
    var calcAreaAcres = MutableStateFlow(3.5)
    var calcStage = MutableStateFlow(CropGrowthStage.VEGETATIVE_TILLERING)
    var calcSoilType = MutableStateFlow(SoilType.ALLUVIAL)
    var calcMoisturePct = MutableStateFlow(45.0)
    var calcRecentRainfallMm = MutableStateFlow(0.0)
    var calcTemperatureC = MutableStateFlow(32.5)
    var calcIrrigationMethod = MutableStateFlow(IrrigationMethod.AWD)
    var calcIsSensorMeasured = MutableStateFlow(false)

    private val _irrigationResult = MutableStateFlow<IrrigationCalculationResult?>(null)
    val irrigationResult: StateFlow<IrrigationCalculationResult?> = _irrigationResult.asStateFlow()

    // -------------------------------------------------------------
    // Soil Health Form State
    // -------------------------------------------------------------
    var soilPhInput = MutableStateFlow("7.2")
    var soilNInput = MutableStateFlow("260.0")
    var soilPInput = MutableStateFlow("16.5")
    var soilKInput = MutableStateFlow("210.0")
    var soilOcInput = MutableStateFlow("0.58")
    var soilTypeInput = MutableStateFlow(SoilType.ALLUVIAL)

    private val _soilAnalysisResult = MutableStateFlow<SoilAnalysisResult?>(null)
    val soilAnalysisResult: StateFlow<SoilAnalysisResult?> = _soilAnalysisResult.asStateFlow()

    // -------------------------------------------------------------
    // Fertilizer Advisory State
    // -------------------------------------------------------------
    var fertVariety = MutableStateFlow(PaddyVariety.ADT_43)
    var fertNRating = MutableStateFlow(SoilRating.LOW)
    var fertPRating = MutableStateFlow(SoilRating.MEDIUM)
    var fertKRating = MutableStateFlow(SoilRating.MEDIUM)

    private val _fertilizerPlan = MutableStateFlow<FertilizerPlan?>(null)
    val fertilizerPlan: StateFlow<FertilizerPlan?> = _fertilizerPlan.asStateFlow()

    init {
        // Initial calculations
        recalculateIrrigation()
        recalculateSoilAnalysis()
        recalculateFertilizerPlan()
    }

    fun selectFarm(farmId: Long) {
        _selectedFarmId.value = farmId
        val farm = allFarms.value.find { it.id == farmId }
        farm?.let {
            calcAreaAcres.value = it.areaAcres
            calcSoilType.value = SoilType.values().find { s -> s.displayName == it.soilType } ?: SoilType.ALLUVIAL
            recalculateIrrigation()
        }
    }

    fun recalculateIrrigation() {
        val result = IrrigationCalculator.calculateIrrigation(
            areaAcres = calcAreaAcres.value,
            stage = calcStage.value,
            soilType = calcSoilType.value,
            soilMoisturePct = calcMoisturePct.value,
            recentRainfallMm = calcRecentRainfallMm.value,
            temperatureC = calcTemperatureC.value,
            method = calcIrrigationMethod.value,
            isSensorMeasured = calcIsSensorMeasured.value
        )
        _irrigationResult.value = result
    }

    fun saveCurrentIrrigationCalculation() {
        val result = _irrigationResult.value ?: return
        val farmId = _selectedFarmId.value
        viewModelScope.launch {
            val record = IrrigationRecordEntity(
                farmId = farmId,
                timestampMillis = System.currentTimeMillis(),
                areaAcres = calcAreaAcres.value,
                stage = calcStage.value.stageName,
                soilType = calcSoilType.value.displayName,
                soilMoisturePct = calcMoisturePct.value,
                recentRainfallMm = calcRecentRainfallMm.value,
                temperatureC = calcTemperatureC.value,
                irrigationMethod = calcIrrigationMethod.value.methodName,
                netIrrigationMm = result.netIrrigationDepthMm,
                waterVolumeLitres = result.waterVolumeLitres,
                waterVolumeM3 = result.waterVolumeCubicMeters,
                waterSavedLitres = result.waterSavedLitres,
                recommendation = result.recommendationSummary,
                calculationDetails = result.stepByStepExplanation,
                isSensorMeasured = calcIsSensorMeasured.value
            )
            repository.saveIrrigationRecord(record)
        }
    }

    fun recalculateSoilAnalysis() {
        val ph = soilPhInput.value.toDoubleOrNull() ?: 7.0
        val n = soilNInput.value.toDoubleOrNull() ?: 280.0
        val p = soilPInput.value.toDoubleOrNull() ?: 15.0
        val k = soilKInput.value.toDoubleOrNull() ?: 180.0
        val oc = soilOcInput.value.toDoubleOrNull() ?: 0.60

        val result = SoilHealthAnalyzer.analyzeSoil(
            ph = ph,
            nKgHa = n,
            pKgHa = p,
            kKgHa = k,
            ocPct = oc,
            soilType = soilTypeInput.value.displayName
        )
        _soilAnalysisResult.value = result
    }

    fun saveCurrentSoilAnalysis() {
        val analysis = _soilAnalysisResult.value ?: return
        val farmId = _selectedFarmId.value
        val ph = soilPhInput.value.toDoubleOrNull() ?: 7.0
        val n = soilNInput.value.toDoubleOrNull() ?: 280.0
        val p = soilPInput.value.toDoubleOrNull() ?: 15.0
        val k = soilKInput.value.toDoubleOrNull() ?: 180.0
        val oc = soilOcInput.value.toDoubleOrNull() ?: 0.60

        viewModelScope.launch {
            val record = SoilRecordEntity(
                farmId = farmId,
                recordedDateMillis = System.currentTimeMillis(),
                ph = ph,
                nitrogenKgHa = n,
                phosphorusKgHa = p,
                potassiumKgHa = k,
                organicCarbonPct = oc,
                soilType = soilTypeInput.value.displayName,
                phCategory = analysis.phCategory,
                nitrogenRating = analysis.nitrogenRating.label,
                phosphorusRating = analysis.phosphorusRating.label,
                potassiumRating = analysis.potassiumRating.label,
                organicCarbonRating = analysis.organicCarbonRating.label,
                summaryRecommendation = analysis.conditionSummary,
                amendmentRequired = analysis.soilAmendmentAdvice
            )
            repository.saveSoilRecord(record)
        }
    }

    fun recalculateFertilizerPlan() {
        val plan = FertilizerCalculator.calculatePlan(
            areaAcres = calcAreaAcres.value,
            variety = fertVariety.value,
            nRating = fertNRating.value,
            pRating = fertPRating.value,
            kRating = fertKRating.value
        )
        _fertilizerPlan.value = plan
    }

    fun addNewFarm(name: String, area: Double, district: String, soilType: String, waterSource: String) {
        viewModelScope.launch {
            val farm = FarmEntity(
                farmName = name,
                areaAcres = area,
                district = district,
                soilType = soilType,
                waterSource = waterSource,
                isDeltaZone = district.contains("Thanjavur") || district.contains("Tiruvarur") || district.contains("Nagapattinam")
            )
            val newId = repository.addFarm(farm)
            _selectedFarmId.value = newId
        }
    }

    fun registerOrUpdateCropCycle(
        variety: PaddyVariety,
        sowingDateMillis: Long,
        stage: CropGrowthStage,
        targetYield: Double
    ) {
        val farmId = _selectedFarmId.value
        val harvestMillis = sowingDateMillis + (variety.durationDays.toLong() * 24L * 3600L * 1000L)
        viewModelScope.launch {
            val crop = CropCycleEntity(
                farmId = farmId,
                cropName = "Paddy (Rice)",
                varietyName = variety.varietyName,
                varietyCode = variety.code,
                sowingDateMillis = sowingDateMillis,
                stage = stage.stageName,
                expectedHarvestDateMillis = harvestMillis,
                targetYieldTons = targetYield,
                isActive = true
            )
            repository.saveCropCycle(crop)
        }
    }

    fun recordCropObservation(
        symptoms: String,
        affectedPart: String,
        suspectedIssue: String,
        severity: SeverityLevel,
        ipmAdvice: String,
        organicMeasure: String
    ) {
        val farmId = _selectedFarmId.value
        viewModelScope.launch {
            val obs = CropObservationEntity(
                farmId = farmId,
                observationDateMillis = System.currentTimeMillis(),
                observedSymptoms = symptoms,
                affectedPart = affectedPart,
                suspectedCondition = suspectedIssue,
                severityLevel = severity.label,
                ipmGuideline = ipmAdvice,
                organicControl = organicMeasure
            )
            repository.saveObservation(obs)
        }
    }

    fun updateManualWeather(rainfallMm: Double, tempC: Double, alert: String?) {
        val activeDistrict = activeFarm.value?.district ?: "Thanjavur"
        val base = repository.getWeatherForDistrict(activeDistrict)
        _manualWeather.value = base.copy(
            rainfallLast24hMm = rainfallMm,
            temperatureC = tempC,
            irrigationAlert = alert,
            isAlertUrgent = !alert.isNullOrBlank(),
            isManualOverride = true
        )
        calcTemperatureC.value = tempC
        calcRecentRainfallMm.value = rainfallMm
        recalculateIrrigation()
    }

    fun resetWeatherToLiveDistrict() {
        _manualWeather.value = null
        val activeDistrict = activeFarm.value?.district ?: "Thanjavur"
        val base = repository.getWeatherForDistrict(activeDistrict)
        calcTemperatureC.value = base.temperatureC
        calcRecentRainfallMm.value = base.rainfallLast24hMm
        recalculateIrrigation()
    }

    fun saveFarmerProfile(name: String, phone: String, email: String, district: String) {
        viewModelScope.launch {
            val current = farmerProfile.value
            val updated = FarmerProfileEntity(
                id = current?.id ?: "default_farmer",
                name = name,
                phone = phone,
                email = email,
                primaryDistrict = district,
                preferredLanguage = current?.preferredLanguage ?: "English",
                activeFarmId = _selectedFarmId.value
            )
            repository.saveFarmerProfile(updated)
        }
    }
}

class AquaSmartViewModelFactory(
    private val repository: AquaSmartRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AquaSmartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AquaSmartViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
