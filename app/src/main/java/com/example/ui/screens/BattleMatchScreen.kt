package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundManager
import com.example.engine.BattleIndiaEngine
import com.example.engine.MatchPhase
import com.example.model.PlayerStance
import com.example.model.VehicleType
import com.example.model.WeaponType
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DarkBlack
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GunmetalBorder
import com.example.ui.theme.GunmetalDark
import com.example.ui.theme.GunmetalMedium
import com.example.ui.theme.SaffronIndia
import com.example.ui.theme.TacticalOrange
import com.example.ui.theme.TacticalOrangeBright
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun BattleMatchScreen(
    engine: BattleIndiaEngine,
    onExitToLobby: () -> Unit
) {
    // 60 FPS Game Loop
    LaunchedEffect(Unit) {
        var lastTime = System.nanoTime()
        while (true) {
            val now = System.nanoTime()
            val dt = ((now - lastTime) / 1_000_000_000f).coerceIn(0.001f, 0.05f)
            lastTime = now
            engine.update(dt)
            delay(16) // ~60 fps
        }
    }

    // Virtual Joystick offsets
    var stickOffsetX by remember { mutableFloatStateOf(0f) }
    var stickOffsetY by remember { mutableFloatStateOf(0f) }
    var showMinimapEnlarged by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "storm_pulse")
    val stormPulse by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "storm"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlack)
            .testTag("battle_match_screen")
    ) {
        // MAIN TACTICAL BATTLEFIELD CANVAS
        Canvas(modifier = Modifier.fillMaxSize()) {
            val screenCenterX = size.width / 2f
            val screenCenterY = size.height / 2f

            // Offset world relative to player position or plane
            val cameraWorldX = if (engine.matchPhase == MatchPhase.AIRPLANE_FLYOVER) engine.planeX else engine.playerX
            val cameraWorldY = if (engine.matchPhase == MatchPhase.AIRPLANE_FLYOVER) engine.planeY else engine.playerY

            val zoom = if (engine.matchPhase == MatchPhase.AIRPLANE_FLYOVER) 0.35f
            else if (engine.matchPhase == MatchPhase.PARACHUTE_DESCENT) 0.55f
            else if (engine.isAimingScope) 1.65f else 1.0f

            fun worldToScreen(wx: Float, wy: Float): Offset {
                val dx = (wx - cameraWorldX) * zoom
                val dy = (wy - cameraWorldY) * zoom
                return Offset(screenCenterX + dx, screenCenterY + dy)
            }

            // 1. Terrain Ground (India Biomes)
            drawRect(Color(0xFF2B2620)) // Base terrain

            // Biome zones
            // Desert (Thar)
            val desertScr = worldToScreen(1950f, 1800f)
            drawCircle(Color(0xFF6B5838), radius = 320f * zoom, center = desertScr)

            // Punjab Mustard Farms
            val farmScr = worldToScreen(900f, 650f)
            drawCircle(Color(0xFF5E682E), radius = 260f * zoom, center = farmScr)

            // Nilgiri Pine Forest
            val forestScr = worldToScreen(1600f, 1300f)
            drawCircle(Color(0xFF1E3A24), radius = 300f * zoom, center = forestScr)

            // Pokhran Military Base
            val pokhranScr = worldToScreen(1850f, 450f)
            drawRect(Color(0xFF33302D), topLeft = Offset(pokhranScr.x - 120f * zoom, pokhranScr.y - 120f * zoom), size = Size(240f * zoom, 240f * zoom))

            // Ganges River (meandering across middle)
            val riverPath = Path().apply {
                val p0 = worldToScreen(0f, 1200f)
                val p1 = worldToScreen(600f, 1150f)
                val p2 = worldToScreen(1200f, 1250f)
                val p3 = worldToScreen(1800f, 1180f)
                val p4 = worldToScreen(2400f, 1220f)
                moveTo(p0.x, p0.y)
                quadraticTo(p1.x, p1.y, p2.x, p2.y)
                quadraticTo(p3.x, p3.y, p4.x, p4.y)
            }
            drawPath(riverPath, Color(0xFF1A3B4D), style = Stroke(width = 80f * zoom))

            // Howrah Bridge Crossroad
            val bridgeScr = worldToScreen(850f, 1450f)
            drawRect(Color(0xFF4A4440), topLeft = Offset(bridgeScr.x - 25f * zoom, bridgeScr.y - 50f * zoom), size = Size(50f * zoom, 100f * zoom))

            // 2. Safe Zone (Electric Blue Force Field)
            val szCenterScr = worldToScreen(engine.safeZoneCenterX, engine.safeZoneCenterY)
            val szRadiusScr = engine.safeZoneRadius * zoom
            drawCircle(
                color = ElectricBlue.copy(alpha = 0.25f * stormPulse),
                radius = szRadiusScr,
                center = szCenterScr,
                style = Stroke(width = 5f)
            )

            // 3. Red Zone (Artillery Bombardment)
            if (engine.redZoneActive) {
                val rzCenterScr = worldToScreen(engine.redZoneX, engine.redZoneY)
                val rzRadiusScr = engine.redZoneRadius * zoom
                drawCircle(
                    color = DangerRed.copy(alpha = 0.22f),
                    radius = rzRadiusScr,
                    center = rzCenterScr
                )
                drawCircle(
                    color = DangerRed,
                    radius = rzRadiusScr,
                    center = rzCenterScr,
                    style = Stroke(width = 3f)
                )
            }

            // 4. Ground Loot items
            for (loot in engine.groundLoots) {
                val lscr = worldToScreen(loot.x, loot.y)
                if (lscr.x in -50f..size.width + 50f && lscr.y in -50f..size.height + 50f) {
                    drawCircle(if (loot.weaponType != null) AmberGold else CyberCyan, radius = 6f * zoom, center = lscr)
                }
            }

            // 5. Vehicles
            for (veh in engine.vehicles) {
                val vscr = worldToScreen(veh.x, veh.y)
                val vehColor = when (veh.type) {
                    VehicleType.JEEP -> Color(0xFF425438)
                    VehicleType.BIKE -> TacticalOrange
                    VehicleType.CAR -> AmberGold
                    VehicleType.BOAT -> CyberCyan
                }
                drawRect(
                    color = vehColor,
                    topLeft = Offset(vscr.x - 14f * zoom, vscr.y - 24f * zoom),
                    size = Size(28f * zoom, 48f * zoom)
                )
                // Headlights
                drawLine(Color.Yellow.copy(alpha = 0.4f), vscr, Offset(vscr.x, vscr.y - 45f * zoom), 6f)
            }

            // 6. Bots
            for (bot in engine.bots) {
                if (!bot.isAlive) continue
                val bscr = worldToScreen(bot.x, bot.y)
                if (bscr.x in -50f..size.width + 50f && bscr.y in -50f..size.height + 50f) {
                    // Bot body
                    drawCircle(Color(0xFF8B2500), radius = 12f * zoom, center = bscr)
                    // Aim direction line
                    val bAngleRad = Math.toRadians((bot.angleDeg - 90.0))
                    val bAimEnd = Offset(
                        bscr.x + (cos(bAngleRad) * 22f * zoom).toFloat(),
                        bscr.y + (sin(bAngleRad) * 22f * zoom).toFloat()
                    )
                    drawLine(Color.Black, bscr, bAimEnd, 4f * zoom)
                    // Health bar above bot
                    val barW = 28f * zoom
                    val barH = 3f * zoom
                    val hpFrac = bot.hp / bot.maxHp
                    drawRect(Color.Red, topLeft = Offset(bscr.x - barW / 2f, bscr.y - 20f * zoom), size = Size(barW, barH))
                    drawRect(EmeraldGreen, topLeft = Offset(bscr.x - barW / 2f, bscr.y - 20f * zoom), size = Size(barW * hpFrac, barH))
                }
            }

            // 7. Supply Drop Crate
            if (engine.supplyDropActive) {
                val sdScr = worldToScreen(engine.supplyDropX, engine.supplyDropY)
                drawRect(Color.Red, topLeft = Offset(sdScr.x - 12f * zoom, sdScr.y - 12f * zoom), size = Size(24f * zoom, 24f * zoom))
                drawRect(Color.Blue, topLeft = Offset(sdScr.x - 12f * zoom, sdScr.y - 16f * zoom), size = Size(24f * zoom, 4f * zoom))
                // Red/Orange smoke flare
                drawCircle(TacticalOrangeBright.copy(alpha = 0.4f), radius = 30f * zoom, center = Offset(sdScr.x, sdScr.y - 25f * zoom))
            }

            // 8. Player Character
            if (engine.matchPhase == MatchPhase.ACTIVE_COMBAT) {
                val pscr = worldToScreen(engine.playerX, engine.playerY)
                // Player circle
                drawCircle(Color(0xFF1E1B18), radius = 15f * zoom, center = pscr)
                drawCircle(TacticalOrange, radius = 13f * zoom, center = pscr)

                // Aiming weapon line / barrel
                val pAimRad = Math.toRadians((engine.playerRotationDeg - 90.0))
                val pAimEnd = Offset(
                    pscr.x + (cos(pAimRad) * 32f * zoom).toFloat(),
                    pscr.y + (sin(pAimRad) * 32f * zoom).toFloat()
                )
                drawLine(Color.Black, pscr, pAimEnd, 6f * zoom, cap = StrokeCap.Round)
                drawLine(AmberGold, pscr, pAimEnd, 3f * zoom)

                // Muzzle flash when firing
                if (engine.screenShakeAmount > 1.5f) {
                    drawCircle(Color.Yellow, radius = 10f * zoom, center = pAimEnd)
                }
            } else if (engine.matchPhase == MatchPhase.AIRPLANE_FLYOVER) {
                // Airplane Icon
                val planeScr = worldToScreen(engine.planeX, engine.planeY)
                drawCircle(Color.White, radius = 24f, center = planeScr)
                drawLine(Color.White, Offset(planeScr.x - 40f, planeScr.y), Offset(planeScr.x + 40f, planeScr.y), 10f, cap = StrokeCap.Round)
            } else if (engine.matchPhase == MatchPhase.PARACHUTE_DESCENT) {
                // Parachute canopy above player
                val pscr = worldToScreen(engine.playerX, engine.playerY)
                if (engine.parachuteDeployed) {
                    drawArc(
                        color = SaffronIndia,
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = true,
                        topLeft = Offset(pscr.x - 30f, pscr.y - 50f),
                        size = Size(60f, 30f)
                    )
                }
                drawCircle(TacticalOrange, radius = 12f, center = pscr)
            }

            // 9. Bullet Tracers & Explosions
            for (p in engine.bulletParticles) {
                val p1 = worldToScreen(p.startX, p.startY)
                val p2 = worldToScreen(p.endX, p.endY)
                drawLine(p.color, p1, p2, 3f, cap = StrokeCap.Round)
            }

            for (ex in engine.explosionEffects) {
                val escr = worldToScreen(ex.x, ex.y)
                val lifeFrac = ((System.currentTimeMillis() - ex.creationTime).toFloat() / ex.durationMs).coerceIn(0f, 1f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.Yellow, TacticalOrange, DangerRed, Color.Transparent),
                        center = escr,
                        radius = ex.maxRadius * zoom * lifeFrac
                    ),
                    radius = ex.maxRadius * zoom * lifeFrac,
                    center = escr
                )
            }

            // Storm Damage Edge Vignette
            if (engine.isPlayerInStorm) {
                drawRect(DangerRed.copy(alpha = 0.22f * stormPulse))
            }
        }

        // SCOPE OVERLAY (When Aiming Scope)
        if (engine.isAimingScope) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cx = size.width / 2f
                val cy = size.height / 2f
                val scopeRadius = size.height * 0.42f
                // Dark outer mask
                drawRect(Color.Black.copy(alpha = 0.75f))
                drawCircle(Color.Transparent, radius = scopeRadius, center = Offset(cx, cy))
                // Scope reticle
                drawCircle(Color.Black, radius = scopeRadius, center = Offset(cx, cy), style = Stroke(6f))
                drawLine(Color.Black, Offset(cx - scopeRadius, cy), Offset(cx + scopeRadius, cy), 2f)
                drawLine(Color.Black, Offset(cx, cy - scopeRadius), Offset(cx, cy + scopeRadius), 2f)
                // Range ticks
                drawCircle(Color.Red, radius = 4f, center = Offset(cx, cy))
            }
        }

        // TOP HUD: Compass Bearing, Alive & Kill Counters, Safe Zone Timer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Kill Feed (Top Left)
            Column(modifier = Modifier.width(220.dp)) {
                engine.killFeed.take(3).forEach { item ->
                    Row(
                        modifier = Modifier
                            .padding(vertical = 2.dp)
                            .background(DarkBlack.copy(alpha = 0.75f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.killer,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (item.isPlayerKiller) AmberGold else Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (item.isHeadshot) "🎯" else "☠️",
                            fontSize = 9.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = item.victim,
                            fontSize = 9.sp,
                            color = Color(0xFFD0C8C2)
                        )
                    }
                }
            }

            // Top Center: Compass & Stats
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Compass Strip
                Surface(
                    modifier = Modifier
                        .width(280.dp)
                        .height(26.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .border(1.dp, GunmetalBorder, RoundedCornerShape(6.dp)),
                    color = GunmetalDark.copy(alpha = 0.85f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("NW", fontSize = 10.sp, color = Color.Gray)
                        Text("N", fontSize = 11.sp, fontWeight = FontWeight.Black, color = TacticalOrange)
                        Text("015°", fontSize = 10.sp, color = AmberGold, fontWeight = FontWeight.Bold)
                        Text("NE", fontSize = 10.sp, color = Color.Gray)
                        Text("E", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Stats Bar (Alive, Kills, Zone Timer)
                Row(
                    modifier = Modifier
                        .background(GunmetalDark.copy(alpha = 0.9f), RoundedCornerShape(6.dp))
                        .border(1.dp, TacticalOrange.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ALIVE: ${engine.alivePlayersCount}",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "KILLS: ${engine.killCount}",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        color = TacticalOrangeBright
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ElectricBolt,
                            contentDescription = "Safe Zone",
                            tint = ElectricBlue,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = if (engine.isZoneShrinking) "SHRINKING!" else "ZONE: ${engine.zoneTimerSeconds}s",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (engine.isZoneShrinking) DangerRed else ElectricBlue
                        )
                    }
                }
            }

            // Top Right: Minimap Radar (Click to enlarge)
            Box(
                modifier = Modifier
                    .size(if (showMinimapEnlarged) 180.dp else 95.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(GunmetalDark.copy(alpha = 0.95f))
                    .border(1.5.dp, TacticalOrange, RoundedCornerShape(8.dp))
                    .clickable { showMinimapEnlarged = !showMinimapEnlarged }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val scale = size.width / engine.mapWidth
                    // Terrain bounds
                    drawRect(Color(0xFF26211C))
                    // River
                    drawLine(Color(0xFF1A3B4D), Offset(0f, 1200f * scale), Offset(size.width, 1200f * scale), 8f * scale)
                    // Safe Zone ring on minimap
                    drawCircle(
                        color = ElectricBlue,
                        radius = engine.safeZoneRadius * scale,
                        center = Offset(engine.safeZoneCenterX * scale, engine.safeZoneCenterY * scale),
                        style = Stroke(2f)
                    )
                    // Red zone on minimap
                    if (engine.redZoneActive) {
                        drawCircle(
                            color = DangerRed.copy(alpha = 0.4f),
                            radius = engine.redZoneRadius * scale,
                            center = Offset(engine.redZoneX * scale, engine.redZoneY * scale)
                        )
                    }
                    // Player arrow
                    drawCircle(TacticalOrangeBright, radius = 4f, center = Offset(engine.playerX * scale, engine.playerY * scale))
                }
                Text(
                    text = "INDIA MAP",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }

        // AIRPLANE / SKYDIVE ACTION OVERLAY
        if (engine.matchPhase == MatchPhase.AIRPLANE_FLYOVER) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 60.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Button(
                    onClick = {
                        SoundManager.playButtonClick()
                        engine.jumpFromPlane()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TacticalOrange),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .width(220.dp)
                        .height(54.dp)
                        .testTag("jump_plane_button")
                ) {
                    Icon(Icons.Default.FlightTakeoff, contentDescription = "Jump", tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("JUMP OUT OF PLANE", fontWeight = FontWeight.Black, fontSize = 14.sp, color = Color.Black)
                }
            }
        } else if (engine.matchPhase == MatchPhase.PARACHUTE_DESCENT) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 60.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "ALTITUDE: ${engine.playerAltitude.toInt()}m",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = AmberGold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (!engine.parachuteDeployed) {
                        Button(
                            onClick = {
                                SoundManager.playButtonClick()
                                engine.deployParachute()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(44.dp)
                        ) {
                            Text("DEPLOY PARACHUTE", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Black)
                        }
                    }
                }
            }
        }

        // ACTIVE COMBAT MOBILE CONTROLS & HUD
        if (engine.matchPhase == MatchPhase.ACTIVE_COMBAT) {
            // LEFT THUMB: Virtual Analog Joystick
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 28.dp, bottom = 28.dp)
                    .size(130.dp)
                    .clip(CircleShape)
                    .background(GunmetalDark.copy(alpha = 0.65f))
                    .border(1.5.dp, TacticalOrange.copy(alpha = 0.5f), CircleShape)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragEnd = {
                                stickOffsetX = 0f
                                stickOffsetY = 0f
                            },
                            onDragCancel = {
                                stickOffsetX = 0f
                                stickOffsetY = 0f
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                stickOffsetX = (stickOffsetX + dragAmount.x).coerceIn(-65f, 65f)
                                stickOffsetY = (stickOffsetY + dragAmount.y).coerceIn(-65f, 65f)
                                val normX = stickOffsetX / 65f
                                val normY = stickOffsetY / 65f
                                engine.movePlayer(normX, normY, 0.016f)
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                // Joystick Thumb Knob
                Box(
                    modifier = Modifier
                        .offset { IntOffset(stickOffsetX.roundToInt(), stickOffsetY.roundToInt()) }
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(TacticalOrangeBright)
                        .border(2.dp, Color.White, CircleShape)
                )
            }

            // RIGHT THUMB: Tactical Combat Action Cluster
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 20.dp)
                    .size(230.dp)
            ) {
                // PRIMARY FIRE BUTTON (Large high-contrast circular trigger)
                Button(
                    onClick = { engine.fireWeapon() },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
                    shape = CircleShape,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(76.dp)
                        .testTag("fire_button")
                ) {
                    Text("FIRE", fontWeight = FontWeight.Black, fontSize = 14.sp, color = Color.White)
                }

                // SCOPE / AIM TOGGLE
                Button(
                    onClick = {
                        SoundManager.playButtonClick()
                        engine.isAimingScope = !engine.isAimingScope
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (engine.isAimingScope) TacticalOrange else GunmetalMedium
                    ),
                    shape = CircleShape,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(50.dp)
                ) {
                    Text("SCOPE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                // RELOAD BUTTON
                Button(
                    onClick = { engine.reloadWeapon() },
                    colors = ButtonDefaults.buttonColors(containerColor = GunmetalMedium),
                    shape = CircleShape,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(46.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reload", tint = AmberGold, modifier = Modifier.size(20.dp))
                }

                // CROUCH / STANCE
                Button(
                    onClick = {
                        SoundManager.playButtonClick()
                        engine.stance = if (engine.stance == PlayerStance.CROUCHING) PlayerStance.STANDING else PlayerStance.CROUCHING
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (engine.stance == PlayerStance.CROUCHING) TacticalOrange else GunmetalMedium
                    ),
                    shape = CircleShape,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(end = 40.dp)
                        .size(44.dp)
                ) {
                    Text("CRCH", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                // GRENADE THROW
                Button(
                    onClick = { engine.throwGrenade() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4E342E)),
                    shape = CircleShape,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(46.dp)
                ) {
                    Text("💣 ${engine.grenadesCount}", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                // VEHICLE ENTER / EXIT
                Button(
                    onClick = { engine.toggleVehicleEntry() },
                    colors = ButtonDefaults.buttonColors(containerColor = if (engine.isDriving) EmeraldGreen else Color(0xFF37474F)),
                    shape = CircleShape,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .size(46.dp)
                ) {
                    Icon(Icons.Default.DirectionsCar, contentDescription = "Vehicle", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }

            // BOTTOM BAR: Health, Armor, Weapon Slots & Medkit
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp)
                    .background(GunmetalDark.copy(alpha = 0.9f), RoundedCornerShape(10.dp))
                    .border(1.dp, GunmetalBorder, RoundedCornerShape(10.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Health & Armor Meters
                Column(modifier = Modifier.width(130.dp)) {
                    // Armor
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Shield, contentDescription = "Armor", tint = CyberCyan, modifier = Modifier.size(10.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        LinearProgressIndicator(
                            progress = { (engine.playerArmor / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                            color = CyberCyan,
                            trackColor = Color(0xFF2B2620)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    // Health
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocalHospital, contentDescription = "HP", tint = EmeraldGreen, modifier = Modifier.size(10.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        LinearProgressIndicator(
                            progress = { (engine.playerHp / engine.maxHp).coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = if (engine.playerHp > 30f) EmeraldGreen else DangerRed,
                            trackColor = Color(0xFF2B2620)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Weapon Slot 1
                WeaponSlotCard(
                    weapon = engine.primaryWeapon,
                    magAmmo = engine.magAmmo1,
                    reserveAmmo = engine.reserveAmmo1,
                    isSelected = engine.currentWeaponSlot == 1,
                    onClick = { engine.switchWeapon(1) }
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Weapon Slot 2
                WeaponSlotCard(
                    weapon = engine.secondaryWeapon,
                    magAmmo = engine.magAmmo2,
                    reserveAmmo = engine.reserveAmmo2,
                    isSelected = engine.currentWeaponSlot == 2,
                    onClick = { engine.switchWeapon(2) }
                )

                Spacer(modifier = Modifier.width(10.dp))

                // Quick Medkit Use
                Button(
                    onClick = { engine.useMedkit() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(38.dp)
                ) {
                    Text("+ MED (${engine.medkitsCount})", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Pickup Ground Loot button
                Button(
                    onClick = { engine.pickupNearbyLoot() },
                    colors = ButtonDefaults.buttonColors(containerColor = GunmetalMedium),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(38.dp)
                ) {
                    Text("LOOT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AmberGold)
                }
            }
        }

        // MATCH OVER VICTORY / DEFEAT SCREEN
        if (engine.matchPhase == MatchPhase.MATCH_OVER) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier
                        .width(460.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(2.dp, if (engine.isVictory) AmberGold else DangerRed, RoundedCornerShape(16.dp)),
                    color = GunmetalDark
                ) {
                    Column(
                        modifier = Modifier.padding(26.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (engine.isVictory) "WINNER WINNER KHANA KHAZANA!" else "MATCH TERMINATED",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = if (engine.isVictory) AmberGold else DangerRed
                        )
                        Text(
                            text = if (engine.isVictory) "CHAMPION OF BATTLE INDIA #1" else "RANKED PLACEMENT #${engine.finalPlacement}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Match Summary Stats
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("ELIMINATIONS", fontSize = 10.sp, color = Color.Gray)
                                Text("${engine.killCount}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = TacticalOrange)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("DAMAGE DEALT", fontSize = 10.sp, color = Color.Gray)
                                Text("${engine.damageDealt.toInt()}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = AmberGold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("HEADSHOTS", fontSize = 10.sp, color = Color.Gray)
                                Text("${engine.headshotKills}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = CyberCyan)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("RANK RATING", fontSize = 10.sp, color = Color.Gray)
                                Text(if (engine.isVictory) "+45 RP" else "+18 RP", fontSize = 18.sp, fontWeight = FontWeight.Black, color = EmeraldGreen)
                            }
                        }

                        Spacer(modifier = Modifier.height(22.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = {
                                    SoundManager.playButtonClick()
                                    onExitToLobby()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GunmetalMedium),
                                modifier = Modifier.weight(1f).height(44.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("RETURN TO LOBBY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }

                            Button(
                                onClick = {
                                    SoundManager.playButtonClick()
                                    engine.resetMatch()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = TacticalOrange),
                                modifier = Modifier.weight(1f).height(44.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("PLAY AGAIN", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.Black)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeaponSlotCard(
    weapon: WeaponType,
    magAmmo: Int,
    reserveAmmo: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(110.dp)
            .height(44.dp)
            .clip(RoundedCornerShape(6.dp))
            .border(1.5.dp, if (isSelected) TacticalOrange else GunmetalBorder, RoundedCornerShape(6.dp))
            .clickable(onClick = onClick),
        color = if (isSelected) Color(0x33FF6D00) else GunmetalMedium
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = weapon.displayName,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) TacticalOrangeBright else Color.White,
                maxLines = 1
            )
            Text(
                text = "$magAmmo / $reserveAmmo (${weapon.caliber})",
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFC0B8B2)
            )
        }
    }
}
