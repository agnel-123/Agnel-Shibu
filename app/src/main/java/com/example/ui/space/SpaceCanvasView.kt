package com.example.ui.space

import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import com.example.model.CelestialBody
import com.example.model.CelestialRepository
import com.example.model.SolarBody
import com.example.ui.theme.AmberGargantua
import com.example.ui.theme.CyanNebula
import com.example.ui.theme.SpaceBlack
import com.example.ui.theme.TealStarlight
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin
import kotlin.math.sqrt

// Pre-generated static star points for high-performance starry background
private class BackgroundStar(val x: Float, val y: Float, val radius: Float, val alpha: Float)
private val backgroundStars: List<BackgroundStar> by lazy {
    val list = mutableListOf<BackgroundStar>()
    val rng = java.util.Random(1337)
    for (i in 0 until 180) {
        list.add(
            BackgroundStar(
                x = rng.nextFloat(),
                y = rng.nextFloat(),
                radius = 0.6f + rng.nextFloat() * 1.5f,
                alpha = 0.25f + rng.nextFloat() * 0.65f
            )
        )
    }
    list
}

// Pre-generated galaxy particles for 3D simulation
private class GalaxyParticle(
    val dist: Float,
    val angle: Float,
    val armOffset: Float,
    val z: Float,
    val size: Float,
    val baseColor: Color
)

private val milkyWayParticles: List<GalaxyParticle> by lazy {
    val list = mutableListOf<GalaxyParticle>()
    val rng = java.util.Random(42)
    val arms = 4
    for (i in 0 until 480) {
        val arm = rng.nextInt(arms)
        val dist = 10f + rng.nextFloat() * 260f
        // Logarithmic spiral formula: angle = b * ln(r)
        val spiralAngle = (dist / 40f) + (arm * (2f * PI.toFloat() / arms))
        val spread = (rng.nextGaussian().toFloat() * 0.35f) * (1f + dist / 150f)
        val angle = spiralAngle + spread
        val z = (rng.nextGaussian().toFloat() * 18f) * exp(-dist / 140f)
        val size = 0.8f + rng.nextFloat() * 2.2f

        val color = when {
            dist < 40f -> Color(0xFFFFF7ED) // Core golden-white
            dist < 90f -> Color(0xFFFDE68A) // Inner yellow
            dist < 180f -> Color(0xFFBAE6FD) // Spiral arm bright cyan
            else -> Color(0xFF67E8F9) // Outer faint blue
        }

        list.add(GalaxyParticle(dist, angle, spread, z, size, color))
    }
    list
}

private val andromedaParticles: List<GalaxyParticle> by lazy {
    val list = mutableListOf<GalaxyParticle>()
    val rng = java.util.Random(31415)
    val arms = 2
    for (i in 0 until 420) {
        val arm = rng.nextInt(arms)
        val dist = 15f + rng.nextFloat() * 280f
        val spiralAngle = (dist / 32f) + (arm * PI.toFloat())
        val spread = rng.nextGaussian().toFloat() * 0.28f
        val angle = spiralAngle + spread
        val z = (rng.nextGaussian().toFloat() * 12f)
        val size = 0.9f + rng.nextFloat() * 2.0f

        val color = when {
            dist < 50f -> Color(0xFFFEF3C7) // Golden core
            dist < 140f -> Color(0xFFDDD6FE) // Purple dust
            else -> Color(0xFF93C5FD) // Outer blue halo
        }

        list.add(GalaxyParticle(dist, angle, spread, z, size, color))
    }
    list
}

