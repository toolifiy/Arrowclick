package com.example.model

import androidx.compose.ui.graphics.Color

enum class DotStyle {
    CLASSIC_TARGET,       // Standard glowing dot target
    ELECTRIC_RING,        // Pulsing electric outer ring with center core
    STAR_BURST,           // Golden sparkling star with rays
    COSMIC_SINGULARITY,   // Dark void core with swirling vortex halo
    MOLTEN_SUN,           // Pulsing fire ember sun with solar flares
    MATRIX_RADAR,         // Code green target grid radar rings
    RAINBOW_CHROMA,       // Color-shifting rainbow prism dot
    TECH_HEXAGON,         // Sci-fi hexagonal barrier shield dot
    // NEW TO FILL 25 SKINS:
    ICE_CRYSTAL,
    TOXIC_BIOHAZARD,
    SHADOW_PORTAL,
    HEAVENLY_HALO,
    GEAR_CLOCKWORK,
    WATER_RIPPLE,
    SWEET_DONUT,
    CHROME_METAL,
    PIXEL_HEART,
    GOLDEN_SHIELD,
    NEON_CROSSHAIR,
    FIREFLY_SWARM,
    GALAXY_ORBIT,
    PLASMA_BALL,
    EGYPTIAN_EYE,
    YIN_YANG,
    DISCO_BALL
}

data class DotSkin(
    val id: String,
    val name: String,
    val description: String,
    val price: Int,
    val glowColor: Color,
    val centerColor: Color,
    val style: DotStyle,
    val glowRadiusDp: Float = 18f
)

object DotSkinCatalog {
    val CLASSIC = DotSkin(
        id = "dot_classic",
        name = "Classic Target",
        description = "Standard high-visibility red laser core targeting dot.",
        price = 0,
        glowColor = Color(0xFFFF3B30),
        centerColor = Color(0xFFFF5A52),
        style = DotStyle.CLASSIC_TARGET
    )

    val CYAN_RING = DotSkin(
        id = "dot_cyan_ring",
        name = "Cyber Cyan Ring",
        description = "Pulsing neon-cyan energy ring with a blazing core.",
        price = 25,
        glowColor = Color(0xFF00E5FF),
        centerColor = Color(0xFFE0F7FA),
        style = DotStyle.ELECTRIC_RING
    )

    val GOLDEN_STAR = DotSkin(
        id = "dot_golden_star",
        name = "Nebula Star",
        description = "A glittering solar star radiating pure cosmic gold light.",
        price = 50,
        glowColor = Color(0xFFFFD54F),
        centerColor = Color(0xFFFFF8E1),
        style = DotStyle.STAR_BURST
    )

    val COSMIC_VOID = DotSkin(
        id = "dot_cosmic_void",
        name = "Black Hole",
        description = "A localized gravity well drawing light into a violet core.",
        price = 85,
        glowColor = Color(0xFF7C4DFF),
        centerColor = Color(0xFFF3E5F5),
        style = DotStyle.COSMIC_SINGULARITY
    )

    val MOLTEN_SUN = DotSkin(
        id = "dot_molten_sun",
        name = "Solar Flare",
        description = "Volcanic pulsing plasma fire ember with a warm solar aura.",
        price = 120,
        glowColor = Color(0xFFFF3D00),
        centerColor = Color(0xFFFFAB91),
        style = DotStyle.MOLTEN_SUN
    )

    val MATRIX_RADAR = DotSkin(
        id = "dot_matrix_radar",
        name = "Digital Radar",
        description = "Grid-locked matrix green telemetry scanner lock-on rings.",
        price = 180,
        glowColor = Color(0xFF00E676),
        centerColor = Color(0xFFE8F5E9),
        style = DotStyle.MATRIX_RADAR
    )

    val RAINBOW_CHROMA = DotSkin(
        id = "dot_rainbow_chroma",
        name = "Chroma Burst",
        description = "Prismatic color-shifting spectrum dot pulsing with rainbow flares.",
        price = 250,
        glowColor = Color(0xFFE040FB),
        centerColor = Color(0xFFFFFFFF),
        style = DotStyle.RAINBOW_CHROMA
    )

