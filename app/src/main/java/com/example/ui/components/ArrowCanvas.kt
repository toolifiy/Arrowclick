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
    onTailClicked: (touchOffset: Offset) -> Unit = {},
    dotSkin: com.example.model.DotSkin = com.example.model.DotSkinCatalog.CLASSIC,
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
    val hitRadiusPx = with(density) { 68.dp.toPx() }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }

        // Recalculate arrow angle (FIXED IN SCREEN CENTER) whenever it becomes visible again
        LaunchedEffect(isArrowVisible, widthPx, heightPx) {
            if (isArrowVisible && widthPx > 100f && heightPx > 100f) {
                val maxAvailable = minOf(widthPx * 0.82f, heightPx * 0.52f)
                val arrowLength = maxAvailable.coerceIn(
                    with(density) { 200.dp.toPx() },
                    with(density) { 380.dp.toPx() }
                )

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
                            // Any touch that is not on the active target tip is counted as a miss immediately!
                            onMissClicked(tapOffset)
                        }
                    }
                }
        ) {
            if (isArrowVisible && arrowPos != null) {
                val pos = arrowPos!!
                drawSkinObject(
                    pos = pos,
                    skin = skin,
                    strokeWidthPx = strokeWidthPx,
                    headWingLengthPx = headWingLengthPx,
                    tipPulseScale = tipPulseScale,
                    density = density,
                    dotSkin = dotSkin
                )
            }
        }
    }
}

