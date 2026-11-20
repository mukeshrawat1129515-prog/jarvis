package com.jarvis.app

import android.app.Application
import com.jarvis.app.ui.JarvisContainer

class JarvisApp : Application() {
    override fun onCreate() {
        super.onCreate()
        JarvisContainer.init(this)
    }
}