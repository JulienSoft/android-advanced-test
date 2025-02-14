package com.example.androidtest

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraState
import com.mapbox.maps.Projection.getMetersPerPixelAtLatitude
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Open setting of the application to configure permissions.
 */
fun Context.startApplicationSettings() {
    val uri = Uri.fromParts("package", packageName, null)
    val intent = Intent()
    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    intent.data = uri
    startActivity(intent)
}

/**
 * Calculate the distance between two points using the Haversine formula.
 */
fun haversineDistance(p1: Point, p2: Point): Double {
    val radius = 6371e3 // Earth radius in meters
    val lat1 = Math.toRadians(p1.latitude())
    val lat2 = Math.toRadians(p2.latitude())
    val dLat = Math.toRadians(p2.latitude() - p1.latitude())
    val dLon = Math.toRadians(p2.longitude() - p1.longitude())

    val a = sin(dLat / 2).pow(2) +
            cos(lat1) * cos(lat2) * sin(dLon / 2).pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return radius * c // Returns distance in meters
}

fun distanceFromCameraStateInMeters(cameraState: CameraState, screenWidth: Int): Double {
    // Calculate the meters per pixel based on the resolution and latitude
    val metersPerPixel = getMetersPerPixelAtLatitude(cameraState.center.latitude(), cameraState.zoom.toDouble())

    // Calculate the screen width in meters
    val screenWidthMeters = metersPerPixel * screenWidth
    return screenWidthMeters
}

/**
 * Convert meters to latitude offset (approx. 111,000 meters per degree).
 */
fun metersToLatitude(meters: Double): Double {
    return meters / 111_000.0
}

/**
 * Convert meters to longitude offset based on latitude (varies with latitude).
 */
fun metersToLongitude(meters: Double, latitude: Double): Double {
    val metersPerDegree = 111_000 * cos(Math.toRadians(latitude))
    return meters / metersPerDegree
}