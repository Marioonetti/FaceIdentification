package com.marioonetti.pruebatecnicareact

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FaceApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@FaceApp)
            //modules(appModule)
        }
    }
}