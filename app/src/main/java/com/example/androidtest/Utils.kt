package com.example.androidtest

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.mapbox.geojson.Point
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

// Display setting of the application to configure permissions
fun Context.startApplicationSettings() {
    val uri = Uri.fromParts("package", packageName, null)
    val intent = Intent()
    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    intent.data = uri
    startActivity(intent)
}

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

fun interpolateResolution(zoom: Double): Double {
    for (i in 0 until zoomData.size - 1) {
        val (zoom1, res1) = zoomData[i]
        val (zoom2, res2) = zoomData[i + 1]

        if (zoom in zoom1.toDouble()..zoom2.toDouble()) {
            // Linear interpolation formula
            return res1 + (res2 - res1) * ((zoom - zoom1) / (zoom2 - zoom1))
        }
    }
    return -1.0 // Return -1 if out of range
}

// Define zoom levels and their corresponding resolutions (m/px)
val zoomData = listOf(
    0 to 156543.03, 1 to 78271.52, 2 to 39135.76, 3 to 19567.88,
    4 to 9783.94, 5 to 4891.97, 6 to 2445.98, 7 to 1222.99,
    8 to 611.50, 9 to 305.75, 10 to 152.87, 11 to 76.437,
    12 to 38.219, 13 to 19.109, 14 to 9.5546, 15 to 4.7773,
    16 to 2.3887, 17 to 1.1943, 18 to 0.5972
)