@Composable
fun SpaceCanvasView(
    currentWorld: CelestialBody,
    playing: Boolean,
    autoRotate: Boolean,
    grid: Boolean,
    labels: Boolean,
    glow: Float,
    speed: Float,
    panMode: Boolean,
    selectedSolarBody: String?,
    onSelectSolarBody: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var azimuth by remember(currentWorld.id) {
        mutableFloatStateOf(if (currentWorld.id == "andromeda") 0.9f else if (currentWorld.id == "blackhole") 0.15f else 0.45f)
    }
    var pitch by remember(currentWorld.id) {
        mutableFloatStateOf(if (currentWorld.id == "blackhole") 0.12f else 0.65f)
    }
    var zoom by remember(currentWorld.id) { mutableFloatStateOf(1.0f) }
    var panX by remember(currentWorld.id) { mutableFloatStateOf(0f) }
    var panY by remember(currentWorld.id) { mutableFloatStateOf(0f) }

    var animTime by remember { mutableFloatStateOf(0f) }
    var lastFrameTime by remember { mutableLongStateOf(0L) }

    // Animation frame loop
    LaunchedEffect(playing, speed, autoRotate) {
        while (true) {
            withInfiniteAnimationFrameMillis { frameTime ->
                if (lastFrameTime != 0L && playing) {
                    val dt = (frameTime - lastFrameTime) / 1000f
                    animTime += dt * speed
                    if (autoRotate) {
                        azimuth += dt * 0.18f * speed
                    }
                }
                lastFrameTime = frameTime
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SpaceBlack)
            .pointerInput(panMode) {
                if (panMode) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        panX += dragAmount.x
                        panY += dragAmount.y
                    }
                } else {
                    detectTransformGestures { _, pan, gestureZoom, _ ->
                        azimuth += pan.x * 0.007f
                        pitch = (pitch + pan.y * 0.007f).coerceIn(-1.45f, 1.45f)
                        zoom = (zoom * gestureZoom).coerceIn(0.4f, 3.2f)
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f + panX
            val cy = size.height / 2f + panY

            // 1. Draw Starfield Background
            drawStarfieldBackground(size)

            // 2. Draw Reference Coordinate Grid if enabled
            if (grid) {
                drawCelestialGrid(cx, cy, pitch, azimuth, zoom)
            }

            // 3. Render Current Celestial World
            when (currentWorld.id) {
                "milkyway" -> {
                    drawMilkyWay(
                        cx = cx,
                        cy = cy,
                        azimuth = azimuth,
                        pitch = pitch,
                        zoom = zoom,
                        glow = glow,
                        animTime = animTime,
                        showLabels = labels
                    )
                }
                "solar" -> {
                    drawSolarSystem(
                        cx = cx,
                        cy = cy,
                        azimuth = azimuth,
                        pitch = pitch,
                        zoom = zoom,
                        glow = glow,
                        animTime = animTime,
                        showLabels = labels,
                        selectedId = selectedSolarBody,
                        onSelectBody = onSelectSolarBody
                    )
                }
                "andromeda" -> {
                    drawAndromeda(
                        cx = cx,
                        cy = cy,
                        azimuth = azimuth,
                        pitch = pitch,
                        zoom = zoom,
                        glow = glow,
                        animTime = animTime,
                        showLabels = labels
                    )
                }
                "blackhole" -> {
                    drawGargantuaBlackHole(
                        cx = cx,
                        cy = cy,
                        azimuth = azimuth,
                        pitch = pitch,
                        zoom = zoom,
                        glow = glow,
                        animTime = animTime,
                        showLabels = labels
                    )
                }
                "earth" -> {
                    drawEarthSystem(
                        cx = cx,
                        cy = cy,
                        azimuth = azimuth,
                        pitch = pitch,
                        zoom = zoom,
                        glow = glow,
                        animTime = animTime,
                        showLabels = labels
                    )
                }
                "moon" -> {
                    drawMoonCloseUp(
                        cx = cx,
                        cy = cy,
                        azimuth = azimuth,
                        pitch = pitch,
                        zoom = zoom,
                        glow = glow,
                        animTime = animTime,
                        showLabels = labels
                    )
                }
            }
        }
    }
}

// Background Starfield
private fun DrawScope.drawStarfieldBackground(size: Size) {
    for (star in backgroundStars) {
        val px = star.x * size.width
        val py = star.y * size.height
        drawCircle(
            color = Color.White.copy(alpha = star.alpha),
            radius = star.radius,
            center = Offset(px, py)
        )
    }
}

// Coordinate Reference Grid
private fun DrawScope.drawCelestialGrid(cx: Float, cy: Float, pitch: Float, azimuth: Float, zoom: Float) {
    val gridColor = TealStarlight.copy(alpha = 0.14f)
    val radii = listOf(80f, 160f, 240f, 320f)

    for (r in radii) {
        val scaledR = r * zoom
        drawOval(
            color = gridColor,
            topLeft = Offset(cx - scaledR, cy - scaledR * cos(pitch).coerceAtLeast(0.12f)),
            size = Size(scaledR * 2f, scaledR * 2f * cos(pitch).coerceAtLeast(0.12f)),
            style = Stroke(width = 1f)
        )
    }

    // Grid cross-hairs
    val axisLen = 350f * zoom
    val cosA = cos(azimuth) * axisLen
    val sinA = sin(azimuth) * axisLen * cos(pitch).coerceAtLeast(0.12f)
    drawLine(
        color = gridColor,
        start = Offset(cx - cosA, cy - sinA),
        end = Offset(cx + cosA, cy + sinA),
        strokeWidth = 1f
    )
    val cosB = -sin(azimuth) * axisLen
    val sinB = cos(azimuth) * axisLen * cos(pitch).coerceAtLeast(0.12f)
    drawLine(
        color = gridColor,
        start = Offset(cx - cosB, cy - sinB),
        end = Offset(cx + cosB, cy + sinB),
        strokeWidth = 1f
    )
}

// --- 1. Milky Way Renderer ---
private fun DrawScope.drawMilkyWay(
    cx: Float,
    cy: Float,
    azimuth: Float,
    pitch: Float,
    zoom: Float,
    glow: Float,
    animTime: Float,
    showLabels: Boolean
) {
    val rotationOffset = animTime * 0.05f

    // Core bulge glow
    val coreRadius = 45f * zoom
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFFFBF0).copy(alpha = 0.95f * glow),
                Color(0xFFFDE68A).copy(alpha = 0.7f * glow),
                Color(0xFF38BDF8).copy(alpha = 0.25f * glow),
                Color.Transparent
            ),
            center = Offset(cx, cy),
            radius = coreRadius * 2.8f
        ),
        radius = coreRadius * 2.8f,
        center = Offset(cx, cy)
    )

    // Central Bar
    val barLen = 65f * zoom
    val barAngle = azimuth + rotationOffset
    val bx1 = cx + cos(barAngle) * barLen
    val by1 = cy + sin(barAngle) * barLen * cos(pitch)
    val bx2 = cx - cos(barAngle) * barLen
    val by2 = cy - sin(barAngle) * barLen * cos(pitch)
    drawLine(
        brush = Brush.linearGradient(
            colors = listOf(Color.Transparent, Color(0xFFFEF3C7).copy(alpha = 0.6f), Color.Transparent),
            start = Offset(bx1, by1),
            end = Offset(bx2, by2)
        ),
        start = Offset(bx1, by1),
        end = Offset(bx2, by2),
        strokeWidth = 16f * zoom
    )

    // Spiral Arm Particles with 3D projection
    val focal = 600f
    var solarMarkerPos: Offset? = null

    for (p in milkyWayParticles) {
        val currAngle = p.angle + rotationOffset
        val wx = cos(currAngle) * p.dist
        val wy = sin(currAngle) * p.dist
        val wz = p.z

        // 3D rotation by azimuth and pitch
        val rx = wx * cos(azimuth) - wy * sin(azimuth)
        val ryTemp = wx * sin(azimuth) + wy * cos(azimuth)
        val ry = ryTemp * cos(pitch) - wz * sin(pitch)
        val rz = ryTemp * sin(pitch) + wz * cos(pitch)

        val scale = focal / (focal + rz) * zoom
        val sx = cx + rx * scale
        val sy = cy + ry * scale

        val alpha = ((0.35f + (rz + 200f) / 400f * 0.5f) * glow).coerceIn(0.15f, 0.95f)
        drawCircle(
            color = p.baseColor.copy(alpha = alpha),
            radius = (p.size * scale).coerceIn(0.5f, 4f),
            center = Offset(sx, sy)
        )
    }

    // Solar System Location on Orion Spur: 26,000 light years (~135 units distance)
    val sunDist = 135f
    val sunAngle = 1.85f + rotationOffset
    val swx = cos(sunAngle) * sunDist
    val swy = sin(sunAngle) * sunDist
    val srx = swx * cos(azimuth) - swy * sin(azimuth)
    val sryTemp = swx * sin(azimuth) + swy * cos(azimuth)
    val sry = sryTemp * cos(pitch)
    val srz = sryTemp * sin(pitch)
    val sunScale = focal / (focal + srz) * zoom
    val sunScreenX = cx + srx * sunScale
    val sunScreenY = cy + sry * sunScale
    solarMarkerPos = Offset(sunScreenX, sunScreenY)

    // Draw Solar Marker radar ring and dot
    solarMarkerPos.let { pos ->
        val pulse = (sin(animTime * 4f) * 0.5f + 0.5f)
        drawCircle(
            color = CyanNebula.copy(alpha = 0.25f + pulse * 0.35f),
            radius = (10f + pulse * 8f) * zoom,
            center = pos,
            style = Stroke(width = 1.5f)
        )
        drawCircle(
            color = Color.White,
            radius = 3.5f * zoom,
            center = pos
        )

        if (showLabels) {
            // Marker line & label
            val tagX = pos.x + 35f
            val tagY = pos.y - 25f
            drawLine(
                color = CyanNebula.copy(alpha = 0.7f),
                start = pos,
                end = Offset(tagX, tagY),
                strokeWidth = 1.2f
            )
            drawLine(
                color = CyanNebula.copy(alpha = 0.7f),
                start = Offset(tagX, tagY),
                end = Offset(tagX + 60f, tagY),
                strokeWidth = 1.2f
            )
            // Draw small label indicator dot
            drawCircle(
                color = CyanNebula,
                radius = 2.5f,
                center = Offset(tagX + 60f, tagY)
            )
        }
    }
}

