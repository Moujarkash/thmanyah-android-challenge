package com.mod.thmanyah_android_challenge.di

import com.mod.thmanyah_android_challenge.data.remote.api.HomeApiService
import com.mod.thmanyah_android_challenge.data.remote.api.SearchApiService
import org.koin.dsl.module

val apiModule = module {
    single { HomeApiService(get()) }
    single { SearchApiService(get()) }
}