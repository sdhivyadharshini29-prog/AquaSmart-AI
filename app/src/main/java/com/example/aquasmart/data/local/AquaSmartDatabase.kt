package com.example.aquasmart.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.aquasmart.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        FarmerProfileEntity::class,
        FarmEntity::class,
        CropCycleEntity::class,
        SoilRecordEntity::class,
        IrrigationRecordEntity::class,
        CropObservationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AquaSmartDatabase : RoomDatabase() {

    abstract fun aquaSmartDao(): AquaSmartDao

    companion object {
        @Volatile
        private var INSTANCE: AquaSmartDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AquaSmartDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AquaSmartDatabase::class.java,
                    "aquasmart_database"
                )
                    .addCallback(AquaSmartDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class AquaSmartDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialTamilNaduBaseline(database.aquaSmartDao())
                    }
                }
            }
        }

        private suspend fun populateInitialTamilNaduBaseline(dao: AquaSmartDao) {
            // Seed Farmer Profile
            val profile = FarmerProfileEntity(
                id = "default_farmer",
                name = "Karthik Subramanian",
                phone = "+91 94432 18765",
                email = "karthik.subramanian@deltafarms.in",
                primaryDistrict = "Thanjavur",
                preferredLanguage = "English",
                activeFarmId = 1L
            )
            dao.insertOrUpdateProfile(profile)

            // Seed Farms
            val farm1 = FarmEntity(
                id = 1L,
                farmName = "Cauvery Delta North (Paddy Parcel)",
                areaAcres = 3.5,
                district = "Thanjavur",
                soilType = "Delta Alluvial (Cauvery)",
                waterSource = "Grand Anicut Canal & Solar Borewell",
                surveyNumber = "SF-142/2B",
                isDeltaZone = true
            )
            val farm2 = FarmEntity(
                id = 2L,
                farmName = "Vennar Basin Field (Thaladi Plot)",
                areaAcres = 2.0,
                district = "Tiruvarur",
                soilType = "Clayey / Heavy Black",
                waterSource = "Canal Channel & Community Tank",
                surveyNumber = "SF-88/1A",
                isDeltaZone = true
            )
            dao.insertFarm(farm1)
            dao.insertFarm(farm2)

            // Seed Crop Cycle (Paddy ADT 43 in Active Tillering)
            val now = System.currentTimeMillis()
            val thirtyDaysAgo = now - (30L * 24L * 3600L * 1000L)
            val harvestInEightyDays = thirtyDaysAgo + (110L * 24L * 3600L * 1000L)

            val cropCycle1 = CropCycleEntity(
                id = 1L,
                farmId = 1L,
                cropName = "Paddy (Rice)",
                varietyName = "ADT 43 (Kuruvai Special)",
                varietyCode = "ADT 43",
                sowingDateMillis = thirtyDaysAgo,
                stage = "Active Tillering",
                expectedHarvestDateMillis = harvestInEightyDays,
                targetYieldTons = 6.0,
                isActive = true
            )
            dao.insertCropCycle(cropCycle1)

            // Seed Soil Record
            val soilRecord1 = SoilRecordEntity(
                id = 1L,
                farmId = 1L,
                recordedDateMillis = now - (10L * 24L * 3600L * 1000L),
                ph = 7.2,
                nitrogenKgHa = 260.0,
                phosphorusKgHa = 16.5,
                potassiumKgHa = 210.0,
                organicCarbonPct = 0.58,
                soilType = "Delta Alluvial (Cauvery)",
                phCategory = "Neutral & Ideal (pH 6.5 - 7.8)",
                nitrogenRating = "Low - Deficient",
                phosphorusRating = "Medium - Optimal",
                potassiumRating = "Medium - Optimal",
                organicCarbonRating = "Medium - Optimal",
                summaryRecommendation = "Soil pH is excellent. Nitrogen is slightly deficient. Top-dress additional 25% Urea during active tillering.",
                amendmentRequired = "Incorporate green manure or apply Azospirillum @ 2 kg/ha mixed with FYM."
            )
            dao.insertSoilRecord(soilRecord1)

            // Seed Initial Irrigation Record
            val irrigationRecord1 = IrrigationRecordEntity(
                id = 1L,
                farmId = 1L,
                timestampMillis = now - (2L * 24L * 3600L * 1000L),
                areaAcres = 3.5,
                stage = "Active Tillering",
                soilType = "Delta Alluvial (Cauvery)",
                soilMoisturePct = 48.0,
                recentRainfallMm = 2.0,
                temperatureC = 32.5,
                irrigationMethod = "Alternate Wetting & Drying (AWD)",
                netIrrigationMm = 35.0,
                waterVolumeLitres = 495740.0,
                waterVolumeM3 = 495.7,
                waterSavedLitres = 192800.0,
                recommendation = "AWD irrigation cycle completed. Field water tube level was -15 cm. Recharged to 3.5 cm standing water.",
                calculationDetails = "Baseline continuous flooding would require 688,540 L. AWD saved 192,800 Litres (28% water saved).",
                isSensorMeasured = false
            )
            dao.insertIrrigationRecord(irrigationRecord1)

            // Seed Initial Crop Observation
            val observation1 = CropObservationEntity(
                id = 1L,
                farmId = 1L,
                observationDateMillis = now - (1L * 24L * 3600L * 1000L),
                observedSymptoms = "Spindle-shaped elliptical lesions with gray centers on lower leaves",
                affectedPart = "Lower Leaf Blades",
                suspectedCondition = "Rice Blast (Magnaporthe oryzae) - Early Stage",
                severityLevel = "Mild / Early Signs",
                ipmGuideline = "Avoid excessive nitrogen top-dressing. Maintain standing water layer to reduce spore germination.",
                organicControl = "Foliar spray of Pseudomonas fluorescens @ 10 g/litre or 3% Neem Seed Kernel Extract (NSKE).",
                disclaimer = "Preliminary agronomic observation screening. Not a certified laboratory pathology diagnosis."
            )
            dao.insertObservation(observation1)
        }
    }
}
