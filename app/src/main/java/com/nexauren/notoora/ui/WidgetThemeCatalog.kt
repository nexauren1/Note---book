package com.nexauren.paperya.ui

import com.nexauren.paperya.R

data class WidgetThemePreset(
    val id: Int,
    val name: String,
    val namePortuguese: String,
    val cardColor: Long,
    val borderColor: Long,
    val headerStart: Long,
    val headerEnd: Long,
    val accentColor: Long,
    val titleColor: Long,
    val bodyColor: Long,
    val mutedColor: Long,
    val cardDrawable: Int,
    val headerDrawable: Int
)

object WidgetThemeCatalog {
    val themes = listOf(
        WidgetThemePreset(0, "Lavender Bloom", "Lavanda", 0xFFFFFDFEL, 0xFFE7E1F4L, 0xFFF49BB7L, 0xFF7765F7L, 0xFF6B5BE7L, 0xFF262633L, 0xFF66616EL, 0xFF817B89L, R.drawable.widget_card_lavender, R.drawable.widget_header_lavender),
        WidgetThemePreset(1, "Rose Pop", "Rosa pop", 0xFFFFFBFEL, 0xFFF6D2E6L, 0xFFFF3D6DL, 0xFFE64CE4L, 0xFFD6389AL, 0xFF40213AL, 0xFF6F526BL, 0xFF9B7D94L, R.drawable.widget_card_rose, R.drawable.widget_header_rose),
        WidgetThemePreset(2, "Coral Kiss", "Beijo coral", 0xFFFFFFFCL, 0xFFF8D7D2L, 0xFFFF4C64L, 0xFFFFB7C6L, 0xFFF45A78L, 0xFF512B2BL, 0xFF795A5AL, 0xFF9A7777L, R.drawable.widget_card_coral, R.drawable.widget_header_coral),
        WidgetThemePreset(3, "Mint Ocean", "Oceano menta", 0xFFF3FFF9L, 0xFFB8E9DCL, 0xFF64E879L, 0xFF02B8F1L, 0xFF13A69CL, 0xFF173D3EL, 0xFF476B6AL, 0xFF78918EL, R.drawable.widget_card_mint, R.drawable.widget_header_mint),
        WidgetThemePreset(4, "Sunset Glow", "Pôr do sol", 0xFFFFF5E7L, 0xFFFFE0B4L, 0xFFD73AB4L, 0xFFFFE329L, 0xFFE89624L, 0xFF49301EL, 0xFF765940L, 0xFF9F8464L, R.drawable.widget_card_sunset, R.drawable.widget_header_sunset),
        WidgetThemePreset(5, "Galaxy Violet", "Galáxia violeta", 0xFFEAE7FFL, 0xFFBDB8F5L, 0xFF252A65L, 0xFFB1A5FFL, 0xFF5A60D6L, 0xFF25234BL, 0xFF4C4A77L, 0xFF73719AL, R.drawable.widget_card_galaxy, R.drawable.widget_header_galaxy),
        WidgetThemePreset(6, "Night Sky", "Céu noturno", 0xFFD8E4FFL, 0xFF8CA8FFL, 0xFF11175BL, 0xFF2F62E8L, 0xFF263DA6L, 0xFF1C275CL, 0xFF3F5089L, 0xFF6C7BA8L, R.drawable.widget_card_night, R.drawable.widget_header_night),
        WidgetThemePreset(7, "Meadow", "Prado", 0xFFEAF7E7L, 0xFFBEE3B8L, 0xFF28794DL, 0xFFA8E063L, 0xFF4A965DL, 0xFF22402AL, 0xFF4F6A51L, 0xFF7A9278L, R.drawable.widget_card_meadow, R.drawable.widget_header_meadow)
    )

    fun get(id: Int): WidgetThemePreset = themes.firstOrNull { it.id == id } ?: themes.first()
}
