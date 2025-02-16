package com.example.androidtest

import androidx.compose.ui.geometry.Size
import com.example.androidtest.api.APIResult
import com.example.androidtest.models.ChargingStation
import com.example.androidtest.viewmodel.OpenChargeMapRepository
import com.example.androidtest.viewmodel.OpenChargeMapViewModel
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraState
import com.mapbox.maps.EdgeInsets
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.math.cos

@ExperimentalCoroutinesApi
class ViewModelTests {

    private lateinit var viewModel: OpenChargeMapViewModel

    // Mock repository
    private val repository: OpenChargeMapRepository = mockk()

    // Test dispatcher for coroutines
    private val testDispatcher = StandardTestDispatcher()

    // Mock the provider instead of the static function
    val mockProjectionProvider = mockk<MapProjectionProvider>()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher) // Set test dispatcher

        val list = listOf(
            ChargingStation(1, "UUID1", null, null, null, emptyList(), null),
            ChargingStation(2, "UUID2", null, null, null, emptyList(), null)
        )

        coEvery { repository.fetchCharginStations(any(), any(), any(), any()) } returns APIResult.Success<List<ChargingStation>>(
            list)

        coEvery {
            repository.getAllChargingStations()
        } returns flowOf(list)

        viewModel = OpenChargeMapViewModel(repository)
        // Update screen Size
        viewModel.updateScreenSize(Size(1440f, 3040f))

        // Define implementation of getMetersPerPixelAtLatitude because it's a static function in MapBox
        every { mockProjectionProvider.getMetersPerPixelAtLatitude(any(), any()) } answers {
            val latitude = firstArg<Double>()  // Get first argument safely
            val zoom = secondArg<Double>()     // Get second argument safely

            val earthCircumference = 40075016.686 // meters
            val metersPerPixelAtEquator = earthCircumference / 256.0
            metersPerPixelAtEquator * cos(latitude * (Math.PI / 180)) / (1 shl zoom.toInt())
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset the dispatcher after tests
    }

    @Test
    fun checkScreenSize() = runTest {
        assertEquals(1440f, viewModel.screenSize.value.width)
        assertEquals(3040f, viewModel.screenSize.value.height)
    }

    @Test
    fun fetchCharginStationsRepositoryTest() = runTest {
        val result = repository.fetchCharginStations(
            longitude = 7.7115347,
            latitude = 48.5940562,
            maxResults = 2,
            distance = 10)

        assertEquals(APIResult.Success::class, result::class)

        val data = (result as APIResult.Success).data
        assertEquals(2, data.size)
    }

    @Test
    fun getAllStationsTest() = runTest {
        val stations = repository.getAllChargingStations()
        assertEquals(2, stations.first().size)
        assertEquals(2, viewModel.chargingStations.first().size)
    }

    @Test
    fun fetchChargingStationsTest() = runTest {
        // Call the function
        viewModel.fetchStations(
            CameraState(
                Point.fromLngLat(7.7115347, 48.5940562),
                EdgeInsets(0.0, 0.0, 0.0, 0.0),
                0.0,
                0.0,
                0.0),
            mapProjectionProvider = mockProjectionProvider)


        // Assert expected state
        assertEquals(2, viewModel.chargingStations.first().size)
    }
}