// --- 2. Solar System Renderer ---
private fun DrawScope.drawSolarSystem(
    cx: Float,
    cy: Float,
    azimuth: Float,
    pitch: Float,
    zoom: Float,
    glow: Float,
    animTime: Float,
    showLabels: Boolean,
    selectedId: String?,
    onSelectBody: (String) -> Unit
) {
    val bodies = CelestialRepository.solarBodies
    val focal = 600f

    // Draw Sun at center
    val sunRadius = 26f * zoom
    val sunPulse = (sin(animTime * 3f) * 0.5f + 0.5f)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFFFFFF),
                Color(0xFFFFE082),
                Color(0xFFFFB300).copy(alpha = 0.8f * glow),
                Color(0xFFFF8F00).copy(alpha = 0.25f * glow),
                Color.Transparent
            ),
            center = Offset(cx, cy),
            radius = sunRadius * 3.5f
        ),
        radius = sunRadius * 3.5f,
        center = Offset(cx, cy)
    )
    drawCircle(
        color = Color(0xFFFFD54F),
        radius = sunRadius,
        center = Offset(cx, cy)
    )

    // Sun solar flares
    for (i in 0 until 6) {
        val angle = animTime * 0.3f + (i * PI.toFloat() / 3f)
        val fx = cx + cos(angle) * (sunRadius + 4f + sunPulse * 5f)
        val fy = cy + sin(angle) * (sunRadius + 4f + sunPulse * 5f) * cos(pitch).coerceAtLeast(0.2f)
        drawCircle(
            color = Color(0xFFFFCC80).copy(alpha = 0.6f * glow),
            radius = 3f * zoom,
            center = Offset(fx, fy)
        )
    }

    // Planetary Orbits and Bodies
    for (b in bodies) {
        if (b.id == "sun") continue

        val orbitR = b.orbit * 1.6f * zoom
        val ellipseH = orbitR * cos(pitch).coerceAtLeast(0.1f)

        // Draw orbital trajectory ring
        val isSelected = b.id == selectedId
        drawOval(
            color = if (isSelected) TealStarlight.copy(alpha = 0.55f) else Color.White.copy(alpha = 0.12f),
            topLeft = Offset(cx - orbitR, cy - ellipseH),
            size = Size(orbitR * 2f, ellipseH * 2f),
            style = Stroke(width = if (isSelected) 1.8f else 1f)
        )

        // Calculate planet position along its orbit
        val orbitalAngle = (animTime * (0.8f / b.period)) + b.phase
        val wx = cos(orbitalAngle) * orbitR
        val wy = sin(orbitalAngle) * orbitR

        // 3D projection
        val rx = wx * cos(azimuth) - wy * sin(azimuth)
        val ryTemp = wx * sin(azimuth) + wy * cos(azimuth)
        val ry = ryTemp * cos(pitch)
        val rz = ryTemp * sin(pitch)
        val scale = focal / (focal + rz)
        val px = cx + rx * scale
        val py = cy + ry * scale

        val planetSize = (b.radius * 7f * zoom).coerceIn(3.5f, 22f)

        // Selected halo indicator
        if (isSelected) {
            drawCircle(
                color = TealStarlight.copy(alpha = 0.4f),
                radius = planetSize + 7f,
                center = Offset(px, py),
                style = Stroke(width = 1.5f)
            )
        }

        // Draw Saturn's Rings
        if (b.id == "saturn") {
            val ringWidth = planetSize * 2.8f
            val ringHeight = planetSize * 0.85f * cos(pitch).coerceAtLeast(0.25f)
            drawOval(
                color = Color(0xFFE2D4B7).copy(alpha = 0.85f),
                topLeft = Offset(px - ringWidth / 2f, py - ringHeight / 2f),
                size = Size(ringWidth, ringHeight),
                style = Stroke(width = 2.5f * zoom)
            )
        }

        // Planet body
        drawCircle(
            color = b.color,
            radius = planetSize,
            center = Offset(px, py)
        )

        // Earth's Moon
        if (b.id == "earth") {
            val moonAngle = animTime * 4.5f
            val moonDist = planetSize + 8f * zoom
            val mx = px + cos(moonAngle) * moonDist
            val my = py + sin(moonAngle) * moonDist * 0.4f
            drawCircle(
                color = Color(0xFFCBD5E1),
                radius = 2f * zoom,
                center = Offset(mx, my)
            )
        }

        // Label annotation
        if (showLabels || isSelected) {
            drawCircle(
                color = if (isSelected) TealStarlight else Color.White.copy(alpha = 0.7f),
                radius = 1.5f,
                center = Offset(px, py - planetSize - 6f)
            )
        }
    }
}

