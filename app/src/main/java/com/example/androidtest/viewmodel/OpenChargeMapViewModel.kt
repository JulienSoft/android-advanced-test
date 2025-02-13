package com.example.androidtest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtest.api.APIResult
import com.example.androidtest.models.ChargingStation
import com.example.androidtest.haversineDistance
import com.example.androidtest.interpolateResolution
import com.mapbox.maps.CameraState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StationsUIStation(
    val stations: List<ChargingStation> = emptyList(),
    val error: String? = null,
    val isLoading: Boolean = false
)

@OptIn(FlowPreview::class)
class OpenChargeMapViewModel(
    private val repository: OpenChargeMapRepository
): ViewModel() {
    val minDistanceThreshold = 50.0   // In meters
    val debounceCameraStateChange: Long = 300     // Adjust as needed

    // Store the current state of the stations UI
    private val _stationsUIState = MutableStateFlow(StationsUIStation())
    val stationsUIState: StateFlow<StationsUIStation> = _stationsUIState

    // Store the current camera state (center, zoom, pitch, bearing, etc...)
    private val _cameraState = MutableStateFlow<CameraState?>(null)
    val cameraState: StateFlow<CameraState?> = _cameraState
    fun onCameraChanged(cameraState: CameraState) {
        viewModelScope.launch {
            _cameraState.emit(cameraState)
        }
    }

    // Store screen width in pixels to calculate distance with zoom
    private val _screenWidth = MutableStateFlow(0) // Store screen width
    val screenWidth = _screenWidth.asStateFlow()

    fun updateScreenWidth(width: Int) {
        viewModelScope.launch {
            _screenWidth.value = width
        }
    }

    init {
        viewModelScope.launch {
            cameraState.debounce(debounceCameraStateChange).distinctUntilChanged {
                old, new ->
                    val oldPoint = old?.center ?: return@distinctUntilChanged false
                    val newPoint = new?.center ?: return@distinctUntilChanged false

                // Check if the distance between old and new point is less than the threshold
                val distance = haversineDistance(oldPoint, newPoint)
                if (distance < minDistanceThreshold) {
                    return@distinctUntilChanged true // Ignore update
                }

                false // Accept update
            }.collect { cameraState ->
                // If the camera state changes, get the stations from OpenChargeMap API
                cameraState?.let {
                    // Get the distance in meter per pixels based on the zoom level
                    val meterByPx = interpolateResolution(it.zoom)

                    // Calculate the distance in km based on the screen width
                    val distance = screenWidth.value * meterByPx / 1000

                    // Get the stations based on the new camera state
                    getStationResults(it.center.longitude(), it.center.latitude(), distance = distance.toInt())
                }
            }
        }
    }

    // maxResults: Maximum number of results to return, can be configured in settings
    private fun getStationResults(longitude: Double, latitude: Double, maxResults: Int = 50, distance: Int = 10) {
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