package com.example.bridgetranslator

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bridge.translator.service.FloatingBubbleService
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        applySystemInsets(topViewId = R.id.topBar, bottomNavId = R.id.bottomNav)

        val switchScreen = findViewById<SwitchMaterial>(R.id.switchScreenTranslate)
        val tvScreenToggle = findViewById<TextView>(R.id.tvScreenToggle)

        updateSwitchState(switchScreen, tvScreenToggle)

        // Screen Translate card
        findViewById<View>(R.id.cardModeScreen).setOnClickListener {
            toggleService(!isServiceEnabled())
            updateSwitchState(switchScreen, tvScreenToggle)
        }
        switchScreen.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked != isServiceEnabled()) {
                toggleService(isChecked)
                updateSwitchState(switchScreen, tvScreenToggle)
            }
        }

        // Camera card
        findViewById<View>(R.id.cardModeCamera).setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }

        // Live Scan card (shape-aware translation + speaker)
        findViewById<View>(R.id.cardModeLiveScan).setOnClickListener {
            startActivity(Intent(this, CameraFeatureActivity::class.java))
        }

        // Input Bridge card
        findViewById<View>(R.id.cardModeInput).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        // View History link
        findViewById<View>(R.id.tvViewHistory).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        // Settings icon
        findViewById<View>(R.id.ivSettingsTop).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        // Bottom Navigation
        findViewById<View>(R.id.navHome).setOnClickListener { /* already here */ }
        findViewById<View>(R.id.navTranslate).setOnClickListener {
            startActivity(Intent(this, TranslateActivity::class.java))
            overridePendingTransition(0, 0)
        }
        findViewById<View>(R.id.navHistory).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
            overridePendingTransition(0, 0)
        }
        findViewById<View>(R.id.navSettings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            overridePendingTransition(0, 0)
        }

        // Wire recent history cards to live Room data
        loadRecentHistory()
    }

    private fun loadRecentHistory() {
        val card1 = findViewById<View>(R.id.homeHistoryCard1)
        val card2 = findViewById<View>(R.id.homeHistoryCard2)
        val tv1Text = findViewById<TextView>(R.id.tvHome1Text)
        val tv1Time = findViewById<TextView>(R.id.tvHome1Time)
        val tv2Text = findViewById<TextView>(R.id.tvHome2Text)
        val tv2Time = findViewById<TextView>(R.id.tvHome2Time)
        val timeFmt = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault())

        lifecycleScope.launch {
            AppDatabase.get(this@HomeActivity).historyDao().getAllFlow().collect { entries ->
                val recent = entries.take(2)

                if (recent.isNotEmpty()) {
                    card1.visibility = View.VISIBLE
                    tv1Text.text = "\"${recent[0].sourceText.take(30)}...\""
                    tv1Time.text = timeFmt.format(Date(recent[0].timestamp))
                    card1.setOnClickListener {
                        startActivity(Intent(this@HomeActivity, HistoryActivity::class.java))
                    }
                } else {
                    card1.visibility = View.GONE
                }

                if (recent.size > 1) {
                    card2.visibility = View.VISIBLE
                    tv2Text.text = "\"${recent[1].sourceText.take(30)}...\""
                    tv2Time.text = timeFmt.format(Date(recent[1].timestamp))
                    card2.setOnClickListener {
                        startActivity(Intent(this@HomeActivity, HistoryActivity::class.java))
                    }
                } else {
                    card2.visibility = View.GONE
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Always retry starting the service when the user returns to this screen.
        // canDrawOverlays() is unreliable on some OEM ROMs, so the service itself
        // is the authoritative check (it fails gracefully if permission is missing).
        if (getSharedPreferences("bridge_prefs", MODE_PRIVATE)
                .getBoolean("bubble_service_enabled", false)) {
            startBridgeService()
        }
        val sw = findViewById<SwitchMaterial>(R.id.switchScreenTranslate) ?: return
        val tv = findViewById<TextView>(R.id.tvScreenToggle) ?: return
        updateSwitchState(sw, tv)
    }

    // - Service helpers -

    private fun isServiceEnabled(): Boolean =
        getSharedPreferences("bridge_prefs", MODE_PRIVATE)
            .getBoolean("bubble_service_enabled", false)

    private fun toggleService(enable: Boolean) {
        val prefs = getSharedPreferences("bridge_prefs", MODE_PRIVATE)
        prefs.edit().putBoolean("bubble_service_enabled", enable).apply()

        if (!enable) {
            stopService(Intent(this, FloatingBubbleService::class.java))
            Toast.makeText(this, "Bridge Service Stopped", Toast.LENGTH_SHORT).show()
            updateSwitchState(
                findViewById(R.id.switchScreenTranslate),
                findViewById(R.id.tvScreenToggle)
            )
            return
        }

        // If the OS API says the permission isn't granted, show a tip and open
        // the system settings page so the user can allow it.  We ALSO start the
        // service immediately: on OEM ROMs where canDrawOverlays() lies, the
        // service will succeed and the bubble will appear right away.
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(
                this,
                "Grant 'Display over other apps' for Bridge, then tap Allow on the next screen",
                Toast.LENGTH_LONG
            ).show()
            startActivity(
                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                    .setData(Uri.parse("package:$packageName"))
            )
        }

        startBridgeService()
    }

    private fun startBridgeService() {
        val intent = Intent(this, FloatingBubbleService::class.java)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        } catch (e: Exception) {
            Log.e("HomeActivity", "Could not start FloatingBubbleService", e)
            Toast.makeText(this, "Could not start service: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateSwitchState(switch: SwitchMaterial?, textView: TextView?) {
        switch ?: return; textView ?: return
        val isEnabled = isServiceEnabled()
        switch.isChecked = isEnabled
        if (isEnabled) {
            textView.text = "Bridge Service is Active  ->"
            textView.setTextColor(getColor(R.color.accent_green))
        } else {
            textView.text = "Turn on Bridge Service  ->"
            textView.setTextColor(getColor(R.color.text_accent_purple))
        }
    }
}
