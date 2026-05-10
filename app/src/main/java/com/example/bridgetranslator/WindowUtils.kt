package com.example.bridgetranslator

import android.app.Activity
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

/**
 * Call once in each Activity.onCreate() after setContentView().
 *
 * @param topViewId  The view that should receive status-bar top padding
 *                   (e.g. the header / first child). Pass 0 to skip.
 * @param bottomNavId The bottom navigation view that gets nav-bar bottom padding.
 *                   Defaults to R.id.bottomNav.
 */
fun Activity.applySystemInsets(
    topViewId: Int = 0,
    bottomNavId: Int = R.id.bottomNav
) {
    // Draw content behind system bars
    WindowCompat.setDecorFitsSystemWindows(window, false)

    val root = window.decorView

    // Snapshot the original paddings BEFORE the listener fires
    val topView = if (topViewId != 0) findViewById<View>(topViewId) else null
    val bottomNav = if (bottomNavId != 0) findViewById<View>(bottomNavId) else null

    val origTopPad = topView?.paddingTop ?: 0
    val origBottomPad = bottomNav?.paddingBottom ?: 0
    val origBottomHeight = bottomNav?.layoutParams?.height ?: 0

    ViewCompat.setOnApplyWindowInsetsListener(root) { _, insets ->
        val statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars())
        val navBars   = insets.getInsets(WindowInsetsCompat.Type.navigationBars())

        topView?.updatePadding(top = origTopPad + statusBars.top)
        bottomNav?.let { nav ->
            nav.updatePadding(bottom = origBottomPad + navBars.bottom)
            if (origBottomHeight > 0) {
                nav.layoutParams = nav.layoutParams.apply {
                    height = origBottomHeight + navBars.bottom
                }
            }
        }

        insets
    }
    ViewCompat.requestApplyInsets(root)
}