// --- 3. Andromeda Galaxy Renderer ---
private fun DrawScope.drawAndromeda(
    cx: Float,
    cy: Float,
    azimuth: Float,
    pitch: Float,
    zoom: Float,
    glow: Float,
    animTime: Float,
    showLabels: Boolean
) {
    val rotationOffset = animTime * 0.03f
    val focal = 650f

    // Intense central core bulge
    val coreR = 50f * zoom
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFEF3C7).copy(alpha = 0.95f * glow),
                Color(0xFFDDD6FE).copy(alpha = 0.6f * glow),
                Color(0xFF818CF8).copy(alpha = 0.2f * glow),
                Color.Transparent
            ),
            center = Offset(cx, cy),
            radius = coreR * 3f
        ),
        radius = coreR * 3f,
        center = Offset(cx, cy)
    )

    // Inclined Spiral Particles
    val inclination = 0.32f // M31 is seen at ~77 degrees inclination
    for (p in andromedaParticles) {
        val currAngle = p.angle + rotationOffset
        val wx = cos(currAngle) * p.dist * 1.3f // elongated major axis
        val wy = sin(currAngle) * p.dist * inclination
        val wz = p.z

        val rx = wx * cos(azimuth) - wy * sin(azimuth)
        val ryTemp = wx * sin(azimuth) + wy * cos(azimuth)
        val ry = ryTemp * cos(pitch) - wz * sin(pitch)
        val rz = ryTemp * sin(pitch) + wz * cos(pitch)

        val scale = focal / (focal + rz) * zoom
        val sx = cx + rx * scale
        val sy = cy + ry * scale

        val alpha = ((0.3f + (rz + 200f) / 400f * 0.5f) * glow).coerceIn(0.12f, 0.9f)
        drawCircle(
            color = p.baseColor.copy(alpha = alpha),
            radius = (p.size * scale).coerceIn(0.6f, 3.8f),
            center = Offset(sx, sy)
        )
    }

    // Satellite Galaxies: M32 (compact dwarf) & NGC 205 (M110 diffuse)
    val m32X = cx + 90f * zoom * cos(azimuth + 0.6f)
    val m32Y = cy + 45f * zoom * sin(azimuth + 0.6f) * cos(pitch)
    drawCircle(
        color = Color(0xFFFEF3C7).copy(alpha = 0.8f * glow),
        radius = 5f * zoom,
        center = Offset(m32X, m32Y)
    )

    val m110X = cx - 130f * zoom * cos(azimuth - 0.4f)
    val m110Y = cy - 65f * zoom * sin(azimuth - 0.4f) * cos(pitch)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFC7D2FE).copy(alpha = 0.6f), Color.Transparent),
            center = Offset(m110X, m110Y),
            radius = 16f * zoom
        ),
        radius = 16f * zoom,
        center = Offset(m110X, m110Y)
    )
}

