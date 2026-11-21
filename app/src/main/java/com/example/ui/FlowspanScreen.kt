package com.example.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.HourglassTop
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.model.AppLanguage
import com.example.model.TimerPhase
import com.example.ui.components.AmbientMeshBackdrop
import com.example.ui.components.EditorialTimerRing
import com.example.ui.components.PetalFieldCanvas
import com.example.ui.components.SettingsScreen
import com.example.ui.components.VellumButton
import com.example.ui.theme.FlowspanTheme
import com.example.ui.theme.LocalFlowspanPalette
import com.example.ui.theme.editorialSerifFamily
import com.example.viewmodel.FlowspanViewModel
import java.util.Locale

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FlowspanRoot(viewModel: FlowspanViewModel) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  // Silently pre-warm SettingsScreen after initial frame so opening Settings never cold-composes
  var keepSettingsWarmed by remember { mutableStateOf(false) }
  LaunchedEffect(Unit) {
    kotlinx.coroutines.delay(160L)
    keepSettingsWarmed = true
  }
  if (uiState.isSettingsOpen) {
    keepSettingsWarmed = true
  }

  val settingsSlideState =
    animateFloatAsState(
      targetValue = if (uiState.isSettingsOpen) 1f else 0f,
      animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
      label = "settings_slide_progress",
    )

  val context = androidx.compose.ui.platform.LocalContext.current
  val activePalette = uiState.preferences.palette
  LaunchedEffect(activePalette) {
    val activity = context as? android.app.Activity ?: return@LaunchedEffect
    val themeRes = com.example.ui.theme.splashThemeResFor(activePalette)
    val bgArgb =
      with(com.example.ui.theme.paletteFor(activePalette).meshBase) {
        android.graphics.Color.argb(
          (alpha * 255f + 0.5f).toInt(),
          (red * 255f + 0.5f).toInt(),
          (green * 255f + 0.5f).toInt(),
          (blue * 255f + 0.5f).toInt(),
        )
      }
    activity.window?.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(bgArgb))
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
      try {
        activity.splashScreen.setSplashScreenTheme(themeRes)
      } catch (_: Throwable) {}
    }
  }

  FlowspanTheme(paletteId = uiState.preferences.palette) {
    Box(modifier = Modifier.fillMaxSize()) {
      Box(
        modifier =
          Modifier
            .fillMaxSize()
            .drawWithContent {
              if (settingsSlideState.value < 0.999f) {
                drawContent()
              }
            }
      ) {
        FlowspanScreen(
          phase = uiState.phase,
          language = uiState.preferences.language,
          remainingMillis = uiState.remainingMillis,
          progressFraction = uiState.progressFraction,
          completedCycles = uiState.completedCycles,
          windEventsEnabled = uiState.preferences.windEventsEnabled,
          isAmbientPaused = uiState.isSettingsOpen,
          onPrimaryLeftClick = viewModel::onPrimaryLeftAction,
          onOpenSettings = viewModel::openSettings,
        )
      }

      if (keepSettingsWarmed) {
        Box(
          modifier =
            Modifier
              .fillMaxSize()
              .graphicsLayer {
                val p = settingsSlideState.value
                translationX = (1f - p) * size.width
              }
              .drawWithContent {
                if (settingsSlideState.value > 0.001f) {
                  drawContent()
                }
              }
              .semantics {
                if (!uiState.isSettingsOpen) invisibleToUser()
              }
        ) {
          SettingsScreen(
            isActive = uiState.isSettingsOpen,
            preferences = uiState.preferences,
            currentPhase = uiState.phase,
            onDismiss = viewModel::closeSettings,
            onFocusMinutesChange = viewModel::setFocusMinutes,
            onBreakMinutesChange = viewModel::setBreakMinutes,
            onAutoCycleToggle = viewModel::setAutoCycle,
            onPaletteSelect = viewModel::setPalette,
            onAmbientTrackSelect = viewModel::setAmbientTrack,
            onTransitionChimeSelect = viewModel::setTransitionChime,
            onLanguageSelect = viewModel::setLanguage,
          )
        }
      }
    }
  }
}

internal fun phaseCanonicalName(
  phase: TimerPhase,
  language: AppLanguage,
): String =
  when (phase) {
    TimerPhase.IDLE -> language.readyTitle
    TimerPhase.FOCUS -> language.focusTitle
    TimerPhase.BREAK -> language.breakTitle
  }

