package com.mmk.kmpauth.sample

import android.app.Application
import com.facebook.FacebookSdk

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppInitializer.onApplicationStart()
        FacebookSdk.fullyInitialize()
    }

}