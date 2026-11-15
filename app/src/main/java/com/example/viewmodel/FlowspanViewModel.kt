package com.example.viewmodel

import android.app.Application
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AmbientAudioEngine
import com.example.data.PreferencesRepository
import com.example.model.AmbientTrack
import com.example.model.PomodoroUiState
import com.example.model.ThemePaletteId
import com.example.model.TimerPhase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class FlowspanViewModel(application: Application) : AndroidViewModel(application) {

  private val repository = PreferencesRepository(application.applicationContext)
  private val audioEngine = AmbientAudioEngine(application.applicationContext)

  private val initialPrefs = repository.readInitialSnapshot()

  private val _uiState =
    MutableStateFlow(
      PomodoroUiState(
        remainingMillis = initialPrefs.focusMinutes * 60_000L,
        totalPhaseMillis = initialPrefs.focusMinutes * 60_000L,
        preferences = initialPrefs,
      )
    )
  val uiState: StateFlow<PomodoroUiState> = _uiState.asStateFlow()

  private val persistDispatcher = Dispatchers.IO.limitedParallelism(1)
  private var tickerJob: Job? = null
  private var phaseEndElapsedMs: Long = 0L
  @Volatile private var hasLocalMutation = false

  init {
    viewModelScope.launch(persistDispatcher) {
      val prefs = repository.preferencesFlow.first()
      if (!hasLocalMutation) {
        val previous = _uiState.value.preferences
        _uiState.update { current ->
          val updatedTotal =
            when (current.phase) {
              TimerPhase.IDLE -> prefs.focusMinutes * 60_000L
              else -> current.totalPhaseMillis
            }
          val updatedRemaining =
            when (current.phase) {
              TimerPhase.IDLE -> prefs.focusMinutes * 60_000L
              else -> current.remainingMillis
            }
          current.copy(
            preferences = prefs,
            totalPhaseMillis = updatedTotal,
            remainingMillis = updatedRemaining,
          )
        }
        if (previous.ambientTrack != prefs.ambientTrack) {
          syncAudioWithState()
        }
      }
    }
  }

  /**
   * Bottom-left primary button action:
   * - IDLE: starts the Focus -> Break Pomodoro cycle.
   * - FOCUS: strictly ignored (button is also disabled in UI).
   * - BREAK: returns immediately to IDLE default state.
   */
  fun onPrimaryLeftAction() {
    when (_uiState.value.phase) {
      TimerPhase.IDLE -> startFocusPhase()
      TimerPhase.FOCUS -> Unit
      TimerPhase.BREAK -> resetToDefault()
    }
  }

  fun openSettings() {
    _uiState.update { it.copy(isSettingsOpen = true) }
    val prefs = _uiState.value.preferences
    if (_uiState.value.phase == TimerPhase.IDLE && prefs.ambientTrack != AmbientTrack.NONE) {
      audioEngine.previewBrief(prefs.ambientTrack, prefs.audioVolume)
    }
  }

  fun closeSettings() {
    _uiState.update { it.copy(isSettingsOpen = false) }
    syncAudioWithState()
  }

  fun setFocusMinutes(minutes: Int) {
    hasLocalMutation = true
    val clamped = minutes.coerceIn(1, 120)
    _uiState.update { current ->
      val updatedIdleMs = clamped * 60_000L
      current.copy(
        preferences = current.preferences.copy(focusMinutes = clamped),
        totalPhaseMillis = if (current.phase == TimerPhase.IDLE) updatedIdleMs else current.totalPhaseMillis,
        remainingMillis = if (current.phase == TimerPhase.IDLE) updatedIdleMs else current.remainingMillis,
      )
    }
    viewModelScope.launch(persistDispatcher) { repository.setFocusMinutes(clamped) }
  }

  fun setBreakMinutes(minutes: Int) {
    hasLocalMutation = true
    val clamped = minutes.coerceIn(1, 60)
    _uiState.update { current ->
      current.copy(preferences = current.preferences.copy(breakMinutes = clamped))
    }
    viewModelScope.launch(persistDispatcher) { repository.setBreakMinutes(clamped) }
  }

  fun setAutoCycle(enabled: Boolean) {
    hasLocalMutation = true
    _uiState.update { current ->
      current.copy(preferences = current.preferences.copy(autoCycle = enabled))
    }
    viewModelScope.launch(persistDispatcher) { repository.setAutoCycle(enabled) }
  }

  fun setPalette(palette: ThemePaletteId) {
    hasLocalMutation = true
    _uiState.update { current ->
      current.copy(preferences = current.preferences.copy(palette = palette))
    }
    viewModelScope.launch(persistDispatcher) { repository.setPalette(palette) }
  }

  fun setAmbientTrack(track: AmbientTrack) {
    hasLocalMutation = true
    _uiState.update { current ->
      current.copy(preferences = current.preferences.copy(ambientTrack = track))
    }
    val prefs = _uiState.value.preferences
    if (_uiState.value.isSettingsOpen) {
      audioEngine.previewBrief(track, prefs.audioVolume)
    }
    viewModelScope.launch(persistDispatcher) { repository.setAmbientTrack(track) }
  }

  fun setTransitionChime(chime: com.example.model.TransitionChime) {
    hasLocalMutation = true
    _uiState.update { current ->
      current.copy(preferences = current.preferences.copy(transitionChime = chime))
    }
    if (_uiState.value.isSettingsOpen && chime != com.example.model.TransitionChime.NONE) {
      audioEngine.previewTransitionChime(chime)
    }
    viewModelScope.launch(persistDispatcher) { repository.setTransitionChime(chime) }
  }

  fun setWindEventsEnabled(enabled: Boolean) {
    hasLocalMutation = true
    _uiState.update { current ->
      current.copy(preferences = current.preferences.copy(windEventsEnabled = enabled))
    }
    viewModelScope.launch(persistDispatcher) {
      repository.setWindEventsEnabled(enabled)
    }
  }

  fun setLanguage(language: com.example.model.AppLanguage) {
    hasLocalMutation = true
    _uiState.update { current ->
      current.copy(preferences = current.preferences.copy(language = language))
    }
    viewModelScope.launch(persistDispatcher) { repository.setLanguage(language) }
  }

  private fun startFocusPhase() {
    val focusMs = _uiState.value.preferences.focusMinutes * 60_000L
    phaseEndElapsedMs = SystemClock.elapsedRealtime() + focusMs
    _uiState.update {
      it.copy(
        phase = TimerPhase.FOCUS,
        totalPhaseMillis = focusMs,
        remainingMillis = focusMs,
      )
    }
    syncAudioWithState()
    startTicker()
  }

  private fun startBreakPhase() {
    val breakMs = _uiState.value.preferences.breakMinutes * 60_000L
    phaseEndElapsedMs = SystemClock.elapsedRealtime() + breakMs
    _uiState.update {
      it.copy(
        phase = TimerPhase.BREAK,
        totalPhaseMillis = breakMs,
        remainingMillis = breakMs,
        completedCycles = it.completedCycles + 1,
      )
    }
    syncAudioWithState()
    startTicker()
  }

  private fun resetToDefault() {
    tickerJob?.cancel()
    tickerJob = null
    val focusMs = _uiState.value.preferences.focusMinutes * 60_000L
    _uiState.update {
      it.copy(
        phase = TimerPhase.IDLE,
        totalPhaseMillis = focusMs,
        remainingMillis = focusMs,
      )
    }
    syncAudioWithState()
  }

  private fun startTicker() {
    tickerJob?.cancel()
    tickerJob =
      viewModelScope.launch {
        while (isActive) {
          val now = SystemClock.elapsedRealtime()
          val left = (phaseEndElapsedMs - now).coerceAtLeast(0L)
          _uiState.update { it.copy(remainingMillis = left) }

          if (left <= 0L) {
            val chime = _uiState.value.preferences.transitionChime
            val autoCycle = _uiState.value.preferences.autoCycle
            when (_uiState.value.phase) {
              TimerPhase.FOCUS -> {
                audioEngine.playTransitionChime(chime, isBreakStarting = true)
                startBreakPhase()
              }
              TimerPhase.BREAK -> {
                audioEngine.playTransitionChime(chime, isBreakStarting = false)
                if (autoCycle) {
                  startFocusPhase()
                } else {
                  resetToDefault()
                }
              }
              TimerPhase.IDLE -> break
            }
            break
          }
          // 50ms cadence keeps ring progress & seconds readout butter-smooth with minimal CPU cost
          delay(50L)
        }
      }
  }

  private fun syncAudioWithState() {
    val state = _uiState.value
    val prefs = state.preferences
    val shouldPlay =
      when {
        state.phase == TimerPhase.FOCUS -> true
        state.phase == TimerPhase.BREAK -> true
        state.isSettingsOpen && prefs.ambientTrack != AmbientTrack.NONE -> true
        else -> false
      }
    val volumeMultiplier = if (state.phase == TimerPhase.BREAK) 0.45f else 1.0f
    audioEngine.startOrUpdate(
      track = prefs.ambientTrack,
      volume = (prefs.audioVolume * volumeMultiplier).coerceIn(0f, 1f),
      shouldPlay = shouldPlay,
    )
  }

  override fun onCleared() {
    super.onCleared()
    tickerJob?.cancel()
    audioEngine.release()
  }
}
