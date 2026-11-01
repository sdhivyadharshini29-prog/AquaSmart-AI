package com.example.aquasmart.data.repository

import com.example.aquasmart.data.local.AquaSmartDao
import com.example.aquasmart.data.model.*
import kotlinx.coroutines.flow.Flow

data class AgroWeatherSnapshot(
    val districtName: String,
    val zone: String,
    val temperatureC: Double,
    val minTempC: Double,
    val maxTempC: Double,
    val humidityPct: Int,
    val windSpeedKmh: Double,
    val rainfallLast24hMm: Double,
    val rainForecastNext48hMm: Double,
    val weatherCondition: String,
    val irrigationAlert: String?,
    val isAlertUrgent: Boolean,
    val isManualOverride: Boolean = false
)

class AquaSmartRepository(private val dao: AquaSmartDao) {

    val farmerProfile: Flow<FarmerProfileEntity?> = dao.getFarmerProfile()
    val allFarms: Flow<List<FarmEntity>> = dao.getAllFarms()
    val allIrrigationRecords: Flow<List<IrrigationRecordEntity>> = dao.getAllIrrigationRecords()
    val allSoilRecords: Flow<List<SoilRecordEntity>> = dao.getAllSoilRecords()
    val allObservations: Flow<List<CropObservationEntity>> = dao.getAllObservations()

    fun getFarmById(farmId: Long): Flow<FarmEntity?> = dao.getFarmById(farmId)

    fun getActiveCropCycle(farmId: Long): Flow<CropCycleEntity?> = dao.getActiveCropCycle(farmId)

    fun getLatestSoilRecord(farmId: Long): Flow<SoilRecordEntity?> = dao.getLatestSoilRecord(farmId)

    fun getSoilRecordsForFarm(farmId: Long): Flow<List<SoilRecordEntity>> = dao.getSoilRecordsForFarm(farmId)

    fun getLatestIrrigationRecord(farmId: Long): Flow<IrrigationRecordEntity?> = dao.getLatestIrrigationRecord(farmId)

    fun getIrrigationRecordsForFarm(farmId: Long): Flow<List<IrrigationRecordEntity>> = dao.getIrrigationRecordsForFarm(farmId)

    fun getObservationsForFarm(farmId: Long): Flow<List<CropObservationEntity>> = dao.getObservationsForFarm(farmId)

    suspend fun saveFarmerProfile(profile: FarmerProfileEntity) {
        dao.insertOrUpdateProfile(profile)
    }

    suspend fun addFarm(farm: FarmEntity): Long {
        return dao.insertFarm(farm)
    }

    suspend fun updateFarm(farm: FarmEntity) {
        dao.updateFarm(farm)
    }

    suspend fun deleteFarm(farm: FarmEntity) {
        dao.deleteFarm(farm)
    }

    suspend fun saveCropCycle(cropCycle: CropCycleEntity): Long {
        return dao.insertCropCycle(cropCycle)
    }

    suspend fun updateCropCycle(cropCycle: CropCycleEntity) {
        dao.updateCropCycle(cropCycle)
    }

    suspend fun saveSoilRecord(record: SoilRecordEntity): Long {
        return dao.insertSoilRecord(record)
    }

    suspend fun saveIrrigationRecord(record: IrrigationRecordEntity): Long {
        return dao.insertIrrigationRecord(record)
    }

    suspend fun saveObservation(observation: CropObservationEntity): Long {
        return dao.insertObservation(observation)
    }

    suspend fun deleteObservation(observation: CropObservationEntity) {
        dao.deleteObservation(observation)
    }

    fun getWeatherForDistrict(districtName: String): AgroWeatherSnapshot {
        val zone = DistrictZone.values().find { it.districtName.equals(districtName, ignoreCase = true) }
            ?: DistrictZone.THANJAVUR

        return when (zone) {
            DistrictZone.THANJAVUR -> AgroWeatherSnapshot(
                districtName = "Thanjavur",
                zone = "Cauvery Delta Zone",
                temperatureC = 32.5,
                minTempC = 24.0,
                maxTempC = 34.5,
                humidityPct = 74,
                windSpeedKmh = 14.0,
                rainfallLast24hMm = 3.5,
                rainForecastNext48hMm = 38.0,
                weatherCondition = "Scattered Monsoon Clouds",
                irrigationAlert = "RAIN ALERT: Moderate to heavy rain (38 mm) forecast over Cauvery delta in next 36h. Postpone canal irrigation and hold nitrogen top-dressing.",
                isAlertUrgent = true
            )
            DistrictZone.TIRUVARUR -> AgroWeatherSnapshot(
                districtName = "Tiruvarur",
                zone = "Cauvery Delta Zone",
                temperatureC = 31.8,
                minTempC = 24.5,
                maxTempC = 33.2,
                humidityPct = 78,
                windSpeedKmh = 16.5,
                rainfallLast24hMm = 5.0,
                rainForecastNext48hMm = 45.0,
                weatherCondition = "Cloudy with Thunderstorm Outlook",
                irrigationAlert = "FLOODING PRONE ALERT: Heavy delta rainfall anticipated. Keep drainage sluices clear to prevent submergence of young tillers.",
                isAlertUrgent = true
            )
            DistrictZone.MADURAI -> AgroWeatherSnapshot(
                districtName = "Madurai",
                zone = "Southern Zone",
                temperatureC = 34.0,
                minTempC = 25.0,
                maxTempC = 36.0,
                humidityPct = 62,
                windSpeedKmh = 11.0,
                rainfallLast24hMm = 0.0,
                rainForecastNext48hMm = 4.0,
                weatherCondition = "Sunny / Warm Breezes",
                irrigationAlert = "DRY SPELL: High evaporative demand (5.6 mm/day). Ensure scheduled AWD wetting cycle is maintained.",
                isAlertUrgent = false
            )
            DistrictZone.COIMBATORE -> AgroWeatherSnapshot(
                districtName = "Coimbatore",
                zone = "Western Zone",
                temperatureC = 29.5,
                minTempC = 21.0,
                maxTempC = 31.0,
                humidityPct = 68,
                windSpeedKmh = 18.0,
                rainfallLast24hMm = 1.0,
                rainForecastNext48hMm = 12.0,
                weatherCondition = "Partly Cloudy with Mild Westerly Winds",
                irrigationAlert = null,
                isAlertUrgent = false
            )
            else -> AgroWeatherSnapshot(
                districtName = zone.districtName,
                zone = zone.agroZone,
                temperatureC = zone.defaultTempC,
                minTempC = zone.defaultTempC - 6.0,
                maxTempC = zone.defaultTempC + 4.0,
                humidityPct = zone.defaultHumidityPct,
                windSpeedKmh = 12.0,
                rainfallLast24hMm = 0.0,
                rainForecastNext48hMm = 8.0,
                weatherCondition = "Fair Weather",
                irrigationAlert = null,
                isAlertUrgent = false
            )
        }
    }
}
