package com.bridge.translator.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings.Secure
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bridge.translator.ui.LanguageSelectorActivity
import com.bridge.translator.service.FloatingBubbleService
import com.example.bridgetranslator.R
import com.example.bridgetranslator.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
    }

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    private val overlayPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        viewModel.syncState()
        updatePermissionUi()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupClickListeners()
        checkCameraPermission()
        setupCaptureModSelector()
        updatePermissionUi()
        updateAccessibilityUi()
    }

    override fun onResume() {
        super.onResume()
        viewModel.syncState()
        updatePermissionUi()
        updateAccessibilityUi()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        android.util.Log.d("CameraMode", "onRequestPermissionsResult: requestCode=$requestCode, results=${grantResults.contentToString()}")

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                android.util.Log.d("CameraMode", "Camera permission granted")
                viewModel.setCaptureMode(FloatingBubbleService.CaptureMode.CAMERA)
                binding.btnCameraMode.isSelected = true
                binding.btnScreenMode.isSelected = false
            } else {
                android.util.Log.w("CameraMode", "Camera permission denied")
                binding.btnScreenMode.isSelected = true
                binding.btnCameraMode.isSelected = false
                android.widget.Toast.makeText(this, "Camera permission required for camera mode", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupObservers() {
        viewModel.serviceRunning.observe(this) { running ->
            binding.btnToggleService.text = getString(
                if (running) R.string.stop_bubble else R.string.start_bubble
            )
            binding.tvStatus.text = getString(
                if (running) R.string.status_running else R.string.status_stopped
            )
        }

        viewModel.targetLanguageLabel.observe(this) { label ->
            binding.tvTargetLanguage.text = label
        }
    }

    private fun setupClickListeners() {
        binding.btnGrantPermission.setOnClickListener { requestOverlayPermission() }

        binding.btnToggleService.setOnClickListener {
            if (Settings.canDrawOverlays(this)) viewModel.toggleService()
            else requestOverlayPermission()
        }

        binding.btnResetLanguage.setOnClickListener {
            viewModel.clearTargetLanguage()
        }

        binding.btnOpenAccessibilitySettings.setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        binding.btnLanguageSelector.setOnClickListener {
            startActivity(Intent(this, LanguageSelectorActivity::class.java))
        }
    }

    private fun requestOverlayPermission() {
        overlayPermissionLauncher.launch(
            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
        )
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        }
    }

    private fun setupCaptureModSelector() {
        val screenBtn = binding.btnScreenMode
        val cameraBtn = binding.btnCameraMode

        screenBtn.setOnClickListener {
            viewModel.setCaptureMode(FloatingBubbleService.CaptureMode.SCREEN)
            screenBtn.isSelected = true
            cameraBtn.isSelected = false
        }

        cameraBtn.setOnClickListener {
            val hasPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            android.util.Log.d("CameraMode", "Camera permission check: $hasPermission (GRANTED=${PackageManager.PERMISSION_GRANTED})")

            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                android.util.Log.d("CameraMode", "Requesting camera permission")
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_CODE
                )
            } else {
                android.util.Log.d("CameraMode", "Permission already granted, switching to CAMERA mode")
                viewModel.setCaptureMode(FloatingBubbleService.CaptureMode.CAMERA)
                screenBtn.isSelected = false
                cameraBtn.isSelected = true
            }
        }
    }

    private fun updatePermissionUi() {
        val has = Settings.canDrawOverlays(this)
        binding.btnGrantPermission.visibility = if (has) android.view.View.GONE else android.view.View.VISIBLE
        binding.btnToggleService.isEnabled = has
        binding.tvPermissionStatus.text = getString(
            if (has) R.string.permission_granted else R.string.permission_required
        )
    }

    private fun updateAccessibilityUi() {
        val enabled = isAccessibilityServiceEnabled()
        binding.tvAccessibilityStatus.text = if (enabled) {
            "Auto refresh enabled"
        } else {
            "Auto refresh disabled"
        }
        val target = accessibilityTargetLabel()
        binding.tvCacheStatus.text = "Overlay translating to: $target"
    }

    private fun accessibilityTargetLabel(): String {
        val code = getSharedPreferences("bridge_prefs", MODE_PRIVATE)
            .getString("target_language", "en") ?: "en"
        return when (code) {
            "ko" -> "Korean"
            "ja" -> "Japanese"
            "zh" -> "Chinese"
            "es" -> "Spanish"
            "ar" -> "Arabic"
            "hi" -> "Hindi"
            "fr" -> "French"
            else -> "English"
        }
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val expected = "$packageName/com.bridge.translator.service.TranslatorAccessibilityService"
        val enabledServices = Secure.getString(
            contentResolver,
            Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false

        val splitter = TextUtils.SimpleStringSplitter(':')
        splitter.setString(enabledServices)
        for (service in splitter) {
            if (service.equals(expected, ignoreCase = true)) return true
        }
        return false
    }
}
