package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.AmbientTrack
import com.example.model.AppLanguage
import com.example.model.ThemePaletteId
import com.example.model.TimerPhase
import com.example.model.TransitionChime
import com.example.ui.components.PetalSimulationEngine
import com.example.ui.phaseCanonicalName
import com.example.ui.theme.paletteFor
import com.example.viewmodel.FlowspanViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Flowspan", appName)
  }

  @Test
  fun `pomodoro state machine enforces focus lock and idle transition`() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = FlowspanViewModel(app)

    val initial = viewModel.uiState.value
    assertEquals(TimerPhase.IDLE, initial.phase)
    assertEquals(25 * 60_000L, initial.remainingMillis)

    viewModel.onPrimaryLeftAction()
    assertEquals(TimerPhase.FOCUS, viewModel.uiState.value.phase)

    viewModel.onPrimaryLeftAction()
    assertEquals(TimerPhase.FOCUS, viewModel.uiState.value.phase)

    viewModel.openSettings()
    assertTrue(viewModel.uiState.value.isSettingsOpen)
    viewModel.closeSettings()
    assertFalse(viewModel.uiState.value.isSettingsOpen)
  }

  @Test
  fun `unified phase canonical names use ready focus break without duration suffix`() {
    assertEquals("准备", phaseCanonicalName(TimerPhase.IDLE, AppLanguage.ZH_CN))
    assertEquals("Ready", phaseCanonicalName(TimerPhase.IDLE, AppLanguage.EN))
    assertEquals("集中", phaseCanonicalName(TimerPhase.FOCUS, AppLanguage.JA))
    assertEquals("집중", phaseCanonicalName(TimerPhase.FOCUS, AppLanguage.KO))
    assertEquals("Pause", phaseCanonicalName(TimerPhase.BREAK, AppLanguage.FR))
    assertEquals("Descanso", phaseCanonicalName(TimerPhase.BREAK, AppLanguage.ES))
  }

  @Test
  fun `all twelve palettes including new washes have distinct tinted paper and deep ring accents`() {
    assertEquals(12, ThemePaletteId.entries.size)
    assertEquals("枫叶", ThemePaletteId.RED.label(AppLanguage.ZH_CN))
    assertEquals("Maple", ThemePaletteId.RED.label(AppLanguage.EN))
    assertEquals("楓", ThemePaletteId.RED.label(AppLanguage.JA))
    assertEquals("松烟", ThemePaletteId.PINE_SOOT.label(AppLanguage.ZH_CN))
    assertEquals("暮霞", ThemePaletteId.ALPENGLOW.label(AppLanguage.ZH_CN))
    assertEquals("金桂", ThemePaletteId.OSMANTHUS.label(AppLanguage.ZH_CN))
    assertEquals("琉璃", ThemePaletteId.SEA_GLASS.label(AppLanguage.ZH_CN))
    assertEquals("海雾", ThemePaletteId.SEA_MIST.label(AppLanguage.ZH_CN))

    val backgrounds = ThemePaletteId.entries.map { paletteFor(it).paperBackground }.toSet()
    assertEquals(12, backgrounds.size)
    val splashThemes =
      ThemePaletteId.entries.map { com.example.ui.theme.splashThemeResFor(it) }.toSet()
    assertEquals(12, splashThemes.size)
    assertEquals(ThemePaletteId.RED, ThemePaletteId.fromKey(null))
    ThemePaletteId.entries.forEach { id ->
      val p = paletteFor(id)
      assertNotEquals(p.paperBackground, p.paperGradientBottom)
      assertNotEquals(p.paperBackground, p.accent)
      assertNotEquals(p.meshBase, p.meshBlob1)
      assertNotEquals(p.meshBase, p.meshBlob2)
      assertNotEquals(p.meshBlob1, p.meshBlob3)
      assertEquals(4, p.petalColors.size)
      assertTrue(p.petalColors.all { it.alpha == 1f })
    }
  }

  @Test
  fun `selected palette and language persist immediately for next cold start`() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val repo = com.example.data.PreferencesRepository(app)
    kotlinx.coroutines.runBlocking {
      repo.setPalette(ThemePaletteId.SEA_GLASS)
      repo.setLanguage(AppLanguage.EN)
      repo.setFocusMinutes(40)
    }

    val freshViewModel = FlowspanViewModel(app)
    val state = freshViewModel.uiState.value
    assertEquals(ThemePaletteId.SEA_GLASS, state.preferences.palette)
    assertEquals(AppLanguage.EN, state.preferences.language)
    assertEquals(40, state.preferences.focusMinutes)
    assertEquals(40 * 60_000L, state.remainingMillis)
  }

  @Test
  fun `aerodynamic petal simulation produces finite trajectories across wind and touch steps`() {
    val engine = PetalSimulationEngine(dpToPx = 2.0f)
    engine.ensureDimensions(width = 1080f, height = 2200f)
    assertTrue(engine.initialized)

    engine.triggerTapBreeze(540f, 900f)
    engine.triggerDragBreeze(580f, 880f, 35f, -15f)

    // Step through 10 seconds of simulated curl noise + gusts + falling-plate ODEs
    repeat(600) {
      engine.step(0.016f, windEventsEnabled = true)
    }
    assertTrue(engine.initialized)
    for (i in 0 until engine.poolSize) {
      val p = engine.particleAt(i)
      assertFalse(p.x.isNaN())
      assertFalse(p.y.isNaN())
      assertFalse(p.vx.isNaN())
      assertFalse(p.vy.isNaN())
      assertFalse(p.theta.isNaN())
    }

    // Toggling wind off and back on must preserve initialization
    engine.step(0.016f, windEventsEnabled = false)
    assertTrue(engine.initialized)
    engine.step(0.016f, windEventsEnabled = true)
    assertTrue(engine.initialized)
  }

  @Test
  fun `curated world language options cover all 8 target cultures`() {
    assertEquals(8, AppLanguage.entries.size)
    assertEquals(AppLanguage.ZH_CN, AppLanguage.fromKey("zh"))
    assertEquals(AppLanguage.ZH_TW, AppLanguage.fromKey("zh_tw"))
    assertEquals(AppLanguage.EN, AppLanguage.fromKey("en"))
    assertEquals(AppLanguage.JA, AppLanguage.fromKey("ja"))
    assertEquals(AppLanguage.KO, AppLanguage.fromKey("ko"))
    assertEquals(AppLanguage.FR, AppLanguage.fromKey("fr"))
    assertEquals(AppLanguage.DE, AppLanguage.fromKey("de"))
    assertEquals(AppLanguage.ES, AppLanguage.fromKey("es"))
  }

  @Test
  fun `built-in ambient tracks include pink noise and switch reliably`() {
    assertEquals(6, AmbientTrack.entries.size)
    assertEquals(
      AmbientTrack.NONE,
      AmbientTrack.fromKey("custom_file"),
    )

    val app = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = FlowspanViewModel(app)
    viewModel.openSettings()

    viewModel.setAmbientTrack(AmbientTrack.PINK_NOISE)
    assertEquals(
      AmbientTrack.PINK_NOISE,
      viewModel.uiState.value.preferences.ambientTrack,
    )

    viewModel.setAmbientTrack(AmbientTrack.WARM_STUDY)
    assertEquals(
      AmbientTrack.WARM_STUDY,
      viewModel.uiState.value.preferences.ambientTrack,
    )

    viewModel.setAmbientTrack(AmbientTrack.SOFT_RAIN)
    assertEquals(
      AmbientTrack.SOFT_RAIN,
      viewModel.uiState.value.preferences.ambientTrack,
    )

    viewModel.setAmbientTrack(AmbientTrack.SINGING_BOWL)
    assertEquals(
      AmbientTrack.SINGING_BOWL,
      viewModel.uiState.value.preferences.ambientTrack,
    )

    viewModel.setAmbientTrack(AmbientTrack.NONE)
    assertEquals(
      AmbientTrack.NONE,
      viewModel.uiState.value.preferences.ambientTrack,
    )
    viewModel.closeSettings()
  }

  @Test
  fun `transition chime options cover bell woodblock droplet harmonic and none`() {
    assertEquals(5, TransitionChime.entries.size)
    assertEquals(TransitionChime.BELL, TransitionChime.fromKey(null))

    val app = ApplicationProvider.getApplicationContext<Application>()
    val repo = com.example.data.PreferencesRepository(app)
    kotlinx.coroutines.runBlocking {
      repo.setTransitionChime(TransitionChime.WOODBLOCK)
    }

    val viewModel = FlowspanViewModel(app)
    assertEquals(
      TransitionChime.WOODBLOCK,
      viewModel.uiState.value.preferences.transitionChime,
    )

    viewModel.setTransitionChime(TransitionChime.DROPLET)
    assertEquals(
      TransitionChime.DROPLET,
      viewModel.uiState.value.preferences.transitionChime,
    )
  }

  @Test
  fun `autoCycle option can be toggled and persisted`() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val repo = com.example.data.PreferencesRepository(app)
    kotlinx.coroutines.runBlocking {
      repo.setAutoCycle(true)
    }
    assertEquals(true, repo.readInitialSnapshot().autoCycle)

    val viewModel = FlowspanViewModel(app)
    assertTrue(viewModel.uiState.value.preferences.autoCycle)

    viewModel.setAutoCycle(false)
    assertFalse(viewModel.uiState.value.preferences.autoCycle)
  }
}
