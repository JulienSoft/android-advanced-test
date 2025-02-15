package com.example.androidtest.models

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.androidtest.distanceFromCameraStateInMeters
import com.example.androidtest.metersToLatitude
import com.example.androidtest.metersToLongitude
import com.example.androidtest.ui.theme.stationOfflineColor
import com.example.androidtest.ui.theme.stationPowerHigh
import com.example.androidtest.ui.theme.stationPowerLow
import com.example.androidtest.ui.theme.stationPowerUnknown
import com.example.androidtest.ui.theme.stationUnknownColor
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraState
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
@Entity(tableName = "chargingStation")
data class ChargingStation(
    @PrimaryKey @SerialName("ID") val id: Int,
    @SerialName("UUID") val uuid: String,
    @SerialName("UsageType") val usageType: UsageType?,
    @SerialName("StatusType") val statusType: StatusType?,
    @SerialName("AddressInfo") val addressInfo: AddressInfo?,
    @SerialName("Connections") val connections: List<Connection>?,
    @SerialName("OperatorInfo") val operatorInfo: OperatorInfo?,
) {
    fun locationPoint(): Point = Point.fromLngLat(addressInfo?.longitude ?: 0.0, addressInfo?.latitude ?: 0.0)
    fun inViewPort(cameraState: CameraState?, screenSize: Size): Boolean {
        if (cameraState == null) return false

        val centerLat = cameraState.center.latitude()
        val centerLon = cameraState.center.longitude()

        val distance = distanceFromCameraStateInMeters(cameraState, screenSize)

        // Convert meters to latitude/longitude offsets
        val latOffset = metersToLatitude(distance.width.toDouble() / 2)  // Divide by 2 for half viewport
        val lonOffset = metersToLongitude(distance.height.toDouble() / 2, centerLat)

        // Get charging station location
        val stationLat = locationPoint().latitude()
        val stationLon = locationPoint().longitude()

        // Check if inside bounding box
        return stationLat in (centerLat - latOffset)..(centerLat + latOffset) &&
                stationLon in (centerLon - lonOffset)..(centerLon + lonOffset)
    }

    fun backgroundColor(): Color {
        if(statusType?.isOperational == null) return Color(stationUnknownColor)
        else if(statusType.isOperational == false) return Color(stationOfflineColor)

        val connection = connections?.maxBy({ it.levelID ?: 0 })
        return connection?.color() ?: Color(stationUnknownColor)
    }
}

@Serializable
@Entity(tableName = "usageType")
data class UsageType(
    @PrimaryKey @SerialName("ID") val id: Int,
    @SerialName("Title") val title: String
)

@Serializable
@Entity(tableName = "statusType")
data class StatusType(
    @SerialName("IsOperational") val isOperational: Boolean?,
    @PrimaryKey @SerialName("Title") val title: String
)

@Serializable
@Entity(tableName = "addressinfo")
data class AddressInfo(
    @PrimaryKey @SerialName("Title") val title: String,
    @SerialName("AddressLine1") val addressLine1: String?,
    @SerialName("Latitude") val latitude: Double?,
    @SerialName("Longitude") val longitude: Double?,
    @SerialName("Country") val country: Country?
)

@Serializable
@Entity(tableName = "country")
data class Country(
    @PrimaryKey @SerialName("Title") val title: String
)

@Serializable
@Entity(tableName = "connection")
data class Connection(
    @PrimaryKey @SerialName("ID") val id: Int,
    @SerialName("ConnectionType") val connectionType: ConnectionType?,
    @SerialName("PowerKW") val powerKW: Double?,
    @SerialName("Quantity") val quantity: Int?,
    @SerialName("LevelID") val levelID: Int?,
) {
    fun color(): Color {
        return Color(when(levelID) {
            3 -> stationPowerHigh
            2 -> stationPowerLow
            else -> stationPowerUnknown
        })
    }
}

@Serializable
@Entity(tableName = "connectiontype")
data class ConnectionType(
    @PrimaryKey @SerialName("ID") val id: Int,
    @SerialName("Title") val title: String
)

@Serializable
@Entity(tableName = "operatorinfo")
data class OperatorInfo(
    @PrimaryKey @SerialName("ID") val id: Int,
    @SerialName("WebsiteURL") val website: String?
)
