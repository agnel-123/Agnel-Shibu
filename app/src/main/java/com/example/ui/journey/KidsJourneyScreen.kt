package com.example.ui.journey

import android.speech.tts.TextToSpeech
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CelestialRepository
import com.example.ui.theme.AmberGargantua
import com.example.ui.theme.CyanNebula
import com.example.ui.theme.EarthAtmosphere
import com.example.ui.theme.EarthBlue
import com.example.ui.theme.SpaceBlack
import com.example.ui.theme.SpaceBorder
import com.example.ui.theme.SpaceDeepNavy
import com.example.ui.theme.SpaceSurface
import com.example.ui.theme.SpaceSurfaceElevated
import com.example.ui.theme.TealAccent
import com.example.ui.theme.TealStarlight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun KidsJourneyScreen(
    onBack: () -> Unit
) {
    val chapters = CelestialRepository.kidChapters
    var currentChapterIndex by remember { mutableIntStateOf(0) }
    val currentChapter = chapters[currentChapterIndex]

    var isAutoFlying by remember { mutableStateOf(false) }
    var flightSpeed by remember { mutableFloatStateOf(1.0f) } // 0.5f, 1.0f, 2.0f
    var showCompanionResponse by remember { mutableStateOf(false) }

    val context = LocalContext.current
    var tts: TextToSpeech? by remember { mutableStateOf(null) }

    // Initialize TTS
    DisposableEffect(Unit) {
        var speechInstance: TextToSpeech? = null
        speechInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                speechInstance?.language = Locale.CHINESE
            }
        }
        tts = speechInstance
        onDispose {
            speechInstance?.stop()
            speechInstance?.shutdown()
        }
    }

    // Auto-flight progression timer
    LaunchedEffect(isAutoFlying, flightSpeed, currentChapterIndex) {
        if (isAutoFlying) {
            val stepDelay = (6000L / flightSpeed).toLong()
            delay(stepDelay)
            if (currentChapterIndex < chapters.size - 1) {
                currentChapterIndex++
                showCompanionResponse = false
            } else {
                isAutoFlying = false
            }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "rocket")
    val enginePulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SpaceBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(SpaceSurface)
                        .border(1.dp, SpaceBorder, CircleShape)
                        .testTag("journey_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "返回深空观测站",
                        tint = TextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "儿童旅程 · 我的第一次升空",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "WINDOW ${currentChapter.number} / 06 · ${currentChapter.eyebrow}",
                        color = TealStarlight,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }
            }

            // Altitude Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(SpaceDeepNavy)
                    .border(1.dp, TealStarlight.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = currentChapter.altitude,
                    color = TealStarlight,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // Progress line
        LinearProgressIndicator(
            progress = { currentChapter.progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp),
            color = TealStarlight,
            trackColor = SpaceSurface
        )

        // Main Content (Scrollable for compact screens)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Porthole Window (舷窗 3D 模拟)
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .clip(CircleShape)
                    .background(SpaceDeepNavy)
                    .border(8.dp, SpaceSurfaceElevated, CircleShape)
                    .border(10.dp, SpaceBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Inside Porthole Canvas View
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawPortholeView(
                        chapterIndex = currentChapterIndex,
                        size = size,
                        pulse = enginePulse
                    )
                }

                // Porthole Glass Glare Effect
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.15f),
                                    Color.Transparent,
                                    Color.Transparent,
                                    Color.White.copy(alpha = 0.05f)
                                ),
                                start = Offset(0f, 0f),
                                end = Offset(260f, 260f)
                            )
                        )
                )

                // Chapter Indicator Pill inside porthole
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SpaceBlack.copy(alpha = 0.75f))
                        .border(1.dp, SpaceBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = currentChapter.name,
                        color = TealStarlight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Title & Subtitle
            Text(
                text = currentChapter.title,
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = currentChapter.line,
                color = TealAccent,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Companion "小星 · 你的飞行伙伴" Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SpaceDeepNavy)
                    .border(1.dp, TealStarlight.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(AmberGargantua.copy(alpha = 0.2f))
                                .border(1.dp, AmberGargantua, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("★", color = AmberGargantua, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "小星 · 你的飞行伙伴",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "LITTLE STAR COMPANION",
                                color = TextMuted,
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    // TTS "再听一次" Button
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(SpaceSurface)
                            .border(1.dp, SpaceBorder, RoundedCornerShape(12.dp))
                            .clickable {
                                tts?.speak(
                                    currentChapter.speech,
                                    TextToSpeech.QUEUE_FLUSH,
                                    null,
                                    "journey_tts"
                                )
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "朗读语音",
                            tint = TealStarlight,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("再听一次", color = TealStarlight, fontSize = 10.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = currentChapter.speech,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Interactive Clue Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(TealStarlight.copy(alpha = 0.1f))
                        .border(1.dp, TealStarlight.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .clickable { showCompanionResponse = !showCompanionResponse }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = AmberGargantua,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "互动发现：${currentChapter.clue}",
                            color = TealStarlight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Text(
                        text = if (showCompanionResponse) "收起" else "点击互动",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }

                // Companion response speech bubble
                AnimatedVisibility(visible = showCompanionResponse) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(SpaceSurfaceElevated)
                            .border(1.dp, AmberGargantua.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "小星回答：",
                            color = AmberGargantua,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = currentChapter.response,
                            color = TextPrimary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Flight Controls: Launch / Pause & Speed
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Launch button
                Button(
                    onClick = { isAutoFlying = !isAutoFlying },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("launch_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isAutoFlying) AmberGargantua else TealStarlight
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = if (isAutoFlying) Icons.Default.Pause else Icons.Default.RocketLaunch,
                        contentDescription = null,
                        tint = SpaceDeepNavy,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isAutoFlying) "暂停航行" else "点火，出发！",
                        color = SpaceDeepNavy,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                // Speed Selectors
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(SpaceDeepNavy)
                        .border(1.dp, SpaceBorder, RoundedCornerShape(10.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(Pair(0.5f, "½×"), Pair(1.0f, "1×"), Pair(2.0f, "2×")).forEach { (speed, label) ->
                        val isSelected = flightSpeed == speed
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) TealStarlight.copy(alpha = 0.2f) else Color.Transparent)
                                .border(1.dp, if (isSelected) TealStarlight else Color.Transparent, RoundedCornerShape(6.dp))
                            .clickable { flightSpeed = speed }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) TealStarlight else TextMuted,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Chapter Pills Carousel
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                chapters.forEachIndexed { index, chapter ->
                    val isSelected = index == currentChapterIndex
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) TealStarlight.copy(alpha = 0.2f) else SpaceDeepNavy)
                            .border(1.dp, if (isSelected) TealStarlight else SpaceBorder, RoundedCornerShape(8.dp))
                            .clickable {
                                currentChapterIndex = index
                                showCompanionResponse = false
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = chapter.number,
                            color = if (isSelected) TealStarlight else TextMuted,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = chapter.name,
                            color = if (isSelected) TextPrimary else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

// Draw the changing perspective inside the porthole
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPortholeView(
    chapterIndex: Int,
    size: Size,
    pulse: Float
) {
    val cx = size.width / 2f
    val cy = size.height / 2f

    when (chapterIndex) {
        0 -> {
            // Chapter 1: Launch Pad
            // Blue sky gradient to ground
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF38BDF8), Color(0xFFBAE6FD), Color(0xFFF1F5F9)),
                    startY = 0f,
                    endY = cy
                ),
                size = Size(size.width, cy)
            )
            // Ground green
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF4ADE80), Color(0xFF16A34A)),
                    startY = cy,
                    endY = size.height
                ),
                topLeft = Offset(0f, cy),
                size = Size(size.width, cy)
            )
            // Launch tower on right
            drawRect(
                color = Color(0xFFEF4444),
                topLeft = Offset(cx + 40f, cy - 80f),
                size = Size(18f, 120f)
            )
            // Little house on left
            drawRect(
                color = Color(0xFFFDE68A),
                topLeft = Offset(cx - 70f, cy - 10f),
                size = Size(36f, 30f)
            )
            val roofPath = Path().apply {
                moveTo(cx - 75f, cy - 10f)
                lineTo(cx - 52f, cy - 30f)
                lineTo(cx - 29f, cy - 10f)
                close()
            }
            drawPath(roofPath, color = Color(0xFFF97316))
            // Smoke puff
            drawCircle(
                color = Color.White.copy(alpha = 0.6f * pulse),
                radius = 28f * pulse,
                center = Offset(cx + 10f, cy + 30f)
            )
        }
        1 -> {
            // Chapter 2: Above City
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0284C7), Color(0xFF7DD3FC)),
                    startY = 0f,
                    endY = size.height * 0.35f
                ),
                size = Size(size.width, size.height * 0.35f)
            )
            // City patchwork ground
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF86EFAC), Color(0xFF64748B)),
                    startY = size.height * 0.35f,
                    endY = size.height
                ),
                topLeft = Offset(0f, size.height * 0.35f),
                size = Size(size.width, size.height * 0.65f)
            )
            // Roads
            drawLine(
                color = Color(0xFFCBD5E1),
                start = Offset(0f, cy + 20f),
                end = Offset(size.width, cy + 60f),
                strokeWidth = 5f
            )
            drawLine(
                color = Color(0xFFCBD5E1),
                start = Offset(cx - 20f, size.height * 0.35f),
                end = Offset(cx + 40f, size.height),
                strokeWidth = 4f
            )
            // Tiny rooftops
            for (i in -3..3) {
                for (j in -2..2) {
                    val bx = cx + i * 28f
                    val by = cy + 35f + j * 22f
                    drawRect(
                        color = Color(0xFFE2E8F0),
                        topLeft = Offset(bx, by),
                        size = Size(14f, 12f)
                    )
                }
            }
        }
        2 -> {
            // Chapter 3: Clouds
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF1E3A8A), Color(0xFF38BDF8)),
                    startY = 0f,
                    endY = cy
                ),
                size = Size(size.width, cy)
            )
            // Billowing fluffy cloud sea
            for (i in 0..6) {
                val clx = (i * 45f)
                val cly = cy - 10f + (sin(i * 1.5f) * 15f)
                drawCircle(
                    color = Color.White.copy(alpha = 0.85f),
                    radius = 35f,
                    center = Offset(clx, cly)
                )
            }
            drawRect(
                color = Color(0xFFF8FAFC),
                topLeft = Offset(0f, cy + 15f),
                size = Size(size.width, size.height - cy)
            )
        }
        3 -> {
            // Chapter 4: Curved Earth Horizon
            // Deep space at top
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF030712), Color(0xFF0F172A), Color(0xFF1E3A8A)),
                    startY = 0f,
                    endY = cy + 20f
                ),
                size = Size(size.width, size.height)
            )
            // Stars in upper space
            for (k in 0..15) {
                drawCircle(
                    color = Color.White,
                    radius = 1.2f,
                    center = Offset((k * 18f) % size.width, (k * 22f) % (cy - 10f))
                )
            }
            // Curved Earth edge
            val earthCurve = Path().apply {
                moveTo(0f, cy + 60f)
                cubicTo(cx * 0.5f, cy + 10f, cx * 1.5f, cy + 10f, size.width, cy + 60f)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
            drawPath(
                earthCurve,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF2563EB), Color(0xFF1D4ED8)),
                    startY = cy + 10f,
                    endY = size.height
                )
            )
            // Blue atmospheric glowing rim
            drawPath(
                earthCurve,
                color = Color(0xFF67E8F9),
                style = Stroke(width = 4f)
            )
        }
        4 -> {
            // Chapter 5: Into Space
            drawRect(color = Color(0xFF020617), size = size)
            // Brilliant twinkling stars
            for (k in 0..30) {
                val sx = (k * 29f + 13f) % size.width
                val sy = (k * 37f + 7f) % (size.height * 0.7f)
                drawCircle(
                    color = Color.White.copy(alpha = 0.8f),
                    radius = 1.5f,
                    center = Offset(sx, sy)
                )
            }
            // Curved earth at bottom edge
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF38BDF8), Color(0xFF1D4ED8), Color(0xFF0F172A)),
                    center = Offset(cx, size.height + 60f),
                    radius = 180f
                ),
                radius = 180f,
                center = Offset(cx, size.height + 60f)
            )
        }
        5 -> {
            // Chapter 6: Look Back at Earth
            drawRect(color = SpaceBlack, size = size)
            // Stars
            for (k in 0..35) {
                val sx = (k * 23f + 17f) % size.width
                val sy = (k * 41f + 11f) % size.height
                drawCircle(
                    color = Color.White.copy(alpha = 0.75f),
                    radius = 1.2f,
                    center = Offset(sx, sy)
                )
            }
            // Whole Blue Marble Earth in distance
            val earthR = 55f
            // Atmosphere glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF38BDF8).copy(alpha = 0.5f), Color.Transparent),
                    center = Offset(cx, cy),
                    radius = earthR * 1.4f
                ),
                radius = earthR * 1.4f,
                center = Offset(cx, cy)
            )
            // Earth sphere
            drawCircle(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF3B82F6), Color(0xFF1E3A8A), Color(0xFF0F172A)),
                    start = Offset(cx - earthR * 0.6f, cy - earthR * 0.6f),
                    end = Offset(cx + earthR * 0.8f, cy + earthR * 0.8f)
                ),
                radius = earthR,
                center = Offset(cx, cy)
            )
            // Green continents
            drawCircle(
                color = Color(0xFF22C55E).copy(alpha = 0.7f),
                radius = 14f,
                center = Offset(cx - 10f, cy - 8f)
            )
            drawCircle(
                color = Color(0xFF22C55E).copy(alpha = 0.7f),
                radius = 10f,
                center = Offset(cx + 12f, cy + 10f)
            )
            // Clouds
            drawOval(
                color = Color.White.copy(alpha = 0.6f),
                topLeft = Offset(cx - 25f, cy - 15f),
                size = Size(35f, 10f)
            )
        }
    }
}
