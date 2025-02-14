package com.example.androidtest.models

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.androidtest.ui.theme.stationOfflineColor
import com.example.androidtest.ui.theme.stationOperationnalColor
import com.example.androidtest.ui.theme.stationUnknownColor
import com.mapbox.geojson.Point
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
    @SerialName("Connections") val connections: List<Connection>?
) {
    fun isValid() = addressInfo?.latitude != null && addressInfo.longitude != null
    fun locationPoint(): Point = Point.fromLngLat(addressInfo?.longitude ?: 0.0, addressInfo?.latitude ?: 0.0)
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
) {
    fun color(): Color {
        return Color(when (isOperational) {
            true -> stationOperationnalColor
            false -> stationOfflineColor
            null -> stationUnknownColor
        })
    }
}

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
    @SerialName("PowerKW") val powerKW: Double?
)

@Serializable
@Entity(tableName = "connectiontype")
data class ConnectionType(
    @PrimaryKey @SerialName("Title") val title: String
)
