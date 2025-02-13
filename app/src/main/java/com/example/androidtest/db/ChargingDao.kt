package com.example.androidtest.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.androidtest.models.ChargingStation
import kotlinx.coroutines.flow.Flow

@Dao
interface ChargingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllChargingStation(post: List<ChargingStation>)

    @Query("SELECT * FROM chargingStation")
    fun getAllChargingStations(): Flow<List<ChargingStation>>
}