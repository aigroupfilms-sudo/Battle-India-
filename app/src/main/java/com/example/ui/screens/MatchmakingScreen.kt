package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundManager
import com.example.ui.theme.DarkBlack
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GunmetalDark
import com.example.ui.theme.TacticalOrange
import com.example.ui.theme.TacticalOrangeBright
import kotlinx.coroutines.delay

@Composable
fun MatchmakingScreen(
    mode: String,
    onMatchReady: () -> Unit,
    onCancel: () -> Unit
) {
    var elapsedSeconds by remember { mutableIntStateOf(0) }
    var realPlayersFound by remember { mutableIntStateOf(1) }
    var statusText by remember { mutableStateOf("SEARCHING FOR REAL PLAYERS (INDIA-WEST)...") }
    val matchmakingTimeout = 7 // 7 seconds timeout

    LaunchedEffect(Unit) {
        SoundManager.playZoneWarning()
        while (elapsedSeconds < matchmakingTimeout) {
            delay(1000)
            elapsedSeconds++
            // Incrementally finding authentic players
            realPlayersFound = (realPlayersFound + (3..7).random()).coerceAtMost(50)
            if (elapsedSeconds == 3) {
                statusText = "MATCHING HIGH-TIER SQUADS & LOW-LATENCY SHARDS..."
            } else if (elapsedSeconds == 5) {
                statusText = "MATCHMAKING TIMEOUT REACHED — FILLING SLOTS WITH TACTICAL AGENTS..."
            }
        }
        statusText = "BATTLE ROYALE READY! PREPARING AIRPLANE DEPLOYMENT..."
        SoundManager.playButtonClick()
        delay(600)
        onMatchReady()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "radar_spin")
    val radarAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlack)
            .testTag("matchmaking_screen"),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .width(480.dp)
                .clip(RoundedCornerShape(14.dp))
                .border(1.5.dp, TacticalOrange, RoundedCornerShape(14.dp)),
            color = GunmetalDark
        ) {
            Column(
                modifier = Modifier.padding(26.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Public, contentDescription = "Region", tint = TacticalOrange)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "BATTLE INDIA MATCHMAKING",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Tactical Radar Animation
                Box(
                    modifier = Modifier.size(130.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val r = size.width / 2f

                        // Radar concentric rings
                        drawCircle(Color(0xFF2A2320), radius = r, style = Stroke(2f))
                        drawCircle(Color(0xFF2A2320), radius = r * 0.66f, style = Stroke(1.5f))
                        drawCircle(Color(0xFF2A2320), radius = r * 0.33f, style = Stroke(1.5f))

                        // Crosshairs
                        drawLine(Color(0xFF2A2320), Offset(0f, center.y), Offset(size.width, center.y), 1f)
                        drawLine(Color(0xFF2A2320), Offset(center.x, 0f), Offset(center.x, size.height), 1f)

                        // Rotating sweep line
                        val sweepRad = Math.toRadians(radarAngle.toDouble())
                        val sweepEnd = Offset(
                            center.x + (Math.cos(sweepRad) * r).toFloat(),
                            center.y + (Math.sin(sweepRad) * r).toFloat()
                        )
                        drawLine(TacticalOrangeBright, center, sweepEnd, 3f)

                        // Center blip
                        drawCircle(TacticalOrange, radius = 5f, center = center)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Stats row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF14110F), RoundedCornerShape(6.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "MODE: $mode", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TacticalOrange)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Wifi, contentDescription = "Ping", tint = EmeraldGreen, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "24ms (Mumbai Node)", fontSize = 11.sp, color = EmeraldGreen, fontWeight = FontWeight.Bold)
                    }
                    Text(text = "WAIT: 00:0$elapsedSeconds", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Status Text
                Text(
                    text = statusText,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFC0B8B2),
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { (elapsedSeconds.toFloat() / matchmakingTimeout).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = TacticalOrange,
                    trackColor = Color(0xFF241E1C)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        SoundManager.playButtonClick()
                        onCancel()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF29221F)),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(38.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Cancel", tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("CANCEL MATCHMAKING", color = Color(0xFFA09994), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
