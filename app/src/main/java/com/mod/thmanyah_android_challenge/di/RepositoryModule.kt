package com.mod.thmanyah_android_challenge.di

import com.mod.thmanyah_android_challenge.data.repository.HomeRepositoryImpl
import com.mod.thmanyah_android_challenge.data.repository.SearchRepositoryImpl
import com.mod.thmanyah_android_challenge.domain.repository.HomeRepository
import com.mod.thmanyah_android_challenge.domain.repository.SearchRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<HomeRepository> { HomeRepositoryImpl(get()) }
    single<SearchRepository> { SearchRepositoryImpl(get()) }
}