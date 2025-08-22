package com.mod.thmanyah_android_challenge

import android.app.Application
import com.mod.thmanyah_android_challenge.di.apiModule
import com.mod.thmanyah_android_challenge.di.networkModule
import com.mod.thmanyah_android_challenge.di.repositoryModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(networkModule, apiModule, repositoryModule)
        }
    }
}