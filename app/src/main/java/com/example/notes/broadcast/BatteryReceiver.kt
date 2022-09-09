package com.example.notes.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class BatteryReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        println(p1)
        Toast(p0).apply {
            setText("Welcome Back")
            duration = Toast.LENGTH_SHORT
            show()
        }
    }
}