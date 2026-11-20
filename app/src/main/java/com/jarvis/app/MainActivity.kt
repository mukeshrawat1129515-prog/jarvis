package com.jarvis.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.jarvis.app.ui.theme.JarvisTheme
import com.jarvis.app.ui.screens.ChatScreen
import com.jarvis.app.viewmodels.JarvisViewModel

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: JarvisViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = JarvisContainer.provideViewModel()

        setContent {
            JarvisTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var state by viewModel.state

                    ChatScreen(
                        state = state,
                        onSend = { viewModel.sendMessage(it) },
                        onClear = { viewModel.clearError() }
                    )
                }
            }
        }
    }
}