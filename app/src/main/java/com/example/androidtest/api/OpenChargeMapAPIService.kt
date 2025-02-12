package com.example.androidtest.api

import com.example.androidtest.api.models.ChargingStation
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

val httpClient = HttpClient {
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            }
        )
    }
}

class OpenChargeMapAPIService {
    private val apiKey = "0323586f-3067-4edd-a1fa-e1dfb3f4e894"

    suspend fun getChargingStations(longitude: Double, latitude: Double, maxResults: Int, distance: Int): List<ChargingStation> {
        val url = "https://api.openchargemap.io/v3/poi/?output=json&countrycode=FR&maxresults=$maxResults&key=$apiKey&latitude=$latitude&longitude=$longitude&distance=$distance&distanceunit=km"

        return httpClient
            .get(url)
            .body<List<ChargingStation>>()
    }
}