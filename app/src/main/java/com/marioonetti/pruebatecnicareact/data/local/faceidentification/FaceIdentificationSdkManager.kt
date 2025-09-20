package com.marioonetti.pruebatecnicareact.data.local.faceidentification

import android.content.Context
import com.marioonetti.pruebatecnicareact.MainActivity
import com.regula.facesdk.FaceSDK
import com.regula.facesdk.callback.FaceInitializationCompletion
import com.regula.facesdk.configuration.FaceCaptureConfiguration
import com.regula.facesdk.configuration.InitializationConfiguration

class FaceIdentificationSdkManager(private val context: Context) {

    fun init() {
        val license = context.assets.open("regula.license").use { it.readBytes() }
        val configuration = InitializationConfiguration.Builder(license).build()
        FaceSDK.Instance().initialize(
            context, configuration,
            FaceInitializationCompletion { status, exception -> })
    }
}