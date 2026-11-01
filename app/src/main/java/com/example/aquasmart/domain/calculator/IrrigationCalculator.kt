package com.example.aquasmart.domain.calculator

import com.example.aquasmart.data.model.CropGrowthStage
import com.example.aquasmart.data.model.IrrigationMethod
import com.example.aquasmart.data.model.SoilType
import kotlin.math.max
import kotlin.math.roundToInt

data class IrrigationCalculationResult(
    val et0MmDay: Double,
    val etcMmDay: Double,
    val percolationLossMmDay: Double,
    val effectiveRainfallMm: Double,
    val netIrrigationDepthMm: Double,
    val waterVolumeLitres: Double,
    val waterVolumeCubicMeters: Double,
    val waterSavedLitres: Double,
    val recommendationAction: String,
    val recommendationSummary: String,
    val stepByStepExplanation: String,
    val isIrrigationNeededToday: Boolean,
    val awdPaniPipeStatus: String
)

object IrrigationCalculator {

    private const val SQ_METERS_PER_ACRE = 4046.86

    fun calculateIrrigation(
        areaAcres: Double,
        stage: CropGrowthStage,
        soilType: SoilType,
        soilMoisturePct: Double,
        recentRainfallMm: Double,
        temperatureC: Double,
        method: IrrigationMethod,
        isSensorMeasured: Boolean = false
    ): IrrigationCalculationResult {
        val safeArea = if (areaAcres <= 0) 1.0 else areaAcres
        val safeTemp = if (temperatureC <= 0) 32.0 else temperatureC
        val safeMoisture = soilMoisturePct.coerceIn(0.0, 100.0)
        val safeRain = max(0.0, recentRainfallMm)

        // 1. Reference Evapotranspiration (ET0) estimation based on Hargreaves-Samani / Tamil Nadu temperature baseline
        val et0 = (0.155 * safeTemp + 0.35).coerceIn(3.5, 7.5)

        // 2. Crop Evapotranspiration (ETc) = Kc * ET0
        val etc = stage.kc * et0

        // 3. Daily Soil Percolation loss
        val percolation = soilType.percolationMmDay

        // 4. Effective Rainfall (Pe) using USDA SCS / FAO-24 method adapted for bunded rice
        val effectiveRainfall = when {
            safeRain < 5.0 -> 0.0
            safeRain <= 40.0 -> (safeRain - 5.0) * 0.8
            else -> (35.0 * 0.8) + ((safeRain - 40.0) * 0.25).coerceAtMost(20.0)
        }

        // 5. Total Daily Consumptive Field Requirement
        val grossDailyDemand = etc + percolation

        // 6. Moisture Deficit & Need assessment
        val fieldCapacity = soilType.fieldCapacityPct
        val moistureDepletionRatio = (fieldCapacity - safeMoisture) / fieldCapacity

        // Alternate Wetting and Drying (AWD) threshold logic
        val paniPipeWaterLevelCm = when {
            safeMoisture >= 85.0 -> +5.0 // 5cm standing water
            safeMoisture >= 70.0 -> +2.0 // 2cm shallow water
            safeMoisture >= 50.0 -> -7.0 // 7cm below ground level
            safeMoisture >= 35.0 -> -15.0 // 15cm below ground level (AWD trigger!)
            else -> -20.0 // severe depletion
        }

        val isRipeningDraining = (stage == CropGrowthStage.RIPENING_MATURITY)
        val recentHeavyRain = (safeRain >= 25.0 || effectiveRainfall >= grossDailyDemand)
        val soilIsSaturated = (safeMoisture >= 80.0)

        val needsIrrigation: Boolean
        val netDepthMm: Double
        val recommendationAction: String
        val recommendationSummary: String
        val paniPipeStatus: String

        if (isRipeningDraining) {
            needsIrrigation = false
            netDepthMm = 0.0
            recommendationAction = "DRAIN / DO NOT IRRIGATE"
            recommendationSummary = "Paddy is in Ripening/Maturity stage. Keep field drained 10-14 days before harvest to hasten uniform grain ripening."
            paniPipeStatus = "Field drained for harvest."
        } else if (recentHeavyRain) {
            needsIrrigation = false
            netDepthMm = 0.0
            recommendationAction = "POSTPONE IRRIGATION"
            recommendationSummary = "Recent rainfall of ${safeRain.format(1)} mm provides ${effectiveRainfall.format(1)} mm effective water. Conserve canal/well water."
            paniPipeStatus = "Water perched above surface from rain."
        } else if (soilIsSaturated && !stage.criticalWaterStage) {
            needsIrrigation = false
            netDepthMm = 0.0
            recommendationAction = "DELAY IRRIGATION (AWD Drying phase)"
            recommendationSummary = "Soil moisture (${safeMoisture.format(0)}%) is sufficient. Under AWD protocol, let water subside to stimulate deeper root aeration."
            paniPipeStatus = "Pani pipe level: ~${paniPipeWaterLevelCm.format(0)} cm. Re-irrigate when water drops to -15 cm."
        } else {
            needsIrrigation = true
            // Calculate irrigation depth required to bring soil to stage standard
            val targetDepthMm = if (stage.criticalWaterStage) {
                // Continuous 50 mm (5 cm) water layer required during panicle initiation & flowering
                50.0
            } else {
                when (method) {
                    IrrigationMethod.AWD -> 35.0 // 3.5 cm recharge
                    IrrigationMethod.DRIP_SYSTEM -> (grossDailyDemand - effectiveRainfall).coerceAtLeast(4.0)
                    IrrigationMethod.CONTINUOUS_FLOODING -> 50.0
                    IrrigationMethod.FURROW_BORDER -> 40.0
                }
            }

            // Adjust for irrigation efficiency
            val efficiency = method.efficiencyPct / 100.0
            netDepthMm = (targetDepthMm / efficiency).coerceIn(5.0, 75.0)

            recommendationAction = if (stage.criticalWaterStage) {
                "MANDATORY IRRIGATION (Critical Growth Phase)"
            } else {
                "RECOMMENDED IRRIGATION (${method.methodName})"
            }

            recommendationSummary = if (method == IrrigationMethod.AWD) {
                "Water level in field water tube is at the -15 cm threshold. Apply 3.5 to 5 cm depth to replenish root zone."
            } else {
                "Apply ${netDepthMm.format(1)} mm water to maintain optimal vegetative growth."
            }

            paniPipeStatus = "Field water tube level: -15 cm reached. Irrigation triggered."
        }

        // 7. Volumetric Calculations
        val totalAreaSqMeters = safeArea * SQ_METERS_PER_ACRE
        val waterVolumeLitres = if (needsIrrigation) {
            netDepthMm * totalAreaSqMeters // 1 mm depth on 1 m2 = 1 litre
        } else 0.0

        val waterVolumeCubicMeters = waterVolumeLitres / 1000.0

        // 8. Water Saved Calculation compared to Continuous Flooding baseline
        val baselineDepthMm = if (needsIrrigation) (50.0 / 0.60) else 0.0 // 83.3 mm for continuous flooding
        val baselineLitres = baselineDepthMm * totalAreaSqMeters
        val waterSavedLitres = if (needsIrrigation && method != IrrigationMethod.CONTINUOUS_FLOODING) {
            max(0.0, baselineLitres - waterVolumeLitres)
        } else {
            0.0
        }

        // 9. Transparent Step-by-step Agronomic Explanation
        val inputSource = if (isSensorMeasured) "IoT Telemetry Sensors" else "Farmer-Entered Agronomic Inputs"
        val explanation = buildString {
            appendLine("=== AQUASMART AGRONOMIC CALCULATION BREAKDOWN ===")
            appendLine("Data Origin: $inputSource")
            appendLine("1. Climatic Evaporative Demand (ETo): ${et0.format(2)} mm/day at ${safeTemp.format(1)}°C")
            appendLine("2. Crop Stage (${stage.stageName}): Crop Coefficient Kc = ${stage.kc}")
            appendLine("   ETc = Kc × ETo = ${stage.kc} × ${et0.format(2)} = ${etc.format(2)} mm/day")
            appendLine("3. Soil Percolation (${soilType.displayName}): +${percolation.format(1)} mm/day")
            appendLine("   Gross Daily Field Demand = ETc + Percolation = ${(etc + percolation).format(2)} mm/day")
            appendLine("4. Rainfall Credit: Recorded ${safeRain.format(1)} mm → Effective Infiltration = ${effectiveRainfall.format(1)} mm")
            appendLine("5. Soil Moisture: ${safeMoisture.format(0)}% (Field Capacity: ${fieldCapacity.format(0)}%)")
            appendLine("6. Application Method: ${method.methodName} (Efficiency: ${method.efficiencyPct}%)")
            if (needsIrrigation) {
                appendLine("7. Net Irrigation Depth: ${netDepthMm.format(1)} mm")
                appendLine("8. Total Water Volume for ${safeArea.format(2)} acres (${totalAreaSqMeters.roundToInt()} m²):")
                appendLine("   Volume = ${netDepthMm.format(1)} mm × ${totalAreaSqMeters.roundToInt()} m² = ${waterVolumeLitres.format(0)} Litres (${waterVolumeCubicMeters.format(1)} m³)")
                if (waterSavedLitres > 0) {
                    appendLine("9. Water Saved vs Continuous Flooding: ${waterSavedLitres.format(0)} Litres (${(method.waterSavingPct)}% conservation)")
                }
            } else {
                appendLine("7. Irrigation Postponed: Soil has sufficient moisture or paddy is in dry ripening stage.")
            }
        }

        return IrrigationCalculationResult(
            et0MmDay = et0,
            etcMmDay = etc,
            percolationLossMmDay = percolation,
            effectiveRainfallMm = effectiveRainfall,
            netIrrigationDepthMm = netDepthMm,
            waterVolumeLitres = waterVolumeLitres,
            waterVolumeCubicMeters = waterVolumeCubicMeters,
            waterSavedLitres = waterSavedLitres,
            recommendationAction = recommendationAction,
            recommendationSummary = recommendationSummary,
            stepByStepExplanation = explanation,
            isIrrigationNeededToday = needsIrrigation,
            awdPaniPipeStatus = paniPipeStatus
        )
    }

    private fun Double.format(digits: Int): String = "%.${digits}f".format(this)
}