// --- 4. Gargantua Black Hole Renderer ---
private fun DrawScope.drawGargantuaBlackHole(
    cx: Float,
    cy: Float,
    azimuth: Float,
    pitch: Float,
    zoom: Float,
    glow: Float,
    animTime: Float,
    showLabels: Boolean
) {
    val bhRadius = 55f * zoom

    // 1. Warped Upper Accretion Disk (Light bent over the top of the event horizon)
    val upperDiskPath = Path().apply {
        val w = bhRadius * 2.8f
        val h = bhRadius * 1.8f
        moveTo(cx - w, cy)
        cubicTo(cx - w * 0.6f, cy - h, cx + w * 0.6f, cy - h, cx + w, cy)
        cubicTo(cx + w * 0.4f, cy - h * 0.7f, cx - w * 0.4f, cy - h * 0.7f, cx - w, cy)
        close()
    }
    drawPath(
        path = upperDiskPath,
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFFE082).copy(alpha = 0.85f * glow),
                Color(0xFFFF6F00).copy(alpha = 0.4f * glow),
                Color.Transparent
            ),
            startY = cy - bhRadius * 1.8f,
            endY = cy
        )
    )

    // 2. Warped Lower Accretion Disk (Light bent under the bottom)
    val lowerDiskPath = Path().apply {
        val w = bhRadius * 2.5f
        val h = bhRadius * 1.4f
        moveTo(cx - w, cy)
        cubicTo(cx - w * 0.5f, cy + h, cx + w * 0.5f, cy + h, cx + w, cy)
        cubicTo(cx + w * 0.35f, cy + h * 0.7f, cx - w * 0.35f, cy + h * 0.7f, cx - w, cy)
        close()
    }
    drawPath(
        path = lowerDiskPath,
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                Color(0xFFF57C00).copy(alpha = 0.35f * glow),
                Color(0xFFFFB74D).copy(alpha = 0.7f * glow)
            ),
            startY = cy,
            endY = cy + bhRadius * 1.4f
        )
    )

    // 3. Primary Equatorial Accretion Disk Ring
    val eqWidth = bhRadius * 3.4f
    val eqHeight = bhRadius * 0.85f * cos(pitch).coerceAtLeast(0.25f)

    // Doppler beaming asymmetry: approaching plasma on left side is brighter & bluer
    drawOval(
        brush = Brush.horizontalGradient(
            colors = listOf(
                Color(0xFFFFF9C4).copy(alpha = 0.95f * glow), // Bright blue/white approaching
                Color(0xFFFFB74D).copy(alpha = 0.85f * glow),
                Color(0xFFE65100).copy(alpha = 0.45f * glow)  // Dimmer red receding
            ),
            startX = cx - eqWidth,
            endX = cx + eqWidth
        ),
        topLeft = Offset(cx - eqWidth, cy - eqHeight),
        size = Size(eqWidth * 2f, eqHeight * 2f),
        style = Stroke(width = 18f * zoom)
    )

    // 4. Photon Sphere Thin Glowing Ring right outside horizon
    drawCircle(
        color = Color(0xFFFFF8E1).copy(alpha = 0.95f * glow),
        radius = bhRadius + 2.5f,
        center = Offset(cx, cy),
        style = Stroke(width = 2f)
    )

    // 5. Absolute Event Horizon Shadow (Schwarzschild sphere)
    drawCircle(
        color = Color.Black,
        radius = bhRadius,
        center = Offset(cx, cy)
    )

    // 6. Infalling Matter Stream Particles
    for (i in 0 until 35) {
        val angle = (animTime * 1.8f) + (i * 0.38f)
        val dist = bhRadius + 5f + (i % 6) * 14f * zoom
        val px = cx + cos(angle) * dist
        val py = cy + sin(angle) * dist * 0.38f
        drawCircle(
            color = Color(0xFFFFE082).copy(alpha = 0.75f),
            radius = 1.8f * zoom,
            center = Offset(px, py)
        )
    }
}

