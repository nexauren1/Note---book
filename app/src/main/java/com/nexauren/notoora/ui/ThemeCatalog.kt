package com.nexauren.notoora.ui

import androidx.compose.ui.graphics.Color

data class NotePalette(
    val name: String,
    val primary: Color,
    val secondary: Color,
    val page: Color,
    val card: Color,
    val ink: Color
)

val NotePalettes = listOf(
    NotePalette("Lavender", Color(0xFF6654D9), Color(0xFFE8E2FF), Color(0xFFFAF8FF), Color(0xFFFFFFFF), Color(0xFF24213A)),
    NotePalette("Ocean", Color(0xFF087EA4), Color(0xFFD6F1F8), Color(0xFFF4FCFF), Color(0xFFFFFFFF), Color(0xFF15333F)),
    NotePalette("Rose", Color(0xFFCB5478), Color(0xFFFFE0E8), Color(0xFFFFF8FA), Color(0xFFFFFFFF), Color(0xFF41202B)),
    NotePalette("Meadow", Color(0xFF2F805C), Color(0xFFD9F1E1), Color(0xFFF5FCF6), Color(0xFFFFFFFF), Color(0xFF1C3829)),
    NotePalette("Sunshine", Color(0xFFB66B11), Color(0xFFFFE7B5), Color(0xFFFFFBF1), Color(0xFFFFFFFF), Color(0xFF432B13)),
    NotePalette("Midnight", Color(0xFF4147B7), Color(0xFFDCE0FF), Color(0xFFF6F6FF), Color(0xFFFFFFFF), Color(0xFF20213F)),
    NotePalette("Mint", Color(0xFF168B88), Color(0xFFD3F7F0), Color(0xFFF3FFFC), Color(0xFFFFFFFF), Color(0xFF183C3B)),
    NotePalette("Peach", Color(0xFFC5684E), Color(0xFFFFE2D4), Color(0xFFFFF8F3), Color(0xFFFFFFFF), Color(0xFF47261D)),
    NotePalette("Sky", Color(0xFF3D6ED5), Color(0xFFDCE9FF), Color(0xFFF7FAFF), Color(0xFFFFFFFF), Color(0xFF202D49)),
    NotePalette("Berry", Color(0xFF9C3E9E), Color(0xFFF2D9F3), Color(0xFFFFF8FF), Color(0xFFFFFFFF), Color(0xFF3D1E42)),
    NotePalette("Forest", Color(0xFF355C42), Color(0xFFDCE7D7), Color(0xFFF7FAF4), Color(0xFFFFFFFF), Color(0xFF202D21)),
    NotePalette("Graphite", Color(0xFF4F5967), Color(0xFFE3E7EC), Color(0xFFF7F8FA), Color(0xFFFFFFFF), Color(0xFF222832))
)

data class PaperPreset(
    val name: String,
    val category: String,
    val base: Color,
    val accent: Color,
    val second: Color,
    val pattern: PaperPattern
)

enum class PaperPattern { PLAIN, RULED, GRID, DOTS, GINGHAM, CONFETTI, LEAVES, GRADIENT, STARS, MARGINS }

val PaperPresets = listOf(
    PaperPreset("Clean white", "Plain", Color(0xFFFFFEFC), Color(0xFFE7E2DB), Color(0xFFFEFEFE), PaperPattern.PLAIN),
    PaperPreset("Blue ruled", "Classic", Color(0xFFFFFEF8), Color(0xFF9CC6E8), Color(0xFF467FAC), PaperPattern.RULED),
    PaperPreset("Graph paper", "Classic", Color(0xFFFEFEFC), Color(0xFFD5D8DC), Color(0xFF9AA4B2), PaperPattern.GRID),
    PaperPreset("Soft dots", "Classic", Color(0xFFFFFCF8), Color(0xFFD9D4E8), Color(0xFFB9AED5), PaperPattern.DOTS),
    PaperPreset("Lilac grid", "Popular", Color(0xFFF8F5FF), Color(0xFFD5CBF1), Color(0xFF9B8AD8), PaperPattern.GRID),
    PaperPreset("Peach checks", "Popular", Color(0xFFFFF0E8), Color(0xFFF1B9B1), Color(0xFFEBA18E), PaperPattern.GINGHAM),
    PaperPreset("Mint confetti", "Popular", Color(0xFFF0FFF7), Color(0xFF80D5B1), Color(0xFFFFB66D), PaperPattern.CONFETTI),
    PaperPreset("Sunset glow", "Trending", Color(0xFFFFD5A4), Color(0xFFF48B7D), Color(0xFF8A5BB7), PaperPattern.GRADIENT),
    PaperPreset("Ocean sky", "Trending", Color(0xFFEAF8FF), Color(0xFF79C8E8), Color(0xFF3B75C8), PaperPattern.STARS),
    PaperPreset("Rose garden", "Popular", Color(0xFFFFF0F4), Color(0xFFF3B8CB), Color(0xFFC86C9A), PaperPattern.LEAVES),
    PaperPreset("Meadow", "Popular", Color(0xFFF1FFE9), Color(0xFF7ECC76), Color(0xFFFFB55E), PaperPattern.CONFETTI),
    PaperPreset("Night sky", "Trending", Color(0xFF222A5A), Color(0xFF737EDD), Color(0xFFE7C8FF), PaperPattern.STARS),
    PaperPreset("Cream notebook", "Classic", Color(0xFFFFF9E8), Color(0xFFDFCDA4), Color(0xFFB99E6A), PaperPattern.RULED),
    PaperPreset("Aqua lines", "Plain", Color(0xFFEDFFFF), Color(0xFF9EDFE4), Color(0xFF5BB9C5), PaperPattern.RULED),
    PaperPreset("Kraft paper", "Classic", Color(0xFFECD6B6), Color(0xFFCAAC80), Color(0xFF94714B), PaperPattern.DOTS),
    PaperPreset("Candy stripes", "Trending", Color(0xFFFFF3F8), Color(0xFFF5B7DB), Color(0xFFB3A0F1), PaperPattern.GRADIENT),
    PaperPreset("Starry lavender", "Popular", Color(0xFFF0EDFF), Color(0xFF9F8FEA), Color(0xFF4D4B9D), PaperPattern.STARS),
    PaperPreset("Green margin", "Classic", Color(0xFFF8FFF1), Color(0xFFBBDFB5), Color(0xFF70B57C), PaperPattern.MARGINS),
    PaperPreset("Cherry grid", "Trending", Color(0xFFFFF4F5), Color(0xFFF0BCC8), Color(0xFFC7657F), PaperPattern.GRID),
    PaperPreset("Blueberry dusk", "Trending", Color(0xFFE5EDFF), Color(0xFF94A8F8), Color(0xFF474CAD), PaperPattern.GRADIENT),
    PaperPreset("Lemon cream", "Popular", Color(0xFFFFFBE1), Color(0xFFF5DB80), Color(0xFFD5A52D), PaperPattern.DOTS),
    PaperPreset("Warm sand", "Plain", Color(0xFFFFF3DE), Color(0xFFE5C69B), Color(0xFFB98558), PaperPattern.GINGHAM),
    PaperPreset("Pistachio", "Popular", Color(0xFFF1FFE8), Color(0xFFC2E6A7), Color(0xFF6FAD65), PaperPattern.GRID),
    PaperPreset("Blue journal", "Classic", Color(0xFFF0F8FF), Color(0xFFB1D4ED), Color(0xFF5C9BCB), PaperPattern.MARGINS)
)