    val TECH_HEXAGON = DotSkin(
        id = "dot_tech_hexagon",
        name = "Shield Lock",
        description = "Futuristic neon-orange defensive energy target shield.",
        price = 350,
        glowColor = Color(0xFFFF9100),
        centerColor = Color(0xFFFFF3E0),
        style = DotStyle.TECH_HEXAGON
    )

    // NEW PRESETS:
    val GLACIAL_FROST = DotSkin(
        id = "dot_glacial_frost",
        name = "Glacial Frost",
        description = "Crystalline frozen ice snowflake target shield.",
        price = 400,
        glowColor = Color(0xFF80DEEA),
        centerColor = Color(0xFFE0F7FA),
        style = DotStyle.ICE_CRYSTAL
    )

    val TOXIC_BIOHAZARD = DotSkin(
        id = "dot_toxic_biohazard",
        name = "Biohazard Leak",
        description = "Danger neon-green warning biohazard radiation scanner target.",
        price = 450,
        glowColor = Color(0xFFCCFF00),
        centerColor = Color(0xFFF1F8E9),
        style = DotStyle.TOXIC_BIOHAZARD
    )

    val ABYSSAL_RIFT = DotSkin(
        id = "dot_abyssal_rift",
        name = "Abyssal Rift",
        description = "Swirling deep purple void gateway dragging light inside.",
        price = 500,
        glowColor = Color(0xFF9C27B0),
        centerColor = Color(0xFFE1BEE7),
        style = DotStyle.SHADOW_PORTAL
    )

    val DIVINE_HALO = DotSkin(
        id = "dot_divine_halo",
        name = "Divine Halo",
        description = "Heavenly gold halo rings flanked by tiny spinning angel feathers.",
        price = 555,
        glowColor = Color(0xFFFFD54F),
        centerColor = Color(0xFFFFFFFF),
        style = DotStyle.HEAVENLY_HALO
    )

    val TEMPUS_GEAR = DotSkin(
        id = "dot_tempus_gear",
        name = "Tempus Gear",
        description = "Rotating clockwork bronze teeth gear target.",
        price = 600,
        glowColor = Color(0xFF8D6E63),
        centerColor = Color(0xFFFFE082),
        style = DotStyle.GEAR_CLOCKWORK
    )

    val TSUNAMI_CORE = DotSkin(
        id = "dot_tsunami_core",
        name = "Tsunami Core",
        description = "Flowing sea-teal water drop ripple effect.",
        price = 650,
        glowColor = Color(0xFF00E5FF),
        centerColor = Color(0xFFE0F7FA),
        style = DotStyle.WATER_RIPPLE
    )

    val SPRINKLES_DONUT = DotSkin(
        id = "dot_sprinkles_donut",
        name = "Sprinkles Donut",
        description = "Glazed delicious pink cream donut target with multi-color sprinkles.",
        price = 700,
        glowColor = Color(0xFFF06292),
        centerColor = Color(0xFFFFFDE7),
        style = DotStyle.SWEET_DONUT
    )

    val TITANIUM_ALLOY = DotSkin(
        id = "dot_titanium_alloy",
        name = "Titanium Alloy",
        description = "Shiny chrome steel metal plating target with cross bolt rivets.",
        price = 750,
        glowColor = Color(0xFFCFD8DC),
        centerColor = Color(0xFFFFFFFF),
        style = DotStyle.CHROME_METAL
    )

    val LOVE_HEART = DotSkin(
        id = "dot_love_heart",
        name = "Love Heart",
        description = "Pulsing pixelated 8-bit retro gaming heart core.",
        price = 800,
        glowColor = Color(0xFFFF1744),
        centerColor = Color(0xFFFFCDD2),
        style = DotStyle.PIXEL_HEART
    )

