package com.example.androidtest.db

import androidx.room.TypeConverter
import com.example.androidtest.models.AddressInfo
import com.example.androidtest.models.Connection
import com.example.androidtest.models.ConnectionType
import com.example.androidtest.models.Country
import com.example.androidtest.models.OperatorInfo
import com.example.androidtest.models.StatusType
import com.example.androidtest.models.UsageType
import com.google.gson.Gson

class RoomTypeConverter {
    // For ChargingStation
    @TypeConverter
    fun usageTypeToString(usageType: UsageType): String {
        return Gson().toJson(usageType)
    }

    @TypeConverter
    fun stringToUsageType(usageType: String): UsageType {
        return Gson().fromJson(usageType, UsageType::class.java)
    }

    @TypeConverter
    fun statusTypeToString(statusType: StatusType): String {
        return Gson().toJson(statusType)
    }

    @TypeConverter
    fun stringToStatusType(statusType: String): StatusType {
        return Gson().fromJson(statusType, StatusType::class.java)
    }

    @TypeConverter
    fun addressInfoToString(addressInfo: AddressInfo): String {
        return Gson().toJson(addressInfo)
    }

    @TypeConverter
    fun stringToAddressInfo(addressInfo: String): AddressInfo {
        return Gson().fromJson(addressInfo, AddressInfo::class.java)
    }

    @TypeConverter
    fun fromListConnectionToString(connections: List<Connection>): String = Gson().toJson(connections)

    @TypeConverter
    fun toListConnectionFromString(stringList: String): List<Connection> {
        return Gson().fromJson(stringList, Array<Connection>::class.java).toList()
    }

    // For Connection
    @TypeConverter
    fun connectionTypeToString(connectionType: ConnectionType): String {
        return Gson().toJson(connectionType)
    }

    @TypeConverter
    fun stringToConnectionType(connectionType: String): ConnectionType {
        return Gson().fromJson(connectionType, ConnectionType::class.java)
    }

    // For AddressInfo
    @TypeConverter
    fun countryToString(country: Country): String {
        return country.title
    }

    @TypeConverter
    fun stringToCountry(title: String): Country {
        return Country(title)
    }

    // For OperatorInfo
    @TypeConverter
    fun operatorInfoToString(operatorInfo: OperatorInfo): String {
        return Gson().toJson(operatorInfo)
    }

    @TypeConverter
    fun stringToOperatorInfo(operatorInfo: String): OperatorInfo {
        return Gson().fromJson(operatorInfo, OperatorInfo::class.java)
    }
}