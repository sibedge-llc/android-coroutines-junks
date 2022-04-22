package com.sibedge.corutinesdemo

import android.app.Application
import android.util.Log
import timber.log.Timber

class DemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}