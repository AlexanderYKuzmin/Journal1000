package com.example.journal1000

import android.app.Application
import android.content.Context

class App : Application() {
    companion object {
        private lateinit var sApplication: Application

        fun getApplication(): Application {
            return sApplication
        }

        fun getContext(): Context {
            return getApplication().applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        sApplication = this
    }
}