// --- 5. Earth System Renderer ---
private fun DrawScope.drawEarthSystem(
    cx: Float,
    cy: Float,
    azimuth: Float,
    pitch: Float,
    zoom: Float,
    glow: Float,
    animTime: Float,
    showLabels: Boolean
) {
    val earthRadius = 90f * zoom
    val sunDirX = 0.7f // Sunlight comes from top-right
    val sunDirY = -0.3f

    // Atmosphere scattering glow
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.Transparent,
                Color(0xFF38BDF8).copy(alpha = 0.45f * glow),
                Color(0xFF0284C7).copy(alpha = 0.2f * glow),
                Color.Transparent
            ),
            center = Offset(cx, cy),
            radius = earthRadius * 1.35f
        ),
        radius = earthRadius * 1.35f,
        center = Offset(cx, cy)
    )

    // Ocean Globe base
    drawCircle(
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFF2563EB), Color(0xFF1E3A8A), Color(0xFF0F172A)),
            start = Offset(cx - earthRadius * sunDirX, cy - earthRadius * sunDirY),
            end = Offset(cx + earthRadius * sunDirX, cy + earthRadius * sunDirY)
        ),
        radius = earthRadius,
        center = Offset(cx, cy)
    )

    // Continents / Landmasses (Projected spherical patches)
    val rotationOffset = animTime * 0.12f
    val landColor = Color(0xFF22C55E).copy(alpha = 0.65f)

    for (lat in -3..3) {
        val yOffset = lat * 22f * zoom
        val rAtLat = sqrt((earthRadius * earthRadius - yOffset * yOffset).coerceAtLeast(0f))

        for (lon in 0..5) {
            val angle = lon * (PI.toFloat() / 3f) + rotationOffset + azimuth
            val cosA = cos(angle)
            if (cosA > 0f) { // Front-facing hemisphere
                val lx = cx + sin(angle) * rAtLat
                val ly = cy + yOffset * cos(pitch)
                drawCircle(
                    color = landColor,
                    radius = (14f + (lat % 2) * 4f) * zoom * cosA,
                    center = Offset(lx, ly)
                )
            }
        }
    }

    // Dynamic Cloud Layers
    val cloudOffset = animTime * 0.18f
    val cloudColor = Color.White.copy(alpha = 0.45f)
    for (i in 0..7) {
        val angle = i * 0.8f + cloudOffset
        val cosA = cos(angle)
        if (cosA > 0f) {
            val clx = cx + sin(angle) * (earthRadius * 0.85f)
            val cly = cy + sin(i * 1.2f) * (earthRadius * 0.5f)
            drawOval(
                color = cloudColor,
                topLeft = Offset(clx - 16f * zoom, cly - 6f * zoom),
                size = Size(32f * zoom * cosA, 12f * zoom)
            )
        }
    }

    // Day/Night Terminator Shadow
    drawCircle(
        brush = Brush.linearGradient(
            colors = listOf(
                Color.Transparent,
                Color.Black.copy(alpha = 0.4f),
                Color.Black.copy(alpha = 0.88f)
            ),
            start = Offset(cx - earthRadius * 0.2f, cy - earthRadius * 0.2f),
            end = Offset(cx + earthRadius * 0.9f, cy + earthRadius * 0.9f)
        ),
        radius = earthRadius,
        center = Offset(cx, cy)
    )

    // Night City Lights
    for (i in 0..8) {
        val nx = cx + earthRadius * 0.3f + (i * 7f * zoom)
        val ny = cy + earthRadius * 0.2f + (i * 6f * zoom)
        val twinkle = (sin(animTime * 5f + i) * 0.4f + 0.6f)
        drawCircle(
            color = Color(0xFFFDE68A).copy(alpha = 0.7f * twinkle),
            radius = 1.2f * zoom,
            center = Offset(nx, ny)
        )
    }

    // Orbiting Moon
    val moonAngle = animTime * 0.6f
    val moonDist = earthRadius * 2.3f
    val mx = cx + cos(moonAngle) * moonDist
    val my = cy + sin(moonAngle) * moonDist * 0.45f
    drawCircle(
        color = Color(0xFFCBD5E1),
        radius = 12f * zoom,
        center = Offset(mx, my)
    )
    // Moon shadow
    drawCircle(
        color = Color.Black.copy(alpha = 0.65f),
        radius = 12f * zoom,
        center = Offset(mx + 4f, my + 3f)
    )
}

