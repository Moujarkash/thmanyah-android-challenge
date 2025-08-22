package com.mod.thmanyah_android_challenge.di

import com.mod.thmanyah_android_challenge.data.remote.api.HomeApiService
import org.koin.dsl.module

val apiModule = module {
    single { HomeApiService(get()) }
}