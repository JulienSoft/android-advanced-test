package com.example.androidtest.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.androidtest.viewmodel.OpenChargeMapViewModel
import com.mapbox.geojson.Point
import com.mapbox.maps.coroutine.mapLoadedEvents
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotationGroup
import com.mapbox.maps.extension.compose.rememberMapState
import com.mapbox.maps.extension.compose.style.standard.LightPresetValue
import com.mapbox.maps.extension.compose.style.standard.MapboxStandardSatelliteStyle
import com.mapbox.maps.extension.compose.style.standard.MapboxStandardStyle
import com.mapbox.maps.extension.style.expressions.generated.Expression
import com.mapbox.maps.extension.style.expressions.generated.Expression.Companion.literal
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.animation.MapAnimationOptions.Companion.mapAnimationOptions
import com.mapbox.maps.plugin.annotation.AnnotationConfig
import com.mapbox.maps.plugin.annotation.AnnotationSourceOptions
import com.mapbox.maps.plugin.annotation.ClusterOptions
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateOptions
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch


@OptIn(FlowPreview::class)
@Composable
fun MapScreen(viewModel: OpenChargeMapViewModel) {
    val isDarkMode = isSystemInDarkTheme()
    val coroutineScope = rememberCoroutineScope()
    val mapState = rememberMapState()       // Contains the state of the map (current viewport)

    val centerMapOnUser by viewModel.centerMapOnUser.collectAsStateWithLifecycle()
    val mapLayers by viewModel.mapLayers.collectAsStateWithLifecycle()
    val chargingStations by viewModel.chargingStations.collectAsStateWithLifecycle()
    val filteredStations by viewModel.filteredStations.collectAsStateWithLifecycle()
    val configuration = LocalConfiguration.current

    var screenWidth = 0f
    with(LocalDensity.current) {
        screenWidth = configuration.screenWidthDp.dp.toPx()
        val screenHeight = configuration.screenHeightDp.dp.toPx()
        viewModel.updateScreenSize(Size(screenWidth, screenHeight))
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

    LaunchedEffect(centerMapOnUser) {
        if(centerMapOnUser) {
            viewModel.centerMapOnUser(false)
            transitionToFollowPuckState(mapViewportState)
        }
    }

    // Add map
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
            if(mapLayers == 0) MapboxStandardStyle {
                lightPreset = if (isDarkMode) LightPresetValue.NIGHT else LightPresetValue.DAY
            } else MapboxStandardSatelliteStyle {
                lightPreset = if (isDarkMode) LightPresetValue.NIGHT else LightPresetValue.DAY
            }

            PointAnnotationGroup(
                annotations = chargingStations.map {
                    PointAnnotationOptions()
                        .withPoint(it.locationPoint())
                },
                annotationConfig = AnnotationConfig(
                    annotationSourceOptions = AnnotationSourceOptions(
                        clusterOptions = ClusterOptions(
                            textColorExpression = Expression.color(Color.Cyan.toArgb()),
                            textSize = 20.0,
                            clusterMaxZoom = 10,
                            circleRadiusExpression = literal(20.0),
                            colorLevels = listOf(
                                Pair(100, Color.Red.toArgb()),
                                Pair(50, Color.Blue.toArgb()),
                                Pair(0, Color.Green.toArgb())
                            )
                        )
                    )
                ),
            ) {
                interactionsState.isDraggable = true
                interactionsState
                    .onClusterClicked {
                        // Fly in the cluster
                        it.originalFeature.geometry()?.let { geometry ->
                            mapViewportState.flyTo(
                                cameraOptions {
                                    center(geometry as Point)
                                    zoom(12.0)
                                },
                                mapAnimationOptions {
                                    duration(3_000)
                                }
                            )
                        }
                        true
                    }
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

            transitionToFollowPuckState(mapViewportState)

            // Only show view annotation after all the runtime layers are added.
            mapView.mapboxMap.mapLoadedEvents.firstOrNull()?.let {
                showDynamicViewAnnotations = true
            }
        }

        // Display annotations for each station on the map
        if(showDynamicViewAnnotations && (mapViewportState.cameraState?.zoom ?: 0.0) > 11) {
            filteredStations.forEach { station ->
                ViewAnnotation(
                    options = viewAnnotationOptions {
                        geometry(station.locationPoint()) // Place at correct coordinates
                        allowOverlap(true)
                    }
                ) {
                    MapChargingStationAnnotation(viewModel, station)
                }
            }
        }
    }
}

fun transitionToFollowPuckState(mapViewportState: MapViewportState) {
    // Set the zoom to display a bigger zone around the puck
    mapViewportState.transitionToFollowPuckState(
        followPuckViewportStateOptions = FollowPuckViewportStateOptions.Builder()
            .zoom(10.0)
            .build()
    )
}