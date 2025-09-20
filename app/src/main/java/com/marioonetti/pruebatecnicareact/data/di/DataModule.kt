package com.marioonetti.pruebatecnicareact.data.di

import com.marioonetti.pruebatecnicareact.data.local.faceidentification.FaceIdentificationSdkManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single(createdAtStart = true) {
        FaceIdentificationSdkManager(androidContext()).apply { init() }
    }
}