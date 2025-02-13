package com.example.androidtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.androidtest.ui.components.MapScreen
import com.example.androidtest.ui.components.PermissionsRequest
import com.example.androidtest.ui.theme.AndroidTestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            AndroidTestTheme {
                val snackbarHostState = remember { SnackbarHostState() }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets(0.dp),
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState)
                    }) { contentPadding ->

                    Box(modifier = Modifier.padding(contentPadding)) {
                        PermissionsRequest(snackbarHostState)
                        MapScreen()
                    }
                }
            }
        }
    }
}