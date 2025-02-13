package com.example.androidtest.api

import androidx.room.Room
import com.example.androidtest.db.ChargingDao
import com.example.androidtest.db.ChargingDatabase
import com.example.androidtest.viewmodel.OpenChargeMapRepository
import com.example.androidtest.viewmodel.OpenChargeMapRepositoryImpl
import com.example.androidtest.viewmodel.OpenChargeMapViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModules = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            ChargingDatabase::class.java, "my_database"
        ).fallbackToDestructiveMigration().build()
    }

    single { get<ChargingDatabase>().getChargingDao() }

    single { provideChargingDao(get()) }

    // API Service dependency
    single <OpenChargeMapAPIService> { OpenChargeMapAPIService() }
    // Coroutine dispatcher dependency
    single { Dispatchers.IO }

    // Repository dependency
    single <OpenChargeMapRepository>{
        OpenChargeMapRepositoryImpl(
            apiService = get(),
            dispatcher = get(),
            chargingDao = get()
        )
    }
    // Viewmodel dependency
    single { OpenChargeMapViewModel(repository = get()) }
}

// Provide DAO
fun provideChargingDao(database: ChargingDatabase): ChargingDao {
    return database.getChargingDao()
}