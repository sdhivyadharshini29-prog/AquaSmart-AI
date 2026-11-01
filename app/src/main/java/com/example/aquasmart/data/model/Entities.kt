package com.example.aquasmart.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "farmer_profiles")
data class FarmerProfileEntity(
    @PrimaryKey val id: String = "default_farmer",
    val name: String,
    val phone: String,
    val email: String,
    val primaryDistrict: String,
    val preferredLanguage: String = "English",
    val activeFarmId: Long = 1L,
    val createdDateMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "farms")
data class FarmEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val farmName: String,
    val areaAcres: Double,
    val district: String,
    val soilType: String,
    val waterSource: String,
    val surveyNumber: String = "",
    val isDeltaZone: Boolean = true
)

@Entity(tableName = "crop_cycles")
data class CropCycleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val farmId: Long,
    val cropName: String = "Paddy (Rice)",
    val varietyName: String,
    val varietyCode: String,
    val sowingDateMillis: Long,
    val stage: String,
    val expectedHarvestDateMillis: Long,
    val targetYieldTons: Double = 6.0,
    val isActive: Boolean = true
)

@Entity(tableName = "soil_records")
data class SoilRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val farmId: Long,
    val recordedDateMillis: Long = System.currentTimeMillis(),
    val ph: Double,
    val nitrogenKgHa: Double,
    val phosphorusKgHa: Double,
    val potassiumKgHa: Double,
    val organicCarbonPct: Double,
    val soilType: String,
    val phCategory: String,
    val nitrogenRating: String,
    val phosphorusRating: String,
    val potassiumRating: String,
    val organicCarbonRating: String,
    val summaryRecommendation: String,
    val amendmentRequired: String
)

@Entity(tableName = "irrigation_records")
data class IrrigationRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val farmId: Long,
    val timestampMillis: Long = System.currentTimeMillis(),
    val areaAcres: Double,
    val stage: String,
    val soilType: String,
    val soilMoisturePct: Double,
    val recentRainfallMm: Double,
    val temperatureC: Double,
    val irrigationMethod: String,
    val netIrrigationMm: Double,
    val waterVolumeLitres: Double,
    val waterVolumeM3: Double,
    val waterSavedLitres: Double,
    val recommendation: String,
    val calculationDetails: String,
    val isSensorMeasured: Boolean
)

@Entity(tableName = "crop_observations")
data class CropObservationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val farmId: Long,
    val observationDateMillis: Long = System.currentTimeMillis(),
    val observedSymptoms: String,
    val affectedPart: String,
    val suspectedCondition: String,
    val severityLevel: String,
    val ipmGuideline: String,
    val organicControl: String,
    val disclaimer: String = "Preliminary agronomic observation screening. Not a certified laboratory pathology diagnosis."
)
