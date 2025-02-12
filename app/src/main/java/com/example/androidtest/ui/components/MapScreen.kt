package com.example.androidtest.ui.components

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
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.extension.compose.style.standard.LightPresetValue
import com.mapbox.maps.extension.compose.style.standard.MapboxStandardStyle
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateOptions
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import org.koin.androidx.compose.koinViewModel


@Composable
fun MapScreen() {
    val isDarkMode = isSystemInDarkTheme()
    val viewModel: OpenChargeMapViewModel = koinViewModel()
    val stationsUIState = viewModel.stationsUIState.collectAsStateWithLifecycle().value

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

    MapboxMap(
        Modifier
            .fillMaxSize(),
        mapViewportState = mapViewportState,
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
        }

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
        }
    }
}