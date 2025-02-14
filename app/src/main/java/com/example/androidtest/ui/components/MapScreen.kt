package com.example.androidtest.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.androidtest.viewmodel.OpenChargeMapViewModel
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.extension.compose.rememberMapState
import com.mapbox.maps.extension.compose.style.standard.LightPresetValue
import com.mapbox.maps.extension.compose.style.standard.MapboxStandardStyle
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateOptions
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


@OptIn(FlowPreview::class)
@Composable
fun MapScreen() {
    val isDarkMode = isSystemInDarkTheme()
    val coroutineScope = rememberCoroutineScope()
    val mapState = rememberMapState()       // Contains the state of the map (current viewport)
    val viewModel: OpenChargeMapViewModel = koinViewModel()

    val chargingStations by viewModel.chargingStations.collectAsStateWithLifecycle()
    val filteredStations by viewModel.filteredStations.collectAsStateWithLifecycle()
    val configuration = LocalConfiguration.current

    var screenWidth = 0f
    with(LocalDensity.current) {
        screenWidth = configuration.screenWidthDp.dp.toPx()
        viewModel.updateScreenWidth(screenWidth.toInt())
    }

    var showDynamicViewAnnotations by remember {
        mutableStateOf(false)
    }

    // Default viewport state
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            // To see Earth globe before localization is available
            zoom(0.0)
            // Default position of ChargeMap head office
            center(Point.fromLngLat(7.7115347, 48.5940562))
            pitch(0.0)
            bearing(0.0)
        }
    }

    // Manage camera changes
    DisposableEffect(Unit) {
        val job = coroutineScope.launch {
            mapState.cameraChangedEvents.collect { cameraChanged ->
                viewModel.onCameraChanged(cameraChanged.cameraState)
            }
        }

        // Cancel the job when the composable is disposed
        onDispose {
            job.cancel()
        }
    }

    MapboxMap(
        Modifier
            .fillMaxSize(),
        mapState = mapState,
        mapViewportState = mapViewportState,
        compass = { },         // Disable compass
        scaleBar = { },        // Disable scaleBar
        logo = { },            // Disable logo
        attribution = { },     // Disable attribution
        style = {
            MapboxStandardStyle { // Another option MapboxStandardSatelliteStyle()
                // Change light mode with system
                lightPreset = if (isDarkMode) LightPresetValue.NIGHT else LightPresetValue.DAY
            }
        },
    ) {
        // Display annotations for each station on the map
        stationsUIState.stations.filter { station -> station.isValid() }.map { station ->
            ViewAnnotation(
                options = viewAnnotationOptions {
                    // View annotation is placed at the specific geo coordinate
                    geometry(station.locationPoint())
                    allowOverlap(true)

                }
            ) {
                MapChargingStationAnnotation(station)
            }
        },
    ) {
        // Enable location puck
        MapEffect(Unit) { mapView ->
            mapView.location.updateSettings {
                locationPuck = createDefault2DPuck(withBearing = true)
                enabled = true
                puckBearing = PuckBearing.COURSE
                puckBearingEnabled = false
                pulsingEnabled = true
            }

            // Set the zoom to display a bigger zone around the puck
            mapViewportState.transitionToFollowPuckState(
                followPuckViewportStateOptions = FollowPuckViewportStateOptions.Builder()
                    .zoom(10.0)
                    .build()
            )

            // Only show view annotation after all the runtime layers are added.
            mapView.mapboxMap.mapLoadedEvents.firstOrNull()?.let {
                showDynamicViewAnnotations = true
            }
        }

        // Display annotations for each station on the map
        if(showDynamicViewAnnotations && (mapViewportState.cameraState?.zoom ?: 0.0) > 11) {
            val stationsToRender = remember(filteredStations) { filteredStations }
            stationsToRender.forEach { station ->
                ViewAnnotation(
                    options = viewAnnotationOptions {
                        geometry(station.locationPoint()) // Place at correct coordinates
                        allowOverlap(true)
                    }
                ) {
                    MapChargingStationAnnotation(station)
                }
            }
        }
    }
}