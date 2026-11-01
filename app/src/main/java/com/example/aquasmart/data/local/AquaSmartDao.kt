package com.example.aquasmart.data.local

import androidx.room.*
import com.example.aquasmart.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AquaSmartDao {

    // Farmer Profile
    @Query("SELECT * FROM farmer_profiles WHERE id = :id LIMIT 1")
    fun getFarmerProfile(id: String = "default_farmer"): Flow<FarmerProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: FarmerProfileEntity)

    // Farms
    @Query("SELECT * FROM farms ORDER BY id ASC")
    fun getAllFarms(): Flow<List<FarmEntity>>

    @Query("SELECT * FROM farms WHERE id = :farmId LIMIT 1")
    fun getFarmById(farmId: Long): Flow<FarmEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFarm(farm: FarmEntity): Long

    @Update
    suspend fun updateFarm(farm: FarmEntity)

    @Delete
    suspend fun deleteFarm(farm: FarmEntity)

    // Crop Cycles
    @Query("SELECT * FROM crop_cycles WHERE farmId = :farmId AND isActive = 1 ORDER BY id DESC LIMIT 1")
    fun getActiveCropCycle(farmId: Long): Flow<CropCycleEntity?>

    @Query("SELECT * FROM crop_cycles WHERE farmId = :farmId ORDER BY sowingDateMillis DESC")
    fun getCropCyclesForFarm(farmId: Long): Flow<List<CropCycleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCropCycle(cropCycle: CropCycleEntity): Long

    @Update
    suspend fun updateCropCycle(cropCycle: CropCycleEntity)

    // Soil Records
    @Query("SELECT * FROM soil_records WHERE farmId = :farmId ORDER BY recordedDateMillis DESC LIMIT 1")
    fun getLatestSoilRecord(farmId: Long): Flow<SoilRecordEntity?>

    @Query("SELECT * FROM soil_records WHERE farmId = :farmId ORDER BY recordedDateMillis DESC")
    fun getSoilRecordsForFarm(farmId: Long): Flow<List<SoilRecordEntity>>

    @Query("SELECT * FROM soil_records ORDER BY recordedDateMillis DESC")
    fun getAllSoilRecords(): Flow<List<SoilRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSoilRecord(record: SoilRecordEntity): Long

    // Irrigation Records
    @Query("SELECT * FROM irrigation_records WHERE farmId = :farmId ORDER BY timestampMillis DESC LIMIT 1")
    fun getLatestIrrigationRecord(farmId: Long): Flow<IrrigationRecordEntity?>

    @Query("SELECT * FROM irrigation_records WHERE farmId = :farmId ORDER BY timestampMillis DESC")
    fun getIrrigationRecordsForFarm(farmId: Long): Flow<List<IrrigationRecordEntity>>

    @Query("SELECT * FROM irrigation_records ORDER BY timestampMillis DESC")
    fun getAllIrrigationRecords(): Flow<List<IrrigationRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIrrigationRecord(record: IrrigationRecordEntity): Long

    // Crop Health Observations
    @Query("SELECT * FROM crop_observations WHERE farmId = :farmId ORDER BY observationDateMillis DESC")
    fun getObservationsForFarm(farmId: Long): Flow<List<CropObservationEntity>>

    @Query("SELECT * FROM crop_observations ORDER BY observationDateMillis DESC")
    fun getAllObservations(): Flow<List<CropObservationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertObservation(observation: CropObservationEntity): Long

    @Delete
    suspend fun deleteObservation(observation: CropObservationEntity)
}
