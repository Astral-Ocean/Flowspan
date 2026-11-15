package com.example.ui.theme

import androidx.annotation.StyleRes
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.ThemePaletteId

val LocalFlowspanPalette = staticCompositionLocalOf { paletteFor(ThemePaletteId.RED) }

val FlowspanShapes =
  Shapes(
    small = RoundedCornerShape(15.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(18.dp),
  )

@StyleRes
fun splashThemeResFor(paletteId: ThemePaletteId): Int =
  when (paletteId) {
    ThemePaletteId.PINK -> R.style.Theme_Flowspan_Pink
    ThemePaletteId.RED -> R.style.Theme_Flowspan_Red
    ThemePaletteId.GREEN -> R.style.Theme_Flowspan_Green
    ThemePaletteId.BLUE -> R.style.Theme_Flowspan_Blue
    ThemePaletteId.CYAN -> R.style.Theme_Flowspan_Cyan
    ThemePaletteId.YELLOW -> R.style.Theme_Flowspan_Yellow
    ThemePaletteId.PURPLE -> R.style.Theme_Flowspan_Purple
    ThemePaletteId.PINE_SOOT -> R.style.Theme_Flowspan_PineSoot
    ThemePaletteId.ALPENGLOW -> R.style.Theme_Flowspan_Alpenglow
    ThemePaletteId.OSMANTHUS -> R.style.Theme_Flowspan_Osmanthus
    ThemePaletteId.SEA_GLASS -> R.style.Theme_Flowspan_SeaGlass
    ThemePaletteId.SEA_MIST -> R.style.Theme_Flowspan_SeaMist
  }

@Composable
fun FlowspanTheme(
  paletteId: ThemePaletteId = ThemePaletteId.RED,
  content: @Composable () -> Unit,
) {
  val palette = paletteFor(paletteId)

  val colorScheme =
    lightColorScheme(
      primary = palette.accent,
      onPrimary = palette.paperBackground,
      secondary = palette.accentBreak,
      onSecondary = palette.paperBackground,
      background = palette.paperBackground,
      onBackground = palette.inkPrimary,
      surface = palette.sheetSurface,
      onSurface = palette.inkPrimary,
      surfaceVariant = palette.paperGradientBottom,
      onSurfaceVariant = palette.inkSecondary,
      outline = palette.hairlineDivider,
    )

  CompositionLocalProvider(LocalFlowspanPalette provides palette) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = FlowspanTypography,
      shapes = FlowspanShapes,
      content = content,
    )
  }
}
