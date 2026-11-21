package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AmbientTrack
import com.example.model.AppLanguage
import com.example.model.ThemePaletteId
import com.example.model.TimerPhase
import com.example.model.TransitionChime
import com.example.model.UserPreferences
import com.example.ui.theme.LocalFlowspanPalette
import com.example.ui.theme.NewsreaderFontFamily
import com.example.ui.theme.editorialSerifFamily

private val UnifiedOuterRadius = 18.dp
private val UnifiedInnerRadius = 15.dp

/**
 * Full-screen iOS-inspired inset-grouped Settings page with right-to-left push navigation,
 * unified 18.dp / 15.dp corner geometry, auto-cycle toggle, curated multi-language support,
 * selectable ambient and transition chime tracks, author link, and zero grey ripple shadows.
 */
@Composable
fun SettingsScreen(
  isActive: Boolean,
  preferences: UserPreferences,
  currentPhase: TimerPhase,
  onDismiss: () -> Unit,
  onFocusMinutesChange: (Int) -> Unit,
  onBreakMinutesChange: (Int) -> Unit,
  onAutoCycleToggle: (Boolean) -> Unit,
  onPaletteSelect: (ThemePaletteId) -> Unit,
  onAmbientTrackSelect: (AmbientTrack) -> Unit,
  onTransitionChimeSelect: (TransitionChime) -> Unit,
  onLanguageSelect: (AppLanguage) -> Unit,
  modifier: Modifier = Modifier,
) {
  BackHandler(enabled = isActive, onBack = onDismiss)

  val context = LocalContext.current
  val palette = LocalFlowspanPalette.current
  val lang = preferences.language
  val serifFamily = editorialSerifFamily(lang)

  val paletteRows = remember { ThemePaletteId.entries.chunked(6) }
  val ambientTracks = remember { AmbientTrack.entries }
  val transitionChimes = remember { TransitionChime.entries }
  val languages = remember { AppLanguage.entries }

  val backdropColor by animateColorAsState(
    targetValue = lerp(palette.paperBackground, palette.paperGradientBottom, 0.34f),
    animationSpec = tween(220),
    label = "settings_backdrop",
  )
  val cardColor = Color.White.copy(alpha = 0.94f)

  Box(
    modifier =
      modifier
        .fillMaxSize()
        .background(backdropColor)
        .clickable(
          enabled = isActive,
          interactionSource = remember { MutableInteractionSource() },
          indication = null,
          onClick = {},
        )
        .windowInsetsPadding(
          WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
        ),
    contentAlignment = Alignment.TopCenter,
  ) {
    Column(
      modifier = Modifier.widthIn(max = 600.dp).fillMaxSize(),
    ) {
      // Minimal iOS-style top navigation bar with zero-ripple spring Back button
      Row(
        modifier =
          Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        val backInteraction = remember { MutableInteractionSource() }
        val backPressed by backInteraction.collectIsPressedAsState()
        val backScale by animateFloatAsState(
          targetValue = if (backPressed) 0.94f else 1f,
          animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMedium),
          label = "back_press_scale",
        )
        val backAlpha by animateFloatAsState(
          targetValue = if (backPressed) 0.52f else 1f,
          animationSpec = tween(90),
          label = "back_press_alpha",
        )

        Row(
          modifier =
            Modifier
              .graphicsLayer {
                scaleX = backScale
                scaleY = backScale
                alpha = backAlpha
              }
              .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
              .clip(RoundedCornerShape(UnifiedOuterRadius))
              .clickable(
                role = Role.Button,
                interactionSource = backInteraction,
                indication = null,
                onClick = onDismiss,
              )
              .padding(start = 6.dp, end = 14.dp, top = 8.dp, bottom = 8.dp)
              .testTag("settings_back_button"),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
            contentDescription = lang.back,
            tint = palette.accent,
            modifier = Modifier.size(24.dp),
          )
          Text(
            text = lang.back,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
            color = palette.accent,
          )
        }
      }

      Column(
        modifier =
          Modifier
            .weight(1f)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .navigationBarsPadding()
            .padding(bottom = 36.dp),
      ) {
        Text(
          text = lang.settingsTitle,
          style =
            MaterialTheme.typography.headlineLarge.copy(
              fontFamily = serifFamily,
              fontSize = 32.sp,
            ),
          color = palette.inkPrimary,
          modifier = Modifier.padding(top = 4.dp, bottom = 22.dp).testTag("settings_sheet_title"),
        )

        // Section 1: Timer Intervals & Auto Cycle
        IosSectionLabel(text = lang.sectionTimer)
        IosGroupedCard(backgroundColor = cardColor) {
          IosDurationRow(
            title = lang.focusDuration,
            unit = lang.minutesUnit,
            minutes = preferences.focusMinutes,
            minMinutes = 5,
            maxMinutes = 90,
            testTagPrefix = "focus_duration",
            onMinutesChange = onFocusMinutesChange,
          )
          IosRowDivider()
          IosDurationRow(
            title = lang.breakDuration,
            unit = lang.minutesUnit,
            minutes = preferences.breakMinutes,
            minMinutes = 1,
            maxMinutes = 30,
            testTagPrefix = "break_duration",
            onMinutesChange = onBreakMinutesChange,
          )
          IosRowDivider()
          IosSwitchRow(
            title = lang.autoCycle,
            subtitle = lang.autoCycleDesc,
            checked = preferences.autoCycle,
            testTag = "auto_cycle_toggle",
            onCheckedChange = onAutoCycleToggle,
          )
        }
        if (currentPhase != TimerPhase.IDLE) {
          Text(
            text = lang.durationNotice,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
            color = palette.inkSecondary,
            modifier = Modifier.padding(start = 16.dp, top = 8.dp),
          )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section 2: Theme Palette
        IosSectionLabel(text = lang.sectionAppearance)
        IosGroupedCard(backgroundColor = cardColor) {
          Row(
            modifier =
              Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 48.dp)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Text(
              text = lang.themeColor,
              style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.5.sp),
              color = palette.inkPrimary,
            )
            Text(
              text = preferences.palette.label(lang),
              style =
                MaterialTheme.typography.titleSmall.copy(
                  fontFamily = serifFamily,
                  fontSize = 15.sp,
                ),
              color = palette.accent,
              modifier = Modifier.testTag("selected_palette_label"),
            )
          }
          IosRowDivider()
          Column(
            modifier =
              Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
          ) {
            paletteRows.forEach { rowItems ->
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                rowItems.forEach { item ->
                  val selected = item == preferences.palette
                  val swatchInteraction = remember { MutableInteractionSource() }
                  val swatchPressed by swatchInteraction.collectIsPressedAsState()
                  val swatchScale by animateFloatAsState(
                    targetValue =
                      when {
                        swatchPressed -> 0.85f
                        selected -> 1.06f
                        else -> 1f
                      },
                    animationSpec =
                      spring(dampingRatio = 0.58f, stiffness = Spring.StiffnessMedium),
                    label = "swatch_scale_${item.storageKey}",
                  )

                  Box(
                    modifier =
                      Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 48.dp)
                        .clickable(
                          role = Role.RadioButton,
                          interactionSource = swatchInteraction,
                          indication = null,
                          onClick = { onPaletteSelect(item) },
                        )
                        .testTag("palette_swatch_${item.storageKey}"),
                    contentAlignment = Alignment.Center,
                  ) {
                    Box(
                      modifier =
                        Modifier
                          .size(36.dp)
                          .graphicsLayer {
                            scaleX = swatchScale
                            scaleY = swatchScale
                          }
                          .border(
                            width = if (selected) 2.dp else 0.dp,
                            color = if (selected) item.swatchColor else Color.Transparent,
                            shape = CircleShape,
                          ),
                      contentAlignment = Alignment.Center,
                    ) {
                      Box(
                        modifier =
                          Modifier
                            .size(if (selected) 24.dp else 26.dp)
                            .clip(CircleShape)
                            .background(item.swatchColor)
                            .border(
                              width = if (selected) 0.dp else 0.75.dp,
                              color =
                                if (selected) Color.Transparent
                                else Color.Black.copy(alpha = 0.12f),
                              shape = CircleShape,
                            )
                      )
                    }
                  }
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section 3: Ambient Soundscapes
        IosSectionLabel(text = lang.sectionAmbience)
        IosGroupedCard(backgroundColor = cardColor) {
          ambientTracks.forEachIndexed { idx, track ->
            val selected = track == preferences.ambientTrack
            val rowInteraction = remember { MutableInteractionSource() }
            val rowPressed by rowInteraction.collectIsPressedAsState()
            val rowHighlight by animateColorAsState(
              targetValue =
                if (rowPressed) palette.accent.copy(alpha = 0.055f) else Color.Transparent,
              animationSpec = tween(100),
              label = "track_row_highlight_${track.storageKey}",
            )

            Row(
              modifier =
                Modifier
                  .fillMaxWidth()
                  .background(rowHighlight)
                  .defaultMinSize(minHeight = 50.dp)
                  .clickable(
                    role = Role.RadioButton,
                    interactionSource = rowInteraction,
                    indication = null,
                  ) {
                    onAmbientTrackSelect(track)
                  }
                  .padding(horizontal = 16.dp, vertical = 13.dp)
                  .testTag("ambient_track_${track.storageKey}"),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                text = track.title(lang),
                style =
                  MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 15.5.sp,
                    fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                  ),
                color = palette.inkPrimary,
                modifier = Modifier.weight(1f),
              )

              if (selected) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                  imageVector = Icons.Rounded.Check,
                  contentDescription = null,
                  tint = palette.accent,
                  modifier = Modifier.size(18.dp),
                )
              }
            }

            if (idx < ambientTracks.lastIndex) {
              IosRowDivider()
            }
          }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section 4: Transition Chime
        IosSectionLabel(text = lang.sectionChime)
        IosGroupedCard(backgroundColor = cardColor) {
          transitionChimes.forEachIndexed { idx, chime ->
            val selected = chime == preferences.transitionChime
            val rowInteraction = remember { MutableInteractionSource() }
            val rowPressed by rowInteraction.collectIsPressedAsState()
            val rowHighlight by animateColorAsState(
              targetValue =
                if (rowPressed) palette.accent.copy(alpha = 0.055f) else Color.Transparent,
              animationSpec = tween(100),
              label = "chime_row_highlight_${chime.storageKey}",
            )

            Row(
              modifier =
                Modifier
                  .fillMaxWidth()
                  .background(rowHighlight)
                  .defaultMinSize(minHeight = 50.dp)
                  .clickable(
                    role = Role.RadioButton,
                    interactionSource = rowInteraction,
                    indication = null,
                  ) {
                    onTransitionChimeSelect(chime)
                  }
                  .padding(horizontal = 16.dp, vertical = 13.dp)
                  .testTag("transition_chime_${chime.storageKey}"),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                text = chime.title(lang),
                style =
                  MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 15.5.sp,
                    fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                  ),
                color = palette.inkPrimary,
                modifier = Modifier.weight(1f),
              )

              if (selected) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                  imageVector = Icons.Rounded.Check,
                  contentDescription = null,
                  tint = palette.accent,
                  modifier = Modifier.size(18.dp),
                )
              }
            }

            if (idx < transitionChimes.lastIndex) {
              IosRowDivider()
            }
          }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section 5: Preferences (Language)
        IosSectionLabel(text = lang.sectionLanguage)
        IosGroupedCard(backgroundColor = cardColor) {
          languages.forEachIndexed { idx, languageItem ->
            val selected = languageItem == preferences.language
            val rowInteraction = remember { MutableInteractionSource() }
            val rowPressed by rowInteraction.collectIsPressedAsState()
            val rowHighlight by animateColorAsState(
              targetValue =
                if (rowPressed) palette.accent.copy(alpha = 0.055f) else Color.Transparent,
              animationSpec = tween(100),
              label = "lang_row_highlight_${languageItem.storageKey}",
            )

            Row(
              modifier =
                Modifier
                  .fillMaxWidth()
                  .background(rowHighlight)
                  .defaultMinSize(minHeight = 50.dp)
                  .clickable(
                    role = Role.RadioButton,
                    interactionSource = rowInteraction,
                    indication = null,
                  ) {
                    onLanguageSelect(languageItem)
                  }
                  .padding(horizontal = 16.dp, vertical = 13.dp)
                  .testTag("language_option_${languageItem.storageKey}"),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                text = languageItem.nativeLabel,
                style =
                  MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 15.5.sp,
                    fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                  ),
                color = palette.inkPrimary,
                modifier = Modifier.weight(1f),
              )

              if (selected) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                  imageVector = Icons.Rounded.Check,
                  contentDescription = null,
                  tint = palette.accent,
                  modifier = Modifier.size(18.dp),
                )
              }
            }

            if (idx < languages.lastIndex) {
              IosRowDivider()
            }
          }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section 6: About
        IosSectionLabel(text = lang.sectionAbout)
        IosGroupedCard(backgroundColor = cardColor) {
          val aboutInteraction = remember { MutableInteractionSource() }
          val aboutPressed by aboutInteraction.collectIsPressedAsState()
          val aboutHighlight by animateColorAsState(
            targetValue =
              if (aboutPressed) palette.accent.copy(alpha = 0.055f) else Color.Transparent,
            animationSpec = tween(100),
            label = "about_row_highlight",
          )

          Row(
            modifier =
              Modifier
                .fillMaxWidth()
                .background(aboutHighlight)
                .defaultMinSize(minHeight = 56.dp)
                .clickable(
                  interactionSource = aboutInteraction,
                  indication = null,
                ) {
                  try {
                    val url = "https://astralocean.rth1.xyz"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                  } catch (_: Exception) {}
                }
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .testTag("about_me_row"),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
              Text(
                text = lang.aboutMe,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.5.sp),
                color = palette.inkPrimary,
              )
              Text(
                text = "astralocean.rth1.xyz",
                style =
                  MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 12.sp,
                    color = palette.accent,
                  ),
              )
            }

            Icon(
              imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
              contentDescription = lang.openPersonalWebsite,
              tint = palette.accent,
              modifier = Modifier.size(18.dp),
            )
          }

          IosRowDivider()

          Row(
            modifier =
              Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 48.dp)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Text(
              text = lang.versionLabel,
              style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.5.sp),
              color = palette.inkPrimary,
            )
            Text(
              text = com.example.BuildConfig.VERSION_NAME,
              style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
              color = palette.inkSecondary,
            )
          }
        }
      }
    }
  }
}

