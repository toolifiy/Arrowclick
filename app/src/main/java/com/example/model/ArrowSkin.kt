package com.example.model

import androidx.compose.ui.graphics.Color

enum class ArrowTailStyle {
    CLASSIC_SOLID,
    NEON_CYBER,
    GOLDEN_CHROME,
    FIRE_EMBER,
    EMERALD_CRYSTAL,
    COSMIC_STAR,
    STEALTH_OBSIDIAN
}

data class ArrowSkin(
    val id: String,
    val name: String,
    val description: String,
    val price: Int,
    val strokeColor: Color,
    val tipGlowColor: Color,
    val tipCenterColor: Color,
    val tailStyle: ArrowTailStyle,
    val strokeWidthDp: Float = 16f,
    val headWingLengthDp: Float = 75f,
    val headWingAngleDeg: Float = 36f,
    val glowRadiusDp: Float = 18f
)

object ArrowSkinCatalog {
    val CLASSIC = ArrowSkin(
        id = "skin_classic",
        name = "Classic Minimal",
        description = "Pure black minimalist sharp 2X arrow with high-visibility red laser tip.",
        price = 0,
        strokeColor = Color(0xFF111111),
        tipGlowColor = Color(0xFFFF3B30),
        tipCenterColor = Color(0xFFFF5A52),
        tailStyle = ArrowTailStyle.CLASSIC_SOLID,
        strokeWidthDp = 16f,
        headWingLengthDp = 75f
    )

    val CYBER_NEON = ArrowSkin(
        id = "skin_cyber_neon",
        name = "Cyber Neon",
        description = "Electrified cyan beam crafted for hyper-speed twitch reflexes.",
        price = 25,
        strokeColor = Color(0xFF00E5FF),
        tipGlowColor = Color(0xFF00E5FF),
        tipCenterColor = Color(0xFFE0F7FA),
        tailStyle = ArrowTailStyle.NEON_CYBER,
        strokeWidthDp = 17f,
        headWingLengthDp = 78f,
        glowRadiusDp = 22f
    )

    val SOLAR_GOLD = ArrowSkin(
        id = "skin_solar_gold",
        name = "Solar Gold",
        description = "Forged in liquid gold with a dazzling amber star tip.",
        price = 60,
        strokeColor = Color(0xFFFFB300),
        tipGlowColor = Color(0xFFFFD54F),
        tipCenterColor = Color(0xFFFFF8E1),
        tailStyle = ArrowTailStyle.GOLDEN_CHROME,
        strokeWidthDp = 18f,
        headWingLengthDp = 80f,
        glowRadiusDp = 20f
    )

    val CRIMSON_FLAME = ArrowSkin(
        id = "skin_crimson_flame",
        name = "Crimson Fury",
        description = "Intense fiery red strike with volcanic spark particles.",
        price = 120,
        strokeColor = Color(0xFFFF1744),
        tipGlowColor = Color(0xFFFF5252),
        tipCenterColor = Color(0xFFFFEBEE),
        tailStyle = ArrowTailStyle.FIRE_EMBER,
        strokeWidthDp = 18f,
        headWingLengthDp = 80f,
        glowRadiusDp = 24f
    )

    val EMERALD_VIPER = ArrowSkin(
        id = "skin_emerald_viper",
        name = "Emerald Viper",
        description = "Lethal radioactive green arrow with high precision gem tip.",
        price = 200,
        strokeColor = Color(0xFF00E676),
        tipGlowColor = Color(0xFF69F0AE),
        tipCenterColor = Color(0xFFE8F5E9),
        tailStyle = ArrowTailStyle.EMERALD_CRYSTAL,
        strokeWidthDp = 17f,
        headWingLengthDp = 76f,
        glowRadiusDp = 22f
    )

    val COSMIC_VIOLET = ArrowSkin(
        id = "skin_cosmic_violet",
        name = "Cosmic Violet",
        description = "Deep galaxy purple with ultra-bright pulsar singularity tip.",
        price = 350,
        strokeColor = Color(0xFF7C4DFF),
        tipGlowColor = Color(0xFFE040FB),
        tipCenterColor = Color(0xFFF3E5F5),
        tailStyle = ArrowTailStyle.COSMIC_STAR,
        strokeWidthDp = 18f,
        headWingLengthDp = 78f,
        glowRadiusDp = 24f
    )

    val OBSIDIAN_STEALTH = ArrowSkin(
        id = "skin_obsidian_stealth",
        name = "Obsidian Stealth",
        description = "Matte stealth black shaft with a blinding diamond white tip.",
        price = 500,
        strokeColor = Color(0xFF263238),
        tipGlowColor = Color(0xFFFFFFFF),
        tipCenterColor = Color(0xFFECEFF1),
        tailStyle = ArrowTailStyle.STEALTH_OBSIDIAN,
        strokeWidthDp = 19f,
        headWingLengthDp = 84f,
        glowRadiusDp = 24f
    )

    val allSkins: List<ArrowSkin> = listOf(
        CLASSIC,
        CYBER_NEON,
        SOLAR_GOLD,
        CRIMSON_FLAME,
        EMERALD_VIPER,
        COSMIC_VIOLET,
        OBSIDIAN_STEALTH
    )

    fun getSkinById(id: String): ArrowSkin {
        return allSkins.find { it.id == id } ?: CLASSIC
    }
}
