package com.jarvis.app.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jarvis.app.JarvisApp
import com.jarvis.app.data.GeminiApi
import com.jarvis.app.data.PhoneAccess
import com.jarvis.app.viewmodels.JarvisViewModel

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

    fun provideGeminiApi(): GeminiApi = geminiApi ?: error("JarvisContainer not initialized")
    fun providePhoneAccess(): PhoneAccess = phoneAccess ?: error("JarvisContainer not initialized")

    fun provideViewModel(): JarvisViewModel {
        return ViewModelProvider(
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return JarvisViewModel(provideGeminiApi(), providePhoneAccess()) as T
                }
            }
        )[JarvisViewModel::class.java]
    }
}