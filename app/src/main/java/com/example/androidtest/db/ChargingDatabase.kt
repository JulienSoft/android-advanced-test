package com.example.androidtest.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.androidtest.models.AddressInfo
import com.example.androidtest.models.ChargingStation
import com.example.androidtest.models.Connection
import com.example.androidtest.models.ConnectionType
import com.example.androidtest.models.Country
import com.example.androidtest.models.StatusType
import com.example.androidtest.models.UsageType

@Database(entities = [
    ChargingStation::class,
    ConnectionType::class,
    Connection::class,
    Country::class,
    AddressInfo::class,
    StatusType::class,
    UsageType::class], version = 1)
@TypeConverters(RoomTypeConverter::class)
abstract class ChargingDatabase :RoomDatabase() {
    abstract fun getChargingDao() : ChargingDao
}