    val AEGIS_SHIELD = DotSkin(
        id = "dot_aegis_shield",
        name = "Aegis Paladin",
        description = "Divine radiant golden shield target with defensive cross marks.",
        price = 850,
        glowColor = Color(0xFFFFC107),
        centerColor = Color(0xFFFFFDE7),
        style = DotStyle.GOLDEN_SHIELD
    )

    val SPECOPS_SIGHT = DotSkin(
        id = "dot_specops_sight",
        name = "Spec-Ops Sight",
        description = "High precision neon-red tactical military crosshair targeting grid.",
        price = 900,
        glowColor = Color(0xFFFF1744),
        centerColor = Color(0xFFFFFFFF),
        style = DotStyle.NEON_CROSSHAIR
    )

    val FIREFLY_SWARM = DotSkin(
        id = "dot_firefly_swarm",
        name = "Firefly Swarm",
        description = "Swirling magic fireflies dancing around a forest amber core.",
        price = 950,
        glowColor = Color(0xFFAEEA00),
        centerColor = Color(0xFFFFF9C4),
        style = DotStyle.FIREFLY_SWARM
    )

    val GALAXY_ORBIT = DotSkin(
        id = "dot_galaxy_orbit",
        name = "Andromeda Orbit",
        description = "Celestial swirling rings with orbiting cosmic dwarf planets.",
        price = 1000,
        glowColor = Color(0xFF651FFF),
        centerColor = Color(0xFFEDE7F6),
        style = DotStyle.GALAXY_ORBIT
    )

    val PLASMA_BALL = DotSkin(
        id = "dot_plasma_ball",
        name = "Plasma Sphere",
        description = "Electric lightning tesla plasma glass ball with purple sparks.",
        price = 1100,
        glowColor = Color(0xFFD500F9),
        centerColor = Color(0xFFF3E5F5),
        style = DotStyle.PLASMA_BALL
    )

    val EGYPTIAN_EYE = DotSkin(
        id = "dot_egyptian_eye",
        name = "Eye of Horus",
        description = "Mythical neon blue and gold Egyptian protective eye target.",
        price = 1200,
        glowColor = Color(0xFF00B0FF),
        centerColor = Color(0xFFFFD54F),
        style = DotStyle.EGYPTIAN_EYE
    )

    val ZEN_BALANCE = DotSkin(
        id = "dot_zen_balance",
        name = "Zen Yin-Yang",
        description = "Spinning black-and-white fluid equilibrium target representing peace.",
        price = 1300,
        glowColor = Color(0xFF37474F),
        centerColor = Color(0xFFFFFFFF),
        style = DotStyle.YIN_YANG
    )

    val RETRO_DISCO = DotSkin(
        id = "dot_retro_disco",
        name = "Retro Disco Ball",
        description = "Fabulous shiny glittering 70s party disco dance floor light ball.",
        price = 1500,
        glowColor = Color(0xFF00E5FF),
        centerColor = Color(0xFFE040FB),
        style = DotStyle.DISCO_BALL
    )

    val allSkins: List<DotSkin> = listOf(
        CLASSIC,
        CYAN_RING,
        GOLDEN_STAR,
        COSMIC_VOID,
        MOLTEN_SUN,
        MATRIX_RADAR,
        RAINBOW_CHROMA,
        TECH_HEXAGON,
        GLACIAL_FROST,
        TOXIC_BIOHAZARD,
        ABYSSAL_RIFT,
        DIVINE_HALO,
        TEMPUS_GEAR,
        TSUNAMI_CORE,
        SPRINKLES_DONUT,
        TITANIUM_ALLOY,
        LOVE_HEART,
        AEGIS_SHIELD,
        SPECOPS_SIGHT,
        FIREFLY_SWARM,
        GALAXY_ORBIT,
        PLASMA_BALL,
        EGYPTIAN_EYE,
        ZEN_BALANCE,
        RETRO_DISCO
    )

    fun getSkinById(id: String): DotSkin {
        return allSkins.find { it.id == id } ?: CLASSIC
    }
}
