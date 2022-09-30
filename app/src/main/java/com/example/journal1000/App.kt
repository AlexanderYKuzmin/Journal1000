package com.example.journal1000

import android.app.Application
import android.content.Context

/*class ContextHolder {
    companion object {
        //var appContext: Context? = null
        lateinit var app: Application

        fun initial(application: Application) {
            app = application
        }

        fun getContext(): Context {
            return app!!.applicationContext
        }
    }
}*/

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