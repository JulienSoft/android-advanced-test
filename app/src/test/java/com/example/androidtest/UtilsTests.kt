package com.example.androidtest

import androidx.compose.ui.geometry.Size
import com.example.androidtest.models.AddressInfo
import com.example.androidtest.models.ChargingStation
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraState
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.Projection
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkStatic
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.math.cos

class UtilsTests {
    @Test
    fun haversineDistance() {
        val p1 = Point.fromLngLat(0.0, 0.0)
        val p2 = Point.fromLngLat(0.0, 1.0)
        assertEquals(111194.9, haversineDistance(p1, p2), 0.1)
    }

    @Test
    fun haversineDistance2() {
        val p1 = Point.fromLngLat(50.0359, 5.4253)
        val p2 = Point.fromLngLat(58.3838, 3.0412)
        assertEquals(962853.0, haversineDistance(p1, p2), 0.1)
    }

    @Test
    fun metersToLatitude() {
        assertEquals(1.0, metersToLatitude(111000.0), 0.001)
    }

    @Test
    fun metersToLongitude() {
        assertEquals(1.0, metersToLongitude(111000.0, 0.0), 0.001)
    }

    @Test
    fun metersToLongitude2() {
        assertEquals(0.454, metersToLongitude(50000.0, 7.7115347), 0.001)
    }

    // Mock the provider instead of the static function
    val mockProjectionProvider = mockk<MapProjectionProvider>()

    @Before
    fun setup() {
        // Define implementation of getMetersPerPixelAtLatitude because it's a static function in MapBox
        every { mockProjectionProvider.getMetersPerPixelAtLatitude(any(), any()) } answers {
            val latitude = firstArg<Double>()  // Get first argument safely
            val zoom = secondArg<Double>()     // Get second argument safely

            val earthCircumference = 40075016.686 // meters
            val metersPerPixelAtEquator = earthCircumference / 256.0
            metersPerPixelAtEquator * cos(latitude * (Math.PI / 180)) / (1 shl zoom.toInt())
        }
    }

    // Check if mocked getMetersPerPixelAtLatitude works well
    @Test
    fun getMetersPerPixelAtLatitudeMockedVersion() {
        val meters = mockProjectionProvider.getMetersPerPixelAtLatitude(0.0, 0.0)
        assertEquals(156543.03392804097, meters, 0.1)

        val metersZoom = mockProjectionProvider.getMetersPerPixelAtLatitude(0.0, 10.0)
        assertEquals(152.87405657196044, metersZoom, 0.1)

        val metersParis = mockProjectionProvider.getMetersPerPixelAtLatitude(48.8566, 0.0)
        assertEquals(102996.84124157715, metersParis, 0.1)

        val metersParisZoom = mockProjectionProvider.getMetersPerPixelAtLatitude(48.8566, 10.0)
        assertEquals(100.58285277497768, metersParisZoom, 0.1)

        // Cleanup after test
        unmockkStatic(Projection::class)
    }

    @Test
    fun distanceFromCameraStateInMetersFromParis() {
        val cameraState = CameraState(
            Point.fromLngLat(2.333333, 48.866667),
            EdgeInsets(0.0, 0.0, 0.0, 0.0),
            10.0,
            0.0,
            0.0
        )

        val screenSize = Size(1440f, 3040f)

        val distance = distanceFromCameraStateInMeters(cameraState, screenSize, mockProjectionProvider)

        // Check size of viewport
        assertEquals(144810.17f, distance.width, 0.1f)          // 144Km
        assertEquals(305710.38f, distance.height, 0.1f)         // 305Km

        // Cleanup after test
        unmockkStatic(Projection::class)
    }

    @Test
    fun distanceFromCameraStateInMetersFromHeadOffice() {
        val cameraState = CameraState(
            Point.fromLngLat(7.7115347, 48.5940562),
            EdgeInsets(0.0, 0.0, 0.0, 0.0),
            10.0,
            0.0,
            0.0
        )

        val screenSize = Size(1440f, 3040f)

        val distance = distanceFromCameraStateInMeters(cameraState, screenSize, mockProjectionProvider)

        // Check size of viewport
        assertEquals(145597.42f, distance.width, 0.1f)          // 144Km
        assertEquals(307372.34f, distance.height, 0.1f)         // 305Km

        // Cleanup after test
        unmockkStatic(Projection::class)
    }

    @Test
    fun chargingStationInViewPort() {
        val cameraState = CameraState(
            Point.fromLngLat(7.7115347, 48.5940562),
            EdgeInsets(0.0, 0.0, 0.0, 0.0),
            10.0,
            0.0,
            0.0
        )

        val screenSize = Size(1440f, 3040f)

        val chargingStation = ChargingStation(
            0,
            "uuid",
            null,
            null,
            AddressInfo(
                "title",
                null,
                48.5940562,
                7.7115347,
            )
        )

        assertEquals(true, chargingStation.inViewPort(cameraState, screenSize, mockProjectionProvider))
    }
}