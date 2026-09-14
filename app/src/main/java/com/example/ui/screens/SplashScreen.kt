package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundManager
import com.example.ui.theme.DarkBlack
import com.example.ui.theme.SaffronIndia
import com.example.ui.theme.TacticalOrange
import com.example.ui.theme.TacticalOrangeBright
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun SplashScreen(
    onLoadingComplete: () -> Unit
) {
    var progress by remember { mutableFloatStateOf(0f) }
    var tipIndex by remember { mutableIntStateOf(0) }

    val tips = listOf(
        "PRO TIP: Headshots with AWM deal 3.0x catastrophic damage!",
        "TACTIC: Stay inside the Safe Zone; storm damage increases with each phase.",
        "MAP INTEL: Pokhran Military Base holds high-tier weapons and Level 3 armor.",
        "SURVIVAL: Use the Jeep or Buggy to traverse Ganges bridges safely.",
        "COMBAT: Crouch or prone to drastically stabilize AKM vertical recoil."
    )

    LaunchedEffect(Unit) {
        SoundManager.playZoneWarning()
        val startTime = System.currentTimeMillis()
        while (progress < 1.0f) {
            delay(40)
            progress += 0.015f
            if ((System.currentTimeMillis() - startTime) % 2500 < 60) {
                tipIndex = (tipIndex + 1) % tips.size
            }
        }
        delay(400)
        onLoadingComplete()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "cinematic_bg")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_glow"
    )

    val particleRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particle_rot"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlack)
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Dynamic Orange-Black Cinematic Lighting Canvas & Particles
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)

            // Radial orange-black beam
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        TacticalOrange.copy(alpha = 0.35f * pulseGlow),
                        Color(0xFF240A00).copy(alpha = 0.25f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = size.maxDimension * 0.55f
                ),
                radius = size.maxDimension * 0.55f,
                center = center
            )

            // Dynamic diagonal searchlight beams
            val beamAngleRad = Math.toRadians(particleRotation.toDouble() * 0.4)
            val beamEnd = Offset(
                center.x + (cos(beamAngleRad) * size.width).toFloat(),
                center.y + (sin(beamAngleRad) * size.height).toFloat()
            )
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(TacticalOrangeBright.copy(alpha = 0.25f), Color.Transparent),
                    start = center,
                    end = beamEnd
                ),
                start = center,
                end = beamEnd,
                strokeWidth = 140f,
                cap = StrokeCap.Round
            )

            // Embers and tactical spark particles
            val rand = Random(1337)
            for (i in 0..35) {
                val pDist = rand.nextFloat() * size.width * 0.45f
                val pAngle = Math.toRadians((particleRotation * (0.8f + (i % 3) * 0.4f) + i * 18).toDouble())
                val px = center.x + (cos(pAngle) * pDist).toFloat()
                val py = center.y + (sin(pAngle) * pDist * 0.6f).toFloat()
                val pRadius = 1.5f + (i % 4) * 1.5f
                drawCircle(
                    color = if (i % 3 == 0) SaffronIndia else TacticalOrangeBright,
                    radius = pRadius,
                    center = Offset(px, py)
                )
            }

            // Tactical Corner brackets
            val bracketLen = 40f
            val strokeW = 3f
            val bracketColor = TacticalOrange.copy(alpha = 0.6f)
            // Top Left
            drawLine(bracketColor, Offset(40f, 40f), Offset(40f + bracketLen, 40f), strokeW)
            drawLine(bracketColor, Offset(40f, 40f), Offset(40f, 40f + bracketLen), strokeW)
            // Top Right
            drawLine(bracketColor, Offset(size.width - 40f, 40f), Offset(size.width - 40f - bracketLen, 40f), strokeW)
            drawLine(bracketColor, Offset(size.width - 40f, 40f), Offset(size.width - 40f, 40f + bracketLen), strokeW)
            // Bottom Left
            drawLine(bracketColor, Offset(40f, size.height - 40f), Offset(40f + bracketLen, size.height - 40f), strokeW)
            drawLine(bracketColor, Offset(40f, size.height - 40f), Offset(40f, size.height - 40f - bracketLen), strokeW)
            // Bottom Right
            drawLine(bracketColor, Offset(size.width - 40f, size.height - 40f), Offset(size.width - 40f - bracketLen, size.height - 40f), strokeW)
            drawLine(bracketColor, Offset(size.width - 40f, size.height - 40f), Offset(size.width - 40f, size.height - 40f - bracketLen), strokeW)
        }

        // Center Animated Battle India Emblem & Logo
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            // Metallic Emblem Badge
            Box(
                modifier = Modifier.size(110.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cx = size.width / 2f
                    val cy = size.height / 2f
                    // Outer Hexagon
                    val hexPath = Path().apply {
                        val r = size.width * 0.46f
                        for (j in 0..5) {
                            val rad = Math.toRadians((j * 60 - 30).toDouble())
                            val x = cx + (r * cos(rad)).toFloat()
                            val y = cy + (r * sin(rad)).toFloat()
                            if (j == 0) moveTo(x, y) else lineTo(x, y)
                        }
                        close()
                    }
                    drawPath(hexPath, Color(0xFF1F1A18))
                    drawPath(hexPath, TacticalOrange, style = Stroke(width = 4f))

                    // Indian Tricolor Ribbon in Crest
                    drawLine(SaffronIndia, Offset(cx - 24f, cy + 18f), Offset(cx + 24f, cy + 18f), 4f)
                    drawLine(Color.White, Offset(cx - 24f, cy + 23f), Offset(cx + 24f, cy + 23f), 4f)
                    drawLine(Color(0xFF138808), Offset(cx - 24f, cy + 28f), Offset(cx + 24f, cy + 28f), 4f)

                    // Stylized Tiger Skull / Military Star
                    val starPath = Path().apply {
                        val outerR = 24f
                        val innerR = 10f
                        for (k in 0..9) {
                            val r = if (k % 2 == 0) outerR else innerR
                            val rad = Math.toRadians((k * 36 - 90).toDouble())
                            val x = cx + (r * cos(rad)).toFloat()
                            val y = cy + (r * sin(rad)).toFloat()
                            if (k == 0) moveTo(x, y) else lineTo(x, y)
                        }
                        close()
                    }
                    drawPath(starPath, TacticalOrangeBright)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Animated Battle India Logo
            Text(
                text = "BATTLE INDIA",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                fontSize = 34.sp,
                letterSpacing = 4.sp,
                color = Color.White
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(6.dp).background(TacticalOrangeBright, RoundedCornerShape(3.dp)))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "NEXT-GEN AAA BATTLE ROYALE",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 3.sp,
                    color = TacticalOrangeBright
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.size(6.dp).background(TacticalOrangeBright, RoundedCornerShape(3.dp)))
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Loading Progress Bar
            Box(
                modifier = Modifier
                    .width(360.dp)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF26211E))
            ) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(),
                    color = TacticalOrange,
                    trackColor = Color(0xFF26211E),
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Loading Tip
            Text(
                text = tips[tipIndex],
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFC0B8B2),
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "SYNCHRONIZING ASSETS & SERVER SHARDS... ${(progress * 100).toInt()}%",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp,
                letterSpacing = 1.sp,
                color = Color(0xFF88807A)
            )
        }
    }
}
