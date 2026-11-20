package com.jarvis.app.ui

import android.content.Context
import com.jarvis.app.BuildConfig
import com.jarvis.app.JarvisApp
import com.jarvis.app.data.GeminiApi
import com.jarvis.app.data.PhoneAccess
import com.jarvis.app.viewmodel.JarvisViewModel

object JarvisContainer {
    private var geminiApi: GeminiApi? = null
    private var phoneAccess: PhoneAccess? = null

    fun init(context: Context) {
        if (geminiApi == null) {
            geminiApi = GeminiApi(
                apiKey = BuildConfig.GEMINI_API_KEY,
                baseUrl = BuildConfig.GEMINI_BASE_URL
            )
        }
        if (phoneAccess == null) {
            phoneAccess = PhoneAccess(context.applicationContext)
        }
    }

    fun provideViewModel(app: JarvisApp): JarvisViewModel {
        return JarvisViewModel(
            geminiApi ?: error("JarvisContainer not initialized"),
            phoneAccess ?: error("JarvisContainer not initialized")
        )
    }
}