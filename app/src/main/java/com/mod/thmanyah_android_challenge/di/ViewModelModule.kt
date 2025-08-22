package com.mod.thmanyah_android_challenge.di

import com.mod.thmanyah_android_challenge.ui.home.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { HomeViewModel(get()) }
}