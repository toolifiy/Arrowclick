package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.ArrowSkin
import com.example.model.ArrowTailStyle
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

data class ArrowPosition(
    val centerX: Float,
    val centerY: Float,
    val angleDeg: Float,
    val lengthPx: Float,
    val tipX: Float,
    val tipY: Float,
    val tailX: Float,
    val tailY: Float
)

@Composable
fun ArrowGameCanvas(
    skin: ArrowSkin,
    isArrowVisible: Boolean,
    onArrowSpawned: (spawnTimeMs: Long) -> Unit,
    onTipClicked: (reactionTimeMs: Long, tipOffset: Offset) -> Unit,
    onMissClicked: (touchOffset: Offset) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    var arrowPos by remember { mutableStateOf<ArrowPosition?>(null) }
    var spawnTimeMs by remember { mutableLongStateOf(0L) }

    // Pulse animation for the glowing tip
    val infiniteTransition = rememberInfiniteTransition(label = "tip_pulse")
    val tipPulseScale by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(550, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val strokeWidthPx = with(density) { skin.strokeWidthDp.dp.toPx() }
    val headWingLengthPx = with(density) { skin.headWingLengthDp.dp.toPx() }
    val hitRadiusPx = with(density) { 68.dp.toPx() } // Generous hit radius for responsive finger taps on 2x arrow

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }

        // Recalculate arrow angle (FIXED IN SCREEN CENTER) whenever it becomes visible again
        LaunchedEffect(isArrowVisible, widthPx, heightPx) {
            if (isArrowVisible && widthPx > 100f && heightPx > 100f) {
                // Double size arrow (2X length)
                val maxAvailable = minOf(widthPx * 0.85f, heightPx * 0.55f)
                val arrowLength = maxAvailable.coerceIn(
                    with(density) { 280.dp.toPx() },
                    with(density) { 400.dp.toPx() }
                )

                // Fixed exactly in center
                val cX = widthPx / 2f
                val cY = heightPx / 2f
                val angleDeg = Random.nextFloat() * 360f
                val angleRad = Math.toRadians(angleDeg.toDouble())

                val halfL = arrowLength / 2f
                val tipX = (cX + halfL * cos(angleRad)).toFloat()
                val tipY = (cY + halfL * sin(angleRad)).toFloat()
                val tailX = (cX - halfL * cos(angleRad)).toFloat()
                val tailY = (cY - halfL * sin(angleRad)).toFloat()

                arrowPos = ArrowPosition(
                    centerX = cX,
                    centerY = cY,
                    angleDeg = angleDeg,
                    lengthPx = arrowLength,
                    tipX = tipX,
                    tipY = tipY,
                    tailX = tailX,
                    tailY = tailY
                )
                val now = System.currentTimeMillis()
                spawnTimeMs = now
                onArrowSpawned(now)
            }
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("arrow_game_canvas")
                .pointerInput(isArrowVisible, arrowPos) {
                    detectTapGestures { tapOffset ->
                        if (!isArrowVisible) return@detectTapGestures
                        val currentArrow = arrowPos ?: return@detectTapGestures
                        val dx = tapOffset.x - currentArrow.tipX
                        val dy = tapOffset.y - currentArrow.tipY
                        val distance = sqrt((dx * dx + dy * dy).toDouble()).toFloat()

                        if (distance <= hitRadiusPx) {
                            val now = System.currentTimeMillis()
                            val reaction = (now - spawnTimeMs).coerceAtLeast(1L)
                            onTipClicked(reaction, Offset(currentArrow.tipX, currentArrow.tipY))
                        } else {
                            onMissClicked(tapOffset)
                        }
                    }
                }
        ) {
            if (isArrowVisible && arrowPos != null) {
                val pos = arrowPos!!
                drawCustomArrow(
                    pos = pos,
                    skin = skin,
                    strokeWidthPx = strokeWidthPx,
                    headWingLengthPx = headWingLengthPx,
                    tipPulseScale = tipPulseScale,
                    density = density
                )
            }
        }
    }
}

