package com.zodiactap.mapper.base.overlay

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import android.widget.ImageView
import dagger.hilt.android.qualifiers.ApplicationContext
import com.zodiactap.mapper.base.R
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

/**
 * Shows a small overlay with the ZodiacTap logo briefly on screen to confirm
 * that key maps (macros) have been resumed/enabled. This is purely a visual
 * confirmation, similar to a toast, but drawn as a system overlay so it is
 * visible on top of games and other full-screen apps.
 *
 * Requires the "Display over other apps" (SYSTEM_ALERT_WINDOW) permission.
 * If the permission has not been granted, this silently does nothing rather
 * than crashing, since the popup is a nice-to-have and never something that
 * should block or interfere with macros actually running.
 */
@Singleton
class OverlayLogoManager @Inject constructor(
    @ApplicationContext private val ctx: Context,
) {

    companion object {
        private const val DISPLAY_DURATION_MS = 1600L
        private const val FADE_DURATION_MS = 220L
        private const val LOGO_SIZE_DP = 96
    }

    private val windowManager: WindowManager? by lazy {
        ctx.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
    }

    private var currentView: ImageView? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    private var hideRunnable: Runnable? = null

    fun hasOverlayPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(ctx)
        } else {
            true
        }
    }

    /**
     * Show the ZodiacTap logo briefly in the centre-top of the screen. Safe to
     * call from any thread; the actual view work is posted to the main thread.
     */
    fun showBriefly() {
        if (!hasOverlayPermission()) {
            Timber.d("OverlayLogoManager: no SYSTEM_ALERT_WINDOW permission, skipping popup")
            return
        }

        mainHandler.post { showInternal() }
    }

    private fun showInternal() {
        val wm = windowManager ?: return

        // Remove any existing popup first so repeated toggles don't stack views.
        removeInternal()

        val density = ctx.resources.displayMetrics.density
        val sizePx = (LOGO_SIZE_DP * density).toInt()

        val imageView = ImageView(ctx).apply {
            setImageResource(R.drawable.zodiactap_overlay_logo)
            alpha = 0f
        }

        val overlayType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            sizePx,
            sizePx,
            overlayType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT,
        ).apply {
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            y = (48 * density).toInt()
        }

        try {
            wm.addView(imageView, params)
            currentView = imageView

            ObjectAnimator.ofFloat(imageView, "alpha", 0f, 1f).apply {
                duration = FADE_DURATION_MS
                start()
            }

            val runnable = Runnable { fadeOutAndRemove() }
            hideRunnable = runnable
            mainHandler.postDelayed(runnable, DISPLAY_DURATION_MS)
        } catch (e: Exception) {
            Timber.e(e, "OverlayLogoManager: failed to add overlay view")
        }
    }

    private fun fadeOutAndRemove() {
        val imageView = currentView ?: return

        ObjectAnimator.ofFloat(imageView, "alpha", 1f, 0f).apply {
            duration = FADE_DURATION_MS
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    removeInternal()
                }
            })
            start()
        }
    }

    private fun removeInternal() {
        hideRunnable?.let { mainHandler.removeCallbacks(it) }
        hideRunnable = null

        val view = currentView ?: return
        currentView = null

        try {
            windowManager?.removeView(view)
        } catch (e: Exception) {
            // View may have already been removed (e.g. activity/service torn down).
            Timber.d("OverlayLogoManager: view already removed")
        }
    }
}
