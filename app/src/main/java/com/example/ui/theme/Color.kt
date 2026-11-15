package com.example.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.example.model.ThemePaletteId

@Immutable
data class FlowspanPalette(
  val id: ThemePaletteId,
  val paperBackground: Color,
  val paperGradientBottom: Color,
  val meshBase: Color,
  val meshBlob1: Color,
  val meshBlob2: Color,
  val meshBlob3: Color,
  val sheetSurface: Color,
  val inkPrimary: Color,
  val inkSecondary: Color,
  val inkMuted: Color,
  val accent: Color,
  val accentBreak: Color,
  val ringTrack: Color,
  val hairlineDivider: Color,
  val frostSurface: Color,
  val frostHighlight: Color,
  val frostBorder: Color,
  val petalColors: List<Color>,
)

/**
 * Curated publication palettes:
 * - Full-bleed moving gradient washes bathe the entire screen in recognizable light theme tint.
 * - Timer ring uses a noticeably deeper, richer shade of the same theme color.
 * - Petals use four opaque harmonious tones that remain vivid over both paper and ring.
 */
fun paletteFor(id: ThemePaletteId): FlowspanPalette =
  when (id) {
    ThemePaletteId.PINK ->
      FlowspanPalette(
        id = id,
        paperBackground = Color(0xFFFDF0F3),
        paperGradientBottom = Color(0xFFF4D2DC),
        meshBase = Color(0xFFFAE3EA),
        meshBlob1 = Color(0xFFFFF1F5),
        meshBlob2 = Color(0xFFF2BFD0),
        meshBlob3 = Color(0xFFEBB4C8),
        sheetSurface = Color(0xFFFEF5F7),
        inkPrimary = Color(0xFF28141A),
        inkSecondary = Color(0xFF6D4753),
        inkMuted = Color(0xFFA07C87),
        accent = Color(0xFF9C2B4B),
        accentBreak = Color(0xFF7D4859),
        ringTrack = Color(0xFFE7BDC9),
        hairlineDivider = Color(0xFFECD2DA),
        frostSurface = Color(0x8CFEF2F5),
        frostHighlight = Color(0x99FFFFFF),
        frostBorder = Color(0x2228141A),
        petalColors = listOf(
          Color(0xFFDF6889),
          Color(0xFFC23E63),
          Color(0xFFEE99B1),
          Color(0xFFD05276),
        ),
      )

    // Autumn Maple (枫叶): warm maple-tinted ivory paper, deep crimson maple ring, scarlet-amber maple petals
    ThemePaletteId.RED ->
      FlowspanPalette(
        id = id,
        paperBackground = Color(0xFFFDF0EA),
        paperGradientBottom = Color(0xFFF5D2C1),
        meshBase = Color(0xFFF9E1D6),
        meshBlob1 = Color(0xFFFCEFE6),
        meshBlob2 = Color(0xFFF2C0AA),
        meshBlob3 = Color(0xFFEDB29E),
        sheetSurface = Color(0xFFFEF6F2),
        inkPrimary = Color(0xFF2A120C),
        inkSecondary = Color(0xFF703D31),
        inkMuted = Color(0xFFA17064),
        accent = Color(0xFFA42817),
        accentBreak = Color(0xFF7F4231),
        ringTrack = Color(0xFFE8C0B0),
        hairlineDivider = Color(0xFFEED3C8),
        frostSurface = Color(0x8CFEF3EE),
        frostHighlight = Color(0x99FFFFFF),
        frostBorder = Color(0x222A120C),
        petalColors = listOf(
          Color(0xFFD64227),
          Color(0xFFB02B17),
          Color(0xFFEB6F3B),
          Color(0xFFC73822),
        ),
      )

    ThemePaletteId.GREEN ->
      FlowspanPalette(
        id = id,
        paperBackground = Color(0xFFEBF5EE),
        paperGradientBottom = Color(0xFFCEE4D5),
        meshBase = Color(0xFFDCF0E2),
        meshBlob1 = Color(0xFFEFF8F1),
        meshBlob2 = Color(0xFFBBE0C6),
        meshBlob3 = Color(0xFFAED7BC),
        sheetSurface = Color(0xFFF3F9F5),
        inkPrimary = Color(0xFF112118),
        inkSecondary = Color(0xFF42634F),
        inkMuted = Color(0xFF759481),
        accent = Color(0xFF1D5A39),
        accentBreak = Color(0xFF446E57),
        ringTrack = Color(0xFFB5D3BE),
        hairlineDivider = Color(0xFFCDE1D3),
        frostSurface = Color(0x8CF3FAF5),
        frostHighlight = Color(0x99FFFFFF),
        frostBorder = Color(0x22112118),
        petalColors = listOf(
          Color(0xFF4B9167),
          Color(0xFF2D7049),
          Color(0xFF78B590),
          Color(0xFF3C8058),
        ),
      )

    ThemePaletteId.BLUE ->
      FlowspanPalette(
        id = id,
        paperBackground = Color(0xFFEAF1FC),
        paperGradientBottom = Color(0xFFCCDDF4),
        meshBase = Color(0xFFDCE8FA),
        meshBlob1 = Color(0xFFEEF4FD),
        meshBlob2 = Color(0xFFB8D0F2),
        meshBlob3 = Color(0xFFACC6EC),
        sheetSurface = Color(0xFFF3F7FD),
        inkPrimary = Color(0xFF111A29),
        inkSecondary = Color(0xFF425878),
        inkMuted = Color(0xFF768BA8),
        accent = Color(0xFF1B447D),
        accentBreak = Color(0xFF44628A),
        ringTrack = Color(0xFFB3C8E6),
        hairlineDivider = Color(0xFFCCD9EE),
        frostSurface = Color(0x8CF3F7FD),
        frostHighlight = Color(0x99FFFFFF),
        frostBorder = Color(0x22111A29),
        petalColors = listOf(
          Color(0xFF4A7DBB),
          Color(0xFF2B5B99),
          Color(0xFF79A4D8),
          Color(0xFF3A6CA8),
        ),
      )

    ThemePaletteId.CYAN ->
      FlowspanPalette(
        id = id,
        paperBackground = Color(0xFFE7F5F5),
        paperGradientBottom = Color(0xFFC8E6E7),
        meshBase = Color(0xFFD9EFEF),
        meshBlob1 = Color(0xFFECF8F8),
        meshBlob2 = Color(0xFFB3DFE0),
        meshBlob3 = Color(0xFFA6D6D8),
        sheetSurface = Color(0xFFF1FAFA),
        inkPrimary = Color(0xFF0F2122),
        inkSecondary = Color(0xFF3F6466),
        inkMuted = Color(0xFF729698),
        accent = Color(0xFF135D63),
        accentBreak = Color(0xFF3F7377),
        ringTrack = Color(0xFFACD3D5),
        hairlineDivider = Color(0xFFC6E2E3),
        frostSurface = Color(0x8CF1FAFA),
        frostHighlight = Color(0x99FFFFFF),
        frostBorder = Color(0x220F2122),
        petalColors = listOf(
          Color(0xFF359297),
          Color(0xFF1E7276),
          Color(0xFF69BFC3),
          Color(0xFF2A8287),
        ),
      )

    ThemePaletteId.YELLOW ->
      FlowspanPalette(
        id = id,
        paperBackground = Color(0xFFFDF6E0),
        paperGradientBottom = Color(0xFFF4E2B6),
        meshBase = Color(0xFFFBEEC8),
        meshBlob1 = Color(0xFFFDF6DF),
        meshBlob2 = Color(0xFFF2D796),
        meshBlob3 = Color(0xFFEAC882),
        sheetSurface = Color(0xFFFEFAEE),
        inkPrimary = Color(0xFF241B0B),
        inkSecondary = Color(0xFF6B5630),
        inkMuted = Color(0xFF9C865E),
        accent = Color(0xFF915808),
        accentBreak = Color(0xFF7A5D2E),
        ringTrack = Color(0xFFE6CF9E),
        hairlineDivider = Color(0xFFEDE0C0),
        frostSurface = Color(0x8CFEF9EA),
        frostHighlight = Color(0x99FFFFFF),
        frostBorder = Color(0x22241B0B),
        petalColors = listOf(
          Color(0xFFD99926),
          Color(0xFFB8780E),
          Color(0xFFECBC5B),
          Color(0xFFC9891A),
        ),
      )

    ThemePaletteId.PURPLE ->
      FlowspanPalette(
        id = id,
        paperBackground = Color(0xFFF2EDFA),
        paperGradientBottom = Color(0xFFDDD2F0),
        meshBase = Color(0xFFE8DFF7),
        meshBlob1 = Color(0xFFF4F0FC),
        meshBlob2 = Color(0xFFCEC0EC),
        meshBlob3 = Color(0xFFC4B3E6),
        sheetSurface = Color(0xFFF7F4FD),
        inkPrimary = Color(0xFF1B1329),
        inkSecondary = Color(0xFF564473),
        inkMuted = Color(0xFF8A78A6),
        accent = Color(0xFF4E2E7D),
        accentBreak = Color(0xFF65508A),
        ringTrack = Color(0xFFC8B8E2),
        hairlineDivider = Color(0xFFDBD0EB),
        frostSurface = Color(0x8CF7F4FD),
        frostHighlight = Color(0x99FFFFFF),
        frostBorder = Color(0x221B1329),
        petalColors = listOf(
          Color(0xFF8561B3),
          Color(0xFF654194),
          Color(0xFFAC8FD4),
          Color(0xFF7551A3),
        ),
      )

    // 松烟 / Pine Soot: aged rice paper wash, deep ink-charcoal ring, ash-stone & tiny cinnabar seal petals
    ThemePaletteId.PINE_SOOT ->
      FlowspanPalette(
        id = id,
        paperBackground = Color(0xFFF3F0E8),
        paperGradientBottom = Color(0xFFDDD9CE),
        meshBase = Color(0xFFECE8DF),
        meshBlob1 = Color(0xFFF8F5ED),
        meshBlob2 = Color(0xFFD6D4CE),
        meshBlob3 = Color(0xFFE2DCD2),
        sheetSurface = Color(0xFFF8F6F0),
        inkPrimary = Color(0xFF191B1E),
        inkSecondary = Color(0xFF555961),
        inkMuted = Color(0xFF878B91),
        accent = Color(0xFF23262C),
        accentBreak = Color(0xFF525864),
        ringTrack = Color(0xFFCBC9C1),
        hairlineDivider = Color(0xFFDFDDD5),
        frostSurface = Color(0x8CF8F6F0),
        frostHighlight = Color(0x99FFFFFF),
        frostBorder = Color(0x22191B1E),
        petalColors = listOf(
          Color(0xFF797B80),
          Color(0xFF4A4E56),
          Color(0xFFA7A59D),
          Color(0xFFBC4332),
        ),
      )

    // 暮霞 / Alpenglow: cross-hue dusk wash spanning apricot, rose, and twilight lilac
    ThemePaletteId.ALPENGLOW ->
      FlowspanPalette(
        id = id,
        paperBackground = Color(0xFFFCEEEB),
        paperGradientBottom = Color(0xFFEBD6E8),
        meshBase = Color(0xFFF7E2E5),
        meshBlob1 = Color(0xFFFFF1E2),
        meshBlob2 = Color(0xFFF2C2BD),
        meshBlob3 = Color(0xFFD9C9ED),
        sheetSurface = Color(0xFFFEF4F3),
        inkPrimary = Color(0xFF29141C),
        inkSecondary = Color(0xFF704656),
        inkMuted = Color(0xFFA17887),
        accent = Color(0xFF9F3042),
        accentBreak = Color(0xFF714B78),
        ringTrack = Color(0xFFE3C2CD),
        hairlineDivider = Color(0xFFEBD2DA),
        frostSurface = Color(0x8CFEF2F2),
        frostHighlight = Color(0x99FFFFFF),
        frostBorder = Color(0x2229141C),
        petalColors = listOf(
          Color(0xFFDC5C61),
          Color(0xFFB83D55),
          Color(0xFFEA965B),
          Color(0xFF9C6FB0),
        ),
      )

    // 金桂 / Osmanthus: warm sweet-tea amber wash, rich persimmon ring, golden osmanthus petals
    ThemePaletteId.OSMANTHUS ->
      FlowspanPalette(
        id = id,
        paperBackground = Color(0xFFFDF2E2),
        paperGradientBottom = Color(0xFFF3D7B3),
        meshBase = Color(0xFFFAEAD4),
        meshBlob1 = Color(0xFFFFF6E4),
        meshBlob2 = Color(0xFFF3CEA2),
        meshBlob3 = Color(0xFFEFBD8E),
        sheetSurface = Color(0xFFFEF7EC),
        inkPrimary = Color(0xFF2A1909),
        inkSecondary = Color(0xFF744F2B),
        inkMuted = Color(0xFFA37E59),
        accent = Color(0xFFAD4712),
        accentBreak = Color(0xFF8A5D2E),
        ringTrack = Color(0xFFE8C9A4),
        hairlineDivider = Color(0xFFEEDAC0),
        frostSurface = Color(0x8CFEF6E9),
        frostHighlight = Color(0x99FFFFFF),
        frostBorder = Color(0x222A1909),
        petalColors = listOf(
          Color(0xFFE68A24),
          Color(0xFFC55E14),
          Color(0xFFF3B74B),
          Color(0xFFDA7120),
        ),
      )

    // 琉璃 / Sea Glass: luminous ice-mint, moon-blue, and pale snow-violet night-focus wash
    ThemePaletteId.SEA_GLASS ->
      FlowspanPalette(
        id = id,
        paperBackground = Color(0xFFE9F6F7),
        paperGradientBottom = Color(0xFFCDE1EF),
        meshBase = Color(0xFFDFEFF4),
        meshBlob1 = Color(0xFFE8FBF5),
        meshBlob2 = Color(0xFFBFE0F2),
        meshBlob3 = Color(0xFFD7D4F2),
        sheetSurface = Color(0xFFF2FAFB),
        inkPrimary = Color(0xFF102028),
        inkSecondary = Color(0xFF3E616E),
        inkMuted = Color(0xFF71929E),
        accent = Color(0xFF145B6A),
        accentBreak = Color(0xFF475888),
        ringTrack = Color(0xFFB5D3DC),
        hairlineDivider = Color(0xFFC9E0E6),
        frostSurface = Color(0x8CF2FAFB),
        frostHighlight = Color(0x99FFFFFF),
        frostBorder = Color(0x22102028),
        petalColors = listOf(
          Color(0xFF2D909C),
          Color(0xFF23668E),
          Color(0xFF66C2BD),
          Color(0xFF6E7DC2),
        ),
      )

    // 海雾 / Sea Mist: low-saturation Morandi hermit coast wash with slate sea-pine ring
    ThemePaletteId.SEA_MIST ->
      FlowspanPalette(
        id = id,
        paperBackground = Color(0xFFEDF2F1),
        paperGradientBottom = Color(0xFFD2DCDF),
        meshBase = Color(0xFFE4ECEB),
        meshBlob1 = Color(0xFFF3F6F4),
        meshBlob2 = Color(0xFFC7D7DC),
        meshBlob3 = Color(0xFFD9D6D2),
        sheetSurface = Color(0xFFF5F8F7),
        inkPrimary = Color(0xFF172226),
        inkSecondary = Color(0xFF51666C),
        inkMuted = Color(0xFF83969B),
        accent = Color(0xFF33525B),
        accentBreak = Color(0xFF5D6E73),
        ringTrack = Color(0xFFC0CED2),
        hairlineDivider = Color(0xFFD4DFE1),
        frostSurface = Color(0x8CF5F8F7),
        frostHighlight = Color(0x99FFFFFF),
        frostBorder = Color(0x22172226),
        petalColors = listOf(
          Color(0xFF6E8E97),
          Color(0xFF4C6C75),
          Color(0xFFA0B3AD),
          Color(0xFFC08E8B),
        ),
      )
  }
