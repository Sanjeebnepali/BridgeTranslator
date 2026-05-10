package com.example.bridgetranslator

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

class OnboardingActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var btnNext: View
    private lateinit var tvNextButton: TextView
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = getSharedPreferences("bridge_prefs", Context.MODE_PRIVATE)

        // Check if onboarding was already shown
        if (prefs.getBoolean("onboarding_completed", false)) {
            startHome()
            return
        }

        setContentView(R.layout.activity_onboarding)

        viewPager = findViewById(R.id.viewPager)
        btnNext = findViewById(R.id.btnNext)
        tvNextButton = findViewById(R.id.tvNextButton)

        val adapter = OnboardingAdapter()
        viewPager.adapter = adapter

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateUI(position)
            }
        })

        btnNext.setOnClickListener {
            val current = viewPager.currentItem
            if (current < adapter.itemCount - 1) {
                viewPager.currentItem = current + 1
            } else {
                completeOnboarding()
            }
        }
    }

    private fun updateUI(position: Int) {
        if (position == 2) {
            tvNextButton.text = "Get Started"
        } else {
            tvNextButton.text = "Next"
        }
    }

    private fun completeOnboarding() {
        prefs.edit().putBoolean("onboarding_completed", true).apply()
        startHome()
    }

    private fun startHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    inner class OnboardingAdapter : RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

        private val titles = arrayOf(
            "Step 1: Enable\nBridge.",
            "Step 2: Aim & \nTranslate.",
            "Step 3: Global Chat"
        )

        private val descriptions = arrayOf(
            "Allow Bridge to float over other apps for instant translation.",
            "Point your camera at any text or use screen selection to bridge the language gap.",
            "Type in your language; we'll send it in theirs. Seamless communication across any border."
        )

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
            return OnboardingViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_onboarding_page, parent, false)
            )
        }

        override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
            holder.title.text = titles[position]
            holder.desc.text = descriptions[position]
            
            // Set visibility of simplified graphics based on position
            holder.graphic1.visibility = if (position == 0) View.VISIBLE else View.GONE
            holder.graphic2.visibility = if (position == 1) View.VISIBLE else View.GONE
            holder.graphic3.visibility = if (position == 2) View.VISIBLE else View.GONE
        }

        override fun getItemCount(): Int = 3

        inner class OnboardingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val title: TextView = view.findViewById(R.id.tvStepTitle)
            val desc: TextView = view.findViewById(R.id.tvStepDescription)
            val graphic1: View = view.findViewById(R.id.step1Graphic)
            val graphic2: View = view.findViewById(R.id.step2Graphic)
            val graphic3: View = view.findViewById(R.id.step3Graphic)
        }
    }
}
