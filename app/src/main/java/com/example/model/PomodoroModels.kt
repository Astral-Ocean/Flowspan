package com.example.model

import androidx.compose.ui.graphics.Color

enum class TimerPhase {
  IDLE,
  FOCUS,
  BREAK,
}

enum class AppLanguage(
  val storageKey: String,
  val nativeLabel: String,
  val englishLabel: String,
) {
  ZH_CN("zh", "简体中文", "Simplified Chinese"),
  ZH_TW("zh_tw", "繁體中文", "Traditional Chinese"),
  EN("en", "English", "English"),
  JA("ja", "日本語", "Japanese"),
  KO("ko", "한국어", "Korean"),
  FR("fr", "Français", "French"),
  DE("de", "Deutsch", "German"),
  ES("es", "Español", "Spanish");

  val readyTitle: String
    get() =
      when (this) {
        ZH_CN -> "准备"
        ZH_TW -> "準備"
        EN -> "Ready"
        JA -> "準備"
        KO -> "준비"
        FR -> "Prêt"
        DE -> "Bereit"
        ES -> "Listo"
      }

  val focusTitle: String
    get() =
      when (this) {
        ZH_CN -> "专注"
        ZH_TW -> "專注"
        EN -> "Focus"
        JA -> "集中"
        KO -> "집중"
        FR -> "Concentration"
        DE -> "Fokus"
        ES -> "Enfoque"
      }

  val breakTitle: String
    get() =
      when (this) {
        ZH_CN -> "休息"
        ZH_TW -> "休息"
        EN -> "Break"
        JA -> "休憩"
        KO -> "휴식"
        FR -> "Pause"
        DE -> "Pause"
        ES -> "Descanso"
      }

  val startFocus: String
    get() =
      when (this) {
        ZH_CN -> "开始专注"
        ZH_TW -> "開始專注"
        EN -> "Start"
        JA -> "開始"
        KO -> "시작"
        FR -> "Démarrer"
        DE -> "Starten"
        ES -> "Comenzar"
      }

  val focusing: String
    get() =
      when (this) {
        ZH_CN -> "专注"
        ZH_TW -> "專注"
        EN -> "Focus"
        JA -> "集中中"
        KO -> "집중 중"
        FR -> "Concentration"
        DE -> "Fokus"
        ES -> "Enfoque"
      }

  val resetReady: String
    get() =
      when (this) {
        ZH_CN -> "回到准备"
        ZH_TW -> "回到準備"
        EN -> "Reset"
        JA -> "リセット"
        KO -> "초기화"
        FR -> "Réinitialiser"
        DE -> "Zurücksetzen"
        ES -> "Restablecer"
      }

  val settingsTitle: String
    get() =
      when (this) {
        ZH_CN -> "设置"
        ZH_TW -> "設定"
        EN -> "Settings"
        JA -> "設定"
        KO -> "설정"
        FR -> "Réglages"
        DE -> "Einstellungen"
        ES -> "Ajustes"
      }

  val back: String
    get() =
      when (this) {
        ZH_CN -> "返回"
        ZH_TW -> "返回"
        EN -> "Back"
        JA -> "戻る"
        KO -> "뒤로"
        FR -> "Retour"
        DE -> "Zurück"
        ES -> "Volver"
      }

  val sectionTimer: String
    get() =
      when (this) {
        ZH_CN -> "计时"
        ZH_TW -> "計時"
        EN -> "TIMER"
        JA -> "タイマー"
        KO -> "타이머"
        FR -> "MINUTEUR"
        DE -> "TIMER"
        ES -> "TEMPORIZADOR"
      }

  val focusDuration: String
    get() =
      when (this) {
        ZH_CN -> "专注时长"
        ZH_TW -> "專注時長"
        EN -> "Focus"
        JA -> "集中時間"
        KO -> "집중 시간"
        FR -> "Concentration"
        DE -> "Fokuszeit"
        ES -> "Tiempo de enfoque"
      }

  val breakDuration: String
    get() =
      when (this) {
        ZH_CN -> "休息时长"
        ZH_TW -> "休息時長"
        EN -> "Break"
        JA -> "休憩時間"
        KO -> "휴식 시간"
        FR -> "Pause"
        DE -> "Pausenzeit"
        ES -> "Tiempo de descanso"
      }

  val minutesUnit: String
    get() =
      when (this) {
        ZH_CN -> "分钟"
        ZH_TW -> "分鐘"
        EN -> "min"
        JA -> "分"
        KO -> "분"
        FR -> "min"
        DE -> "Min."
        ES -> "min"
      }

  val durationNotice: String
    get() =
      when (this) {
        ZH_CN -> "时长调整将在当前周期结束后生效"
        ZH_TW -> "時長調整將在當前週期結束後生效"
        EN -> "Duration changes apply after the current cycle."
        JA -> "時間の変更は現在のサイクル終了後に適用されます"
        KO -> "시간 설정은 현재 사이클이 끝난 후 적용됩니다"
        FR -> "Les modifications s'appliqueront après le cycle en cours."
        DE -> "Änderungen werden nach dem aktuellen Zyklus wirksam."
        ES -> "Los cambios se aplicarán tras el ciclo actual."
      }

  val autoCycle: String
    get() =
      when (this) {
        ZH_CN -> "自动循环"
        ZH_TW -> "自動循環"
        EN -> "Auto Cycle"
        JA -> "自動ループ"
        KO -> "자동 반복"
        FR -> "Cycle continu"
        DE -> "Automatischer Zyklus"
        ES -> "Ciclo automático"
      }

  val autoCycleDesc: String
    get() =
      when (this) {
        ZH_CN -> "一轮结束后自动开启下一个专注时段"
        ZH_TW -> "一輪結束後自動開啟下一個專注時段"
        EN -> "Automatically start next focus after break"
        JA -> "休憩終了後に自動で次の集中を開始"
        KO -> "휴식이 끝나면 자동으로 다음 집중 시작"
        FR -> "Enchaîne automatiquement la concentration suivante"
        DE -> "Startet nach der Pause automatisch den nächsten Fokus"
        ES -> "Inicia el siguiente enfoque automáticamente tras el descanso"
      }

  val sectionAppearance: String
    get() =
      when (this) {
        ZH_CN -> "外观"
        ZH_TW -> "外觀"
        EN -> "APPEARANCE"
        JA -> "外観"
        KO -> "화면 스타일"
        FR -> "APPARENCE"
        DE -> "ERSCHEINUNGSBILD"
        ES -> "APARIENCIA"
      }

  val themeColor: String
    get() =
      when (this) {
        ZH_CN -> "主题配色"
        ZH_TW -> "主題配色"
        EN -> "Theme"
        JA -> "テーマカラー"
        KO -> "테마 색상"
        FR -> "Thème"
        DE -> "Farbschema"
        ES -> "Tema"
      }

  val sectionAmbience: String
    get() =
      when (this) {
        ZH_CN -> "背景白噪"
        ZH_TW -> "背景白噪"
        EN -> "AMBIENCE"
        JA -> "環境音"
        KO -> "배경 사운드"
        FR -> "AMBIANCE"
        DE -> "ATMOSPHÄRE"
        ES -> "AMBIENTE"
      }

  val sectionChime: String
    get() =
      when (this) {
        ZH_CN -> "阶段提示音"
        ZH_TW -> "階段提示音"
        EN -> "TRANSITION SOUND"
        JA -> "切替チャイム"
        KO -> "전환 알림음"
        FR -> "SONNERIE DE TRANSITION"
        DE -> "ÜBERGANGSSIGNAL"
        ES -> "SONIDO DE TRANSICIÓN"
      }

  val sectionLanguage: String
    get() =
      when (this) {
        ZH_CN -> "语言"
        ZH_TW -> "語言"
        EN -> "LANGUAGE"
        JA -> "言語"
        KO -> "언어"
        FR -> "LANGUE"
        DE -> "SPRACHE"
        ES -> "IDIOMA"
      }

  val sectionAbout: String
    get() =
      when (this) {
        ZH_CN -> "关于"
        ZH_TW -> "關於"
        EN -> "ABOUT"
        JA -> "情報"
        KO -> "정보"
        FR -> "À PROPOS"
        DE -> "ÜBER"
        ES -> "ACERCA DE"
      }

  val aboutMe: String
    get() =
      when (this) {
        ZH_CN -> "关于开发者"
        ZH_TW -> "關於開發者"
        EN -> "About Developer"
        JA -> "開発者について"
        KO -> "개발자 소개"
        FR -> "À propos du développeur"
        DE -> "Über den Entwickler"
        ES -> "Sobre el desarrollador"
      }

  val openPersonalWebsite: String
    get() =
      when (this) {
        ZH_CN -> "打开个人网站"
        ZH_TW -> "打開個人網站"
        EN -> "Open personal website"
        JA -> "ウェブサイトを開く"
        KO -> "웹사이트 열기"
        FR -> "Ouvrir le site web"
        DE -> "Website öffnen"
        ES -> "Abrir sitio web"
      }

  companion object {
    fun fromKey(key: String?): AppLanguage =
      entries.firstOrNull { it.storageKey == key } ?: ZH_CN
  }
}

