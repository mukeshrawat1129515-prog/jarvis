package com.jarvis.app

import android.app.Application
import android.util.Log
import com.jarvis.app.ui.JarvisContainer

class JarvisApp : Application() {
    companion object {
        const val TAG = "Jarvis"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Jarvis app started")
        JarvisContainer.init(this)
    }
}