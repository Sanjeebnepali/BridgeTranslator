package com.example.bridgetranslator

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    private val prefs by lazy {
        getSharedPreferences("bridge_prefs", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        applySystemInsets(topViewId = R.id.topBar, bottomNavId = R.id.bottomNav)

        // --- Overlay permission row ---
        val tvOverlayStatus = findViewById<TextView>(R.id.tvOverlayStatus)
        val tvAccessibilityStatus = findViewById<TextView>(R.id.tvAccessibilityStatus)
        updateOverlayStatus(tvOverlayStatus)
        updateAccessibilityStatus(tvAccessibilityStatus)
        findViewById<View>(R.id.btnOverlay).setOnClickListener {
            if (!Settings.canDrawOverlays(this)) {
                // Permission not granted - save intent and send user to system settings.
                // onResume will auto-start the service when they return with permission.
                prefs.edit().putBoolean("bubble_service_enabled", true).apply()
                Toast.makeText(
                    this,
                    "Tap the toggle next to Bridge Translator to allow it",
                    Toast.LENGTH_LONG
                ).show()
                startActivity(
                    Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                        .setData(Uri.parse("package:$packageName"))
                )
            } else {
                val isEnabled = prefs.getBoolean("bubble_service_enabled", false)
                prefs.edit().putBoolean("bubble_service_enabled", !isEnabled).apply()
                if (!isEnabled) startBridgeService() else stopBridgeService()
                updateOverlayStatus(tvOverlayStatus)
            }
        }

        findViewById<View>(R.id.btnAccessibility).setOnClickListener {
            Toast.makeText(
                this,
                "Open Bridge Translator and turn the Accessibility service ON",
                Toast.LENGTH_LONG
            ).show()
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        // --- Offline Mode switch ---
        val switchOffline = findViewById<SwitchMaterial>(R.id.switchOffline)
        val tvOfflineStatus = findViewById<TextView>(R.id.tvOfflineStatus)
        switchOffline.isChecked = prefs.getBoolean("wifi_only_download", false)
        updateOfflineStatus(switchOffline.isChecked, tvOfflineStatus)
        switchOffline.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("wifi_only_download", isChecked).apply()
            updateOfflineStatus(isChecked, tvOfflineStatus)
        }

        // --- Start on Boot switch ---
        val switchBoot = findViewById<SwitchMaterial>(R.id.switchBoot)
        switchBoot.isChecked = prefs.getBoolean("start_on_boot_enabled", false)
        switchBoot.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("start_on_boot_enabled", isChecked).apply()
        }

        // --- Input Bridge switch ---
        val switchInputBridge = findViewById<SwitchMaterial>(R.id.switchInputBridge)
        val tvInputBridgeStatus = findViewById<TextView>(R.id.tvInputBridgeStatus)
        switchInputBridge.isChecked = prefs.getBoolean("input_bridge_enabled", false)
        updateInputBridgeStatus(switchInputBridge.isChecked, tvInputBridgeStatus)
        switchInputBridge.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("input_bridge_enabled", isChecked).apply()
            updateInputBridgeStatus(isChecked, tvInputBridgeStatus)
            if (isChecked && !isBridgeKeyboardEnabled()) {
                Toast.makeText(
                    this,
                    "Enable Bridge Keyboard in keyboard settings",
                    Toast.LENGTH_LONG
                ).show()
                openKeyboardSettings()
            }
            if (isChecked) requestMicPermissionForKeyboard()
        }
        findViewById<View>(R.id.btnInputBridgeAccess).setOnClickListener {
            Toast.makeText(
                this,
                "Enable Bridge Keyboard, then choose it while typing",
                Toast.LENGTH_LONG
            ).show()
            openKeyboardSettings()
        }

        // --- Language rows - live labels + clickable bottom sheets ---
        val languageManager = LanguageManager(this)
        val tvSource = findViewById<TextView>(R.id.tvSourceLangValue)
        val tvTarget = findViewById<TextView>(R.id.tvTargetLangValue)

        // Observe both language codes and update labels reactively
        lifecycleScope.launch {
            combine(languageManager.sourceLangCode, languageManager.targetLangCode) { s, t ->
                Pair(s, t)
            }.collect { (src, tgt) ->
                val srcLang = Language.getLanguageByCode(src)
                val tgtLang = Language.getLanguageByCode(tgt)
                tvSource.text = "${srcLang?.flagEmoji ?: ""} ${srcLang?.name ?: src}"
                tvTarget.text = "${tgtLang?.flagEmoji ?: ""} ${tgtLang?.name ?: tgt}"
            }
        }

        // Tap Source Language row - open picker
        findViewById<View>(R.id.rowSourceLang).setOnClickListener {
            LanguageBottomSheet.newInstance(isSource = true).also { sheet ->
                sheet.show(supportFragmentManager, "settingSrcLang")
            }
        }

        // Tap Target Language row - open picker
        findViewById<View>(R.id.rowTargetLang).setOnClickListener {
            LanguageBottomSheet.newInstance(isSource = false).also { sheet ->
                sheet.show(supportFragmentManager, "settingTgtLang")
            }
        }

        // Back button
        findViewById<View>(R.id.ivBack).setOnClickListener { finish() }

        // ── Fast Mode (Live Scan) ──────────────────────────────────────────────
        val switchFastMode   = findViewById<SwitchMaterial>(R.id.switchFastMode)
        val tvFastModeStatus = findViewById<TextView>(R.id.tvFastModeStatus)
        switchFastMode.isChecked = prefs.getBoolean("fast_mode_enabled", false)
        updateFastModeStatus(switchFastMode.isChecked, tvFastModeStatus)
        switchFastMode.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("fast_mode_enabled", isChecked).apply()
            updateFastModeStatus(isChecked, tvFastModeStatus)
        }

        // ── Auto-Speak (Live Scan) ─────────────────────────────────────────────
        val switchSpeaker   = findViewById<SwitchMaterial>(R.id.switchSpeaker)
        val tvSpeakerStatus = findViewById<TextView>(R.id.tvSpeakerStatus)
        switchSpeaker.isChecked = prefs.getBoolean("speaker_enabled", false)
        updateSpeakerStatus(switchSpeaker.isChecked, tvSpeakerStatus)
        switchSpeaker.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("speaker_enabled", isChecked).apply()
            updateSpeakerStatus(isChecked, tvSpeakerStatus)
        }

        // How to Use - replay onboarding
        findViewById<View>(R.id.btnHowToUse).setOnClickListener {
            prefs.edit().putBoolean("onboarding_completed", false).apply()
            startActivity(
                Intent(this, OnboardingActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
            finish()
        }

        // Bottom Navigation
        findViewById<View>(R.id.navHome).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            overridePendingTransition(0, 0)
        }
        findViewById<View>(R.id.navTranslate).setOnClickListener {
            startActivity(Intent(this, TranslateActivity::class.java))
            overridePendingTransition(0, 0)
        }
        findViewById<View>(R.id.navHistory).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
            overridePendingTransition(0, 0)
        }
        findViewById<View>(R.id.navSettings).setOnClickListener { /* already here */ }
    }

    private fun updateOverlayStatus(tv: TextView) {
        if (com.bridge.translator.service.FloatingBubbleService.isRunning && Settings.canDrawOverlays(this)) {
            tv.text = "ACTIVE"
            tv.setTextColor(getColor(R.color.accent_green))
        } else {
            tv.text = "INACTIVE"
            tv.setTextColor(getColor(R.color.text_secondary))
        }
    }

    private fun updateAccessibilityStatus(tv: TextView) {
        if (isBridgeAccessibilityEnabled()) {
            tv.text = "ACTIVE"
            tv.setTextColor(getColor(R.color.accent_green))
        } else {
            tv.text = "GRANT"
            tv.setTextColor(getColor(R.color.accent_red))
        }
    }

    private fun isBridgeAccessibilityEnabled(): Boolean {
        return isAccessibilityServiceEnabled("$packageName/com.bridge.translator.service.TranslatorAccessibilityService")
    }

    private fun isInputBridgeAccessibilityEnabled(): Boolean {
        return isAccessibilityServiceEnabled("$packageName/com.bridge.translator.service.BridgeAccessibilityService")
    }

    private fun isBridgeKeyboardEnabled(): Boolean {
        val enabledMethods = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_INPUT_METHODS
        ) ?: return false
        return enabledMethods.contains(
            "$packageName/com.bridge.translator.keyboard.BridgeKeyboardService",
            ignoreCase = true
        )
    }

    private fun isAccessibilityServiceEnabled(expected: String): Boolean {
        val enabled = Settings.Secure.getInt(
            contentResolver,
            Settings.Secure.ACCESSIBILITY_ENABLED,
            0
        ) == 1
        if (!enabled) return false

        val enabledServices = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false

        val splitter = TextUtils.SimpleStringSplitter(':')
        splitter.setString(enabledServices)
        while (splitter.hasNext()) {
            if (splitter.next().equals(expected, ignoreCase = true)) return true
        }
        return false
    }

    private fun updateInputBridgeStatus(isEnabled: Boolean, tv: TextView) {
        val keyboardEnabled = isBridgeKeyboardEnabled()
        when {
            isEnabled && keyboardEnabled -> {
                tv.text = "ON - Bridge Keyboard is enabled"
                tv.setTextColor(getColor(R.color.accent_green))
            }
            isEnabled -> {
                tv.text = "ON - enable Bridge Keyboard"
                tv.setTextColor(getColor(R.color.accent_red))
            }
            else -> {
                tv.text = "OFF"
                tv.setTextColor(getColor(R.color.text_secondary))
            }
        }
    }

    private fun openKeyboardSettings() {
        startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
    }

    private fun requestMicPermissionForKeyboard() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) return
        requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_RECORD_AUDIO)
    }

    private fun updateFastModeStatus(enabled: Boolean, tv: TextView) {
        if (enabled) {
            tv.text = "ON – lower resolution, maximum speed"
            tv.setTextColor(getColor(R.color.accent_cyan))
        } else {
            tv.text = "OFF – balanced speed"
            tv.setTextColor(getColor(R.color.text_secondary))
        }
    }

    private fun updateSpeakerStatus(enabled: Boolean, tv: TextView) {
        if (enabled) {
            tv.text = "ON – reads translation aloud automatically"
            tv.setTextColor(getColor(R.color.accent_green))
        } else {
            tv.text = "OFF – tap speaker manually"
            tv.setTextColor(getColor(R.color.text_secondary))
        }
    }

    companion object {
        private const val REQUEST_RECORD_AUDIO = 4207
    }

    private fun updateOfflineStatus(isEnabled: Boolean, tv: TextView) {
        if (isEnabled) {
            tv.visibility = View.VISIBLE
            tv.text = "ON - Wi-Fi downloads only"
            tv.setTextColor(getColor(R.color.accent_green))
        } else {
            tv.visibility = View.VISIBLE
            tv.text = "OFF - Mobile data allowed for model downloads"
            tv.setTextColor(getColor(R.color.text_secondary))
        }
    }

    private fun startBridgeService() {
        if (!Settings.canDrawOverlays(this)) return
        val intent = Intent(this, com.bridge.translator.service.FloatingBubbleService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(intent)
        else startService(intent)
        Toast.makeText(this, "Bridge Started - look for the bubble on screen", Toast.LENGTH_LONG).show()
    }

    private fun stopBridgeService() {
        stopService(Intent(this, com.bridge.translator.service.FloatingBubbleService::class.java))
        Toast.makeText(this, "Bridge Service Stopped", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        // Auto-start after user returns from system overlay-permission screen.
        if (prefs.getBoolean("bubble_service_enabled", false)
            && Settings.canDrawOverlays(this)
            && !com.bridge.translator.service.FloatingBubbleService.isRunning
        ) {
            startBridgeService()
        }
        findViewById<TextView>(R.id.tvOverlayStatus)?.let { updateOverlayStatus(it) }
        findViewById<TextView>(R.id.tvAccessibilityStatus)?.let { updateAccessibilityStatus(it) }
        findViewById<TextView>(R.id.tvInputBridgeStatus)?.let {
            updateInputBridgeStatus(prefs.getBoolean("input_bridge_enabled", false), it)
        }
    }
}
