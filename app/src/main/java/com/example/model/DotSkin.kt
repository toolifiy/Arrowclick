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
    TECH_HEXAGON          // Sci-fi hexagonal barrier shield dot
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

    val allSkins: List<DotSkin> = listOf(
        CLASSIC,
        CYAN_RING,
        GOLDEN_STAR,
        COSMIC_VOID,
        MOLTEN_SUN,
        MATRIX_RADAR,
        RAINBOW_CHROMA,
        TECH_HEXAGON
    )

    fun getSkinById(id: String): DotSkin {
        return allSkins.find { it.id == id } ?: CLASSIC
    }
}
