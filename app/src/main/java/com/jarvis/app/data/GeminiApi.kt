package com.jarvis.app.data

import com.jarvis.app.BuildConfig
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class GeminiApi(
    private val apiKey: String = BuildConfig.GEMINI_API_KEY,
    private val baseUrl: String = BuildConfig.GEMINI_BASE_URL
) {
    private val gson = Gson()
    private val json: MediaType = "application/json; charset=utf-8".toMediaType()!!
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            chain.request().newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("x-goog-api-key", apiKey)
                .build()
                .let { chain.proceed(it) }
        }
        .build()

    fun generateContent(prompt: String, systemInstruction: String? = null): String {
        val contents = listOf(
            mapOf("role" to "user", "parts" to listOf(mapOf("text" to prompt)))
        )
        val body = mapOf(
            "systemInstruction" to mapOf("parts" to listOf(mapOf("text" to systemInstruction))),
            "contents" to contents,
            "generationConfig" to mapOf("temperature" to 0.7, "maxOutputTokens" to 2048)
        )
        val bodyJson = gson.toJson(body)
        val reqBody = bodyJson.toRequestBody(this.json)
        val request = Request.Builder().url(baseUrl).post(reqBody).build()
        client.newCall(request).execute().use { resp ->
            val text = resp.body?.string().orEmpty()
            if (!resp.isSuccessful) throw RuntimeException("HTTP ${resp.code}: ${text.take(300)}")
            val root = JsonParser.parseString(text).asJsonObject
            val cands = root.getAsJsonArray("candidates")
                ?: throw RuntimeException("No candidates in response")
            val parts = cands[0].asJsonObject.getAsJsonObject("content").getAsJsonArray("parts")
            return parts.joinToString("\n") { it.asJsonObject.get("text").asString }
        }
    }
}