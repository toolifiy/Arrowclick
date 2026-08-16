package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Rich, high-vibrancy Golden Coin icon with multi-layered metallic gold gradient,
 * shiny rim border, and embossed dollar symbol.
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
        // Inner inset ring for authentic coin emboss
        Box(
            modifier = Modifier
                .size(size * 0.72f)
                .clip(CircleShape)
                .border(0.8.dp, Color(0x66FF8F00), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "★",
                fontSize = (size.value * 0.42f).sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF795548),
                modifier = Modifier.offset(y = (-0.5).dp)
            )
        }
    }
}
