package com.example.androidtest

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.androidtest.ui.theme.AndroidTestTheme
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.style.standard.LightPresetValue
import com.mapbox.maps.extension.compose.style.standard.MapboxStandardStyle
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location

class MainActivity : ComponentActivity() {
    lateinit var permissionsManager: PermissionsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            Toast.makeText(this, "Location permissions granted", Toast.LENGTH_LONG).show()

        } else {
            var permissionsListener: PermissionsListener = object : PermissionsListener {
                override fun onExplanationNeeded(permissionsToExplain: List<String>) {
                }

                override fun onPermissionResult(granted: Boolean) {
                    if (granted) {

                    } else {

                    }
                }
            }

            permissionsManager = PermissionsManager(permissionsListener)
            permissionsManager.requestLocationPermissions(this)
        }

        setContent {
            val isDarkMode = isSystemInDarkTheme()

            AndroidTestTheme {
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
        }
    }
}