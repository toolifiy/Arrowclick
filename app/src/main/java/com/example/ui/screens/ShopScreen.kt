package com.example.ui.screens

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ArrowSkin
import com.example.model.ArrowSkinCatalog
import com.example.model.ArrowTailStyle
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ShopScreen(
    coins: Int,
    unlockedSkinIds: Set<String>,
    equippedSkinId: String,
    onBuySkin: (ArrowSkin) -> Unit,
    onEquipSkin: (String) -> Unit,
    onBack: () -> Unit,
    message: String?,
    onClearMessage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    var previewSkin by remember(equippedSkinId) {
        mutableStateOf(ArrowSkinCatalog.getSkinById(equippedSkinId))
    }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(message) {
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            onClearMessage()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFC))
            .testTag("shop_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("shop_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF111111)
                    )
                }

                Text(
                    text = "ARROW VAULT",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    color = Color(0xFF111111)
                )

                // Coin balance badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        com.example.ui.components.VibrantGoldenCoin(size = 20.dp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$coins",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111111)
                        )
                    }
                }
            }

            // Big Showcase Box for Previewing Selected Skin
            ArrowPreviewHero(skin = previewSkin)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "AVAILABLE DESIGNS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = Color(0xFF777777),
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
            )

            // List of all skins
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(ArrowSkinCatalog.allSkins) { skin ->
                    val isUnlocked = unlockedSkinIds.contains(skin.id)
                    val isEquipped = equippedSkinId == skin.id
                    val isSelected = previewSkin.id == skin.id

                    SkinItemCard(
                        skin = skin,
                        isUnlocked = isUnlocked,
                        isEquipped = isEquipped,
                        isSelected = isSelected,
                        userCoins = coins,
                        onSelect = {
                            previewSkin = skin
                        },
                        onBuy = {
                            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                            onBuySkin(skin)
                        },
                        onEquip = {
                            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                            onEquipSkin(skin.id)
                        }
                    )
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }
}

@Composable
private fun ArrowPreviewHero(skin: ArrowSkin) {
    val infiniteTransition = rememberInfiniteTransition(label = "hero_rotate")
    val rotationDeg by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "hero_rotation"
    )

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("skin_preview_hero")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFFBFBFC)),
                contentAlignment = Alignment.Center
            ) {
                SingleArrowStaticCanvas(
                    skin = skin,
                    angleDeg = rotationDeg,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = skin.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF111111)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = skin.description,
                fontSize = 13.sp,
                color = Color(0xFF666666),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun SkinItemCard(
    skin: ArrowSkin,
    isUnlocked: Boolean,
    isEquipped: Boolean,
    isSelected: Boolean,
    userCoins: Int,
    onSelect: () -> Unit,
    onBuy: () -> Unit,
    onEquip: () -> Unit
) {
    val borderColor = if (isSelected) Color(0xFF111111) else Color.Transparent

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color.White else Color(0xFFFFFFFF)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, borderColor, RoundedCornerShape(18.dp))
            .clickable { onSelect() }
            .testTag("skin_card_${skin.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mini Arrow Canvas Preview
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF7F7FA)),
                contentAlignment = Alignment.Center
            ) {
                SingleArrowStaticCanvas(
                    skin = skin,
                    angleDeg = -45f,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = skin.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111111)
                )
                Spacer(modifier = Modifier.height(2.dp))
                if (isUnlocked) {
                    Text(
                        text = "Unlocked",
                        fontSize = 12.sp,
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${skin.price} Coins",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF555555)
                        )
                    }
                }
            }

            // Action Button
            when {
                isEquipped -> {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFE8F5E9)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ACTIVE",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                }
                isUnlocked -> {
                    Button(
                        onClick = onEquip,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF111111)),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("equip_button_${skin.id}")
                    ) {
                        Text(
                            text = "EQUIP",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                else -> {
                    val canAfford = userCoins >= skin.price
                    Button(
                        onClick = onBuy,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (canAfford) Color(0xFFFFB300) else Color(0xFFE0E0E0)
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("buy_button_${skin.id}")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (canAfford) Icons.Default.ShoppingCart else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (canAfford) Color.White else Color(0xFF888888),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${skin.price}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = if (canAfford) Color.White else Color(0xFF888888)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SingleArrowStaticCanvas(
    skin: ArrowSkin,
    angleDeg: Float,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    Canvas(modifier = modifier) {
        val scaleFactor = (size.width / 260f).coerceIn(0.28f, 1f)
        val strokeWidthPx = with(density) { (skin.strokeWidthDp * scaleFactor).dp.toPx() }
        val headWingLengthPx = with(density) { (skin.headWingLengthDp * scaleFactor).dp.toPx() }
        val cX = size.width / 2f
        val cY = size.height / 2f
        val arrowLength = size.width * 0.62f
        val halfL = arrowLength / 2f

        val angleRad = Math.toRadians(angleDeg.toDouble())
        val tipX = (cX + halfL * cos(angleRad)).toFloat()
        val tipY = (cY + halfL * sin(angleRad)).toFloat()
        val tailX = (cX - halfL * cos(angleRad)).toFloat()
        val tailY = (cY - halfL * sin(angleRad)).toFloat()

        val wingAngleRad = Math.toRadians(skin.headWingAngleDeg.toDouble())
        val w1Angle = angleRad + Math.PI - wingAngleRad
        val w2Angle = angleRad + Math.PI + wingAngleRad

        val w1X = (tipX + headWingLengthPx * cos(w1Angle)).toFloat()
        val w1Y = (tipY + headWingLengthPx * sin(w1Angle)).toFloat()
        val w2X = (tipX + headWingLengthPx * cos(w2Angle)).toFloat()
        val w2Y = (tipY + headWingLengthPx * sin(w2Angle)).toFloat()

        // Draw shaft
        drawLine(
            color = skin.strokeColor,
            start = Offset(tailX, tailY),
            end = Offset(tipX, tipY),
            strokeWidth = strokeWidthPx,
            cap = StrokeCap.Round
        )

        // Draw head
        val headPath = Path().apply {
            moveTo(w1X, w1Y)
            lineTo(tipX, tipY)
            lineTo(w2X, w2Y)
        }
        drawPath(
            path = headPath,
            color = skin.strokeColor,
            style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
        )

        // Draw Tip
        val glowR = with(density) { (skin.glowRadiusDp * scaleFactor).coerceAtLeast(4f).dp.toPx() }
        val coreR = with(density) { (6f * scaleFactor).coerceAtLeast(2.5f).dp.toPx() }

        drawCircle(
            color = skin.tipGlowColor.copy(alpha = 0.5f),
            radius = glowR,
            center = Offset(tipX, tipY)
        )
        drawCircle(
            color = skin.tipCenterColor,
            radius = coreR,
            center = Offset(tipX, tipY)
        )
    }
}
