package com.example.notes.permissions

import android.Manifest.permission.*
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat

class Permission(val context: Context) {
    private var count = 0

    companion object {
        const val REQ_CODE = 100
    }

    private var isReadGranted = false
    private var isLocationGranted = false
    private var isAudioGranted = false
    private var isCameraGranted = false

    private fun checkPermission() {
        isReadGranted = ActivityCompat.checkSelfPermission(
            context,
            READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        isLocationGranted = ActivityCompat.checkSelfPermission(
            context,
            ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        isAudioGranted = ActivityCompat.checkSelfPermission(
            context,
            RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        isCameraGranted =
            ActivityCompat.checkSelfPermission(context, CAMERA) == PackageManager.PERMISSION_GRANTED

    }

    fun applyForPermission() {
        checkPermission()
        val permissionRequest = mutableListOf<String>()

        if (!isReadGranted) {
            Toast(context).apply {
                setText("Read Denied")
                duration = Toast.LENGTH_SHORT
                show()
            }
            permissionRequest.add(READ_EXTERNAL_STORAGE)
        }
        if (!isLocationGranted) {
            Toast(context).apply {
                setText("Location Denied")
                duration = Toast.LENGTH_SHORT
                show()
            }
            permissionRequest.add(ACCESS_FINE_LOCATION)
        }
        if (!isAudioGranted) {
            Toast(context).apply {
                setText("Audio Denied")
                duration = Toast.LENGTH_SHORT
                show()
            }
            permissionRequest.add(RECORD_AUDIO)
        }
        if (!isCameraGranted) {
            Toast(context).apply {
                setText("Camera Denied")
                duration = Toast.LENGTH_SHORT
                show()
            }
            permissionRequest.add(CAMERA)
        }
        if (permissionRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                context as Activity, permissionRequest.toTypedArray(),
                REQ_CODE
            )
            count++
            if (count > 2) {
                val intent = Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            }
        }

    }

    fun updatePermission(
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        var index = 0
        permissions.forEach {
            when (it) {
                READ_EXTERNAL_STORAGE -> isReadGranted =
                    grantResults[index] == PackageManager.PERMISSION_GRANTED
                ACCESS_FINE_LOCATION -> isLocationGranted =
                    grantResults[index] == PackageManager.PERMISSION_GRANTED
                RECORD_AUDIO -> isAudioGranted =
                    grantResults[index] == PackageManager.PERMISSION_GRANTED
                CAMERA -> isCameraGranted =
                    grantResults[index] == PackageManager.PERMISSION_GRANTED
                else -> Toast(context).apply {
                    setText("$it doesn't required")
                    duration = Toast.LENGTH_SHORT
                    show()
                }
            }
            index++
        }
    }

}