@Composable
private fun FlowspanScreen(
  phase: TimerPhase,
  language: AppLanguage,
  remainingMillis: Long,
  progressFraction: Float,
  completedCycles: Int,
  windEventsEnabled: Boolean,
  isAmbientPaused: Boolean,
  onPrimaryLeftClick: () -> Unit,
  onOpenSettings: () -> Unit,
) {
  val palette = LocalFlowspanPalette.current
  val haptics = LocalHapticFeedback.current
  val phaseName = phaseCanonicalName(phase, language)
  val serifFamily = editorialSerifFamily(language)

  Box(modifier = Modifier.fillMaxSize()) {
    // LAYER 0: Full-bleed moving color washes across the entire screen
    AmbientMeshBackdrop(
      isPaused = isAmbientPaused,
      modifier = Modifier.fillMaxSize(),
    )

    // LAYER 1: Center Large Ring + Lower Monumental Serif Countdown Readout
    Column(
      modifier =
        Modifier
          .fillMaxSize()
          .windowInsetsPadding(
            WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
          ),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Spacer(modifier = Modifier.height(86.dp))

      BoxWithConstraints(
        modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center,
      ) {
        val ringDiameter = min(maxWidth * 0.88f, maxHeight * 0.90f).coerceIn(220.dp, 420.dp)
        EditorialTimerRing(
          phase = phase,
          progressFraction = progressFraction,
          modifier = Modifier.size(ringDiameter),
        )
      }

      Text(
        text = formatCountdown(remainingMillis),
        style = MaterialTheme.typography.displayMedium,
        color = palette.inkPrimary,
        maxLines = 1,
        softWrap = false,
        modifier = Modifier.padding(horizontal = 28.dp).testTag("countdown_text"),
      )

      Spacer(modifier = Modifier.navigationBarsPadding().height(100.dp))
    }

    // Floating editorial title overlay (aligned strictly to the 28.dp left/right grid of the bottom bar)
    Row(
      modifier =
        Modifier
          .align(Alignment.TopCenter)
          .widthIn(max = 560.dp)
          .fillMaxWidth()
          .windowInsetsPadding(
            WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
          )
          .padding(start = 28.dp, end = 28.dp, top = 28.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.Top,
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
          text = stringResource(R.string.app_name),
          style = MaterialTheme.typography.headlineLarge,
          color = palette.inkPrimary,
          modifier = Modifier.testTag("app_masthead_title"),
        )
        Text(
          text = phaseName,
          style =
            MaterialTheme.typography.titleSmall.copy(
              fontFamily = serifFamily,
              fontSize = 13.5.sp,
            ),
          color = palette.inkSecondary,
          modifier = Modifier.testTag("phase_subtitle"),
        )
      }
    }

    // LAYER 2: Full-screen opaque circular petals falling from the very top edge,
    // drifting ABOVE Flowspan and the center ring, and BELOW the bottom buttons
    PetalFieldCanvas(
      windEventsEnabled = windEventsEnabled,
      isPaused = isAmbientPaused,
      modifier = Modifier.fillMaxSize(),
    )

    // LAYER 3: Symmetrical translucent frosted-vellum buttons (exact same 28.dp grid alignment)
    Row(
      modifier =
        Modifier
          .align(Alignment.BottomCenter)
          .widthIn(max = 560.dp)
          .fillMaxWidth()
          .navigationBarsPadding()
          .padding(horizontal = 28.dp, vertical = 22.dp),
      horizontalArrangement = Arrangement.spacedBy(16.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      val leftLabel =
        when (phase) {
          TimerPhase.IDLE -> language.startFocus
          TimerPhase.FOCUS -> language.focusing
          TimerPhase.BREAK -> language.resetReady
        }
      val leftIcon =
        when (phase) {
          TimerPhase.IDLE -> Icons.Rounded.PlayArrow
          TimerPhase.FOCUS -> Icons.Rounded.HourglassTop
          TimerPhase.BREAK -> Icons.Rounded.Refresh
        }
      val leftEnabled = phase != TimerPhase.FOCUS

      VellumButton(
        label = leftLabel,
        icon = leftIcon,
        enabled = leftEnabled,
        isAccentHint = phase == TimerPhase.IDLE,
        testTag = "start_cycle_button",
        onClick = {
          haptics.performHapticFeedback(HapticFeedbackType.LongPress)
          onPrimaryLeftClick()
        },
        modifier = Modifier.weight(1f),
      )

      VellumButton(
        label = language.settingsTitle,
        icon = Icons.Rounded.Tune,
        enabled = true,
        isAccentHint = false,
        testTag = "settings_button",
        onClick = {
          haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
          onOpenSettings()
        },
        modifier = Modifier.weight(1f),
      )
    }
  }
}

private fun formatCountdown(remainingMillis: Long): String {
  val totalSeconds = ((remainingMillis + 999L) / 1000L).coerceAtLeast(0L)
  val minutes = totalSeconds / 60L
  val seconds = totalSeconds % 60L
  return String.format(Locale.US, "%02d:%02d", minutes, seconds)
}