/**
 * Mathematically symmetric iOS toggle switch row:
 * Width: 51.dp, Height: 31.dp, Outer padding: 2.dp.
 * Thumb diameter: 27.dp.
 * When OFF: 2.dp from left, 2.dp from top, 2.dp from bottom, 2.dp margin preserved.
 * When ON: 2.dp from right, 2.dp from top, 2.dp from bottom.
 * 100% symmetric geometry, spring physics, zero grey shadow.
 */
@Composable
private fun IosSwitchRow(
  title: String,
  subtitle: String?,
  checked: Boolean,
  testTag: String,
  onCheckedChange: (Boolean) -> Unit,
) {
  val palette = LocalFlowspanPalette.current
  val interactionSource = remember { MutableInteractionSource() }

  val switchWidth = 51.dp
  val switchHeight = 31.dp
  val padding = 2.dp
  val thumbSize = switchHeight - padding * 2 // 27.dp
  val maxOffset = switchWidth - padding - thumbSize // 22.dp

  val thumbOffset by animateDpAsState(
    targetValue = if (checked) maxOffset else padding,
    animationSpec = spring(dampingRatio = 0.82f, stiffness = Spring.StiffnessMediumLow),
    label = "switch_thumb_offset",
  )

  val trackColor by animateColorAsState(
    targetValue = if (checked) palette.accent else palette.hairlineDivider.copy(alpha = 0.85f),
    animationSpec = tween(160),
    label = "switch_track_color",
  )

  Row(
    modifier =
      Modifier
        .fillMaxWidth()
        .defaultMinSize(minHeight = 56.dp)
        .clickable(
          role = Role.Switch,
          interactionSource = interactionSource,
          indication = null,
          onClick = { onCheckedChange(!checked) },
        )
        .padding(horizontal = 16.dp, vertical = 12.dp)
        .testTag(testTag),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
      Text(
        text = title,
        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.5.sp),
        color = palette.inkPrimary,
      )
      if (!subtitle.isNullOrBlank()) {
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
          color = palette.inkSecondary,
        )
      }
    }

    Box(
      modifier =
        Modifier
          .size(width = switchWidth, height = switchHeight)
          .clip(CircleShape)
          .background(trackColor),
      contentAlignment = Alignment.CenterStart,
    ) {
      Box(
        modifier =
          Modifier
            .offset(x = thumbOffset)
            .size(thumbSize)
            .clip(CircleShape)
            .background(Color.White)
      )
    }
  }
}

