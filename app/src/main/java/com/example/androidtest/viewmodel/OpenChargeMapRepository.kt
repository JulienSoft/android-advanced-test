package com.example.androidtest.viewmodel

import com.example.androidtest.api.APIResult
import com.example.androidtest.api.OpenChargeMapAPIService
import com.example.androidtest.db.ChargingDao
import com.example.androidtest.models.ChargingStation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface OpenChargeMapRepository {
    suspend fun getChargingStations(longitude: Double, latitude: Double, maxResults: Int, distance: Int): APIResult<List<ChargingStation>>
}

class OpenChargeMapRepositoryImpl(
    private val apiService: OpenChargeMapAPIService,
    private val dispatcher: CoroutineDispatcher,
    private val chargingDao: ChargingDao
): OpenChargeMapRepository {

    override suspend fun getChargingStations(
        longitude: Double,
        latitude: Double,
        maxResults: Int,
        distance: Int
    ): APIResult<List<ChargingStation>> {
        return withContext(dispatcher) {
            try{
                val chargingStations = apiService.getChargingStations(longitude, latitude, maxResults, distance)
                chargingDao.insertAllChargingStation(chargingStations)
                APIResult.Success(chargingStations)
            }catch (e: Exception){
                APIResult.Error(e.message ?: "Something went wrong")
            }
        }
    }
}