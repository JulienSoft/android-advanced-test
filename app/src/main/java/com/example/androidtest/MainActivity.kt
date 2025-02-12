package com.example.androidtest

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.androidtest.ui.components.MapScreen
import com.example.androidtest.ui.theme.AndroidTestTheme
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager

class MainActivity : ComponentActivity() {
    lateinit var permissionsManager: PermissionsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            Toast.makeText(this, "Location permissions granted", Toast.LENGTH_LONG).show()
        } else {
            var permissionsListener: PermissionsListener = object : PermissionsListener {
                override fun onExplanationNeeded(permissionsToExplain: List<String>) {
                }

                override fun onPermissionResult(granted: Boolean) {
                    if (granted) {

                    } else {

                    }
                }
            }

            permissionsManager = PermissionsManager(permissionsListener)
            permissionsManager.requestLocationPermissions(this)
        }

        setContent {
            AndroidTestTheme {
                MapScreen()
            }
        }
    }
}