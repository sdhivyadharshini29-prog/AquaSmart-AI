package com.example

import com.example.aquasmart.data.model.*
import com.example.aquasmart.domain.calculator.*
import org.junit.Assert.*
import org.junit.Test

class AquaSmartAgronomicTests {

    @Test
    fun testIrrigationCalculation_NormalCondition_CalculatesValidVolumeAndSavings() {
        val result = IrrigationCalculator.calculateIrrigation(
            areaAcres = 3.5,
            stage = CropGrowthStage.VEGETATIVE_TILLERING,
            soilType = SoilType.ALLUVIAL,
            soilMoisturePct = 40.0,
            recentRainfallMm = 0.0,
            temperatureC = 32.5,
            method = IrrigationMethod.AWD,
            isSensorMeasured = false
        )

        assertTrue("Irrigation should be recommended when moisture is 40%", result.isIrrigationNeededToday)
        assertTrue("Net irrigation depth should be positive", result.netIrrigationDepthMm > 0)
        assertTrue("Water volume in litres should be positive", result.waterVolumeLitres > 0)
        assertTrue("Cubic meters should correspond to litres / 1000", result.waterVolumeCubicMeters == result.waterVolumeLitres / 1000.0)
        assertTrue("AWD method must yield positive water savings vs continuous flooding", result.waterSavedLitres > 0)
    }

    @Test
    fun testIrrigationCalculation_ExtremeRainfall_PostponesIrrigation() {
        val result = IrrigationCalculator.calculateIrrigation(
            areaAcres = 2.0,
            stage = CropGrowthStage.VEGETATIVE_TILLERING,
            soilType = SoilType.CLAYEY,
            soilMoisturePct = 85.0,
            recentRainfallMm = 55.0, // Heavy monsoon shower
            temperatureC = 28.0,
            method = IrrigationMethod.AWD,
            isSensorMeasured = true
        )

        assertFalse("Irrigation must be postponed after 55mm heavy rain", result.isIrrigationNeededToday)
        assertEquals("Net depth must be zero", 0.0, result.netIrrigationDepthMm, 0.001)
        assertEquals("Water volume must be zero", 0.0, result.waterVolumeLitres, 0.001)
        assertTrue("Recommendation must mention postponing or rain", result.recommendationAction.contains("POSTPONE") || result.recommendationAction.contains("DELAY"))
    }

    @Test
    fun testIrrigationCalculation_RipeningStage_MandatesFieldDraining() {
        val result = IrrigationCalculator.calculateIrrigation(
            areaAcres = 4.0,
            stage = CropGrowthStage.RIPENING_MATURITY,
            soilType = SoilType.CLAY_LOAM,
            soilMoisturePct = 30.0,
            recentRainfallMm = 0.0,
            temperatureC = 34.0,
            method = IrrigationMethod.AWD
        )

        assertFalse("Ripening maturity paddy must not be irrigated", result.isIrrigationNeededToday)
        assertEquals(0.0, result.netIrrigationDepthMm, 0.001)
        assertTrue("Action must mandate draining", result.recommendationAction.contains("DRAIN"))
    }

    @Test
    fun testIrrigationCalculation_ExtremeAndBoundaryInputs_HandledGracefully() {
        // Zero acreage and negative rainfall test
        val resultZero = IrrigationCalculator.calculateIrrigation(
            areaAcres = 0.0,
            stage = CropGrowthStage.NURSERY,
            soilType = SoilType.SANDY_LOAM,
            soilMoisturePct = -10.0,
            recentRainfallMm = -5.0,
            temperatureC = -10.0,
            method = IrrigationMethod.CONTINUOUS_FLOODING
        )
        assertNotNull(resultZero)
        assertTrue(resultZero.waterVolumeLitres >= 0)
        assertTrue(resultZero.netIrrigationDepthMm >= 0)
    }

    @Test
    fun testSoilHealthAnalysis_AcidicSoil_RecommendsLime() {
        val result = SoilHealthAnalyzer.analyzeSoil(
            ph = 5.2,
            nKgHa = 210.0,
            pKgHa = 8.0,
            kKgHa = 90.0,
            ocPct = 0.40,
            soilType = "Red Loam"
        )

        assertEquals(SoilRating.LOW, result.nitrogenRating)
        assertEquals(SoilRating.LOW, result.phosphorusRating)
        assertEquals(SoilRating.LOW, result.potassiumRating)
        assertEquals(SoilRating.LOW, result.organicCarbonRating)
        assertTrue("Acidic soil must recommend Lime/CaCO3", result.soilAmendmentAdvice.contains("Lime"))
    }

    @Test
    fun testSoilHealthAnalysis_AlkaliSoil_RecommendsGypsum() {
        val result = SoilHealthAnalyzer.analyzeSoil(
            ph = 8.8,
            nKgHa = 350.0,
            pKgHa = 18.0,
            kKgHa = 220.0,
            ocPct = 0.65,
            soilType = "Clayey"
        )

        assertTrue("Sodic soil must recommend Gypsum", result.soilAmendmentAdvice.contains("Gypsum"))
    }

    @Test
    fun testFertilizerAdvisory_CalculatesValidSplitsAndBagCounts() {
        val plan = FertilizerCalculator.calculatePlan(
            areaAcres = 2.471, // exactly 1 hectare
            variety = PaddyVariety.ADT_43,
            nRating = SoilRating.MEDIUM,
            pRating = SoilRating.MEDIUM,
            kRating = SoilRating.MEDIUM
        )

        assertTrue("Urea requirement must be greater than 0", plan.totalUreaKg > 0)
        assertTrue("DAP requirement must be greater than 0", plan.totalDapKg > 0)
        assertTrue("MOP requirement must be greater than 0", plan.totalMopKg > 0)
        assertEquals("Must have 4 split stages", 4, plan.splitSchedules.size)
        assertTrue("Zinc sulphate must be recommended for basal", plan.zincSulphateKg > 0)
    }
}
