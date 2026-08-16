package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

/**
 * Rich, high-vibrancy Golden Coin icon with multi-layered metallic gold gradient,
 * shiny rim border, and a mathematically centered embossed star.
 */
@Composable
fun VibrantGoldenCoin(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(elevation = 2.dp, shape = CircleShape)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFF375), // Bright radiant gold highlight
                        Color(0xFFFFD700), // Pure golden yellow
                        Color(0xFFFFB300), // Deep amber gold
                        Color(0xFFE65100)  // Rich bronze outer edge
                    )
                )
            )
            .border(
                width = 1.2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFFF9C4), // Shining edge
                        Color(0xFFFF8F00)  // Darker gold border
                    )
                ),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        // Inner inset ring with precisely centered geometric star
        Canvas(modifier = Modifier.size(size * 0.72f)) {
            val cX = this.size.width / 2f
            val cY = this.size.height / 2f
            val outerRadius = this.size.width * 0.44f
            val innerRadius = outerRadius * 0.45f

            // Inner circle border
            drawCircle(
                color = Color(0x66FF8F00),
                radius = this.size.width / 2f * 0.95f,
                center = Offset(cX, cY),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 0.8.dp.toPx())
            )

            // Mathematically centered 5-point star
            val starPath = Path()
            val points = 5
            val step = Math.PI / points
            var angle = -Math.PI / 2.0 // start at top vertex

            for (i in 0 until (points * 2)) {
                val r = if (i % 2 == 0) outerRadius else innerRadius
                val x = (cX + r * cos(angle)).toFloat()
                val y = (cY + r * sin(angle)).toFloat()
                if (i == 0) starPath.moveTo(x, y) else starPath.lineTo(x, y)
                angle += step
            }
            starPath.close()

            // Shadow / emboss under star
            drawPath(
                path = starPath,
                color = Color(0xFF5D4037)
            )
        }
    }
}
