package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.audio.SoundManager
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
import kotlin.random.Random

// 1. DAILY REWARDS MODAL
@Composable
fun DailyRewardsDialog(
    claimedDay: Int,
    onClaimReward: (day: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val rewards = listOf(
        "Day 1: 500 Coins",
        "Day 2: M416 Tiger Skin (3D)",
        "Day 3: 50 Diamonds",
        "Day 4: Elite Supply Crate",
        "Day 5: 1,500 Coins",
        "Day 6: Tricolor Parachute",
        "Day 7: Bengal Tiger Pet"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .width(520.dp)
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, AmberGold, RoundedCornerShape(14.dp)),
            color = GunmetalDark
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DAILY 7-DAY LOGIN REWARDS",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = AmberGold
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(modifier = Modifier.height(240.dp)) {
                    items(rewards.indices.toList()) { index ->
                        val day = index + 1
                        val isClaimed = day <= claimedDay
                        val isCurrent = day == claimedDay + 1

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .background(
                                    if (isCurrent) Color(0x33FFB300) else GunmetalMedium,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CardGiftcard,
                                    contentDescription = "Reward",
                                    tint = if (isClaimed) EmeraldGreen else AmberGold,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = rewards[index],
                                    color = if (isClaimed) Color(0xFF9E9E9E) else Color.White,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                            }

                            if (isClaimed) {
                                Text("CLAIMED", color = EmeraldGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            } else if (isCurrent) {
                                Button(
                                    onClick = {
                                        SoundManager.playButtonClick()
                                        onClaimReward(day)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                                    modifier = Modifier.height(32.dp),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("CLAIM", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            } else {
                                Text("LOCKED", color = Color(0xFF6E6762), fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// 2. LUCKY SPIN WHEEL MODAL
@Composable
fun LuckySpinDialog(
    userDiamonds: Int,
    onRewardWon: (reward: String, coins: Int, diamonds: Int) -> Unit,
    onDismiss: () -> Unit
) {
    var isSpinning by remember { mutableStateOf(false) }
    var rotationAngle by remember { mutableFloatStateOf(0f) }
    var wonText by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val animatedRotation by animateFloatAsState(
        targetValue = rotationAngle,
        animationSpec = tween(durationMillis = 2800, easing = FastOutSlowInEasing),
        label = "spin_anim"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .width(440.dp)
                .clip(RoundedCornerShape(14.dp))
                .border(1.5.dp, TacticalOrange, RoundedCornerShape(14.dp)),
            color = GunmetalDark
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ROYAL BATTLE LUCKY SPIN",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TacticalOrange
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Spin Wheel Visual
                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .rotate(animatedRotation)
                        .clip(CircleShape)
                        .background(
                            Brush.sweepGradient(
                                listOf(
                                    TacticalOrange,
                                    AmberGold,
                                    CyberCyan,
                                    EmeraldGreen,
                                    SaffronIndia,
                                    TacticalOrange
                                )
                            )
                        )
                        .border(4.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(DarkBlack)
                            .border(2.dp, TacticalOrangeBright, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("SPIN", fontWeight = FontWeight.Black, fontSize = 11.sp, color = AmberGold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                wonText?.let {
                    Text(
                        text = "CONGRATULATIONS: $it",
                        fontWeight = FontWeight.Bold,
                        color = AmberGold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(
                    onClick = {
                        if (!isSpinning) {
                            isSpinning = true
                            wonText = null
                            SoundManager.playButtonClick()
                            val addDegrees = 1440f + Random.nextInt(360)
                            rotationAngle += addDegrees
                            coroutineScope.launch {
                                delay(2800)
                                isSpinning = false
                                val rewardsList = listOf(
                                    "1,000 Battle Coins",
                                    "AWM Golden Tiger Skin",
                                    "50 Diamonds Voucher",
                                    "Bengal Roar Emote",
                                    "Tactical Armor Crate"
                                )
                                val prize = rewardsList.random()
                                wonText = prize
                                onRewardWon(prize, 1000, 20)
                                SoundManager.playVictoryFanfare()
                            }
                        }
                    },
                    enabled = !isSpinning,
                    colors = ButtonDefaults.buttonColors(containerColor = TacticalOrange),
                    modifier = Modifier.fillMaxWidth().height(42.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(if (isSpinning) "SPINNING WHEEL..." else "SPIN (FREE 1st DAILY)", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// 3. SETTINGS MODAL
@Composable
fun SettingsDialog(
    currentGraphics: String,
    onGraphicsChange: (String) -> Unit,
    sfxVolume: Float,
    onSfxVolumeChange: (Float) -> Unit,
    gyroscopeEnabled: Boolean,
    onGyroscopeChange: (Boolean) -> Unit,
    isHindi: Boolean,
    onLanguageChange: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .width(520.dp)
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, GunmetalBorder, RoundedCornerShape(14.dp)),
            color = GunmetalDark
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = TacticalOrange)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHindi) "गेम सेटिंग्स (SETTINGS)" else "GAME SETTINGS",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Graphics Option
                Text(
                    text = if (isHindi) "ग्राफिक्स प्रोफाइल (60 FPS सक्षम)" else "GRAPHICS QUALITY (60 FPS TARGET)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TacticalOrangeBright
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Low", "Medium", "High (60 FPS)").forEach { level ->
                        val selected = currentGraphics == level
                        Button(
                            onClick = {
                                SoundManager.playButtonClick()
                                onGraphicsChange(level)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selected) TacticalOrange else GunmetalMedium
                            ),
                            modifier = Modifier.weight(1f).height(36.dp),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = level,
                                fontSize = 11.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                color = if (selected) Color.Black else Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Audio Slider
                Text(
                    text = if (isHindi) "ध्वनि प्रभाव वॉल्यूम (SFX)" else "SFX AUDIO VOLUME",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TacticalOrangeBright
                )
                Slider(
                    value = sfxVolume,
                    onValueChange = {
                        onSfxVolumeChange(it)
                        SoundManager.sfxVolume = it
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = TacticalOrange,
                        activeTrackColor = TacticalOrange
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Gyroscope & Language Toggles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Gyroscope Aiming Assist", fontSize = 13.sp, color = Color.White)
                    Switch(
                        checked = gyroscopeEnabled,
                        onCheckedChange = {
                            SoundManager.playButtonClick()
                            onGyroscopeChange(it)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = TacticalOrange,
                            checkedTrackColor = TacticalOrange.copy(alpha = 0.5f)
                        )
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Language: हिन्दी (Hindi)", fontSize = 13.sp, color = Color.White)
                    Switch(
                        checked = isHindi,
                        onCheckedChange = {
                            SoundManager.playButtonClick()
                            onLanguageChange(it)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = SaffronIndia,
                            checkedTrackColor = SaffronIndia.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        }
    }
}

// 4. GLOBAL CHAT DRAWER MODAL
@Composable
fun GlobalChatDialog(
    userName: String,
    onDismiss: () -> Unit
) {
    val messages = remember {
        mutableStateListOf(
            "System: Welcome to Battle India Official Beta Shard 1!",
            "Vikram_Sniper: Looking for Duo squad in Pokhran military zone",
            "Aarav_Tactical: Who wants to push rank to Heroic today?",
            "Commander_Rohan: Need 1 player for Squad tournaments"
        )
    }
    var currentText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .width(480.dp)
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, TacticalOrange, RoundedCornerShape(14.dp)),
            color = GunmetalDark
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ALL INDIA GLOBAL CHAT",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TacticalOrange
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(DarkBlack, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    items(messages) { msg ->
                        Text(
                            text = msg,
                            fontSize = 12.sp,
                            color = if (msg.startsWith("System:")) AmberGold else Color(0xFFECE5E0),
                            modifier = Modifier.padding(vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = currentText,
                        onValueChange = { currentText = it },
                        placeholder = { Text("Type message...") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TacticalOrange,
                            unfocusedBorderColor = Color(0xFF423C38)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (currentText.isNotBlank()) {
                                messages.add("$userName: $currentText")
                                currentText = ""
                                SoundManager.playButtonClick()
                            }
                        },
                        modifier = Modifier
                            .background(TacticalOrange, RoundedCornerShape(8.dp))
                            .size(48.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.Black)
                    }
                }
            }
        }
    }
}

// 5. PROFILE & LEADERBOARD MODAL
@Composable
fun ProfileAndLeaderboardDialog(
    userName: String,
    uid: String,
    rank: String,
    kills: Int,
    wins: Int,
    matches: Int,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .width(540.dp)
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, TacticalOrange, RoundedCornerShape(14.dp)),
            color = GunmetalDark
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        modifier = Modifier.width(300.dp),
                        containerColor = Color.Transparent,
                        contentColor = TacticalOrange,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = TacticalOrange
                            )
                        }
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("PROFILE", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("LEADERBOARDS", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (selectedTab == 0) {
                    // Profile Tab
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(GunmetalMedium, RoundedCornerShape(8.dp))
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(TacticalOrange),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = "Avatar", tint = Color.Black, modifier = Modifier.size(32.dp))
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(userName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                            Text("UID: $uid", fontSize = 11.sp, color = Color(0xFFA09994))
                            Text("Tier: $rank", fontSize = 12.sp, color = AmberGold, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatCard("K/D Ratio", "3.42", Modifier.weight(1f))
                        StatCard("Matches", "$matches", Modifier.weight(1f))
                        StatCard("Wins", "$wins", Modifier.weight(1f))
                        StatCard("Total Kills", "$kills", Modifier.weight(1f))
                    }
                } else {
                    // Leaderboards Tab
                    Text(
                        text = "TOP INDIA TIER PLAYERS (AUTHENTIC RANKING)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberGold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val topPlayers = listOf(
                        Triple("#1", "Arjun_DesiSniper", "7,450 RP (Legend)"),
                        Triple("#2", "Vikram_Rajput", "7,120 RP (Legend)"),
                        Triple("#3", "Rohan_Thunder", "6,880 RP (Heroic)"),
                        Triple("#4", "Aarav_Ghost99", "6,640 RP (Heroic)"),
                        Triple("#5", "Dev_TigerForce", "6,390 RP (Heroic)")
                    )
                    topPlayers.forEach { (pos, name, rp) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .background(GunmetalMedium, RoundedCornerShape(6.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row {
                                Text(pos, fontWeight = FontWeight.Black, color = if (pos == "#1") AmberGold else Color.White, fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(name, color = Color.White, fontSize = 13.sp)
                            }
                            Text(rp, color = CyberCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(GunmetalMedium, RoundedCornerShape(8.dp))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, fontSize = 11.sp, color = Color(0xFFA09994))
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, fontSize = 15.sp, fontWeight = FontWeight.Black, color = TacticalOrangeBright)
    }
}
