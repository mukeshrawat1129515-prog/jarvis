package com.jarvis.app.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.jarvis.app.data.GeminiApi
import com.jarvis.app.data.PhoneAccess
import com.jarvis.app.data.PhoneState
import kotlinx.coroutines.*

private const val TAG = "JarvisViewModel"

data class JarvisState(
    val messages: List<ChatMessage> = emptyList(),
    val isThinking: Boolean = false,
    val phoneContext: String = "",
    val error: String? = null
)

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class JarvisViewModel(
    private val geminiApi: GeminiApi,
    private val phoneAccess: PhoneAccess
) : ViewModel() {

    var state by mutableStateOf(JarvisState())
        private set

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    init {
        loadPhoneContext()
    }

    private fun loadPhoneContext() {
        state = state.copy(phoneContext = phoneAccess.getPhoneContext())
    }

    fun sendMessage(userMessage: String) {
        if (userMessage.isBlank()) return

        val userMsg = ChatMessage(text = userMessage, isUser = true)
        state = state.copy(
            messages = state.messages + userMsg,
            isThinking = true,
            error = null
        )

        scope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val prompt = buildPrompt(userMessage)
                    val result = CompletableDeferred<String>()

                    geminiApi.generateContent(
                        prompt = prompt,
                        systemInstruction = SYSTEM_INSTRUCTION,
                        jsonMode = false,
                        onResult = { text ->
                            result.complete(text)
                        },
                        onError = { err ->
                            result.completeExceptionally(Exception(err))
                        }
                    )

                    val response = result.await()
                    val assistantMsg = ChatMessage(text = response, isUser = false)

                    withContext(Dispatchers.Main) {
                        state = state.copy(
                            messages = state.messages + assistantMsg,
                            isThinking = false
                        )
                    }
                }
            } catch (e: Exception) {
                state = state.copy(
                    isThinking = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }

    private fun buildPrompt(userMessage: String): String {
        return buildString {
            appendLine("Phone context:")
            appendLine(state.phoneContext)
            appendLine()
            appendLine("User message:")
            appendLine(userMessage)
        }
    }

    fun clearError() {
        state = state.copy(error = null)
    }

    fun clearChat() {
        state = state.copy(messages = emptyList())
    }

    companion object {
        const val SYSTEM_INSTRUCTION = """You are Jarvis, a helpful AI assistant on an Android phone.
You have access to the phone's hardware and context.
Be concise, helpful, and natural. Reply in the user's language.
When the user asks you to do something with the phone, explain what you can do and what you need.
Never perform actions without explicit user permission."""
    }
}