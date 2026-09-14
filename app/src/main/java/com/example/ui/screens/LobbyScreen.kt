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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundManager
import com.example.model.SquadMember
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkBlack
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GunmetalBorder
import com.example.ui.theme.GunmetalDark
import com.example.ui.theme.GunmetalMedium
import com.example.ui.theme.SaffronIndia
import com.example.ui.theme.TacticalOrange
import com.example.ui.theme.TacticalOrangeBright
import com.example.ui.theme.TacticalOrangeDark
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun LobbyScreen(
    userName: String,
    uid: String,
    isGuest: Boolean,
    onStartMatchmaking: (mode: String) -> Unit,
    onLogout: () -> Unit
) {
    var coins by remember { mutableIntStateOf(12800) }
    var diamonds by remember { mutableIntStateOf(420) }
    var userRank by remember { mutableStateOf("Platinum III") }
    var selectedMode by remember { mutableStateOf("SQUAD") }

    // Character customization
    var isMaleCharacter by remember { mutableStateOf(true) }
    var selectedSkinIndex by remember { mutableIntStateOf(0) }
    val skins = listOf("Tricolor Commando", "Royal Rajput", "Cyber Tiger", "Desert Ghost")

    // Squad
    val squadMembers = remember {
        mutableStateListOf(
            SquadMember(uid, userName, userRank, isReady = true, isLeader = true, micOn = true),
            SquadMember("BI-901", "Vikram_Sniper", "Diamond II", isReady = true, isLeader = false, micOn = false),
            SquadMember("BI-342", "Aarav_Tactical", "Platinum I", isReady = false, isLeader = false, micOn = true)
        )
    }

    // Dialog state
    var showDailyRewards by remember { mutableStateOf(false) }
    var showLuckySpin by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var showGlobalChat by remember { mutableStateOf(false) }
    var showProfileLeaderboard by remember { mutableStateOf(false) }
    var claimedDailyDay by remember { mutableIntStateOf(2) }

    // Settings state
    var graphicsQuality by remember { mutableStateOf("High (60 FPS)") }
    var sfxVolume by remember { mutableFloatStateOf(0.85f) }
    var gyroscopeEnabled by remember { mutableStateOf(true) }
    var isHindi by remember { mutableStateOf(false) }

    // Idle camera breathing animation
    val infiniteTransition = rememberInfiniteTransition(label = "lobby_idle")
    val idleBreathing by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "idle_breathing"
    )
    val backgroundLightRotate by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(16000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bg_light"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlack)
            .testTag("main_lobby_screen")
    ) {
        // Living 3D Character Canvas & Dynamic Lighting Background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width * 0.52f
            val cy = size.height * 0.52f + idleBreathing

            // Dynamic orange radial floodlight
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        TacticalOrange.copy(alpha = 0.22f),
                        Color(0xFF1F0D04).copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    center = Offset(cx, cy - 80f),
                    radius = size.height * 0.65f
                ),
                radius = size.height * 0.65f,
                center = Offset(cx, cy - 80f)
            )

            // Metallic Tactical Grid Floor Perspective
            val floorY = size.height * 0.72f
            drawLine(GunmetalBorder, Offset(0f, floorY), Offset(size.width, floorY), 2f)
            for (line in -6..6) {
                val startX = cx + line * 90f
                val endX = cx + line * 210f
                drawLine(
                    TacticalOrange.copy(alpha = 0.15f),
                    Offset(startX, floorY),
                    Offset(endX, size.height),
                    1.5f
                )
            }

            // Stylized 3D Character Rendering (Tactical Commando)
            // Head / Helmet
            val headCenter = Offset(cx, cy - 140f)
            drawCircle(Color(0xFF332A24), radius = 26f, center = headCenter)
            // Beret / Helmet crest
            val skinColor = when (selectedSkinIndex) {
                0 -> TacticalOrangeBright
                1 -> AmberGold
                2 -> CyberCyan
                else -> Color(0xFFE0E0E0)
            }
            drawArc(
                color = skinColor,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(headCenter.x - 28f, headCenter.y - 28f),
                size = androidx.compose.ui.geometry.Size(56f, 32f)
            )

            // Torso / Tactical Plate Carrier
            val torsoPath = Path().apply {
                moveTo(cx - 36f, cy - 110f)
                lineTo(cx + 36f, cy - 110f)
                lineTo(cx + 28f, cy - 10f)
                lineTo(cx - 28f, cy - 10f)
                close()
            }
            drawPath(torsoPath, Color(0xFF211D1A))
            drawPath(torsoPath, skinColor.copy(alpha = 0.6f), style = Stroke(width = 3f))

            // Indian Tricolor Commando Badge on Chest
            drawLine(SaffronIndia, Offset(cx - 14f, cy - 70f), Offset(cx + 14f, cy - 70f), 3f)
            drawLine(Color.White, Offset(cx - 14f, cy - 66f), Offset(cx + 14f, cy - 66f), 3f)
            drawLine(Color(0xFF138808), Offset(cx - 14f, cy - 62f), Offset(cx + 14f, cy - 62f), 3f)

            // Slung M416 / AKM Assault Rifle
            drawLine(
                color = Color(0xFF111111),
                start = Offset(cx - 42f, cy - 90f),
                end = Offset(cx + 52f, cy + 10f),
                strokeWidth = 10f,
                cap = StrokeCap.Round
            )
            // Gun barrel & magazine
            drawLine(
                color = TacticalOrange,
                start = Offset(cx + 40f, cy),
                end = Offset(cx + 55f, cy + 12f),
                strokeWidth = 5f
            )

            // Legs
            drawLine(Color(0xFF1E1B19), Offset(cx - 18f, cy - 10f), Offset(cx - 24f, cy + 105f), 14f, StrokeCap.Round)
            drawLine(Color(0xFF1E1B19), Offset(cx + 18f, cy - 10f), Offset(cx + 24f, cy + 105f), 14f, StrokeCap.Round)

            // Bengal Tiger Companion Pet at side
            val petX = cx + 85f
            val petY = cy + 60f
            drawCircle(Color(0xFFD87D20), radius = 20f, center = Offset(petX, petY))
            // Tiger stripes
            drawLine(Color.Black, Offset(petX - 8f, petY - 10f), Offset(petX - 4f, petY + 2f), 2f)
            drawLine(Color.Black, Offset(petX, petY - 14f), Offset(petX, petY + 4f), 2f)
            drawLine(Color.Black, Offset(petX + 8f, petY - 10f), Offset(petX + 4f, petY + 2f), 2f)
            // Glowing cyan tactical pet collar
            drawCircle(CyberCyan, radius = 4f, center = Offset(petX - 4f, petY + 12f))
        }

        // TOP BAR: Player profile, currencies, settings
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Card (Clickable)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(GunmetalDark.copy(alpha = 0.9f))
                    .border(1.dp, TacticalOrange.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .clickable {
                        SoundManager.playButtonClick()
                        showProfileLeaderboard = true
                    }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(TacticalOrange),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color.Black, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(userName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                        if (isGuest) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("(GUEST)", fontSize = 10.sp, color = AmberGold, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text("UID: $uid • $userRank", fontSize = 10.sp, color = Color(0xFFA09994))
                }
            }

            // Currencies & System Icons
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Battle Coins
                Row(
                    modifier = Modifier
                        .background(GunmetalDark, RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🪙", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("$coins", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = AmberGold)
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Diamonds
                Row(
                    modifier = Modifier
                        .background(GunmetalDark, RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("💎", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("$diamonds", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CyberCyan)
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Settings Button
                IconButton(
                    onClick = {
                        SoundManager.playButtonClick()
                        showSettings = true
                    },
                    modifier = Modifier
                        .background(GunmetalDark, CircleShape)
                        .size(36.dp)
                ) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }

        // LEFT-SIDE TACTICAL ACTION RIBBONS
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            LobbyRibbonButton(
                icon = Icons.Default.CardGiftcard,
                label = "DAILY REWARDS",
                badge = "CLAIM",
                color = AmberGold,
                onClick = {
                    SoundManager.playButtonClick()
                    showDailyRewards = true
                }
            )

            LobbyRibbonButton(
                icon = Icons.Default.Casino,
                label = "LUCKY SPIN",
                badge = "FREE",
                color = TacticalOrangeBright,
                onClick = {
                    SoundManager.playButtonClick()
                    showLuckySpin = true
                }
            )

            LobbyRibbonButton(
                icon = Icons.Default.Chat,
                label = "GLOBAL CHAT",
                badge = null,
                color = CyberCyan,
                onClick = {
                    SoundManager.playButtonClick()
                    showGlobalChat = true
                }
            )

            LobbyRibbonButton(
                icon = Icons.Default.Leaderboard,
                label = "LEADERBOARD",
                badge = null,
                color = Color.White,
                onClick = {
                    SoundManager.playButtonClick()
                    showProfileLeaderboard = true
                }
            )
        }

        // RIGHT-SIDE CUSTOMIZATION COLUMN (Characters & Skins)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 18.dp)
                .width(160.dp)
                .background(GunmetalDark.copy(alpha = 0.85f), RoundedCornerShape(10.dp))
                .border(1.dp, GunmetalBorder, RoundedCornerShape(10.dp))
                .padding(12.dp)
        ) {
            Text(
                text = "TACTICAL VAULT",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = TacticalOrange
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Gender Switch
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = {
                        SoundManager.playButtonClick()
                        isMaleCharacter = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isMaleCharacter) TacticalOrange else GunmetalMedium
                    ),
                    modifier = Modifier.weight(1f).height(30.dp),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("MALE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isMaleCharacter) Color.Black else Color.White)
                }
                Button(
                    onClick = {
                        SoundManager.playButtonClick()
                        isMaleCharacter = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isMaleCharacter) TacticalOrange else GunmetalMedium
                    ),
                    modifier = Modifier.weight(1f).height(30.dp),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("FEMALE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (!isMaleCharacter) Color.Black else Color.White)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text("OUTFIT SKIN:", fontSize = 10.sp, color = Color(0xFFA09994))
            Spacer(modifier = Modifier.height(4.dp))

            skins.forEachIndexed { index, skinName ->
                val isSelected = selectedSkinIndex == index
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                        .background(if (isSelected) Color(0x33FF6D00) else GunmetalMedium, RoundedCornerShape(4.dp))
                        .clickable {
                            SoundManager.playButtonClick()
                            selectedSkinIndex = index
                        }
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(if (isSelected) TacticalOrange else Color(0xFF6E6762), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = skinName,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else Color(0xFFC0B8B2)
                    )
                }
            }
        }

        // BOTTOM DOCK: Squad members & Start Match CTA
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, DarkBlack.copy(alpha = 0.95f))
                    )
                )
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            // SQUAD SLOTS (Max 4 players)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (i in 0..3) {
                    val member = squadMembers.getOrNull(i)
                    if (member != null) {
                        // Active Squad Member Slot
                        Surface(
                            modifier = Modifier
                                .width(90.dp)
                                .height(56.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .border(1.dp, if (member.isReady) EmeraldGreen else TacticalOrange, RoundedCornerShape(6.dp)),
                            color = GunmetalDark
                        ) {
                            Column(
                                modifier = Modifier.padding(6.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = member.name.take(7),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Icon(
                                        imageVector = if (member.micOn) Icons.Default.Mic else Icons.Default.MicOff,
                                        contentDescription = "Mic",
                                        tint = if (member.micOn) CyberCyan else Color.Gray,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                                Text(
                                    text = if (member.isReady) "READY" else "WAITING",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (member.isReady) EmeraldGreen else Color(0xFFFFB300)
                                )
                            }
                        }
                    } else {
                        // Empty Slot / Invite
                        Surface(
                            modifier = Modifier
                                .width(90.dp)
                                .height(56.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .border(1.dp, GunmetalBorder, RoundedCornerShape(6.dp))
                                .clickable {
                                    SoundManager.playButtonClick()
                                    // Add simulated invited teammate
                                    squadMembers.add(SquadMember("BI-881", "Kabir_Hunter", "Gold I", true, false, false))
                                },
                            color = GunmetalDark.copy(alpha = 0.5f)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Invite", tint = Color.Gray, modifier = Modifier.size(18.dp))
                                Text("INVITE", fontSize = 9.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }

            // MODE SELECTOR & START MATCH BUTTON
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Mode Buttons
                Row(
                    modifier = Modifier
                        .background(GunmetalDark, RoundedCornerShape(6.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("SOLO", "DUO", "SQUAD").forEach { mode ->
                        val isModeSelected = selectedMode == mode
                        Button(
                            onClick = {
                                SoundManager.playButtonClick()
                                selectedMode = mode
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isModeSelected) TacticalOrange else Color.Transparent
                            ),
                            modifier = Modifier.height(38.dp),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = mode,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isModeSelected) Color.Black else Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Big High-Voltage "START MATCH" Button
                Button(
                    onClick = {
                        SoundManager.playButtonClick()
                        onStartMatchmaking(selectedMode)
                    },
                    modifier = Modifier
                        .height(52.dp)
                        .width(190.dp)
                        .testTag("start_match_button"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TacticalOrange)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start",
                            tint = Color.Black,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "START MATCH",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                color = Color.Black
                            )
                            Text(
                                text = "BATTLE INDIA MAP",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF261204)
                            )
                        }
                    }
                }
            }
        }

        // Overlay Dialogs
        if (showDailyRewards) {
            DailyRewardsDialog(
                claimedDay = claimedDailyDay,
                onClaimReward = { day ->
                    claimedDailyDay = day
                    coins += 500
                    diamonds += 25
                },
                onDismiss = { showDailyRewards = false }
            )
        }

        if (showLuckySpin) {
            LuckySpinDialog(
                userDiamonds = diamonds,
                onRewardWon = { reward, addCoins, addDiamonds ->
                    coins += addCoins
                    diamonds += addDiamonds
                },
                onDismiss = { showLuckySpin = false }
            )
        }

        if (showSettings) {
            SettingsDialog(
                currentGraphics = graphicsQuality,
                onGraphicsChange = { graphicsQuality = it },
                sfxVolume = sfxVolume,
                onSfxVolumeChange = { sfxVolume = it },
                gyroscopeEnabled = gyroscopeEnabled,
                onGyroscopeChange = { gyroscopeEnabled = it },
                isHindi = isHindi,
                onLanguageChange = { isHindi = it },
                onDismiss = { showSettings = false }
            )
        }

        if (showGlobalChat) {
            GlobalChatDialog(userName = userName, onDismiss = { showGlobalChat = false })
        }

        if (showProfileLeaderboard) {
            ProfileAndLeaderboardDialog(
                userName = userName,
                uid = uid,
                rank = userRank,
                kills = 142,
                wins = 19,
                matches = 48,
                onDismiss = { showProfileLeaderboard = false }
            )
        }
    }
}

@Composable
private fun LobbyRibbonButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    badge: String?,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(GunmetalDark.copy(alpha = 0.9f))
            .border(1.dp, GunmetalBorder, RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = color, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
        badge?.let {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .background(color, RoundedCornerShape(3.dp))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(text = it, fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.Black)
            }
        }
    }
}
