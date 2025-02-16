package com.example.androidtest

import com.mapbox.maps.Projection

// I use a provider to test Utils without mocking static functions
interface MapProjectionProvider {
    fun getMetersPerPixelAtLatitude(latitude: Double, zoom: Double): Double
}

class MapProjectionProviderImpl : MapProjectionProvider {
    override fun getMetersPerPixelAtLatitude(latitude: Double, zoom: Double): Double {
        return Projection.getMetersPerPixelAtLatitude(latitude, zoom)
    }
}