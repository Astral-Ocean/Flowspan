package com.example

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.PreferencesRepository
import com.example.ui.FlowspanRoot
import com.example.ui.theme.paletteFor
import com.example.ui.theme.splashThemeResFor
import com.example.viewmodel.FlowspanViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    val initialPalette = PreferencesRepository(this).readInitialSnapshot().palette
    val themeRes = splashThemeResFor(initialPalette)
    setTheme(themeRes)

    super.onCreate(savedInstanceState)

    val launchBgArgb = paletteFor(initialPalette).meshBase.toArgb()
    window.setBackgroundDrawable(ColorDrawable(launchBgArgb))

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      try {
        splashScreen.setSplashScreenTheme(themeRes)
        splashScreen.setOnExitAnimationListener { splashScreenView ->
          // Instantly tint the transition screen to match the user's chosen palette
          splashScreenView.setBackgroundColor(launchBgArgb)
          try {
            splashScreenView.iconView?.visibility = View.GONE
          } catch (_: Throwable) {}
          // Smooth 120ms fade transition so the user never sees any jarring or mismatched color
          splashScreenView.animate()
            .alpha(0f)
            .setDuration(120L)
            .withEndAction { splashScreenView.remove() }
            .start()
        }
      } catch (_: Throwable) {}
    }

    enableEdgeToEdge(
      statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
      navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
    )
    setContent {
      val viewModel: FlowspanViewModel = viewModel()
      FlowspanRoot(viewModel = viewModel)
    }
  }
}
