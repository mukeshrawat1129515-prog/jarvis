package com.jarvis.app.data

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit

data class GeminiRequest(
    val contents: List<GeminiContent>,
    val systemInstruction: GeminiContent? = null,
    val generationConfig: GeminiConfig? = null
)

data class GeminiContent(
    val role: String = "user",
    val parts: List<GeminiPart>
)

data class GeminiPart(val text: String)

data class GeminiConfig(
    val temperature: Double = 0.2,
    val responseMimeType: String? = null
)

data class GeminiResponse(
    val candidates: List<GeminiCandidate>? = null,
    val error: GeminiError? = null
)

data class GeminiCandidate(
    val content: GeminiContent? = null
)

data class GeminiError(
    val message: String? = null,
    val code: Int? = null
)

class GeminiApi(
    private val apiKey: String,
    private val baseUrl: String = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"
) {
    private val gson = Gson()
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("x-goog-api-key", apiKey)
                .build()
            chain.proceed(request)
        }
        .build()

    fun generateContent(
        prompt: String,
        systemInstruction: String? = null,
        jsonMode: Boolean = true,
        onResult: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val parts = listOf(GeminiPart(prompt))
        val content = GeminiContent(role = "user", parts = parts)

        val config = GeminiConfig(
            temperature = 0.2,
            responseMimeType = if (jsonMode) "application/json" else null
        )

        val sysContent = systemInstruction?.let {
            GeminiContent(role = "user", parts = listOf(GeminiPart(it)))
        }

        val request = GeminiRequest(
            contents = listOf(content),
            systemInstruction = sysContent,
            generationConfig = config
        )

        val json = gson.toJson(request)
        val body = RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            json
        )

        val req = Request.Builder()
            .url(baseUrl)
            .post(body)
            .build()

        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onError("Network error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.body()?.let { rb ->
                    val text = rb.string()
                    try {
                        val resp = gson.fromJson(text, GeminiResponse::class.java)
                        resp?.error?.let { err ->
                            onError("Gemini error: ${err.message}")
                            return
                        }
                        val result = resp?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                        if (result != null) {
                            onResult(result)
                        } else {
                            onError("Empty response from Gemini")
                        }
                    } catch (e: Exception) {
                        onError("Parse error: ${e.message}")
                    }
                } ?: onError("Empty response body")
            }
        })
    }
}