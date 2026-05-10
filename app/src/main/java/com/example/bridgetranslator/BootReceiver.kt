package com.example.bridgetranslator

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.bridge.translator.service.FloatingBubbleService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val prefs = context.getSharedPreferences("bridge_prefs", Context.MODE_PRIVATE)
            val isBootEnabled = prefs.getBoolean("start_on_boot_enabled", false)
            val isServiceEnabled = prefs.getBoolean("bubble_service_enabled", false)
            
            if (isBootEnabled && isServiceEnabled) {
                val serviceIntent = Intent(context, FloatingBubbleService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }
            }
        }
    }
}