enum class ThemePaletteId(
  val storageKey: String,
  val swatchColor: Color,
) {
  RED("red", Color(0xFFB8321C)),
  ALPENGLOW("alpenglow", Color(0xFFD65E53)),
  OSMANTHUS("osmanthus", Color(0xFFD97224)),
  YELLOW("yellow", Color(0xFFBA7A10)),
  GREEN("green", Color(0xFF2B6D47)),
  CYAN("cyan", Color(0xFF1C7579)),
  SEA_GLASS("sea_glass", Color(0xFF1E7B85)),
  SEA_MIST("sea_mist", Color(0xFF627E87)),
  BLUE("blue", Color(0xFF295894)),
  PURPLE("purple", Color(0xFF654194)),
  PINK("pink", Color(0xFFC9486D)),
  PINE_SOOT("pine_soot", Color(0xFF282B30));

  fun label(language: AppLanguage): String =
    when (this) {
      PINK ->
        when (language) {
          AppLanguage.ZH_CN -> "樱花"
          AppLanguage.ZH_TW -> "櫻花"
          AppLanguage.EN -> "Sakura"
          AppLanguage.JA -> "桜"
          AppLanguage.KO -> "사쿠라"
          AppLanguage.FR -> "Cerisier"
          AppLanguage.DE -> "Kirschblüte"
          AppLanguage.ES -> "Cerezo"
        }
      RED ->
        when (language) {
          AppLanguage.ZH_CN -> "枫叶"
          AppLanguage.ZH_TW -> "楓葉"
          AppLanguage.EN -> "Maple"
          AppLanguage.JA -> "楓"
          AppLanguage.KO -> "단풍"
          AppLanguage.FR -> "Érable"
          AppLanguage.DE -> "Ahorn"
          AppLanguage.ES -> "Arce"
        }
      GREEN ->
        when (language) {
          AppLanguage.ZH_CN -> "青苔"
          AppLanguage.ZH_TW -> "青苔"
          AppLanguage.EN -> "Moss"
          AppLanguage.JA -> "苔"
          AppLanguage.KO -> "이끼"
          AppLanguage.FR -> "Mousse"
          AppLanguage.DE -> "Moos"
          AppLanguage.ES -> "Musgo"
        }
      BLUE ->
        when (language) {
          AppLanguage.ZH_CN -> "霁蓝"
          AppLanguage.ZH_TW -> "霽藍"
          AppLanguage.EN -> "Indigo"
          AppLanguage.JA -> "藍"
          AppLanguage.KO -> "남색"
          AppLanguage.FR -> "Indigo"
          AppLanguage.DE -> "Indigoblau"
          AppLanguage.ES -> "Índigo"
        }
      CYAN ->
        when (language) {
          AppLanguage.ZH_CN -> "青瓷"
          AppLanguage.ZH_TW -> "青瓷"
          AppLanguage.EN -> "Celadon"
          AppLanguage.JA -> "青磁"
          AppLanguage.KO -> "청자"
          AppLanguage.FR -> "Céladon"
          AppLanguage.DE -> "Seladon"
          AppLanguage.ES -> "Celadón"
        }
      YELLOW ->
        when (language) {
          AppLanguage.ZH_CN -> "银杏"
          AppLanguage.ZH_TW -> "銀杏"
          AppLanguage.EN -> "Ginkgo"
          AppLanguage.JA -> "銀杏"
          AppLanguage.KO -> "은행"
          AppLanguage.FR -> "Ginkgo"
          AppLanguage.DE -> "Ginkgo"
          AppLanguage.ES -> "Ginkgo"
        }
      PURPLE ->
        when (language) {
          AppLanguage.ZH_CN -> "鸢尾"
          AppLanguage.ZH_TW -> "鳶尾"
          AppLanguage.EN -> "Iris"
          AppLanguage.JA -> "菖蒲"
          AppLanguage.KO -> "붓꽃"
          AppLanguage.FR -> "Iris"
          AppLanguage.DE -> "Schwertlilie"
          AppLanguage.ES -> "Iris"
        }
      PINE_SOOT ->
        when (language) {
          AppLanguage.ZH_CN -> "松烟"
          AppLanguage.ZH_TW -> "松煙"
          AppLanguage.EN -> "Pine Soot"
          AppLanguage.JA -> "松煙"
          AppLanguage.KO -> "송연"
          AppLanguage.FR -> "Suie de pin"
          AppLanguage.DE -> "Kiefernruß"
          AppLanguage.ES -> "Hollín de pino"
        }
      ALPENGLOW ->
        when (language) {
          AppLanguage.ZH_CN -> "暮霞"
          AppLanguage.ZH_TW -> "暮霞"
          AppLanguage.EN -> "Alpenglow"
          AppLanguage.JA -> "夕霞"
          AppLanguage.KO -> "저녁노을"
          AppLanguage.FR -> "Crépuscule"
          AppLanguage.DE -> "Alpenglühen"
          AppLanguage.ES -> "Resplandor"
        }
      OSMANTHUS ->
        when (language) {
          AppLanguage.ZH_CN -> "金桂"
          AppLanguage.ZH_TW -> "金桂"
          AppLanguage.EN -> "Osmanthus"
          AppLanguage.JA -> "金木犀"
          AppLanguage.KO -> "금목서"
          AppLanguage.FR -> "Osmanthe"
          AppLanguage.DE -> "Duftblüte"
          AppLanguage.ES -> "Osmanto"
        }
      SEA_GLASS ->
        when (language) {
          AppLanguage.ZH_CN -> "琉璃"
          AppLanguage.ZH_TW -> "琉璃"
          AppLanguage.EN -> "Sea Glass"
          AppLanguage.JA -> "瑠璃"
          AppLanguage.KO -> "유리"
          AppLanguage.FR -> "Verre poli"
          AppLanguage.DE -> "Meerglas"
          AppLanguage.ES -> "Cristal de mar"
        }
      SEA_MIST ->
        when (language) {
          AppLanguage.ZH_CN -> "海雾"
          AppLanguage.ZH_TW -> "海霧"
          AppLanguage.EN -> "Sea Mist"
          AppLanguage.JA -> "海霧"
          AppLanguage.KO -> "바다안개"
          AppLanguage.FR -> "Brume marine"
          AppLanguage.DE -> "Meernebel"
          AppLanguage.ES -> "Niebla marina"
        }
    }

  companion object {
    fun fromKey(key: String?): ThemePaletteId =
      entries.firstOrNull { it.storageKey == key } ?: RED
  }
}

