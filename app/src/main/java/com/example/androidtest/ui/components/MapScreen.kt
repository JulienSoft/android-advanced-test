package com.example.androidtest.ui.components

import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.androidtest.viewmodel.OpenChargeMapViewModel
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.style.standard.LightPresetValue
import com.mapbox.maps.extension.compose.style.standard.MapboxStandardStyle
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import org.koin.androidx.compose.koinViewModel

@Composable
fun MapScreen() {
    val isDarkMode = isSystemInDarkTheme()
    val viewModel: OpenChargeMapViewModel = koinViewModel()
    val stationsUIState = viewModel.stationsUIState.collectAsStateWithLifecycle().value

    Log.e("MapScreen", "Stations: ${stationsUIState.stations} Error: ${stationsUIState.error} IsLoading: ${stationsUIState.isLoading}")

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(2.0)
            center(Point.fromLngLat(-98.0, 39.5))
            pitch(0.0)
            bearing(0.0)
        }
    }

    MapboxMap(
        Modifier.fillMaxSize(),
        mapViewportState = mapViewportState,
        scaleBar = {  },        // Disable scaleBar
        logo = {  },            // Disable logo
        attribution = {  },     // Disable attribution
        style = {
            MapboxStandardStyle { // Another option MapboxStandardSatelliteStyle()
                // Change light mode with system
                lightPreset = if(isDarkMode) LightPresetValue.NIGHT else LightPresetValue.DAY
            }
        },
    ) {
        MapEffect(Unit) { mapView ->
            mapView.location.updateSettings {
                locationPuck = createDefault2DPuck(withBearing = true)
                enabled = true
                puckBearing = PuckBearing.COURSE
                puckBearingEnabled = false
            }
            mapViewportState.transitionToFollowPuckState()
        }
    }
}