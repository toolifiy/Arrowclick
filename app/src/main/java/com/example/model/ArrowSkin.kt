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
    SNAKE_REALISTIC,       // Realistic slithering viper with scales, snake eye, and red tongue tip
    RED_TIP_BEAM,          // Long sleek laser line with bright luminous red tip (no arrow wings)
    DRAGON_KATANA,         // Japanese steel blade katana with dragon fire tip
    LIGHTNING_BOLT,        // Sharp jagged electric storm zigzag with plasma orb tip
    RAINBOW_HYPER,         // Prismatic chromatic spectrum beam with rainbow pulse tip
    MECHA_RAILGUN,         // Futuristic cyberpunk sci-fi railgun energy rod
    // NEW CODES TO FILL 25 SKINS:
    ICE_SPIKE,
    ROYAL_SCEPTRE,
    SHADOW_ASSASSIN,
    TOXIC_PLAGUE,
    VALKYRIE_SPEAR,
    MAGMA_BURST,
    CHRONO_GEAR,
    BUBBLE_AQUA,
    CANDY_CANE,
    PIXEL_RETRO,
    PIRATE_CUTLASS,
    ANGELIC_WING
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

    // NEW PRESET SKINS TO COMPLY TO EXACTLY 25 UNIQUE PIECES:
    val GLACIAL_ICE = ArrowSkin(
        id = "skin_glacial_ice",
        name = "Glacial Ice Spike",
        description = "Forged in zero-kelvin crystal frost, glowing light-blue spike.",
        price = 800,
        strokeColor = Color(0xFF80DEEA),
        tipGlowColor = Color(0xFF00E5FF),
        tipCenterColor = Color(0xFFE0F7FA),
        tailStyle = ArrowTailStyle.ICE_SPIKE,
        strokeWidthDp = 16f,
        headWingLengthDp = 72f,
        glowRadiusDp = 22f
    )

    val IMPERIAL_SCEPTRE = ArrowSkin(
        id = "skin_imperial_sceptre",
        name = "Imperial Sceptre",
        description = "Elegant deep crimson royal staff with gold filigree and diamond tips.",
        price = 900,
        strokeColor = Color(0xFFB71C1C),
        tipGlowColor = Color(0xFFFFD54F),
        tipCenterColor = Color(0xFFFFFFFF),
        tailStyle = ArrowTailStyle.ROYAL_SCEPTRE,
        strokeWidthDp = 20f,
        headWingLengthDp = 68f,
        glowRadiusDp = 20f
    )

    val SHADOW_ASSASSIN = ArrowSkin(
        id = "skin_shadow_assassin",
        name = "Shadow Assassin",
        description = "Shrouded in black mist and dark purple energy, silent and deadly.",
        price = 1000,
        strokeColor = Color(0xFF121212),
        tipGlowColor = Color(0xFF9C27B0),
        tipCenterColor = Color(0xFFE040FB),
        tailStyle = ArrowTailStyle.SHADOW_ASSASSIN,
        strokeWidthDp = 16f,
        headWingLengthDp = 74f,
        glowRadiusDp = 24f
    )

    val TOXIC_PLAGUE = ArrowSkin(
        id = "skin_toxic_plague",
        name = "Toxic Plague",
        description = "Corrosive neon toxic sludge dripping hazard waste, highly acidic.",
        price = 1100,
        strokeColor = Color(0xFF9E9D24),
        tipGlowColor = Color(0xFFCCFF00),
        tipCenterColor = Color(0xFFF1F8E9),
        tailStyle = ArrowTailStyle.TOXIC_PLAGUE,
        strokeWidthDp = 18f,
        headWingLengthDp = 70f,
        glowRadiusDp = 26f
    )

    val VALKYRIE_SPEAR = ArrowSkin(
        id = "skin_valkyrie_spear",
        name = "Valkyrie Spear",
        description = "Blessed Nordic silver wings with celestial cyan holy light.",
        price = 1200,
        strokeColor = Color(0xFFB0BEC5),
        tipGlowColor = Color(0xFF80DEEA),
        tipCenterColor = Color(0xFFFFFFFF),
        tailStyle = ArrowTailStyle.VALKYRIE_SPEAR,
        strokeWidthDp = 17f,
        headWingLengthDp = 80f,
        glowRadiusDp = 22f
    )

    val MAGMA_BURST = ArrowSkin(
        id = "skin_magma_burst",
        name = "Magma Burst",
        description = "Molten crackling volcanic lava flow, radiating intense superheated orange-red.",
        price = 1300,
        strokeColor = Color(0xFFD84315),
        tipGlowColor = Color(0xFFFF3D00),
        tipCenterColor = Color(0xFFFFEB3B),
        tailStyle = ArrowTailStyle.MAGMA_BURST,
        strokeWidthDp = 20f,
        headWingLengthDp = 75f,
        glowRadiusDp = 28f
    )

    val CHRONO_GEAR = ArrowSkin(
        id = "skin_chrono_gear",
        name = "Chrono Gear",
        description = "Steampunk bronze shaft with rotating brass gear teeth and golden glow.",
        price = 1400,
        strokeColor = Color(0xFF8D6E63),
        tipGlowColor = Color(0xFFFFB300),
        tipCenterColor = Color(0xFFFFF8E1),
        tailStyle = ArrowTailStyle.CHRONO_GEAR,
        strokeWidthDp = 18f,
        headWingLengthDp = 72f,
        glowRadiusDp = 20f
    )

    val BUBBLE_AQUA = ArrowSkin(
        id = "skin_bubble_aqua",
        name = "Oceanic Bubble Wave",
        description = "Fluid sea-teal stream bubbling toward a shiny glowing pearl tip.",
        price = 1500,
        strokeColor = Color(0xFF00ACC1),
        tipGlowColor = Color(0xFF26A69A),
        tipCenterColor = Color(0xFFE0F2F1),
        tailStyle = ArrowTailStyle.BUBBLE_AQUA,
        strokeWidthDp = 18f,
        headWingLengthDp = 76f,
        glowRadiusDp = 24f
    )

    val CANDY_CANE = ArrowSkin(
        id = "skin_candy_cane",
        name = "Sweet Candy Cane",
        description = "Peppermint red and white striped swirl stick with a sugary sweet neon glow.",
        price = 1600,
        strokeColor = Color(0xFFE53935),
        tipGlowColor = Color(0xFFFF8A80),
        tipCenterColor = Color(0xFFFFFFFF),
        tailStyle = ArrowTailStyle.CANDY_CANE,
        strokeWidthDp = 18f,
        headWingLengthDp = 65f,
        glowRadiusDp = 20f
    )

    val PIXEL_RETRO = ArrowSkin(
        id = "skin_pixel_retro",
        name = "Retro 8-Bit Laser",
        description = "Chunky pixelated retro-arcade solid orange laser beam with blocky corners.",
        price = 1700,
        strokeColor = Color(0xFFEF6C00),
        tipGlowColor = Color(0xFFFFB300),
        tipCenterColor = Color(0xFFFFFFFF),
        tailStyle = ArrowTailStyle.PIXEL_RETRO,
        strokeWidthDp = 22f,
        headWingLengthDp = 55f,
        glowRadiusDp = 18f
    )

    val PIRATE_CUTLASS = ArrowSkin(
        id = "skin_pirate_cutlass",
        name = "Dread Pirate Cutlass",
        description = "Curved Damascus steel pirate blade with golden skull hilt and neon red slash trail.",
        price = 1800,
        strokeColor = Color(0xFF78909C),
        tipGlowColor = Color(0xFFFF1744),
        tipCenterColor = Color(0xFFFFD54F),
        tailStyle = ArrowTailStyle.PIRATE_CUTLASS,
        strokeWidthDp = 17f,
        headWingLengthDp = 60f,
        glowRadiusDp = 24f
    )

    val ANGELIC_WING = ArrowSkin(
        id = "skin_angelic_wing",
        name = "Angelic Wings",
        description = "Pure divine feather-white shaft wrapped in holy golden halo rings.",
        price = 2000,
        strokeColor = Color(0xFFECEFF1),
        tipGlowColor = Color(0xFFFFD54F),
        tipCenterColor = Color(0xFFFFFDE7),
        tailStyle = ArrowTailStyle.ANGELIC_WING,
        strokeWidthDp = 16f,
        headWingLengthDp = 84f,
        glowRadiusDp = 26f
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
        OBSIDIAN_STEALTH,
        GLACIAL_ICE,
        IMPERIAL_SCEPTRE,
        SHADOW_ASSASSIN,
        TOXIC_PLAGUE,
        VALKYRIE_SPEAR,
        MAGMA_BURST,
        CHRONO_GEAR,
        BUBBLE_AQUA,
        CANDY_CANE,
        PIXEL_RETRO,
        PIRATE_CUTLASS,
        ANGELIC_WING
    )

    fun getSkinById(id: String): ArrowSkin {
        return allSkins.find { it.id == id } ?: CLASSIC
    }
}