// --- 6. Moon Close-up Renderer ---
private fun DrawScope.drawMoonCloseUp(
    cx: Float,
    cy: Float,
    azimuth: Float,
    pitch: Float,
    zoom: Float,
    glow: Float,
    animTime: Float,
    showLabels: Boolean
) {
    val moonRadius = 110f * zoom

    // Moon spherical base
    drawCircle(
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFFE2E8F0), Color(0xFF94A3B8), Color(0xFF334155)),
            start = Offset(cx - moonRadius * 0.6f, cy - moonRadius * 0.6f),
            end = Offset(cx + moonRadius * 0.8f, cy + moonRadius * 0.8f)
        ),
        radius = moonRadius,
        center = Offset(cx, cy)
    )

    // Lunar Maria (Dark basaltic plains: Oceanus Procellarum, Mare Tranquillitatis)
    val maria = listOf(
        Pair(Offset(-30f, -20f), 42f),
        Pair(Offset(25f, -15f), 32f),
        Pair(Offset(-15f, 35f), 28f),
        Pair(Offset(35f, 25f), 25f)
    )
    for ((pos, r) in maria) {
        val mx = cx + pos.x * zoom
        val my = cy + pos.y * zoom
        drawCircle(
            color = Color(0xFF475569).copy(alpha = 0.55f),
            radius = r * zoom,
            center = Offset(mx, my)
        )
    }

    // Craters with rim highlights & shadows (Tycho, Copernicus)
    val craters = listOf(
        Pair(Offset(10f, 65f), 12f), // Tycho
        Pair(Offset(-25f, 5f), 10f), // Copernicus
        Pair(Offset(45f, -40f), 8f),
        Pair(Offset(-40f, -45f), 9f),
        Pair(Offset(0f, -60f), 11f)
    )
    for ((pos, r) in craters) {
        val crx = cx + pos.x * zoom
        val cry = cy + pos.y * zoom
        val crR = r * zoom

        // Crater shadow
        drawCircle(
            color = Color(0xFF1E293B),
            radius = crR,
            center = Offset(crx, cry)
        )
        // Crater rim highlight
        drawCircle(
            color = Color(0xFFF8FAFC),
            radius = crR,
            center = Offset(crx - 1.5f, cry - 1.5f),
            style = Stroke(width = 1.5f)
        )
    }

    // High-contrast terminator shadow
    drawCircle(
        brush = Brush.linearGradient(
            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.45f), Color.Black.copy(alpha = 0.95f)),
            start = Offset(cx - moonRadius * 0.1f, cy),
            end = Offset(cx + moonRadius * 0.85f, cy)
        ),
        radius = moonRadius,
        center = Offset(cx, cy)
    )
}