private fun DrawScope.drawCustomArrow(
    pos: ArrowPosition,
    skin: ArrowSkin,
    strokeWidthPx: Float,
    headWingLengthPx: Float,
    tipPulseScale: Float,
    density: androidx.compose.ui.unit.Density
) {
    val angleRad = Math.toRadians(pos.angleDeg.toDouble())
    val wingAngleRad = Math.toRadians(skin.headWingAngleDeg.toDouble())

    // 1. Draw arrow main shaft
    when (skin.tailStyle) {
        ArrowTailStyle.NEON_CYBER -> {
            drawLine(
                color = skin.tipGlowColor.copy(alpha = 0.35f),
                start = Offset(pos.tailX, pos.tailY),
                end = Offset(pos.tipX, pos.tipY),
                strokeWidth = strokeWidthPx * 1.9f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = skin.strokeColor,
                start = Offset(pos.tailX, pos.tailY),
                end = Offset(pos.tipX, pos.tipY),
                strokeWidth = strokeWidthPx,
                cap = StrokeCap.Round
            )
        }
        ArrowTailStyle.GOLDEN_CHROME -> {
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFFFD54F), Color(0xFFFF8F00), Color(0xFFFFE082)),
                    start = Offset(pos.tailX, pos.tailY),
                    end = Offset(pos.tipX, pos.tipY)
                ),
                start = Offset(pos.tailX, pos.tailY),
                end = Offset(pos.tipX, pos.tipY),
                strokeWidth = strokeWidthPx,
                cap = StrokeCap.Round
            )
        }
        ArrowTailStyle.FIRE_EMBER -> {
            drawLine(
                color = Color(0xFFFFAB00).copy(alpha = 0.35f),
                start = Offset(pos.tailX, pos.tailY),
                end = Offset(pos.tipX, pos.tipY),
                strokeWidth = strokeWidthPx * 1.8f,
                cap = StrokeCap.Round
            )
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFFF3D00), Color(0xFFFF9100), Color(0xFFFF1744)),
                    start = Offset(pos.tailX, pos.tailY),
                    end = Offset(pos.tipX, pos.tipY)
                ),
                start = Offset(pos.tailX, pos.tailY),
                end = Offset(pos.tipX, pos.tipY),
                strokeWidth = strokeWidthPx,
                cap = StrokeCap.Round
            )
        }
        else -> {
            drawLine(
                color = skin.strokeColor,
                start = Offset(pos.tailX, pos.tailY),
                end = Offset(pos.tipX, pos.tipY),
                strokeWidth = strokeWidthPx,
                cap = StrokeCap.Round
            )
        }
    }

    // 2. Draw arrow head wings pointing backward from tip
    val wing1Angle = angleRad + Math.PI - wingAngleRad
    val wing2Angle = angleRad + Math.PI + wingAngleRad

    val w1X = (pos.tipX + headWingLengthPx * cos(wing1Angle)).toFloat()
    val w1Y = (pos.tipY + headWingLengthPx * sin(wing1Angle)).toFloat()

    val w2X = (pos.tipX + headWingLengthPx * cos(wing2Angle)).toFloat()
    val w2Y = (pos.tipY + headWingLengthPx * sin(wing2Angle)).toFloat()

    val headPath = Path().apply {
        moveTo(w1X, w1Y)
        lineTo(pos.tipX, pos.tipY)
        lineTo(w2X, w2Y)
    }

    if (skin.tailStyle == ArrowTailStyle.NEON_CYBER) {
        drawPath(
            path = headPath,
            color = skin.tipGlowColor.copy(alpha = 0.35f),
            style = Stroke(
                width = strokeWidthPx * 1.9f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }

    drawPath(
        path = headPath,
        color = skin.strokeColor,
        style = Stroke(
            width = strokeWidthPx,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )

    // Optional tail feathers/accents for unique skins
    if (skin.tailStyle == ArrowTailStyle.COSMIC_STAR || skin.tailStyle == ArrowTailStyle.STEALTH_OBSIDIAN) {
        val tailWingLen = headWingLengthPx * 0.55f
        val tailW1X = (pos.tailX + tailWingLen * cos(angleRad + wingAngleRad)).toFloat()
        val tailW1Y = (pos.tailY + tailWingLen * sin(angleRad + wingAngleRad)).toFloat()
        val tailW2X = (pos.tailX + tailWingLen * cos(angleRad - wingAngleRad)).toFloat()
        val tailW2Y = (pos.tailY + tailWingLen * sin(angleRad - wingAngleRad)).toFloat()

        drawLine(
            color = skin.strokeColor,
            start = Offset(pos.tailX, pos.tailY),
            end = Offset(tailW1X, tailW1Y),
            strokeWidth = strokeWidthPx * 0.75f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = skin.strokeColor,
            start = Offset(pos.tailX, pos.tailY),
            end = Offset(tailW2X, tailW2Y),
            strokeWidth = strokeWidthPx * 0.75f,
            cap = StrokeCap.Round
        )
    }

    // 3. Draw glowing tip dot
    val glowRadiusPx = with(density) { skin.glowRadiusDp.dp.toPx() } * tipPulseScale
    val coreRadiusPx = with(density) { 8.dp.toPx() }

    // Outer soft glow halo
    drawCircle(
        color = skin.tipGlowColor.copy(alpha = 0.4f),
        radius = glowRadiusPx,
        center = Offset(pos.tipX, pos.tipY)
    )

    // Mid glow ring
    drawCircle(
        color = skin.tipGlowColor.copy(alpha = 0.85f),
        radius = coreRadiusPx * 1.4f,
        center = Offset(pos.tipX, pos.tipY)
    )

    // Vibrant hot center core
    drawCircle(
        color = skin.tipCenterColor,
        radius = coreRadiusPx,
        center = Offset(pos.tipX, pos.tipY)
    )
}