enum class AmbientTrack(
  val storageKey: String,
) {
  NONE("none"),
  SOFT_RAIN("soft_rain"),
  WARM_STUDY("warm_study"),
  MORNING_BREEZE("morning_breeze"),
  SINGING_BOWL("singing_bowl"),
  PINK_NOISE("pink_noise");

  fun title(language: AppLanguage): String =
    when (this) {
      NONE ->
        when (language) {
          AppLanguage.ZH_CN -> "静谧无声"
          AppLanguage.ZH_TW -> "靜謐無聲"
          AppLanguage.EN -> "Silence"
          AppLanguage.JA -> "静寂"
          AppLanguage.KO -> "고요함"
          AppLanguage.FR -> "Silence"
          AppLanguage.DE -> "Stille"
          AppLanguage.ES -> "Silencio"
        }
      SOFT_RAIN ->
        when (language) {
          AppLanguage.ZH_CN -> "林间微雨"
          AppLanguage.ZH_TW -> "林間微雨"
          AppLanguage.EN -> "Forest Rain"
          AppLanguage.JA -> "森の小雨"
          AppLanguage.KO -> "숲속의 비"
          AppLanguage.FR -> "Pluie en forêt"
          AppLanguage.DE -> "Waldregen"
          AppLanguage.ES -> "Lluvia en el bosque"
        }
      WARM_STUDY ->
        when (language) {
          AppLanguage.ZH_CN -> "暖炉书房"
          AppLanguage.ZH_TW -> "暖爐書房"
          AppLanguage.EN -> "Warm Study"
          AppLanguage.JA -> "暖炉の書斎"
          AppLanguage.KO -> "따뜻한 서재"
          AppLanguage.FR -> "Coin du feu"
          AppLanguage.DE -> "Warme Kaminruhe"
          AppLanguage.ES -> "Estudio acogedor"
        }
      MORNING_BREEZE ->
        when (language) {
          AppLanguage.ZH_CN -> "晨间微风"
          AppLanguage.ZH_TW -> "晨間微風"
          AppLanguage.EN -> "Morning Breeze"
          AppLanguage.JA -> "朝のそよ風"
          AppLanguage.KO -> "아침 산들바람"
          AppLanguage.FR -> "Brise matinale"
          AppLanguage.DE -> "Morgenbrise"
          AppLanguage.ES -> "Brisa matinal"
        }
      SINGING_BOWL ->
        when (language) {
          AppLanguage.ZH_CN -> "颂钵泛音"
          AppLanguage.ZH_TW -> "頌缽泛音"
          AppLanguage.EN -> "Singing Bowl"
          AppLanguage.JA -> "シンギングボウル"
          AppLanguage.KO -> "싱잉볼"
          AppLanguage.FR -> "Bol chantant"
          AppLanguage.DE -> "Klangschale"
          AppLanguage.ES -> "Cuenco tibetano"
        }
      PINK_NOISE ->
        when (language) {
          AppLanguage.ZH_CN -> "粉红噪音"
          AppLanguage.ZH_TW -> "粉紅噪音"
          AppLanguage.EN -> "Pink Noise"
          AppLanguage.JA -> "ピンクノイズ"
          AppLanguage.KO -> "핑크 노이즈"
          AppLanguage.FR -> "Bruit rose"
          AppLanguage.DE -> "Rosa Rauschen"
          AppLanguage.ES -> "Ruido rosa"
        }
    }

  fun description(language: AppLanguage): String =
    when (this) {
      NONE ->
        when (language) {
          AppLanguage.ZH_CN -> "只留计时与花瓣的自然节奏"
          AppLanguage.ZH_TW -> "只留計時與花瓣的自然節奏"
          AppLanguage.EN -> "Pure stillness with drifting petals"
          AppLanguage.JA -> "落ちる花びらとタイマーの静寂"
          AppLanguage.KO -> "타이머와 흩날리는 꽃잎의 평온"
          AppLanguage.FR -> "Le calme pur des pétales qui flottent"
          AppLanguage.DE -> "Reine Stille mit schwebenden Blüten"
          AppLanguage.ES -> "Pura quietud con pétalos flotantes"
        }
      SOFT_RAIN ->
        when (language) {
          AppLanguage.ZH_CN -> "细腻绵长的温润雨声底噪"
          AppLanguage.ZH_TW -> "細膩綿長的溫潤雨聲底噪"
          AppLanguage.EN -> "Gentle, steady rainfall ambience"
          AppLanguage.JA -> "穏やかで心地よい雨音"
          AppLanguage.KO -> "부드럽고 고요한 빗소리"
          AppLanguage.FR -> "Bruine douce et apaisante"
          AppLanguage.DE -> "Sanfter, stetiger Regenklang"
          AppLanguage.ES -> "Suave y constante lluvia relajante"
        }
      WARM_STUDY ->
        when (language) {
          AppLanguage.ZH_CN -> "柔和纸页沙沙与暖木低鸣"
          AppLanguage.ZH_TW -> "柔和紙頁沙沙與暖木低鳴"
          AppLanguage.EN -> "Soft hearth warmth and paper hush"
          AppLanguage.JA -> "暖炉の温もりと静かな本の音"
          AppLanguage.KO -> "포근한 난롯가와 책 넘기는 소리"
          AppLanguage.FR -> "Chaleur du foyer et doux bruissement"
          AppLanguage.DE -> "Kaminfeuer und leises Blätterrauschen"
          AppLanguage.ES -> "Calor de chimenea y murmullo de papel"
        }
      MORNING_BREEZE ->
        when (language) {
          AppLanguage.ZH_CN -> "随自然风势起伏的空灵气音"
          AppLanguage.ZH_TW -> "隨自然風勢起伏的空靈氣音"
          AppLanguage.EN -> "Airy gusts rising and settling softly"
          AppLanguage.JA -> "自然にそよぐ心地よい風の音"
          AppLanguage.KO -> "자연스러운 바람의 은은한 숨결"
          AppLanguage.FR -> "Souffle éthéré qui va et vient"
          AppLanguage.DE -> "Leichter, flüsternder Windhauch"
          AppLanguage.ES -> "Suaves ráfagas de aire fresco"
        }
      SINGING_BOWL ->
        when (language) {
          AppLanguage.ZH_CN -> "悠远平缓的五声音阶共鸣"
          AppLanguage.ZH_TW -> "悠遠平緩的五聲音階共鳴"
          AppLanguage.EN -> "Calm pentatonic harmonic resonance"
          AppLanguage.JA -> "心落ち着く倍音の五声音階"
          AppLanguage.KO -> "마음이 편안해지는 배음 공명"
          AppLanguage.FR -> "Résonance harmonique apaisante"
          AppLanguage.DE -> "Harmonische Resonanz zur Entspannung"
          AppLanguage.ES -> "Resonancia armónica relajante"
        }
      PINK_NOISE ->
        when (language) {
          AppLanguage.ZH_CN -> "温和绵密的 1/f 均衡全频段白噪声"
          AppLanguage.ZH_TW -> "溫和綿密的 1/f 均衡全頻段白噪聲"
          AppLanguage.EN -> "Balanced, gentle 1/f soothing frequency spectrum"
          AppLanguage.JA -> "集中力を高める自然な1/fゆらぎ"
          AppLanguage.KO -> "집중에 최적화된 편안한 1/f 사운드"
          AppLanguage.FR -> "Spectre sonore équilibré en 1/f"
          AppLanguage.DE -> "Ausgewogenes 1/f-Rauschspektrum"
          AppLanguage.ES -> "Espectro relajante equilibrado 1/f"
        }
    }

  companion object {
    fun fromKey(key: String?): AmbientTrack =
      entries.firstOrNull { it.storageKey == key } ?: NONE
  }
}

