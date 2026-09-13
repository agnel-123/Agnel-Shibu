package com.example.ui.space

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CelestialBody
import com.example.model.CelestialRepository
import com.example.ui.theme.CyanNebula
import com.example.ui.theme.SpaceBorder
import com.example.ui.theme.SpaceDeepNavy
import com.example.ui.theme.SpaceSurface
import com.example.ui.theme.SpaceSurfaceElevated
import com.example.ui.theme.TealAccent
import com.example.ui.theme.TealStarlight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ObservatoryTopBar(
    isAudioOn: Boolean,
    onToggleAudio: () -> Unit,
    onOpenJourney: () -> Unit,
    onOpenAtlas: () -> Unit,
    onOpenHelp: () -> Unit,
    onToggleSettings: () -> Unit,
    onCapture: () -> Unit,
    isSettingsOpen: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Brand & System Online indicator
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "ORBITA",
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "深空观测站",
                        color = TealStarlight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(TealStarlight, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "SYSTEM ONLINE",
                        color = TextMuted,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        // Actions
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            // Kid's Journey Pill Button
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(TealStarlight.copy(alpha = 0.12f))
                    .border(1.dp, TealStarlight.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                    .clickable { onOpenJourney() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("kids_journey_btn"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "儿童旅程",
                    tint = TealStarlight,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "儿童旅程",
                    color = TealStarlight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Audio Toggle
            IconButton(
                onClick = onToggleAudio,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(SpaceSurface.copy(alpha = 0.8f))
                    .border(1.dp, SpaceBorder, CircleShape)
                    .testTag("audio_toggle_btn")
            ) {
                Icon(
                    imageVector = if (isAudioOn) Icons.Default.MusicNote else Icons.Default.MusicOff,
                    contentDescription = "音效与音乐",
                    tint = if (isAudioOn) TealStarlight else TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Atlas Gallery Modal Button
            IconButton(
                onClick = onOpenAtlas,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(SpaceSurface.copy(alpha = 0.8f))
                    .border(1.dp, SpaceBorder, CircleShape)
                    .testTag("atlas_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Layers,
                    contentDescription = "天体图鉴",
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Capture Snapshot Button
            IconButton(
                onClick = onCapture,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(SpaceSurface.copy(alpha = 0.8f))
                    .border(1.dp, SpaceBorder, CircleShape)
                    .testTag("capture_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "保存观测截图",
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Settings Toggle
            IconButton(
                onClick = onToggleSettings,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isSettingsOpen) TealStarlight.copy(alpha = 0.2f) else SpaceSurface.copy(alpha = 0.8f))
                    .border(1.dp, if (isSettingsOpen) TealStarlight else SpaceBorder, CircleShape)
                    .testTag("settings_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "观测设置",
                    tint = if (isSettingsOpen) TealStarlight else TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Help Manual Button
            IconButton(
                onClick = onOpenHelp,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(SpaceSurface.copy(alpha = 0.8f))
                    .border(1.dp, SpaceBorder, CircleShape)
                    .testTag("help_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.HelpOutline,
                    contentDescription = "操作指南",
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun CelestialInfoCard(
    world: CelestialBody,
    onOpenArchive: () -> Unit,
    onQuickJump: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .widthIn(max = 340.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(SpaceDeepNavy.copy(alpha = 0.88f))
            .border(1.dp, SpaceBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        // Tag / Code
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${world.code} · ${world.category}",
                color = world.primaryColor,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Text(
                text = world.tag,
                color = TextMuted,
                fontSize = 10.sp
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Title and Chinese/English names
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = world.name,
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = world.en,
                color = TextMuted,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(bottom = 3.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(TealStarlight.copy(alpha = 0.15f))
                    .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
                Text("3D LIVE", color = TealStarlight, fontSize = 8.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Poetic statement
        Text(
            text = world.title,
            color = TealAccent,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = world.description,
            color = TextSecondary,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            world.stats.forEach { stat ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SpaceSurface.copy(alpha = 0.7f))
                        .border(1.dp, SpaceBorder.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 6.dp, vertical = 6.dp)
                ) {
                    Text(stat.label, color = TextMuted, fontSize = 9.sp)
                    Text(
                        stat.value,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(stat.unit, color = TextMuted, fontSize = 8.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Action Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Read Archive
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(TealStarlight.copy(alpha = 0.12f))
                    .border(1.dp, TealStarlight.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .clickable { onOpenArchive() }
                    .padding(vertical = 7.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "阅读天体档案",
                    color = TealStarlight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Quick Jump Contextual Shortcut
            if (world.id == "milkyway") {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SpaceSurface)
                        .border(1.dp, SpaceBorder, RoundedCornerShape(8.dp))
                        .clickable { onQuickJump("solar") }
                        .padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "探索太阳系", color = CyanNebula, fontSize = 11.sp)
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = null,
                        tint = CyanNebula,
                        modifier = Modifier
                            .size(12.dp)
                            .padding(start = 3.dp)
                    )
                }
            } else if (world.id == "earth") {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SpaceSurface)
                        .border(1.dp, SpaceBorder, RoundedCornerShape(8.dp))
                        .clickable { onQuickJump("moon") }
                        .padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "探索月球", color = CyanNebula, fontSize = 11.sp)
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = null,
                        tint = CyanNebula,
                        modifier = Modifier
                            .size(12.dp)
                            .padding(start = 3.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DestinationSelectorBar(
    worlds: List<CelestialBody>,
    currentWorldId: String,
    onSelectWorld: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "DESTINATIONS / 06 天体图鉴",
                color = TextMuted,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Text(
                text = "拖动旋转 · 双指缩放",
                color = TextMuted,
                fontSize = 9.sp
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            worlds.forEachIndexed { index, w ->
                val isSelected = w.id == currentWorldId
                val indexStr = "0${index + 1}"

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isSelected) TealStarlight.copy(alpha = 0.15f)
                            else SpaceDeepNavy.copy(alpha = 0.85f)
                        )
                        .border(
                            1.dp,
                            if (isSelected) TealStarlight else SpaceBorder,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { onSelectWorld(w.id) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .testTag("dest_${w.id}"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = indexStr,
                        color = if (isSelected) TealStarlight else TextMuted,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = w.name,
                            color = if (isSelected) TextPrimary else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                        Text(
                            text = w.en,
                            color = TextMuted,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FloatingControlsBar(
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    panMode: Boolean,
    onTogglePanMode: () -> Unit,
    onResetCamera: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(SpaceDeepNavy.copy(alpha = 0.85f))
            .border(1.dp, SpaceBorder, RoundedCornerShape(24.dp))
            .padding(horizontal = 6.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Play / Pause
        IconButton(
            onClick = onTogglePlay,
            modifier = Modifier.size(34.dp)
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "暂停演化" else "继续演化",
                tint = if (isPlaying) TealStarlight else TextSecondary,
                modifier = Modifier.size(16.dp)
            )
        }

        Box(
            modifier = Modifier
                .width(1.dp)
                .height(16.dp)
                .background(SpaceBorder)
        )

        // Rotate vs Pan mode
        IconButton(
            onClick = onTogglePanMode,
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(if (panMode) TealStarlight.copy(alpha = 0.2f) else Color.Transparent)
        ) {
            Icon(
                imageVector = Icons.Default.PanTool,
                contentDescription = "平移模式",
                tint = if (panMode) TealStarlight else TextSecondary,
                modifier = Modifier.size(16.dp)
            )
        }

        // Reset View
        IconButton(
            onClick = onResetCamera,
            modifier = Modifier.size(34.dp)
        ) {
            Icon(
                imageVector = Icons.Default.RestartAlt,
                contentDescription = "重置视角",
                tint = TextSecondary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun ObservationSettingsDrawer(
    autoRotate: Boolean,
    onToggleAutoRotate: (Boolean) -> Unit,
    grid: Boolean,
    onToggleGrid: (Boolean) -> Unit,
    labels: Boolean,
    onToggleLabels: (Boolean) -> Unit,
    glow: Float,
    onChangeGlow: (Float) -> Unit,
    speed: Float,
    onChangeSpeed: (Float) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .widthIn(max = 300.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(SpaceDeepNavy.copy(alpha = 0.95f))
            .border(1.dp, SpaceBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "观测设置",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onClose, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Close, contentDescription = "关闭", tint = TextMuted, modifier = Modifier.size(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Switches
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("自动旋转", color = TextSecondary, fontSize = 12.sp)
            Switch(
                checked = autoRotate,
                onCheckedChange = onToggleAutoRotate,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = TealStarlight,
                    checkedTrackColor = TealStarlight.copy(alpha = 0.4f),
                    uncheckedTrackColor = SpaceSurface
                )
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("参考网格", color = TextSecondary, fontSize = 12.sp)
            Switch(
                checked = grid,
                onCheckedChange = onToggleGrid,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = TealStarlight,
                    checkedTrackColor = TealStarlight.copy(alpha = 0.4f),
                    uncheckedTrackColor = SpaceSurface
                )
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("天体标注", color = TextSecondary, fontSize = 12.sp)
            Switch(
                checked = labels,
                onCheckedChange = onToggleLabels,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = TealStarlight,
                    checkedTrackColor = TealStarlight.copy(alpha = 0.4f),
                    uncheckedTrackColor = SpaceSurface
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Glow Slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("粒子辉光", color = TextSecondary, fontSize = 12.sp)
            Text("${(glow * 100).toInt()} %", color = TealStarlight, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
        }
        Slider(
            value = glow,
            onValueChange = onChangeGlow,
            valueRange = 0.2f..1.5f,
            colors = SliderDefaults.colors(
                thumbColor = TealStarlight,
                activeTrackColor = TealStarlight,
                inactiveTrackColor = SpaceSurface
            )
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Speed Multipliers
        Text("演化速度", color = TextSecondary, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(0.5f, 1.0f, 2.0f, 5.0f).forEach { s ->
                val isSelected = speed == s
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) TealStarlight.copy(alpha = 0.2f) else SpaceSurface)
                        .border(1.dp, if (isSelected) TealStarlight else SpaceBorder, RoundedCornerShape(6.dp))
                        .clickable { onChangeSpeed(s) }
                        .padding(vertical = 5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${s}×",
                        color = if (isSelected) TealStarlight else TextSecondary,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
fun SolarPlanetsStrip(
    selectedPlanetId: String?,
    onSelectPlanet: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val bodies = CelestialRepository.solarBodies
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .background(SpaceDeepNavy.copy(alpha = 0.9f))
            .border(1.dp, SpaceBorder, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "聚焦行星天体",
            color = TextMuted,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 2.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            bodies.forEach { b ->
                val isSelected = b.id == selectedPlanetId
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) b.color.copy(alpha = 0.2f) else SpaceSurface)
                        .border(1.dp, if (isSelected) b.color else SpaceBorder, RoundedCornerShape(8.dp))
                        .clickable { onSelectPlanet(b.id) }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(b.color, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = b.name,
                        color = if (isSelected) TextPrimary else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
