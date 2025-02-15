package com.example.androidtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.androidtest.ui.components.BottomSheetComponent
import com.example.androidtest.ui.components.LoadingErrorComponent
import com.example.androidtest.ui.components.MapScreen
import com.example.androidtest.ui.components.PermissionsRequest
import com.example.androidtest.ui.theme.AndroidTestTheme
import com.example.androidtest.viewmodel.OpenChargeMapViewModel
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            AndroidTestTheme {
                val viewModel: OpenChargeMapViewModel = koinViewModel()
                val snackbarHostState = remember { SnackbarHostState() }

                Scaffold(
                    modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.navigationBars),
                    contentWindowInsets = WindowInsets(0.dp),
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState)
                    }) { contentPadding ->

                    Box(modifier = Modifier.padding(contentPadding)) {
                        BottomSheetComponent(viewModel)
                        PermissionsRequest(snackbarHostState)
                        MapScreen(viewModel)
                        LoadingErrorComponent(viewModel)
                    }
                }
            }
        }
    }
}