@Composable
private fun IosSectionLabel(text: String) {
  val palette = LocalFlowspanPalette.current
  Text(
    text = text,
    style =
      MaterialTheme.typography.labelSmall.copy(
        fontSize = 12.sp,
        letterSpacing = 0.6.sp,
      ),
    color = palette.inkSecondary,
    modifier = Modifier.padding(start = 16.dp, bottom = 7.dp),
  )
}

@Composable
private fun IosGroupedCard(
  backgroundColor: Color,
  content: @Composable () -> Unit,
) {
  Column(
    modifier =
      Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(UnifiedOuterRadius))
        .background(backgroundColor),
  ) {
    content()
  }
}

@Composable
private fun IosRowDivider() {
  val palette = LocalFlowspanPalette.current
  HorizontalDivider(
    color = palette.hairlineDivider.copy(alpha = 0.65f),
    thickness = 0.5.dp,
    modifier = Modifier.padding(start = 16.dp),
  )
}

@Composable
private fun IosDurationRow(
  title: String,
  unit: String,
  minutes: Int,
  minMinutes: Int,
  maxMinutes: Int,
  testTagPrefix: String,
  onMinutesChange: (Int) -> Unit,
) {
  val palette = LocalFlowspanPalette.current

  Row(
    modifier =
      Modifier
        .fillMaxWidth()
        .defaultMinSize(minHeight = 56.dp)
        .padding(start = 16.dp, end = 12.dp, top = 6.dp, bottom = 6.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      text = title,
      style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.5.sp),
      color = palette.inkPrimary,
      modifier = Modifier.weight(1f),
    )

    Row(
      horizontalArrangement = Arrangement.spacedBy(4.dp),
      modifier = Modifier.padding(end = 12.dp),
    ) {
      Text(
        text = minutes.toString(),
        style =
          MaterialTheme.typography.bodyLarge.copy(
            fontFamily = NewsreaderFontFamily,
            fontFeatureSettings = "lnum, tnum",
            fontSize = 18.sp,
          ),
        color = palette.inkPrimary,
        modifier = Modifier.alignByBaseline().testTag("${testTagPrefix}_value"),
      )
      Text(
        text = unit,
        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
        color = palette.inkSecondary,
        modifier = Modifier.alignByBaseline(),
      )
    }

    val currentMinutes by rememberUpdatedState(minutes)
    val currentOnChange by rememberUpdatedState(onMinutesChange)

    Row(
      modifier =
        Modifier
          .clip(RoundedCornerShape(UnifiedOuterRadius))
          .background(palette.paperBackground.copy(alpha = 0.92f)),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      RepeatableStepperButton(
        icon = Icons.Rounded.Remove,
        contentDescription = "-",
        enabled = minutes > minMinutes,
        testTag = "${testTagPrefix}_minus",
        onStep = { currentOnChange((currentMinutes - 1).coerceAtLeast(minMinutes)) },
      )
      Box(
        modifier =
          Modifier
            .width(0.75.dp)
            .height(18.dp)
            .background(palette.hairlineDivider)
      )
      RepeatableStepperButton(
        icon = Icons.Rounded.Add,
        contentDescription = "+",
        enabled = minutes < maxMinutes,
        testTag = "${testTagPrefix}_plus",
        onStep = { currentOnChange((currentMinutes + 1).coerceAtMost(maxMinutes)) },
      )
    }
  }
}