fun DrawScope.drawSkinObject(
    pos: ArrowPosition,
    skin: ArrowSkin,
    strokeWidthPx: Float,
    headWingLengthPx: Float,
    tipPulseScale: Float,
    density: androidx.compose.ui.unit.Density,
    dotSkin: com.example.model.DotSkin
) {
    val angleRad = Math.toRadians(pos.angleDeg.toDouble())
    val wingAngleRad = Math.toRadians(skin.headWingAngleDeg.toDouble())
    val perpAngleRad = angleRad + Math.PI / 2.0

    when (skin.tailStyle) {
        // 1. REALISTIC SNAKE: Wavy textured serpent body, scales pattern, viper head, snake eyes, and red strike tip
        ArrowTailStyle.SNAKE_REALISTIC -> {
            val segments = 32
            val path = Path()
            val waveAmp = strokeWidthPx * 0.9f
            val waveFreq = 3.2

            var prevPt: Offset? = null
            for (i in 0..segments) {
                val t = i.toFloat() / segments
                val baseX = pos.tailX + (pos.tipX - pos.tailX) * t
                val baseY = pos.tailY + (pos.tipY - pos.tailY) * t
                // Sinusoidal serpentine slither
                val waveOffset = (sin(t * Math.PI * 2.0 * waveFreq) * waveAmp * (1f - t * 0.3f)).toFloat()
                val px = (baseX + waveOffset * cos(perpAngleRad)).toFloat()
                val py = (baseY + waveOffset * sin(perpAngleRad)).toFloat()

                if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                prevPt = Offset(px, py)
            }

            // Outer snake shadow/body
            drawPath(
                path = path,
                color = Color(0xFF1B5E20),
                style = Stroke(width = strokeWidthPx * 1.3f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
            // Mid vibrant green scales
            drawPath(
                path = path,
                color = Color(0xFF4CAF50),
                style = Stroke(width = strokeWidthPx * 0.85f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
            // Center snake spine/pattern dots
            for (i in 2 until segments step 2) {
                val t = i.toFloat() / segments
                val baseX = pos.tailX + (pos.tipX - pos.tailX) * t
                val baseY = pos.tailY + (pos.tipY - pos.tailY) * t
                val waveOffset = (sin(t * Math.PI * 2.0 * waveFreq) * waveAmp * (1f - t * 0.3f)).toFloat()
                val px = (baseX + waveOffset * cos(perpAngleRad)).toFloat()
                val py = (baseY + waveOffset * sin(perpAngleRad)).toFloat()

                drawCircle(
                    color = Color(0xFFFFD54F),
                    radius = strokeWidthPx * 0.22f,
                    center = Offset(px, py)
                )
            }

            // Snake Viper Head (Diamond shaped)
            val headLen = strokeWidthPx * 2.2f
            val headWid = strokeWidthPx * 1.5f
            val hLeft = Offset(
                (pos.tipX - headLen * 0.6f * cos(angleRad) + headWid * 0.6f * cos(perpAngleRad)).toFloat(),
                (pos.tipY - headLen * 0.6f * sin(angleRad) + headWid * 0.6f * sin(perpAngleRad)).toFloat()
            )
            val hRight = Offset(
                (pos.tipX - headLen * 0.6f * cos(angleRad) - headWid * 0.6f * cos(perpAngleRad)).toFloat(),
                (pos.tipY - headLen * 0.6f * sin(angleRad) - headWid * 0.6f * sin(perpAngleRad)).toFloat()
            )
            val hBack = Offset(
                (pos.tipX - headLen * cos(angleRad)).toFloat(),
                (pos.tipY - headLen * sin(angleRad)).toFloat()
            )
            val viperHead = Path().apply {
                moveTo(pos.tipX, pos.tipY)
                lineTo(hLeft.x, hLeft.y)
                lineTo(hBack.x, hBack.y)
                lineTo(hRight.x, hRight.y)
                close()
            }
            drawPath(path = viperHead, color = Color(0xFF2E7D32))

            // Snake Eyes (Yellow venom eyes)
            val eyeDist = strokeWidthPx * 0.35f
            val eyeLeft = Offset(
                (pos.tipX - headLen * 0.4f * cos(angleRad) + eyeDist * cos(perpAngleRad)).toFloat(),
                (pos.tipY - headLen * 0.4f * sin(angleRad) + eyeDist * sin(perpAngleRad)).toFloat()
            )
            val eyeRight = Offset(
                (pos.tipX - headLen * 0.4f * cos(angleRad) - eyeDist * cos(perpAngleRad)).toFloat(),
                (pos.tipY - headLen * 0.4f * sin(angleRad) - eyeDist * sin(perpAngleRad)).toFloat()
            )
            drawCircle(color = Color(0xFFFFEB3B), radius = strokeWidthPx * 0.16f, center = eyeLeft)
            drawCircle(color = Color(0xFFFFEB3B), radius = strokeWidthPx * 0.16f, center = eyeRight)
            drawCircle(color = Color(0xFF000000), radius = strokeWidthPx * 0.08f, center = eyeLeft)
            drawCircle(color = Color(0xFF000000), radius = strokeWidthPx * 0.08f, center = eyeRight)
        }

        // 2. RED TIP VECTOR BEAM: Long smooth minimalist line with hot red laser point (NO ARROW WINGS)
        ArrowTailStyle.RED_TIP_BEAM -> {
            // Shadow / outer track
            drawLine(
                color = Color(0xFFE0E0E0),
                start = Offset(pos.tailX, pos.tailY),
                end = Offset(pos.tipX, pos.tipY),
                strokeWidth = strokeWidthPx * 1.5f,
                cap = StrokeCap.Round
            )
            // Solid dark sleek shaft
            drawLine(
                color = skin.strokeColor,
                start = Offset(pos.tailX, pos.tailY),
                end = Offset(pos.tipX, pos.tipY),
                strokeWidth = strokeWidthPx,
                cap = StrokeCap.Round
            )
            // Red gradient along the front 30% of the line
            val subTipX = (pos.tipX - (pos.tipX - pos.tailX) * 0.35f)
            val subTipY = (pos.tipY - (pos.tipY - pos.tailY) * 0.35f)
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(Color.Transparent, Color(0xFFFF1744)),
                    start = Offset(subTipX, subTipY),
                    end = Offset(pos.tipX, pos.tipY)
                ),
                start = Offset(subTipX, subTipY),
                end = Offset(pos.tipX, pos.tipY),
                strokeWidth = strokeWidthPx * 1.1f,
                cap = StrokeCap.Round
            )
        }

        // 3. LIGHTNING BOLT: Jagged high-voltage electric bolt
        ArrowTailStyle.LIGHTNING_BOLT -> {
            val boltPath = Path()
            boltPath.moveTo(pos.tailX, pos.tailY)

            val segs = 6
            var currentX = pos.tailX
            var currentY = pos.tailY
            val dx = (pos.tipX - pos.tailX) / segs
            val dy = (pos.tipY - pos.tailY) / segs
            val zigzagAmp = strokeWidthPx * 1.4f

            for (i in 1 until segs) {
                val side = if (i % 2 == 0) 1f else -1f
                val targetX = pos.tailX + dx * i + (side * zigzagAmp * cos(perpAngleRad)).toFloat()
                val targetY = pos.tailY + dy * i + (side * zigzagAmp * sin(perpAngleRad)).toFloat()
                boltPath.lineTo(targetX, targetY)
            }
            boltPath.lineTo(pos.tipX, pos.tipY)

            // Outer electric aura
            drawPath(
                path = boltPath,
                color = Color(0xFFFFEA00).copy(alpha = 0.4f),
                style = Stroke(width = strokeWidthPx * 2.2f, cap = StrokeCap.Round, join = StrokeJoin.Miter)
            )
            // Core lightning
            drawPath(
                path = boltPath,
                color = Color(0xFFFFD600),
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round, join = StrokeJoin.Miter)
            )
            // Center white hot line
            drawPath(
                path = boltPath,
                color = Color.White,
                style = Stroke(width = strokeWidthPx * 0.4f, cap = StrokeCap.Round, join = StrokeJoin.Miter)
            )
        }

        // 4. DRAGON KATANA: Curved Samurai Sword blade with golden tsuba guard
        ArrowTailStyle.DRAGON_KATANA -> {
            // Blade shaft
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF37474F), Color(0xFFCFD8DC), Color(0xFFECEFF1)),
                    start = Offset(pos.tailX, pos.tailY),
                    end = Offset(pos.tipX, pos.tipY)
                ),
                start = Offset(pos.tailX, pos.tailY),
                end = Offset(pos.tipX, pos.tipY),
                strokeWidth = strokeWidthPx * 1.1f,
                cap = StrokeCap.Round
            )
            // Golden Katana Guard (Tsuba) at 25% from handle
            val tsubaX = pos.tailX + (pos.tipX - pos.tailX) * 0.25f
            val tsubaY = pos.tailY + (pos.tipY - pos.tailY) * 0.25f
            val guardW = strokeWidthPx * 2.4f
            val g1X = (tsubaX + guardW * 0.5f * cos(perpAngleRad)).toFloat()
            val g1Y = (tsubaY + guardW * 0.5f * sin(perpAngleRad)).toFloat()
            val g2X = (tsubaX - guardW * 0.5f * cos(perpAngleRad)).toFloat()
            val g2Y = (tsubaY - guardW * 0.5f * sin(perpAngleRad)).toFloat()
            drawLine(
                color = Color(0xFFFFB300),
                start = Offset(g1X, g1Y),
                end = Offset(g2X, g2Y),
                strokeWidth = strokeWidthPx * 0.65f,
                cap = StrokeCap.Round
            )
            // Sharp Blade Head Tip
            val w1Angle = angleRad + Math.PI - Math.toRadians(25.0)
            val w1X = (pos.tipX + headWingLengthPx * 0.8f * cos(w1Angle)).toFloat()
            val w1Y = (pos.tipY + headWingLengthPx * 0.8f * sin(w1Angle)).toFloat()
            drawLine(
                color = Color(0xFFCFD8DC),
                start = Offset(w1X, w1Y),
                end = Offset(pos.tipX, pos.tipY),
                strokeWidth = strokeWidthPx * 0.9f,
                cap = StrokeCap.Round
            )
        }

        // 5. RAINBOW SPECTRUM: Prismatic chromatic beam
        ArrowTailStyle.RAINBOW_HYPER -> {
            val rainbowBrush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFFF1744),
                    Color(0xFFFF9100),
                    Color(0xFFFFEA00),
                    Color(0xFF00E676),
                    Color(0xFF00E5FF),
                    Color(0xFF7C4DFF)
                ),
                start = Offset(pos.tailX, pos.tailY),
                end = Offset(pos.tipX, pos.tipY)
            )
            drawLine(
                brush = rainbowBrush,
                start = Offset(pos.tailX, pos.tailY),
                end = Offset(pos.tipX, pos.tipY),
                strokeWidth = strokeWidthPx * 1.3f,
                cap = StrokeCap.Round
            )
            // Arrow Head
            drawClassicHead(pos, skin, headWingLengthPx, wingAngleRad, angleRad, strokeWidthPx)
        }

        // 6. MECHA RAILGUN: Cybernetic futuristic heavy chassis
        ArrowTailStyle.MECHA_RAILGUN -> {
            // Twin magnetic rails
            val railOffset = strokeWidthPx * 0.55f
            val r1StartX = (pos.tailX + railOffset * cos(perpAngleRad)).toFloat()
            val r1StartY = (pos.tailY + railOffset * sin(perpAngleRad)).toFloat()
            val r1EndX = (pos.tipX + railOffset * cos(perpAngleRad)).toFloat()
            val r1EndY = (pos.tipY + railOffset * sin(perpAngleRad)).toFloat()

            val r2StartX = (pos.tailX - railOffset * cos(perpAngleRad)).toFloat()
            val r2StartY = (pos.tailY - railOffset * sin(perpAngleRad)).toFloat()
            val r2EndX = (pos.tipX - railOffset * cos(perpAngleRad)).toFloat()
            val r2EndY = (pos.tipY - railOffset * sin(perpAngleRad)).toFloat()

            drawLine(
                color = Color(0xFF37474F),
                start = Offset(r1StartX, r1StartY),
                end = Offset(r1EndX, r1EndY),
                strokeWidth = strokeWidthPx * 0.5f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color(0xFF37474F),
                start = Offset(r2StartX, r2StartY),
                end = Offset(r2EndX, r2EndY),
                strokeWidth = strokeWidthPx * 0.5f,
                cap = StrokeCap.Round
            )
            // Glowing core energy beam inside
            drawLine(
                color = Color(0xFF00E676),
                start = Offset(pos.tailX, pos.tailY),
                end = Offset(pos.tipX, pos.tipY),
                strokeWidth = strokeWidthPx * 0.6f,
                cap = StrokeCap.Round
            )
            drawClassicHead(pos, skin, headWingLengthPx * 0.8f, wingAngleRad, angleRad, strokeWidthPx)
        }

        // 7. NEON CYBER
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
            drawClassicHead(pos, skin, headWingLengthPx, wingAngleRad, angleRad, strokeWidthPx)
        }

        // 8. GOLDEN CHROME
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
            drawClassicHead(pos, skin, headWingLengthPx, wingAngleRad, angleRad, strokeWidthPx)
        }

        // 9. FIRE EMBER
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
            drawClassicHead(pos, skin, headWingLengthPx, wingAngleRad, angleRad, strokeWidthPx)
        }

        // 10. CLASSIC & REMAINING
        else -> {
            drawLine(
                color = skin.strokeColor,
                start = Offset(pos.tailX, pos.tailY),
                end = Offset(pos.tipX, pos.tipY),
                strokeWidth = strokeWidthPx,
                cap = StrokeCap.Round
            )
            if (skin.headWingLengthDp > 0) {
                drawClassicHead(pos, skin, headWingLengthPx, wingAngleRad, angleRad, strokeWidthPx)
            }
        }
    }

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

    // DRAW GLOWING TIP (THE CLICK TARGET!) USING SELECTED DOT SKIN
    val glowRadiusPx = with(density) { dotSkin.glowRadiusDp.dp.toPx() } * tipPulseScale
    val coreRadiusPx = with(density) { 8.dp.toPx() }
    val centerOffset = Offset(pos.tipX, pos.tipY)

    when (dotSkin.style) {
        com.example.model.DotStyle.CLASSIC_TARGET -> {
            drawCircle(
                color = dotSkin.glowColor.copy(alpha = 0.45f),
                radius = glowRadiusPx,
                center = centerOffset
            )
            drawCircle(
                color = dotSkin.glowColor.copy(alpha = 0.85f),
                radius = coreRadiusPx * 1.4f,
                center = centerOffset
            )
            drawCircle(
                color = dotSkin.centerColor,
                radius = coreRadiusPx,
                center = centerOffset
            )
        }
        com.example.model.DotStyle.ELECTRIC_RING -> {
            drawCircle(
                color = dotSkin.glowColor.copy(alpha = 0.3f),
                radius = glowRadiusPx * 1.2f,
                center = centerOffset
            )
            drawCircle(
                color = dotSkin.glowColor,
                radius = coreRadiusPx * 1.8f * tipPulseScale,
                center = centerOffset,
                style = Stroke(width = with(density) { 3.dp.toPx() })
            )
            drawCircle(
                color = dotSkin.centerColor,
                radius = coreRadiusPx * 0.9f,
                center = centerOffset
            )
        }
        com.example.model.DotStyle.STAR_BURST -> {
            drawCircle(
                color = dotSkin.glowColor.copy(alpha = 0.4f),
                radius = glowRadiusPx,
                center = centerOffset
            )
            val rayLength = coreRadiusPx * 3.5f * tipPulseScale
            drawLine(
                color = dotSkin.glowColor,
                start = Offset(pos.tipX - rayLength, pos.tipY),
                end = Offset(pos.tipX + rayLength, pos.tipY),
                strokeWidth = with(density) { 3.dp.toPx() },
                cap = StrokeCap.Round
            )
            drawLine(
                color = dotSkin.glowColor,
                start = Offset(pos.tipX, pos.tipY - rayLength),
                end = Offset(pos.tipX, pos.tipY + rayLength),
                strokeWidth = with(density) { 3.dp.toPx() },
                cap = StrokeCap.Round
            )
            drawCircle(
                color = dotSkin.centerColor,
                radius = coreRadiusPx * 1.1f,
                center = centerOffset
            )
        }
        com.example.model.DotStyle.COSMIC_SINGULARITY -> {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(dotSkin.centerColor, dotSkin.glowColor, Color.Transparent),
                    center = centerOffset,
                    radius = glowRadiusPx * 1.3f
                ),
                radius = glowRadiusPx * 1.3f,
                center = centerOffset
            )
            drawCircle(
                color = Color(0xFF111122),
                radius = coreRadiusPx * 1.1f,
                center = centerOffset
            )
            drawCircle(
                color = dotSkin.glowColor,
                radius = coreRadiusPx * 1.2f,
                center = centerOffset,
                style = Stroke(width = with(density) { 2.dp.toPx() })
            )
        }
        com.example.model.DotStyle.MOLTEN_SUN -> {
            drawCircle(
                color = dotSkin.glowColor.copy(alpha = 0.25f),
                radius = glowRadiusPx * 1.4f,
                center = centerOffset
            )
            drawCircle(
                color = dotSkin.glowColor.copy(alpha = 0.65f),
                radius = glowRadiusPx * 0.9f,
                center = centerOffset
            )
            drawCircle(
                color = dotSkin.centerColor,
                radius = coreRadiusPx * 1.4f * tipPulseScale,
                center = centerOffset
            )
        }
        com.example.model.DotStyle.MATRIX_RADAR -> {
            drawCircle(
                color = dotSkin.glowColor.copy(alpha = 0.7f),
                radius = coreRadiusPx * 2.2f * tipPulseScale,
                center = centerOffset,
                style = Stroke(width = with(density) { 1.5.dp.toPx() })
            )
            val lineOffset = coreRadiusPx * 2.8f
            drawLine(
                color = dotSkin.glowColor,
                start = Offset(pos.tipX - lineOffset, pos.tipY),
                end = Offset(pos.tipX + lineOffset, pos.tipY),
                strokeWidth = with(density) { 1.dp.toPx() }
            )
            drawLine(
                color = dotSkin.glowColor,
                start = Offset(pos.tipX, pos.tipY - lineOffset),
                end = Offset(pos.tipX, pos.tipY + lineOffset),
                strokeWidth = with(density) { 1.dp.toPx() }
            )
            drawCircle(
                color = dotSkin.centerColor,
                radius = coreRadiusPx * 0.6f,
                center = centerOffset
            )
        }
        com.example.model.DotStyle.RAINBOW_CHROMA -> {
            val offsetVal = (3.dp.toPx() * tipPulseScale)
            drawCircle(
                color = Color.Red.copy(alpha = 0.7f),
                radius = coreRadiusPx * 1.5f,
                center = Offset(pos.tipX - offsetVal, pos.tipY - offsetVal)
            )
            drawCircle(
                color = Color.Green.copy(alpha = 0.7f),
                radius = coreRadiusPx * 1.5f,
                center = Offset(pos.tipX + offsetVal, pos.tipY)
            )
            drawCircle(
                color = Color.Blue.copy(alpha = 0.7f),
                radius = coreRadiusPx * 1.5f,
                center = Offset(pos.tipX, pos.tipY + offsetVal)
            )
            drawCircle(
                color = Color.White,
                radius = coreRadiusPx * 0.7f,
                center = centerOffset
            )
        }
        com.example.model.DotStyle.TECH_HEXAGON -> {
            val hexPath = Path()
            val radius = coreRadiusPx * 2.2f * tipPulseScale
            for (i in 0..5) {
                val angle = Math.toRadians((i * 60).toDouble())
                val hx = (pos.tipX + radius * cos(angle)).toFloat()
                val hy = (pos.tipY + radius * sin(angle)).toFloat()
                if (i == 0) hexPath.moveTo(hx, hy) else hexPath.lineTo(hx, hy)
            }
            hexPath.close()

            drawPath(
                path = hexPath,
                color = dotSkin.glowColor,
                style = Stroke(width = with(density) { 2.5.dp.toPx() })
            )
            drawCircle(
                color = dotSkin.centerColor,
                radius = coreRadiusPx,
                center = centerOffset
            )
        }
    }
}

private fun DrawScope.drawClassicHead(
    pos: ArrowPosition,
    skin: ArrowSkin,
    headWingLengthPx: Float,
    wingAngleRad: Double,
    angleRad: Double,
    strokeWidthPx: Float
) {
    if (headWingLengthPx <= 0) return
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
}
