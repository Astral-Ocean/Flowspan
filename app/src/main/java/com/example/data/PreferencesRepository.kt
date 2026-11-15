package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.model.AmbientTrack
import com.example.model.AppLanguage
import com.example.model.ThemePaletteId
import com.example.model.TransitionChime
import com.example.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "flowspan_prefs")

class PreferencesRepository(private val context: Context) {

  private val syncPrefs =
    context.getSharedPreferences("flowspan_prefs_sync", Context.MODE_PRIVATE)

  private object Keys {
    val FOCUS_MINUTES = intPreferencesKey("focus_minutes")
    val BREAK_MINUTES = intPreferencesKey("break_minutes")
    val AUTO_CYCLE = booleanPreferencesKey("auto_cycle")
    val PALETTE_ID = stringPreferencesKey("palette_id")
    val AMBIENT_TRACK = stringPreferencesKey("ambient_track")
    val TRANSITION_CHIME = stringPreferencesKey("transition_chime")
    val AUDIO_VOLUME = floatPreferencesKey("audio_volume")
    val WIND_EVENTS_ENABLED = booleanPreferencesKey("wind_events_enabled")
    val APP_LANGUAGE = stringPreferencesKey("app_language")
  }

  fun readInitialSnapshot(): UserPreferences =
    UserPreferences(
      focusMinutes = syncPrefs.getInt("focus_minutes", 25).coerceIn(1, 120),
      breakMinutes = syncPrefs.getInt("break_minutes", 5).coerceIn(1, 60),
      autoCycle = syncPrefs.getBoolean("auto_cycle", false),
      palette = ThemePaletteId.fromKey(syncPrefs.getString("palette_id", null)),
      ambientTrack = AmbientTrack.fromKey(syncPrefs.getString("ambient_track", null)),
      transitionChime = TransitionChime.fromKey(syncPrefs.getString("transition_chime", null)),
      audioVolume = syncPrefs.getFloat("audio_volume", 0.65f).coerceIn(0f, 1f),
      windEventsEnabled = syncPrefs.getBoolean("wind_events_enabled", true),
      language = AppLanguage.fromKey(syncPrefs.getString("app_language", null)),
    )

  val preferencesFlow: Flow<UserPreferences> =
    context.dataStore.data.map { prefs ->
      val resolved =
        UserPreferences(
          focusMinutes = (prefs[Keys.FOCUS_MINUTES] ?: syncPrefs.getInt("focus_minutes", 25)).coerceIn(1, 120),
          breakMinutes = (prefs[Keys.BREAK_MINUTES] ?: syncPrefs.getInt("break_minutes", 5)).coerceIn(1, 60),
          autoCycle = prefs[Keys.AUTO_CYCLE] ?: syncPrefs.getBoolean("auto_cycle", false),
          palette = ThemePaletteId.fromKey(prefs[Keys.PALETTE_ID] ?: syncPrefs.getString("palette_id", null)),
          ambientTrack = AmbientTrack.fromKey(prefs[Keys.AMBIENT_TRACK] ?: syncPrefs.getString("ambient_track", null)),
          transitionChime = TransitionChime.fromKey(prefs[Keys.TRANSITION_CHIME] ?: syncPrefs.getString("transition_chime", null)),
          audioVolume = (prefs[Keys.AUDIO_VOLUME] ?: syncPrefs.getFloat("audio_volume", 0.65f)).coerceIn(0f, 1f),
          windEventsEnabled = prefs[Keys.WIND_EVENTS_ENABLED] ?: syncPrefs.getBoolean("wind_events_enabled", true),
          language = AppLanguage.fromKey(prefs[Keys.APP_LANGUAGE] ?: syncPrefs.getString("app_language", null)),
        )
      mirrorToSync(resolved)
      resolved
    }

  private fun mirrorToSync(prefs: UserPreferences) {
    syncPrefs
      .edit()
      .putInt("focus_minutes", prefs.focusMinutes)
      .putInt("break_minutes", prefs.breakMinutes)
      .putBoolean("auto_cycle", prefs.autoCycle)
      .putString("palette_id", prefs.palette.storageKey)
      .putString("ambient_track", prefs.ambientTrack.storageKey)
      .putString("transition_chime", prefs.transitionChime.storageKey)
      .putFloat("audio_volume", prefs.audioVolume)
      .putBoolean("wind_events_enabled", prefs.windEventsEnabled)
      .putString("app_language", prefs.language.storageKey)
      .apply()
  }

  suspend fun setFocusMinutes(minutes: Int) {
    val clamped = minutes.coerceIn(1, 120)
    syncPrefs.edit().putInt("focus_minutes", clamped).apply()
    context.dataStore.edit { it[Keys.FOCUS_MINUTES] = clamped }
  }

  suspend fun setBreakMinutes(minutes: Int) {
    val clamped = minutes.coerceIn(1, 60)
    syncPrefs.edit().putInt("break_minutes", clamped).apply()
    context.dataStore.edit { it[Keys.BREAK_MINUTES] = clamped }
  }

  suspend fun setAutoCycle(enabled: Boolean) {
    syncPrefs.edit().putBoolean("auto_cycle", enabled).apply()
    context.dataStore.edit { it[Keys.AUTO_CYCLE] = enabled }
  }

  suspend fun setPalette(palette: ThemePaletteId) {
    syncPrefs.edit().putString("palette_id", palette.storageKey).apply()
    context.dataStore.edit { it[Keys.PALETTE_ID] = palette.storageKey }
  }

  suspend fun setAmbientTrack(track: AmbientTrack) {
    syncPrefs.edit().putString("ambient_track", track.storageKey).apply()
    context.dataStore.edit { it[Keys.AMBIENT_TRACK] = track.storageKey }
  }

  suspend fun setTransitionChime(chime: TransitionChime) {
    syncPrefs.edit().putString("transition_chime", chime.storageKey).apply()
    context.dataStore.edit { it[Keys.TRANSITION_CHIME] = chime.storageKey }
  }

  suspend fun setAudioVolume(volume: Float) {
    val clamped = volume.coerceIn(0f, 1f)
    syncPrefs.edit().putFloat("audio_volume", clamped).apply()
    context.dataStore.edit { it[Keys.AUDIO_VOLUME] = clamped }
  }

  suspend fun setWindEventsEnabled(enabled: Boolean) {
    syncPrefs.edit().putBoolean("wind_events_enabled", enabled).apply()
    context.dataStore.edit { it[Keys.WIND_EVENTS_ENABLED] = enabled }
  }

  suspend fun setLanguage(language: AppLanguage) {
    syncPrefs.edit().putString("app_language", language.storageKey).apply()
    context.dataStore.edit { it[Keys.APP_LANGUAGE] = language.storageKey }
  }
}
