package com.example.androidtest.viewmodel

import com.example.androidtest.api.APIResult
import com.example.androidtest.api.OpenChargeMapAPIService
import com.example.androidtest.api.models.ChargingStation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface OpenChargeMapRepository {
    suspend fun getChargingStations(longitude: Double, latitude: Double, maxResults: Int, distance: Int): APIResult<List<ChargingStation>>
}

class OpenChargeMapRepositoryImpl(
    private val apiService: OpenChargeMapAPIService,
    private val dispatcher: CoroutineDispatcher
): OpenChargeMapRepository {

    override suspend fun getChargingStations(
        longitude: Double,
        latitude: Double,
        maxResults: Int,
        distance: Int
    ): APIResult<List<ChargingStation>> {
        return withContext(dispatcher){
            try{
                val chargingStations = apiService.getChargingStations(longitude, latitude, maxResults, distance)
                APIResult.Success(chargingStations)
            }catch (e: Exception){
                APIResult.Error(e.message ?: "Something went wrong")
            }
        }
    }
}