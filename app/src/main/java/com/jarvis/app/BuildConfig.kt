package com.jarvis.app

object BuildConfig {
    const val GEMINI_API_KEY: String = BuildConfigFields.GEMINI_API_KEY
    const val GEMINI_BASE_URL: String = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"
}

object BuildConfigFields {
    val GEMINI_API_KEY: String
        get() = System.getenv("GEMINI_API_KEY") ?: ""
}