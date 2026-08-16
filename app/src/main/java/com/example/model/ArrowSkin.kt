package com.example.model

import androidx.compose.ui.graphics.Color

enum class ArrowTailStyle {
    CLASSIC_SOLID,
    NEON_CYBER,
    GOLDEN_CHROME,
    FIRE_EMBER,
    EMERALD_CRYSTAL,
    COSMIC_STAR,
    STEALTH_OBSIDIAN,
    // NEW UNIQUE SKIN TYPES:
    SNAKE_REALISTIC,       // Realistic slithering viper with scales, snake eye, and red tongue tip
    RED_TIP_BEAM,          // Long sleek laser line with bright luminous red tip (no arrow wings)
    DRAGON_KATANA,         // Japanese steel blade katana with dragon fire tip
    LIGHTNING_BOLT,        // Sharp jagged electric storm zigzag with plasma orb tip
    RAINBOW_HYPER,         // Prismatic chromatic spectrum beam with rainbow pulse tip
    MECHA_RAILGUN          // Futuristic cyberpunk sci-fi railgun energy rod
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

    val RED_TIP_LINE = ArrowSkin(
        id = "skin_red_tip_line",
        name = "Crimson Vector Line",
        description = "Clean minimalist solid line without wings, pointing with an ultra-bright crimson red laser core.",
        price = 20,
        strokeColor = Color(0xFF1A1A1A),
        tipGlowColor = Color(0xFFFF1744),
        tipCenterColor = Color(0xFFFF5252),
        tailStyle = ArrowTailStyle.RED_TIP_BEAM,
        strokeWidthDp = 16f,
        headWingLengthDp = 0f,
        glowRadiusDp = 24f
    )

    val SNAKE_VIPER = ArrowSkin(
        id = "skin_snake_viper",
        name = "Realistic Green Viper",
        description = "Dangerous slithering serpent body with textured viper scales, yellow snake eye, and glowing venom red strike tip.",
        price = 45,
        strokeColor = Color(0xFF1B5E20),
        tipGlowColor = Color(0xFFFF1744),
        tipCenterColor = Color(0xFFFF5252),
        tailStyle = ArrowTailStyle.SNAKE_REALISTIC,
        strokeWidthDp = 20f,
        headWingLengthDp = 50f,
        glowRadiusDp = 22f
    )

    val CYBER_NEON = ArrowSkin(
        id = "skin_cyber_neon",
        name = "Cyber Neon Beam",
        description = "Electrified cyan beam crafted for hyper-speed twitch reflexes.",
        price = 70,
        strokeColor = Color(0xFF00E5FF),
        tipGlowColor = Color(0xFF00E5FF),
        tipCenterColor = Color(0xFFE0F7FA),
        tailStyle = ArrowTailStyle.NEON_CYBER,
        strokeWidthDp = 17f,
        headWingLengthDp = 78f,
        glowRadiusDp = 22f
    )

    val LIGHTNING_STRIKE = ArrowSkin(
        id = "skin_lightning_strike",
        name = "Thunder Bolt",
        description = "High-voltage jagged zigzag lightning strike surging with 10,000 volts toward a plasma ball tip.",
        price = 100,
        strokeColor = Color(0xFFFFD600),
        tipGlowColor = Color(0xFFFFEA00),
        tipCenterColor = Color(0xFFFFFFFF),
        tailStyle = ArrowTailStyle.LIGHTNING_BOLT,
        strokeWidthDp = 16f,
        headWingLengthDp = 65f,
        glowRadiusDp = 26f
    )

    val SOLAR_GOLD = ArrowSkin(
        id = "skin_solar_gold",
        name = "Solar Gold Spear",
        description = "Forged in royal liquid gold with a dazzling amber star tip.",
        price = 150,
        strokeColor = Color(0xFFFFB300),
        tipGlowColor = Color(0xFFFFD54F),
        tipCenterColor = Color(0xFFFFF8E1),
        tailStyle = ArrowTailStyle.GOLDEN_CHROME,
        strokeWidthDp = 18f,
        headWingLengthDp = 80f,
        glowRadiusDp = 20f
    )

    val DRAGON_KATANA = ArrowSkin(
        id = "skin_dragon_katana",
        name = "Dragon Katana",
        description = "Forged Japanese Damascus steel blade with a golden guard and molten dragon flame tip.",
        price = 220,
        strokeColor = Color(0xFF78909C),
        tipGlowColor = Color(0xFFFF3D00),
        tipCenterColor = Color(0xFFFFAB91),
        tailStyle = ArrowTailStyle.DRAGON_KATANA,
        strokeWidthDp = 18f,
        headWingLengthDp = 60f,
        glowRadiusDp = 24f
    )

    val CRIMSON_FLAME = ArrowSkin(
        id = "skin_crimson_flame",
        name = "Crimson Fury",
        description = "Intense fiery red strike with volcanic ember aura.",
        price = 300,
        strokeColor = Color(0xFFFF1744),
        tipGlowColor = Color(0xFFFF5252),
        tipCenterColor = Color(0xFFFFEBEE),
        tailStyle = ArrowTailStyle.FIRE_EMBER,
        strokeWidthDp = 18f,
        headWingLengthDp = 80f,
        glowRadiusDp = 24f
    )

    val RAINBOW_SPECTRUM = ArrowSkin(
        id = "skin_rainbow_spectrum",
        name = "Prismatic Spectrum",
        description = "Vibrant flowing rainbow gradient laser beam with dynamic chromatic glow tip.",
        price = 380,
        strokeColor = Color(0xFFE040FB),
        tipGlowColor = Color(0xFF00E5FF),
        tipCenterColor = Color(0xFFFFFFFF),
        tailStyle = ArrowTailStyle.RAINBOW_HYPER,
        strokeWidthDp = 18f,
        headWingLengthDp = 76f,
        glowRadiusDp = 24f
    )

    val MECHA_CANNON = ArrowSkin(
        id = "skin_mecha_cannon",
        name = "Mecha Railgun",
        description = "Armored sci-fi magnetic accelerator rod with warning hazard stripes and neon energy emitter.",
        price = 450,
        strokeColor = Color(0xFF37474F),
        tipGlowColor = Color(0xFF00E676),
        tipCenterColor = Color(0xFFE8F5E9),
        tailStyle = ArrowTailStyle.MECHA_RAILGUN,
        strokeWidthDp = 20f,
        headWingLengthDp = 70f,
        glowRadiusDp = 26f
    )

    val EMERALD_VIPER = ArrowSkin(
        id = "skin_emerald_viper",
        name = "Emerald Crystal",
        description = "Lethal radioactive green arrow with high precision gem tip.",
        price = 520,
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
        price = 600,
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
        price = 750,
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
        RED_TIP_LINE,
        SNAKE_VIPER,
        CYBER_NEON,
        LIGHTNING_STRIKE,
        SOLAR_GOLD,
        DRAGON_KATANA,
        CRIMSON_FLAME,
        RAINBOW_SPECTRUM,
        MECHA_CANNON,
        EMERALD_VIPER,
        COSMIC_VIOLET,
        OBSIDIAN_STEALTH
    )

    fun getSkinById(id: String): ArrowSkin {
        return allSkins.find { it.id == id } ?: CLASSIC
    }
}