@Composable
private fun RepeatableStepperButton(
  icon: ImageVector,
  contentDescription: String,
  enabled: Boolean,
  testTag: String,
  onStep: () -> Unit,
) {
  val palette = LocalFlowspanPalette.current
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val currentOnStep by rememberUpdatedState(onStep)
  var didAutoRepeat by remember { mutableStateOf(false) }

  val pressScale by animateFloatAsState(
    targetValue = if (isPressed && enabled) 0.88f else 1f,
    animationSpec = spring(dampingRatio = 0.62f, stiffness = Spring.StiffnessMedium),
    label = "stepper_press_scale",
  )

  val pressTint by animateColorAsState(
    targetValue = if (isPressed && enabled) palette.accent.copy(alpha = 0.08f) else Color.Transparent,
    animationSpec = tween(90),
    label = "stepper_press_tint",
  )

  LaunchedEffect(isPressed, enabled) {
    if (isPressed && enabled) {
      didAutoRepeat = false
      kotlinx.coroutines.delay(380L)
      var intervalMs = 115L
      while (true) {
        didAutoRepeat = true
        currentOnStep()
        kotlinx.coroutines.delay(intervalMs)
        intervalMs = (intervalMs - 10L).coerceAtLeast(55L)
      }
    }
  }

  Box(
    modifier =
      Modifier
        .size(width = 48.dp, height = 48.dp)
        .background(pressTint)
        .clickable(
          enabled = enabled,
          role = Role.Button,
          interactionSource = interactionSource,
          indication = null,
        ) {
          if (!didAutoRepeat) {
            currentOnStep()
          }
          didAutoRepeat = false
        }
        .testTag(testTag),
    contentAlignment = Alignment.Center,
  ) {
    Icon(
      imageVector = icon,
      contentDescription = contentDescription,
      tint = if (enabled) palette.inkPrimary else palette.inkMuted,
      modifier =
        Modifier
          .size(17.dp)
          .graphicsLayer {
            scaleX = pressScale
            scaleY = pressScale
          },
    )
  }
}
