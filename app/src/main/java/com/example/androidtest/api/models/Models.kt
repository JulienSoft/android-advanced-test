package com.example.androidtest.api.models

import androidx.compose.ui.graphics.Color
import com.example.androidtest.ui.theme.stationOfflineColor
import com.example.androidtest.ui.theme.stationOperationnalColor
import com.mapbox.geojson.Point
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChargingStation(
    @SerialName("ID") val id: Int,
    @SerialName("UUID") val uuid: String,
    @SerialName("UsageType") val usageType: UsageType?,
    @SerialName("StatusType") val statusType: StatusType?,
    @SerialName("AddressInfo") val addressInfo: AddressInfo?,
    @SerialName("Connections") val connections: List<Connection>?
) {
    fun isValid() = addressInfo?.latitude != null && addressInfo.longitude != null
    fun locationPoint(): Point = Point.fromLngLat(addressInfo?.longitude ?: 0.0, addressInfo?.latitude ?: 0.0)
}

@Serializable
data class UsageType(
    @SerialName("ID") val id: Int,
    @SerialName("Title") val title: String
)

@Serializable
data class StatusType(
    @SerialName("IsOperational") val isOperational: Boolean,
    @SerialName("Title") val title: String
) {
    fun color(): Color = Color(if (isOperational) stationOperationnalColor else stationOfflineColor)
}

@Serializable
data class AddressInfo(
    @SerialName("Title") val title: String,
    @SerialName("AddressLine1") val addressLine1: String?,
    @SerialName("Latitude") val latitude: Double?,
    @SerialName("Longitude") val longitude: Double?,
    @SerialName("Country") val country: Country?
)

@Serializable
data class Country(
    @SerialName("Title") val title: String
)

@Serializable
data class Connection(
    @SerialName("ID") val id: Int,
    @SerialName("ConnectionType") val connectionType: ConnectionType?,
    @SerialName("PowerKW") val powerKW: Double?
)

@Serializable
data class ConnectionType(
    @SerialName("Title") val title: String
)
