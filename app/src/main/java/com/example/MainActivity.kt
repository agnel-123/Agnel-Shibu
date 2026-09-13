package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.audio.SpaceAudioEngine
import com.example.model.CelestialRepository
import com.example.ui.dialogs.CelestialArchiveDialog
import com.example.ui.dialogs.CelestialAtlasModal
import com.example.ui.dialogs.FlightManualDialog
import com.example.ui.dialogs.PlanetDetailDialog
import com.example.ui.journey.KidsJourneyScreen
import com.example.ui.space.CelestialInfoCard
import com.example.ui.space.DestinationSelectorBar
import com.example.ui.space.FloatingControlsBar
import com.example.ui.space.ObservationSettingsDrawer
import com.example.ui.space.ObservatoryTopBar
import com.example.ui.space.SolarPlanetsStrip
import com.example.ui.space.SpaceCanvasView
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SpaceBlack
import com.example.ui.theme.SpaceBorder
import com.example.ui.theme.SpaceDeepNavy
import com.example.ui.theme.TealStarlight

class MainActivity : ComponentActivity() {
    private val audioEngine = SpaceAudioEngine()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                OrbitaApp(audioEngine = audioEngine)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        audioEngine.stop()
    }
}

@Composable
fun OrbitaApp(audioEngine: SpaceAudioEngine) {
    val context = LocalContext.current

    // Screen State
    var currentScreen by remember { mutableStateOf("observatory") } // "observatory" | "journey"
    var currentWorldId by remember { mutableStateOf("milkyway") }
    val worlds = CelestialRepository.worlds
    val currentWorld = worlds.find { it.id == currentWorldId } ?: worlds.first()

    // Simulation & Visual Settings
    var isPlaying by remember { mutableStateOf(true) }
    var autoRotate by remember { mutableStateOf(true) }
    var grid by remember { mutableStateOf(true) }
    var labels by remember { mutableStateOf(true) }
    var glow by remember { mutableFloatStateOf(1.0f) }
    var speed by remember { mutableFloatStateOf(1.0f) }
    var panMode by remember { mutableStateOf(false) }
    var isImmersive by remember { mutableStateOf(false) }

    // Solar system focus
    var selectedSolarBodyId by remember { mutableStateOf<String?>(null) }

    // Modals
    var isAtlasOpen by remember { mutableStateOf(false) }
    var isArchiveOpen by remember { mutableStateOf(false) }
    var isHelpOpen by remember { mutableStateOf(false) }
    var isSettingsOpen by remember { mutableStateOf(false) }
    var isPlanetDetailOpen by remember { mutableStateOf(false) }

    // Audio state
    var isAudioOn by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            audioEngine.stop()
        }
    }

    if (currentScreen == "journey") {
        KidsJourneyScreen(
            onBack = { currentScreen = "observatory" }
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SpaceBlack)
        ) {
            // 1. Interactive 3D Space Canvas
            SpaceCanvasView(
                currentWorld = currentWorld,
                playing = isPlaying,
                autoRotate = autoRotate,
                grid = grid,
                labels = labels,
                glow = glow,
                speed = speed,
                panMode = panMode,
                selectedSolarBody = selectedSolarBodyId,
                onSelectSolarBody = { id ->
                    selectedSolarBodyId = id
                    isPlanetDetailOpen = true
                },
                modifier = Modifier.fillMaxSize()
            )

            // 2. Cosmic Vignette Atmosphere Gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                SpaceBlack.copy(alpha = 0.55f),
                                Color.Transparent,
                                Color.Transparent,
                                SpaceBlack.copy(alpha = 0.65f)
                            )
                        )
                    )
            )

            // 3. HUD Layer (Hidden in Immersive Mode)
            AnimatedVisibility(
                visible = !isImmersive,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Top Bar
                    ObservatoryTopBar(
                        isAudioOn = isAudioOn,
                        onToggleAudio = {
                            isAudioOn = audioEngine.toggle()
                        },
                        onOpenJourney = {
                            audioEngine.playChime(648.0)
                            currentScreen = "journey"
                        },
                        onOpenAtlas = { isAtlasOpen = true },
                        onOpenHelp = { isHelpOpen = true },
                        onToggleSettings = { isSettingsOpen = !isSettingsOpen },
                        onCapture = {
                            audioEngine.playChime(720.0)
                            Toast.makeText(
                                context,
                                "观测图像已保存 · ${currentWorld.code} (${currentWorld.name})",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        isSettingsOpen = isSettingsOpen,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )

                    // Top-Left Celestial Info Card
                    CelestialInfoCard(
                        world = currentWorld,
                        onOpenArchive = { isArchiveOpen = true },
                        onQuickJump = { targetId ->
                            audioEngine.playChime(528.0)
                            currentWorldId = targetId
                        },
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(top = 70.dp, start = 16.dp)
                    )

                    // Floating Controls Bar (Center-Right)
                    FloatingControlsBar(
                        isPlaying = isPlaying,
                        onTogglePlay = { isPlaying = !isPlaying },
                        panMode = panMode,
                        onTogglePanMode = { panMode = !panMode },
                        onResetCamera = {
                            // reset local views
                            currentWorldId = currentWorld.id
                            audioEngine.playChime(432.0)
                            Toast.makeText(context, "视角已重置", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 16.dp)
                    )

                    // Settings Drawer Popup
                    AnimatedVisibility(
                        visible = isSettingsOpen,
                        enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
                        exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 70.dp, end = 16.dp)
                    ) {
                        ObservationSettingsDrawer(
                            autoRotate = autoRotate,
                            onToggleAutoRotate = { autoRotate = it },
                            grid = grid,
                            onToggleGrid = { grid = it },
                            labels = labels,
                            onToggleLabels = { labels = it },
                            glow = glow,
                            onChangeGlow = { glow = it },
                            speed = speed,
                            onChangeSpeed = { speed = it },
                            onClose = { isSettingsOpen = false }
                        )
                    }

                    // Bottom Column: Solar strip (if solar) + Destination Selector
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                    ) {
                        if (currentWorldId == "solar") {
                            SolarPlanetsStrip(
                                selectedPlanetId = selectedSolarBodyId,
                                onSelectPlanet = { planetId ->
                                    selectedSolarBodyId = planetId
                                    isPlanetDetailOpen = true
                                    audioEngine.playChime(587.3)
                                }
                            )
                        }

                        DestinationSelectorBar(
                            worlds = worlds,
                            currentWorldId = currentWorldId,
                            onSelectWorld = { id ->
                                audioEngine.playChime(528.0)
                                currentWorldId = id
                                selectedSolarBodyId = null
                            }
                        )
                    }
                }
            }

            // Restore HUD button when in immersive mode
            if (isImmersive) {
                IconButton(
                    onClick = { isImmersive = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 36.dp, end = 16.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SpaceDeepNavy.copy(alpha = 0.8f))
                        .border(1.dp, SpaceBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "显示界面",
                        tint = TealStarlight
                    )
                }
            }
        }

        // Modals
        if (isAtlasOpen) {
            CelestialAtlasModal(
                onSelectWorld = { id ->
                    currentWorldId = id
                    selectedSolarBodyId = null
                    audioEngine.playChime(528.0)
                },
                onDismiss = { isAtlasOpen = false }
            )
        }

        if (isArchiveOpen) {
            CelestialArchiveDialog(
                world = currentWorld,
                onDismiss = { isArchiveOpen = false }
            )
        }

        if (isHelpOpen) {
            FlightManualDialog(
                onDismiss = { isHelpOpen = false }
            )
        }

        if (isPlanetDetailOpen && selectedSolarBodyId != null) {
            val planet = CelestialRepository.solarBodies.find { it.id == selectedSolarBodyId }
            if (planet != null) {
                PlanetDetailDialog(
                    planet = planet,
                    onDismiss = { isPlanetDetailOpen = false }
                )
            }
        }
    }
}
