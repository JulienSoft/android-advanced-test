package com.example.androidtest.ui.components

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.example.androidtest.startApplicationSettings
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionsRequest(snackbarHostState: SnackbarHostState) {
    val fineLocation = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Nothing need the mapbox get the location automatically
        } else {
            scope.launch {
                val result = snackbarHostState
                    .showSnackbar(
                        message = "You have disabled location permission",
                        actionLabel = "Go to settings",
                        duration = SnackbarDuration.Long
                    )
                if (result == SnackbarResult.ActionPerformed) {
                    context.startApplicationSettings()
                }
            }
        }
    }

    LaunchedEffect(fineLocation) {
        if (!fineLocation.status.isGranted) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}