package com.example.androidtest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtest.api.APIResult
import com.example.androidtest.api.models.ChargingStation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StationsUIStation(
    val stations: List<ChargingStation> = emptyList(),
    val error: String? = null,
    val isLoading: Boolean = false
)

class OpenChargeMapViewModel(
    private val repository: OpenChargeMapRepository
): ViewModel() {
    private val _stationsUIState = MutableStateFlow(StationsUIStation())
    val stationsUIState: StateFlow<StationsUIStation> = _stationsUIState

    init {
        getStationResults(7.3389937, 47.7467233)
    }

    private fun getStationResults(longitude: Double, latitude: Double, maxResults: Int = 10, distance: Int = 10) {
        _stationsUIState.value = _stationsUIState.value.copy(isLoading = true, stations = emptyList())

        viewModelScope.launch {
            val result = repository.getChargingStations(longitude, latitude, maxResults, distance)
            when(result){

                is APIResult.Success -> {
                    _stationsUIState.update {
                        it.copy(
                            stations = result.data,
                            isLoading = false,
                        )
                    }
                }

                is APIResult.Error -> {
                    _stationsUIState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }
}