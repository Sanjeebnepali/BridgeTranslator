package com.bridge.translator.ui.overlay

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.bridgetranslator.databinding.ActivityOverlayBinding
import com.google.android.material.snackbar.Snackbar

class OverlayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOverlayBinding
    private val viewModel: OverlayViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Make the activity full-screen and over the status/nav bars
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        binding = ActivityOverlayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        setupObservers()
    }

    private fun setupClickListeners() {
        binding.btnClose.setOnClickListener { finish() }
        binding.overlayBackground.setOnClickListener { finish() }
        binding.btnSelectArea.setOnClickListener {
            viewModel.onSelectAreaClicked()
        }
    }

    private fun setupObservers() {
        viewModel.snackbarMessage.observe(this) { message ->
            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
        }
    }
}
