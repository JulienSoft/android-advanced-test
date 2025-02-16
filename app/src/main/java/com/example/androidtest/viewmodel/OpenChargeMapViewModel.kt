package com.example.androidtest.viewmodel

import androidx.compose.ui.geometry.Size
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtest.MapProjectionProvider
import com.example.androidtest.MapProjectionProviderImpl
import com.example.androidtest.api.APIResult
import com.example.androidtest.distanceFromCameraStateInMeters
import com.example.androidtest.haversineDistance
import com.example.androidtest.models.ChargingStation
import com.mapbox.maps.CameraState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoadingUIState(
    val error: String? = null,
    val isLoading: Boolean = false
)

@OptIn(FlowPreview::class)
class OpenChargeMapViewModel(
    private val repository: OpenChargeMapRepository
): ViewModel() {
    val minDistanceThreshold = 50.0               // In meters
    val debounceCameraStateChange: Long = 300     // Adjust as needed

    // Store the current state of the stations UI
    private val _loadingUIState = MutableStateFlow(LoadingUIState())
    val loadingUIState: StateFlow<LoadingUIState> = _loadingUIState

    val chargingStations = repository.getAllChargingStations()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList()) // Caches and prevents recomputation

    // Viewport stations
    private val _filteredStations = MutableStateFlow<List<ChargingStation>>(emptyList())
    val filteredStations: StateFlow<List<ChargingStation>> = _filteredStations
        .debounce(800)
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList()) // Caches and prevents recomputation

    // Current station selected displayed in bottom sheet
    val selectedStation: MutableStateFlow<ChargingStation?> = MutableStateFlow<ChargingStation?>(null)

    // Store the current camera state (center, zoom, pitch, bearing, etc...)
    private val _cameraState = MutableStateFlow<CameraState?>(null)
    val cameraState: StateFlow<CameraState?> = _cameraState
    fun onCameraChanged(cameraState: CameraState) {
        viewModelScope.launch {
            _cameraState.emit(cameraState)
        }
    }

    // Store screen size in pixels to calculate distance with zoom
    private val _screenSize = MutableStateFlow<Size>(Size(0f, 0f)) // Store screen width
    val screenSize = _screenSize.asStateFlow()

    fun updateScreenSize(size: Size) {
        viewModelScope.launch {
            _screenSize.value = size
        }
    }

    // Change map layers
    private val _mapLayers = MutableStateFlow(0)
    val mapLayers: StateFlow<Int> = _mapLayers
    fun changeMapLayers() {
        _mapLayers.value = (_mapLayers.value + 1) % 2
    }

    // Center map on user
    private val _centerMapOnUser = MutableStateFlow(false)
    val centerMapOnUser: StateFlow<Boolean> = _centerMapOnUser
    fun centerMapOnUser(state: Boolean = true) {
        _centerMapOnUser.value = state
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
                fetchStations(cameraState)

                _filteredStations.update {
                    chargingStations.value.filter { it.inViewPort(cameraState, screenSize.value) }
                }
            }
        }
    }

    /*
     * Retry fetching the stations with current camera state
     */
    fun retry() {
        viewModelScope.launch {
            cameraState.value?.let {
                fetchStations(it)
            }
        }
    }

    fun fetchStations(cameraState: CameraState?, mapProjectionProvider: MapProjectionProvider = MapProjectionProviderImpl()) {
        // If the camera state is null, return
        if (cameraState == null) return

        // Calculate the distance based on the camera state
        val distanceKm = distanceFromCameraStateInMeters(cameraState, screenSize.value, mapProjectionProvider) / 1000f

        // Take the max of the distance (First step to prepare tablet support)
        val distance = kotlin.math.max(distanceKm.width.toInt(), distanceKm.height.toInt())

        // Get the stations based on the new camera state
        fetchStations(cameraState.center.longitude(), cameraState.center.latitude(), distance = distance)
    }

    // maxResults: Maximum number of results to return, can be configured in settings
    private fun fetchStations(longitude: Double, latitude: Double, maxResults: Int = 50, distance: Int = 10) {
        _loadingUIState.value = _loadingUIState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            val result = repository.fetchCharginStations(longitude, latitude, maxResults, distance)
            when(result){
                is APIResult.Success -> {
                    _loadingUIState.update {
                        it.copy(
                            isLoading = false,
                        )
                    }
                }

                is APIResult.Error -> {
                    _loadingUIState.update {
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