package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.engine.BattleIndiaEngine
import com.example.ui.screens.BattleMatchScreen
import com.example.ui.screens.LobbyScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MatchmakingScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.DarkBlack
import com.example.ui.theme.MyApplicationTheme

enum class AppState {
    SPLASH,
    LOGIN,
    LOBBY,
    MATCHMAKING,
    IN_MATCH
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBlack
                ) {
                    BattleIndiaApp()
                }
            }
        }
    }
}

@Composable
fun BattleIndiaApp() {
    var appState by remember { mutableStateOf(AppState.SPLASH) }
    var userName by remember { mutableStateOf("Commander_Arjun") }
    var userUid by remember { mutableStateOf("BI-IND-8849201") }
    var isGuestUser by remember { mutableStateOf(false) }
    var currentMatchMode by remember { mutableStateOf("SQUAD") }

    val engine = remember { BattleIndiaEngine() }

    when (appState) {
        AppState.SPLASH -> {
            SplashScreen(
                onLoadingComplete = {
                    appState = AppState.LOGIN
                }
            )
        }
        AppState.LOGIN -> {
            LoginScreen(
                onLoginSuccess = { name, uid, isGuest ->
                    userName = name
                    userUid = uid
                    isGuestUser = isGuest
                    appState = AppState.LOBBY
                }
            )
        }
        AppState.LOBBY -> {
            LobbyScreen(
                userName = userName,
                uid = userUid,
                isGuest = isGuestUser,
                onStartMatchmaking = { mode ->
                    currentMatchMode = mode
                    appState = AppState.MATCHMAKING
                },
                onLogout = {
                    appState = AppState.LOGIN
                }
            )
        }
        AppState.MATCHMAKING -> {
            MatchmakingScreen(
                mode = currentMatchMode,
                onMatchReady = {
                    engine.resetMatch()
                    appState = AppState.IN_MATCH
                },
                onCancel = {
                    appState = AppState.LOBBY
                }
            )
        }
        AppState.IN_MATCH -> {
            BattleMatchScreen(
                engine = engine,
                onExitToLobby = {
                    appState = AppState.LOBBY
                }
            )
        }
    }
}
