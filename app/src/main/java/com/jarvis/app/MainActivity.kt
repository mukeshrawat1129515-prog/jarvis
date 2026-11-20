package com.jarvis.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.jarvis.app.ui.JarvisContainer
import com.jarvis.app.ui.screens.ChatScreen
import com.jarvis.app.ui.theme.JarvisTheme
import com.jarvis.app.viewmodel.JarvisViewModel

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: JarvisViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = JarvisContainer.provideViewModel(application as JarvisApp)

        setContent {
            JarvisTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val state = viewModel.state

                    ChatScreen(
                        state = state,
                        onSend = { viewModel.sendMessage(it) },
                        onErrorShown = { viewModel.clearError() }
                    )
                }
            }
        }
    }
}