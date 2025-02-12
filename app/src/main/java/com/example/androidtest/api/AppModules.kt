package com.example.androidtest.api

import com.example.androidtest.viewmodel.OpenChargeMapRepository
import com.example.androidtest.viewmodel.OpenChargeMapRepositoryImpl
import com.example.androidtest.viewmodel.OpenChargeMapViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

val appModules = module {
    // API Service dependency
    single <OpenChargeMapAPIService> { OpenChargeMapAPIService() }
    // Coroutine dispatcher dependency
    single { Dispatchers.IO }

    // Repository dependency
    single <OpenChargeMapRepository>{
        OpenChargeMapRepositoryImpl(
            apiService = get(),
            dispatcher = get()
        )
    }
    // Viewmodel dependency
    single { OpenChargeMapViewModel(repository = get()) }
}