package com.example.androidtest.viewmodel

import android.util.Log
import com.example.androidtest.api.APIResult
import com.example.androidtest.api.OpenChargeMapAPIService
import com.example.androidtest.db.ChargingDao
import com.example.androidtest.models.ChargingStation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.net.UnknownHostException

interface OpenChargeMapRepository {
    suspend fun fetchCharginStations(longitude: Double, latitude: Double, maxResults: Int, distance: Int): APIResult<List<ChargingStation>>
    fun getAllChargingStations(): Flow<List<ChargingStation>>
}

class OpenChargeMapRepositoryImpl(
    private val apiService: OpenChargeMapAPIService,
    private val dispatcher: CoroutineDispatcher,
    private val chargingDao: ChargingDao
): OpenChargeMapRepository {

    override fun getAllChargingStations() = chargingDao.getAllChargingStations()

    override suspend fun fetchCharginStations(
        longitude: Double,
        latitude: Double,
        maxResults: Int,
        distance: Int
    ): APIResult<List<ChargingStation>> {
        return withContext(dispatcher) {
            try{
                val chargingStations = apiService.getChargingStations(longitude, latitude, maxResults, distance)
                // Insert in db
                chargingDao.insertAllChargingStation(chargingStations)
                APIResult.Success(chargingStations)
            }catch (e: Exception) {
                val message = if(e is UnknownHostException) {
                    "Une erreur est survenue lors de la connexion à internet"
                } else {
                    Log.e("APIError", "Error: ${e.message}")
                    "Something went wrong"
                }
                APIResult.Error(message)
            }
        }
    }
}