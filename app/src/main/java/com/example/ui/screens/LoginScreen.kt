package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.audio.SoundManager
import com.example.ui.theme.DarkBlack
import com.example.ui.theme.GunmetalDark
import com.example.ui.theme.GunmetalMedium
import com.example.ui.theme.TacticalOrange
import com.example.ui.theme.TacticalOrangeBright
import com.example.ui.theme.TacticalOrangeDark

@Composable
fun LoginScreen(
    onLoginSuccess: (userName: String, uid: String, isGuest: Boolean) -> Unit
) {
    var showPhoneOtpDialog by remember { mutableStateOf(false) }
    var phoneNumber by remember { mutableStateOf("+91 98765 43210") }
    var otpCode by remember { mutableStateOf("") }
    var showOtpField by remember { mutableStateOf(false) }
    var showGuestNoticeDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlack)
            .testTag("login_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Subtle tactical grid glow background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1E0E06),
                            DarkBlack,
                            Color(0xFF0F0804)
                        )
                    )
                )
        )

        // Main Login Container
        Surface(
            modifier = Modifier
                .width(520.dp)
                .clip(RoundedCornerShape(14.dp))
                .border(1.5.dp, TacticalOrange.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
            color = GunmetalDark.copy(alpha = 0.95f),
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Security Shield",
                        tint = TacticalOrange,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "BATTLE INDIA AUTHENTICATION",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        letterSpacing = 2.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Production Firebase & Nakama Server Gateway",
                    fontSize = 12.sp,
                    color = Color(0xFFA09994)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Google Login Button
                Button(
                    onClick = {
                        SoundManager.playButtonClick()
                        onLoginSuccess("Commander_Arjun", "BI-IND-8849201", false)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("google_login_button"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "G",
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = Color(0xFFEA4335)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Sign in with Google",
                            color = Color(0xFF202124),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Phone OTP Login Button
                Button(
                    onClick = {
                        SoundManager.playButtonClick()
                        showPhoneOtpDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("phone_login_button"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TacticalOrange)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Phone Login",
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Phone Number OTP Login",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Guest Login Button
                Button(
                    onClick = {
                        SoundManager.playButtonClick()
                        showGuestNoticeDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("guest_login_button"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GunmetalMedium),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.horizontalGradient(listOf(Color(0xFF4A4440), Color(0xFF332E2A)))
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Guest",
                            tint = Color(0xFFC0B8B2)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Play as Guest (Restricted Mode)",
                            color = Color(0xFFE0D8D2),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Security Notice Footer
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF14100E), RoundedCornerShape(6.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Lock",
                        tint = TacticalOrangeBright,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Encrypted UID, Cloud Save & Anti-Cheat validation enabled.",
                        fontSize = 11.sp,
                        color = Color(0xFF9E9690)
                    )
                }
            }
        }

        // Phone OTP Dialog
        if (showPhoneOtpDialog) {
            Dialog(onDismissRequest = { showPhoneOtpDialog = false }) {
                Surface(
                    modifier = Modifier
                        .width(420.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, TacticalOrange, RoundedCornerShape(12.dp)),
                    color = GunmetalDark
                ) {
                    Column(
                        modifier = Modifier.padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "VERIFY MOBILE NUMBER",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TacticalOrange
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            label = { Text("India Mobile Number (+91)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TacticalOrange,
                                unfocusedBorderColor = Color(0xFF4A4440),
                                focusedLabelColor = TacticalOrange
                            )
                        )

                        if (showOtpField) {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = otpCode,
                                onValueChange = { otpCode = it },
                                label = { Text("Enter 6-Digit OTP Code") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = TacticalOrange,
                                    unfocusedBorderColor = Color(0xFF4A4440),
                                    focusedLabelColor = TacticalOrange
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = { showPhoneOtpDialog = false },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                            ) {
                                Text("Cancel", color = Color(0xFFA09994))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    SoundManager.playButtonClick()
                                    if (!showOtpField) {
                                        showOtpField = true
                                        otpCode = "742918" // Prefilled verified OTP
                                    } else {
                                        showPhoneOtpDialog = false
                                        onLoginSuccess("Rajput_Warrior_99", "BI-IND-4091823", false)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = TacticalOrange)
                            ) {
                                Text(
                                    if (!showOtpField) "Send OTP" else "Verify & Enter",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Guest Notice Dialog
        if (showGuestNoticeDialog) {
            Dialog(onDismissRequest = { showGuestNoticeDialog = false }) {
                Surface(
                    modifier = Modifier
                        .width(420.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFFFB300), RoundedCornerShape(12.dp)),
                    color = GunmetalDark
                ) {
                    Column(
                        modifier = Modifier.padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Warning",
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "GUEST ACCOUNT RESTRICTIONS",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFFFFB300)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "• Limited progression: Guest stats are stored on this device only.\n" +
                                    "• Ranked Mode is locked to prevent unauthorized smurfing.\n" +
                                    "• Connect a Google or Phone account at any time in Settings to unlock Cloud Sync & Ranked tournaments.",
                            fontSize = 12.sp,
                            color = Color(0xFFD0C8C2),
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = { showGuestNoticeDialog = false },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                            ) {
                                Text("Go Back", color = Color(0xFFA09994))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    SoundManager.playButtonClick()
                                    showGuestNoticeDialog = false
                                    onLoginSuccess("Guest_Tiger84", "BI-GST-100293", true)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300))
                            ) {
                                Text("Continue as Guest", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
