package com.example.androidtest.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.androidtest.R
import com.example.androidtest.viewmodel.OpenChargeMapViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen() {
    val viewModel: OpenChargeMapViewModel = koinViewModel()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.navigationBars),
        contentWindowInsets = WindowInsets(0.dp),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            Column {
                FloatingActionButton(
                    modifier = Modifier.padding(bottom = 10.dp).testTag("ButtonLayer"),
                    onClick = {
                        viewModel.changeMapLayers()
                    },
                    shape = RoundedCornerShape(16.dp),
                    contentColor = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.secondary,
                ) {
                    Icon(
                        painterResource(id = R.drawable.floating_layer),
                        contentDescription = "Map layers")
                }
                FloatingActionButton(
                    modifier = Modifier.padding(bottom = 10.dp).testTag("ButtonLocation"),
                    onClick = {
                        viewModel.centerMapOnUser()
                    },
                    shape = RoundedCornerShape(16.dp),
                    contentColor = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.secondary,
                ) {
                    Icon(
                        painterResource(id = R.drawable.floating_gps),
                        contentDescription = "My Location")
                }
            }
        }) { contentPadding ->

        Box(modifier = Modifier.padding(contentPadding)) {
            BottomSheetComponent(viewModel)
            PermissionsRequest(snackbarHostState)
            MapScreen(viewModel)
            LoadingErrorComponent(viewModel)
        }
    }
}