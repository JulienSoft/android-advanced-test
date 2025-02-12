package com.example.androidtest

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

// Display setting of the application to configure permissions
fun Context.startApplicationSettings() {
    val uri = Uri.fromParts("package", packageName, null)
    val intent = Intent()
    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    intent.data = uri
    startActivity(intent)
}