enum class TransitionChime(
  val storageKey: String,
) {
  BELL("bell"),
  WOODBLOCK("woodblock"),
  DROPLET("droplet"),
  HARMONIC("harmonic"),
  NONE("none");

  fun title(language: AppLanguage): String =
    when (this) {
      BELL ->
        when (language) {
          AppLanguage.ZH_CN -> "清雅钟磬"
          AppLanguage.ZH_TW -> "清雅鐘磬"
          AppLanguage.EN -> "Temple Bell"
          AppLanguage.JA -> "寺院の鐘"
          AppLanguage.KO -> "맑은 종소리"
          AppLanguage.FR -> "Cloche zen"
          AppLanguage.DE -> "Tempelglocke"
          AppLanguage.ES -> "Campana zen"
        }
      WOODBLOCK ->
        when (language) {
          AppLanguage.ZH_CN -> "静心木鱼"
          AppLanguage.ZH_TW -> "靜心木魚"
          AppLanguage.EN -> "Zen Woodblock"
          AppLanguage.JA -> "木魚"
          AppLanguage.KO -> "차분한 목탁"
          AppLanguage.FR -> "Mokugyo"
          AppLanguage.DE -> "Zen-Holzblock"
          AppLanguage.ES -> "Bloque de madera"
        }
      DROPLET ->
        when (language) {
          AppLanguage.ZH_CN -> "空谷水滴"
          AppLanguage.ZH_TW -> "空谷水滴"
          AppLanguage.EN -> "Clear Droplet"
          AppLanguage.JA -> "水滴"
          AppLanguage.KO -> "맑은 물방울"
          AppLanguage.FR -> "Goutte claire"
          AppLanguage.DE -> "Wassertropfen"
          AppLanguage.ES -> "Gota cristalina"
        }
      HARMONIC ->
        when (language) {
          AppLanguage.ZH_CN -> "柔和泛音"
          AppLanguage.ZH_TW -> "柔和泛音"
          AppLanguage.EN -> "Soft Harmonic"
          AppLanguage.JA -> "柔らかな倍音"
          AppLanguage.KO -> "부드러운 배음"
          AppLanguage.FR -> "Harmonique douce"
          AppLanguage.DE -> "Weicher Oberton"
          AppLanguage.ES -> "Armónico suave"
        }
      NONE ->
        when (language) {
          AppLanguage.ZH_CN -> "静音"
          AppLanguage.ZH_TW -> "靜音"
          AppLanguage.EN -> "Mute"
          AppLanguage.JA -> "消音"
          AppLanguage.KO -> "무음"
          AppLanguage.FR -> "Muet"
          AppLanguage.DE -> "Stumm"
          AppLanguage.ES -> "Silencio"
        }
    }

  fun description(language: AppLanguage): String =
    when (this) {
      BELL ->
        when (language) {
          AppLanguage.ZH_CN -> "清脆悠远的多泛音钟磬鸣"
          AppLanguage.ZH_TW -> "清脆悠遠的多泛音鐘磬鳴"
          AppLanguage.EN -> "Crisp resonant harmonic temple chime"
          AppLanguage.JA -> "凛とした清らかな鐘の余韻"
          AppLanguage.KO -> "맑고 그윽한 종소리"
          AppLanguage.FR -> "Cloche cristalline et résonante"
          AppLanguage.DE -> "Klarer, lang nachklingender Glockenton"
          AppLanguage.ES -> "Campana nítida y resonante"
        }
      WOODBLOCK ->
        when (language) {
          AppLanguage.ZH_CN -> "温润笃定的禅意笃木声"
          AppLanguage.ZH_TW -> "溫潤篤定的禪意篤木聲"
          AppLanguage.EN -> "Warm rhythmic meditative woodblock strike"
          AppLanguage.JA -> "心を落ち着かせる木魚の響き"
          AppLanguage.KO -> "마음을 가다듬는 온화한 목탁 소리"
          AppLanguage.FR -> "Frappe boisée chaleureuse et apaisante"
          AppLanguage.DE -> "Warmer, meditativer Holzklang"
          AppLanguage.ES -> "Toque de madera rítmico y cálido"
        }
      DROPLET ->
        when (language) {
          AppLanguage.ZH_CN -> "灵动清透的泉水清滴声"
          AppLanguage.ZH_TW -> "靈動清透的泉水清滴聲"
          AppLanguage.EN -> "Pure crystalline droplet falling softly"
          AppLanguage.JA -> "澄み切った一滴の雫の音"
          AppLanguage.KO -> "맑고 투명한 물방울 소리"
          AppLanguage.FR -> "Pure goutte d'eau cristalline"
          AppLanguage.DE -> "Reiner, glasklarer Wassertropfen"
          AppLanguage.ES -> "Gota cristalina cayendo suavemente"
        }
      HARMONIC ->
        when (language) {
          AppLanguage.ZH_CN -> "悠扬安稳的和弦泛音"
          AppLanguage.ZH_TW -> "悠揚安穩的和弦泛音"
          AppLanguage.EN -> "Gentle soothing overtone resonance"
          AppLanguage.JA -> "穏やかで温かいコードの響き"
          AppLanguage.KO -> "포근하고 은은한 화음 배음"
          AppLanguage.FR -> "Accord doux et enveloppant"
          AppLanguage.DE -> "Sanfte, wohlklingende Akkordresonanz"
          AppLanguage.ES -> "Acorde suave y envolvente"
        }
      NONE ->
        when (language) {
          AppLanguage.ZH_CN -> "专注与休息切换时不提示"
          AppLanguage.ZH_TW -> "專注與休息切換時不提示"
          AppLanguage.EN -> "No chime on phase transitions"
          AppLanguage.JA -> "フェーズ切替時に音を鳴らさない"
          AppLanguage.KO -> "단계 전환 시 알림음 끔"
          AppLanguage.FR -> "Aucun son lors des transitions"
          AppLanguage.DE -> "Kein Signal beim Phasenwechsel"
          AppLanguage.ES -> "Sin sonido en las transiciones"
        }
    }

  companion object {
    fun fromKey(key: String?): TransitionChime =
      entries.firstOrNull { it.storageKey == key } ?: BELL
  }
}

data class UserPreferences(
  val focusMinutes: Int = 25,
  val breakMinutes: Int = 5,
  val autoCycle: Boolean = false,
  val palette: ThemePaletteId = ThemePaletteId.RED,
  val ambientTrack: AmbientTrack = AmbientTrack.NONE,
  val transitionChime: TransitionChime = TransitionChime.BELL,
  val audioVolume: Float = 0.65f,
  val windEventsEnabled: Boolean = true,
  val language: AppLanguage = AppLanguage.ZH_CN,
)

data class PomodoroUiState(
  val phase: TimerPhase = TimerPhase.IDLE,
  val remainingMillis: Long = 25 * 60_000L,
  val totalPhaseMillis: Long = 25 * 60_000L,
  val completedCycles: Int = 0,
  val preferences: UserPreferences = UserPreferences(),
  val isSettingsOpen: Boolean = false,
) {
  val progressFraction: Float
    get() =
      if (totalPhaseMillis <= 0L) 0f
      else (remainingMillis.toFloat() / totalPhaseMillis.toFloat()).coerceIn(0f, 1f)
}
