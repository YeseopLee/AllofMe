package com.example.allofme

import android.app.Application
import android.content.Context
import com.example.allofme.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApp: Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = this

        startKoin {
            androidContext(this@MyApp)
            modules(appModule)
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        appContext = null
    }


    companion object {
        var appContext: Context? = null
        private set
    }
}