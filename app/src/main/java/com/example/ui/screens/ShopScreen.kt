package com.example.ui.screens

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonChecked
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ArrowSkin
import com.example.model.ArrowSkinCatalog
import com.example.model.DotSkin
import com.example.model.DotSkinCatalog
import com.example.ui.components.ArrowPosition
import com.example.ui.components.drawSkinObject
import kotlin.math.cos
import kotlin.math.sin

enum class ShopTab { ARROW, DOT }

@Composable
fun ShopScreen(
    coins: Int,
    unlockedSkinIds: Set<String>,
    equippedSkinId: String,
    onBuySkin: (ArrowSkin) -> Unit,
    onEquipSkin: (String) -> Unit,
    unlockedDotIds: Set<String>,
    equippedDotId: String,
    onBuyDot: (DotSkin) -> Unit,
    onEquipDot: (String) -> Unit,
    onBack: () -> Unit,
    message: String?,
    onClearMessage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    var activeTab by remember { mutableStateOf(ShopTab.ARROW) }

    // Keeps track of currently selected item for previewing in hero banner
    var previewSkin by remember(equippedSkinId) {
        mutableStateOf(ArrowSkinCatalog.getSkinById(equippedSkinId))
    }
    var previewDot by remember(equippedDotId) {
        mutableStateOf(DotSkinCatalog.getSkinById(equippedDotId))
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(message) {
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            onClearMessage()
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFC))
            .testTag("shop_screen")
    ) {
        val screenHeight = maxHeight
        val isCompactScreen = screenHeight < 640.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
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
                    text = "VAULT SHOP",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    color = Color(0xFF111111)
                )

                // Coin balance badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0x1A000000)),
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        com.example.ui.components.VibrantGoldenCoin(size = 20.dp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = com.example.util.FormatUtils.formatCoins(coins),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111111)
                        )
                    }
                }
            }

            // Category Tabs (Side-By-Side Two Big Elegant Selection Boxes)
            // Left Box: ARROW, Right Box: DOT as requested by user!
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Left Tab: Arrow Skins
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(68.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (activeTab == ShopTab.ARROW) Color(0xFF111111) else Color.White)
                        .border(
                            width = 2.dp,
                            color = if (activeTab == ShopTab.ARROW) Color(0xFF111111) else Color(0x12000000),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable {
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            activeTab = ShopTab.ARROW
                        }
                        .testTag("arrow_tab_box"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = if (activeTab == ShopTab.ARROW) Color.White else Color(0xFF777777),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ARROW",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = if (activeTab == ShopTab.ARROW) Color.White else Color(0xFF555555)
                        )
                    }
                }

                // Right Tab: Target Dot Skins
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(68.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (activeTab == ShopTab.DOT) Color(0xFF111111) else Color.White)
                        .border(
                            width = 2.dp,
                            color = if (activeTab == ShopTab.DOT) Color(0xFF111111) else Color(0x12000000),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable {
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            activeTab = ShopTab.DOT
                        }
                        .testTag("dot_tab_box"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.RadioButtonChecked,
                            contentDescription = null,
                            tint = if (activeTab == ShopTab.DOT) Color.White else Color(0xFF777777),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "TARGET DOT",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = if (activeTab == ShopTab.DOT) Color.White else Color(0xFF555555)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(if (isCompactScreen) 6.dp else 10.dp))

            // Hero Preview Banner Container
            if (activeTab == ShopTab.ARROW) {
                ArrowPreviewHero(skin = previewSkin, isCompact = isCompactScreen)
            } else {
                DotPreviewHero(dot = previewDot, isCompact = isCompactScreen)
            }

            Spacer(modifier = Modifier.height(if (isCompactScreen) 8.dp else 14.dp))

            // Subtitle
            Text(
                text = if (activeTab == ShopTab.ARROW) "ARROW DESIGNS (${ArrowSkinCatalog.allSkins.size})" else "DOT DESIGNS (${DotSkinCatalog.allSkins.size})",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                color = Color(0xFF777777),
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
            )

            // Dynamic list depending on the selected category tab
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                if (activeTab == ShopTab.ARROW) {
                    items(ArrowSkinCatalog.allSkins) { skin ->
                        val isUnlocked = unlockedSkinIds.contains(skin.id)
                        val isEquipped = equippedSkinId == skin.id
                        val isSelected = previewSkin.id == skin.id

                        SkinItemCard(
                            name = skin.name,
                            description = skin.description,
                            price = skin.price,
                            isUnlocked = isUnlocked,
                            isEquipped = isEquipped,
                            isSelected = isSelected,
                            userCoins = coins,
                            previewContent = {
                                SingleArrowStaticCanvas(
                                    skin = skin,
                                    angleDeg = -45f,
                                    modifier = Modifier.fillMaxSize()
                                )
                            },
                            onSelect = { previewSkin = skin },
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
                } else {
                    items(DotSkinCatalog.allSkins) { dot ->
                        val isUnlocked = unlockedDotIds.contains(dot.id)
                        val isEquipped = equippedDotId == dot.id
                        val isSelected = previewDot.id == dot.id

                        SkinItemCard(
                            name = dot.name,
                            description = dot.description,
                            price = dot.price,
                            isUnlocked = isUnlocked,
                            isEquipped = isEquipped,
                            isSelected = isSelected,
                            userCoins = coins,
                            previewContent = {
                                SingleDotStaticCanvas(
                                    dot = dot,
                                    modifier = Modifier.fillMaxSize()
                                )
                            },
                            onSelect = { previewDot = dot },
                            onBuy = {
                                view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                                onBuyDot(dot)
                            },
                            onEquip = {
                                view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                                onEquipDot(dot.id)
                            }
                        )
                    }
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
private fun ArrowPreviewHero(skin: ArrowSkin, isCompact: Boolean = false) {
    val infiniteTransition = rememberInfiniteTransition(label = "hero_rotate")
    val rotationDeg by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "hero_rotation"
    )

    Card(
        shape = RoundedCornerShape(if (isCompact) 16.dp else 24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0x0F000000)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("arrow_preview_hero")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isCompact) 10.dp else 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(if (isCompact) 80.dp else 130.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFBFBFC)),
                contentAlignment = Alignment.Center
            ) {
                SingleArrowStaticCanvas(
                    skin = skin,
                    angleDeg = rotationDeg,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(if (isCompact) 4.dp else 10.dp))

            Text(
                text = skin.name,
                fontSize = if (isCompact) 14.sp else 18.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF111111)
            )

            if (!isCompact) {
                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = skin.description,
                    fontSize = 12.sp,
                    color = Color(0xFF666666),
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun DotPreviewHero(dot: DotSkin, isCompact: Boolean = false) {
    val infiniteTransition = rememberInfiniteTransition(label = "dot_hero")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot_pulse"
    )

    Card(
        shape = RoundedCornerShape(if (isCompact) 16.dp else 24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0x0F000000)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("dot_preview_hero")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isCompact) 10.dp else 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(if (isCompact) 80.dp else 130.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFBFBFC)),
                contentAlignment = Alignment.Center
            ) {
                SingleDotStaticCanvas(
                    dot = dot,
                    pulseScale = pulseScale,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(if (isCompact) 4.dp else 10.dp))

            Text(
                text = dot.name,
                fontSize = if (isCompact) 14.sp else 18.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF111111)
            )

            if (!isCompact) {
                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = dot.description,
                    fontSize = 12.sp,
                    color = Color(0xFF666666),
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun SkinItemCard(
    name: String,
    description: String,
    price: Int,
    isUnlocked: Boolean,
    isEquipped: Boolean,
    isSelected: Boolean,
    userCoins: Int,
    previewContent: @Composable () -> Unit,
    onSelect: () -> Unit,
    onBuy: () -> Unit,
    onEquip: () -> Unit
) {
    val borderColor = if (isSelected) Color(0xFF111111) else Color.Transparent

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, borderColor, RoundedCornerShape(18.dp))
            .clickable { onSelect() }
            .testTag("skin_item_card_$name")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Preview Canvas Block
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF7F7FA)),
                contentAlignment = Alignment.Center
            ) {
                previewContent()
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Information details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    fontSize = 15.sp,
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
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${com.example.util.FormatUtils.formatCoins(price)} Coins",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF555555)
                        )
                    }
                }
            }

            // Equipped / Select triggers
            when {
                isEquipped -> {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFE8F5E9)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ACTIVE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                }
                isUnlocked -> {
                    Button(
                        onClick = onEquip,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF111111)),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(
                            text = "EQUIP",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                else -> {
                    val canAfford = userCoins >= price
                    Button(
                        onClick = onBuy,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (canAfford) Color(0xFFFFB300) else Color(0xFFE0E0E0)
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (canAfford) Icons.Default.ShoppingCart else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (canAfford) Color.White else Color(0xFF888888),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = com.example.util.FormatUtils.formatCoins(price),
                                fontSize = 12.sp,
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

        val pos = ArrowPosition(
            centerX = cX,
            centerY = cY,
            angleDeg = angleDeg,
            lengthPx = arrowLength,
            tipX = tipX,
            tipY = tipY,
            tailX = tailX,
            tailY = tailY
        )

        drawSkinObject(
            pos = pos,
            skin = skin,
            strokeWidthPx = strokeWidthPx,
            headWingLengthPx = headWingLengthPx,
            tipPulseScale = 1.0f,
            density = density,
            dotSkin = DotSkinCatalog.CLASSIC
        )
    }
}

@Composable
fun SingleDotStaticCanvas(
    dot: DotSkin,
    pulseScale: Float = 1.0f,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    Canvas(modifier = modifier) {
        val cX = size.width / 2f
        val cY = size.height / 2f
        val centerOffset = Offset(cX, cY)

        val baseGlowRadiusPx = with(density) { dot.glowRadiusDp.dp.toPx() } * 0.45f
        val glowRadiusPx = baseGlowRadiusPx * pulseScale
        val coreRadiusPx = with(density) { 6.dp.toPx() }

        when (dot.style) {
            com.example.model.DotStyle.CLASSIC_TARGET -> {
                drawCircle(
                    color = dot.glowColor.copy(alpha = 0.45f),
                    radius = glowRadiusPx,
                    center = centerOffset
                )
                drawCircle(
                    color = dot.glowColor.copy(alpha = 0.85f),
                    radius = coreRadiusPx * 1.4f,
                    center = centerOffset
                )
                drawCircle(
                    color = dot.centerColor,
                    radius = coreRadiusPx,
                    center = centerOffset
                )
            }
            com.example.model.DotStyle.ELECTRIC_RING -> {
                drawCircle(
                    color = dot.glowColor.copy(alpha = 0.3f),
                    radius = glowRadiusPx * 1.2f,
                    center = centerOffset
                )
                drawCircle(
                    color = dot.glowColor,
                    radius = coreRadiusPx * 1.8f * pulseScale,
                    center = centerOffset,
                    style = Stroke(width = with(density) { 2.dp.toPx() })
                )
                drawCircle(
                    color = dot.centerColor,
                    radius = coreRadiusPx * 0.9f,
                    center = centerOffset
                )
            }
            com.example.model.DotStyle.STAR_BURST -> {
                drawCircle(
                    color = dot.glowColor.copy(alpha = 0.4f),
                    radius = glowRadiusPx,
                    center = centerOffset
                )
                val rayLength = coreRadiusPx * 3.5f * pulseScale
                drawLine(
                    color = dot.glowColor,
                    start = Offset(cX - rayLength, cY),
                    end = Offset(cX + rayLength, cY),
                    strokeWidth = with(density) { 2.dp.toPx() },
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = dot.glowColor,
                    start = Offset(cX, cY - rayLength),
                    end = Offset(cX, cY + rayLength),
                    strokeWidth = with(density) { 2.dp.toPx() },
                    cap = StrokeCap.Round
                )
                drawCircle(
                    color = dot.centerColor,
                    radius = coreRadiusPx * 1.1f,
                    center = centerOffset
                )
            }
            com.example.model.DotStyle.COSMIC_SINGULARITY -> {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(dot.centerColor, dot.glowColor, Color.Transparent),
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
                    color = dot.glowColor,
                    radius = coreRadiusPx * 1.2f,
                    center = centerOffset,
                    style = Stroke(width = with(density) { 1.5.dp.toPx() })
                )
            }
            com.example.model.DotStyle.MOLTEN_SUN -> {
                drawCircle(
                    color = dot.glowColor.copy(alpha = 0.25f),
                    radius = glowRadiusPx * 1.4f,
                    center = centerOffset
                )
                drawCircle(
                    color = dot.glowColor.copy(alpha = 0.65f),
                    radius = glowRadiusPx * 0.9f,
                    center = centerOffset
                )
                drawCircle(
                    color = dot.centerColor,
                    radius = coreRadiusPx * 1.4f * pulseScale,
                    center = centerOffset
                )
            }
            com.example.model.DotStyle.MATRIX_RADAR -> {
                drawCircle(
                    color = dot.glowColor.copy(alpha = 0.7f),
                    radius = coreRadiusPx * 2.2f * pulseScale,
                    center = centerOffset,
                    style = Stroke(width = with(density) { 1.dp.toPx() })
                )
                val lineOffset = coreRadiusPx * 2.8f
                drawLine(
                    color = dot.glowColor,
                    start = Offset(cX - lineOffset, cY),
                    end = Offset(cX + lineOffset, cY),
                    strokeWidth = with(density) { 0.75.dp.toPx() }
                )
                drawLine(
                    color = dot.glowColor,
                    start = Offset(cX, cY - lineOffset),
                    end = Offset(cX, cY + lineOffset),
                    strokeWidth = with(density) { 0.75.dp.toPx() }
                )
                drawCircle(
                    color = dot.centerColor,
                    radius = coreRadiusPx * 0.6f,
                    center = centerOffset
                )
            }
            com.example.model.DotStyle.RAINBOW_CHROMA -> {
                val offsetVal = (2.dp.toPx() * pulseScale)
                drawCircle(
                    color = Color.Red.copy(alpha = 0.7f),
                    radius = coreRadiusPx * 1.5f,
                    center = Offset(cX - offsetVal, cY - offsetVal)
                )
                drawCircle(
                    color = Color.Green.copy(alpha = 0.7f),
                    radius = coreRadiusPx * 1.5f,
                    center = Offset(cX + offsetVal, cY)
                )
                drawCircle(
                    color = Color.Blue.copy(alpha = 0.7f),
                    radius = coreRadiusPx * 1.5f,
                    center = Offset(cX, cY + offsetVal)
                )
                drawCircle(
                    color = Color.White,
                    radius = coreRadiusPx * 0.7f,
                    center = centerOffset
                )
            }
            com.example.model.DotStyle.TECH_HEXAGON -> {
                val hexPath = Path()
                val radius = coreRadiusPx * 2.2f * pulseScale
                for (i in 0..5) {
                    val angle = Math.toRadians((i * 60).toDouble())
                    val hx = (cX + radius * Math.cos(angle)).toFloat()
                    val hy = (cY + radius * Math.sin(angle)).toFloat()
                    if (i == 0) hexPath.moveTo(hx, hy) else hexPath.lineTo(hx, hy)
                }
                hexPath.close()

                drawPath(
                    path = hexPath,
                    color = dot.glowColor,
                    style = Stroke(width = with(density) { 1.5.dp.toPx() })
                )
                drawCircle(
                    color = dot.centerColor,
                    radius = coreRadiusPx,
                    center = centerOffset
                )
            }
            com.example.model.DotStyle.ICE_CRYSTAL -> {
                drawCircle(color = dot.glowColor.copy(alpha = 0.35f), radius = glowRadiusPx, center = centerOffset)
                for (i in 0 until 6) {
                    val ang = Math.toRadians((i * 60).toDouble())
                    val rx = (cX + glowRadiusPx * Math.cos(ang)).toFloat()
                    val ry = (cY + glowRadiusPx * Math.sin(ang)).toFloat()
                    drawLine(color = dot.glowColor, start = centerOffset, end = Offset(rx, ry), strokeWidth = with(density) { 1.5.dp.toPx() })
                }
                drawCircle(color = dot.centerColor, radius = coreRadiusPx * 1.1f, center = centerOffset)
            }
            com.example.model.DotStyle.TOXIC_BIOHAZARD -> {
                drawCircle(color = dot.glowColor.copy(alpha = 0.3f), radius = glowRadiusPx, center = centerOffset)
                drawCircle(color = dot.glowColor, radius = coreRadiusPx * 1.8f, center = centerOffset, style = Stroke(width = with(density) { 2.dp.toPx() }))
                drawCircle(color = dot.centerColor, radius = coreRadiusPx * 0.9f, center = centerOffset)
            }
            com.example.model.DotStyle.SHADOW_PORTAL -> {
                drawCircle(color = dot.glowColor.copy(alpha = 0.3f), radius = glowRadiusPx * 1.1f, center = centerOffset)
                drawCircle(color = dot.glowColor.copy(alpha = 0.6f), radius = glowRadiusPx * 0.75f, center = centerOffset, style = Stroke(width = with(density) { 2.5.dp.toPx() }))
                drawCircle(color = Color(0xFF1A0033), radius = coreRadiusPx, center = centerOffset)
            }
            com.example.model.DotStyle.HEAVENLY_HALO -> {
                drawCircle(color = dot.glowColor.copy(alpha = 0.3f), radius = glowRadiusPx, center = centerOffset)
                drawCircle(color = dot.glowColor, radius = coreRadiusPx * 1.8f, center = centerOffset, style = Stroke(width = with(density) { 1.2.dp.toPx() }))
                drawCircle(color = dot.centerColor, radius = coreRadiusPx * 0.8f, center = centerOffset)
            }
            com.example.model.DotStyle.GEAR_CLOCKWORK -> {
                val rad = coreRadiusPx * 2f
                for (i in 0 until 6) {
                    val ang = Math.toRadians((i * 60).toDouble())
                    val hx = (cX + rad * Math.cos(ang)).toFloat()
                    val hy = (cY + rad * Math.sin(ang)).toFloat()
                    drawCircle(color = dot.glowColor, radius = coreRadiusPx * 0.4f, center = Offset(hx, hy))
                }
                drawCircle(color = dot.glowColor, radius = rad, center = centerOffset, style = Stroke(width = with(density) { 1.5.dp.toPx() }))
                drawCircle(color = dot.centerColor, radius = coreRadiusPx * 0.8f, center = centerOffset)
            }
            com.example.model.DotStyle.WATER_RIPPLE -> {
                drawCircle(color = dot.glowColor.copy(alpha = 0.2f), radius = glowRadiusPx * 1.3f, center = centerOffset)
                drawCircle(color = dot.glowColor.copy(alpha = 0.4f), radius = glowRadiusPx * 0.85f, center = centerOffset, style = Stroke(width = with(density) { 1.5.dp.toPx() }))
                drawCircle(color = dot.centerColor, radius = coreRadiusPx, center = centerOffset)
            }
            com.example.model.DotStyle.SWEET_DONUT -> {
                drawCircle(color = dot.glowColor, radius = coreRadiusPx * 2.1f, center = centerOffset)
                drawCircle(color = Color(0xFF8D6E63), radius = coreRadiusPx * 2.1f, center = centerOffset, style = Stroke(width = with(density) { 2.dp.toPx() }))
                drawCircle(color = Color.White, radius = coreRadiusPx * 0.65f, center = centerOffset)
            }
            com.example.model.DotStyle.CHROME_METAL -> {
                drawCircle(color = Color(0xFFB0BEC5), radius = coreRadiusPx * 2f, center = centerOffset)
                drawCircle(color = Color.White, radius = coreRadiusPx * 2f, center = centerOffset, style = Stroke(width = with(density) { 1.5.dp.toPx() }))
                drawCircle(color = Color(0xFF37474F), radius = coreRadiusPx * 0.55f, center = centerOffset)
            }
            com.example.model.DotStyle.PIXEL_HEART -> {
                drawCircle(color = dot.glowColor.copy(alpha = 0.35f), radius = glowRadiusPx, center = centerOffset)
                val heartPath = Path().apply {
                    moveTo(cX, cY + coreRadiusPx * 0.5f)
                    lineTo(cX - coreRadiusPx * 1.4f, cY - coreRadiusPx * 0.8f)
                    lineTo(cX - coreRadiusPx * 0.7f, cY - coreRadiusPx * 1.7f)
                    lineTo(cX, cY - coreRadiusPx * 0.6f)
                    lineTo(cX + coreRadiusPx * 0.7f, cY - coreRadiusPx * 1.7f)
                    lineTo(cX + coreRadiusPx * 1.4f, cY - coreRadiusPx * 0.8f)
                    close()
                }
                drawPath(path = heartPath, color = dot.glowColor)
            }
            com.example.model.DotStyle.GOLDEN_SHIELD -> {
                drawCircle(color = dot.glowColor.copy(alpha = 0.32f), radius = glowRadiusPx, center = centerOffset)
                val shieldPath = Path().apply {
                    moveTo(cX, cY - coreRadiusPx * 1.8f)
                    lineTo(cX + coreRadiusPx * 1.4f, cY - coreRadiusPx * 0.9f)
                    lineTo(cX + coreRadiusPx * 1.1f, cY + coreRadiusPx * 1.3f)
                    lineTo(cX, cY + coreRadiusPx * 2.1f)
                    lineTo(cX - coreRadiusPx * 1.1f, cY + coreRadiusPx * 1.3f)
                    lineTo(cX - coreRadiusPx * 1.4f, cY - coreRadiusPx * 0.9f)
                    close()
                }
                drawPath(path = shieldPath, color = dot.glowColor)
                drawPath(path = shieldPath, color = Color.White, style = Stroke(width = with(density) { 1.2.dp.toPx() }))
            }
            com.example.model.DotStyle.NEON_CROSSHAIR -> {
                drawCircle(color = dot.glowColor, radius = coreRadiusPx * 1.8f, center = centerOffset, style = Stroke(width = with(density) { 1.2.dp.toPx() }))
                val arm = coreRadiusPx * 2.3f
                drawLine(color = dot.glowColor, start = Offset(cX - arm, cY), end = Offset(cX - coreRadiusPx * 1.0f, cY), strokeWidth = with(density) { 1.5.dp.toPx() })
                drawLine(color = dot.glowColor, start = Offset(cX + coreRadiusPx * 1.0f, cY), end = Offset(cX + arm, cY), strokeWidth = with(density) { 1.5.dp.toPx() })
                drawLine(color = dot.glowColor, start = Offset(cX, cY - arm), end = Offset(cX, cY - coreRadiusPx * 1.0f), strokeWidth = with(density) { 1.5.dp.toPx() })
                drawLine(color = dot.glowColor, start = Offset(cX, cY + coreRadiusPx * 1.0f), end = Offset(cX, cY + arm), strokeWidth = with(density) { 1.5.dp.toPx() })
                drawCircle(color = dot.centerColor, radius = coreRadiusPx * 0.5f, center = centerOffset)
            }
            com.example.model.DotStyle.FIREFLY_SWARM -> {
                drawCircle(color = dot.glowColor.copy(alpha = 0.22f), radius = glowRadiusPx, center = centerOffset)
                val swarm = listOf(
                    Offset(cX - coreRadiusPx * 1.0f, cY - coreRadiusPx * 1.0f),
                    Offset(cX + coreRadiusPx * 1.2f, cY + coreRadiusPx * 0.4f),
                    Offset(cX - coreRadiusPx * 0.4f, cY + coreRadiusPx * 1.1f)
                )
                for (pt in swarm) {
                    drawCircle(color = dot.centerColor, radius = with(density) { 2.5.dp.toPx() }, center = pt)
                }
                drawCircle(color = dot.glowColor, radius = coreRadiusPx * 0.8f, center = centerOffset)
            }
            com.example.model.DotStyle.GALAXY_ORBIT -> {
                drawCircle(color = dot.glowColor.copy(alpha = 0.28f), radius = glowRadiusPx * 1.1f, center = centerOffset)
                drawCircle(color = dot.glowColor, radius = coreRadiusPx * 2.0f, center = centerOffset, style = Stroke(width = with(density) { 0.8.dp.toPx() }))
                drawCircle(color = dot.centerColor, radius = coreRadiusPx, center = centerOffset)
            }
            com.example.model.DotStyle.PLASMA_BALL -> {
                drawCircle(color = dot.glowColor.copy(alpha = 0.32f), radius = glowRadiusPx, center = centerOffset)
                for (i in 0 until 4) {
                    val ang = Math.toRadians((i * 90).toDouble())
                    val endX = (cX + glowRadiusPx * 0.75f * Math.cos(ang)).toFloat()
                    val endY = (cY + glowRadiusPx * 0.75f * Math.sin(ang)).toFloat()
                    drawLine(color = dot.glowColor, start = centerOffset, end = Offset(endX, endY), strokeWidth = with(density) { 1.2.dp.toPx() })
                }
                drawCircle(color = Color.White, radius = coreRadiusPx, center = centerOffset)
            }
            com.example.model.DotStyle.EGYPTIAN_EYE -> {
                drawCircle(color = dot.glowColor.copy(alpha = 0.28f), radius = glowRadiusPx, center = centerOffset)
                val eyeW = coreRadiusPx * 1.8f
                val eyeH = coreRadiusPx * 0.9f
                val eyePath = Path().apply {
                    moveTo(cX - eyeW, cY)
                    quadraticTo(cX, cY - eyeH, cX + eyeW, cY)
                    quadraticTo(cX, cY + eyeH, cX - eyeW, cY)
                    close()
                }
                drawPath(path = eyePath, color = dot.glowColor, style = Stroke(width = with(density) { 1.5.dp.toPx() }))
                drawCircle(color = dot.centerColor, radius = coreRadiusPx * 0.6f, center = centerOffset)
            }
            com.example.model.DotStyle.YIN_YANG -> {
                val rad = coreRadiusPx * 1.8f
                drawCircle(color = Color.Black, radius = rad, center = centerOffset)
                drawCircle(color = Color.White, radius = rad, center = centerOffset, style = Stroke(width = with(density) { 1.5.dp.toPx() }))
                drawCircle(color = Color.White, radius = rad * 0.5f, center = Offset(cX, cY - rad * 0.5f))
                drawCircle(color = Color.Black, radius = rad * 0.5f, center = Offset(cX, cY + rad * 0.5f))
                drawCircle(color = Color.Black, radius = rad * 0.15f, center = Offset(cX, cY - rad * 0.5f))
                drawCircle(color = Color.White, radius = rad * 0.15f, center = Offset(cX, cY + rad * 0.5f))
            }
            com.example.model.DotStyle.DISCO_BALL -> {
                drawCircle(
                    brush = Brush.sweepGradient(
                        colors = listOf(Color(0xFFE040FB), Color(0xFF00E5FF), Color(0xFFFFEA00), Color(0xFFE040FB)),
                        center = centerOffset
                    ),
                    radius = coreRadiusPx * 2.2f,
                    center = centerOffset
                )
                drawCircle(color = Color.White.copy(alpha = 0.5f), radius = coreRadiusPx * 2.2f, center = centerOffset, style = Stroke(width = with(density) { 0.8.dp.toPx() }))
            }
        }
    }
}
