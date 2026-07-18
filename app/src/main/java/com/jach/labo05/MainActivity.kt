package com.jach.labo05

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.messaging.FirebaseMessaging
import com.jach.labo05.ui.Navigation
import com.jach.labo05.ui.theme.Labo05Theme
import com.jach.labo05.ui.viewmodel.SessionViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val app = applicationContext as DemoDataApp
            val sessionVm: SessionViewModel = viewModel(
                factory = SessionViewModel.Factory(app.sessionManager)
            )

            // ── Lab 11: solicitud del permiso de notificaciones (Android 13+) ──
            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { /* resultado ignorado: si se rechaza, el push llega pero se descarta */ }

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                // Suscripción al topic global de difusión masiva
                FirebaseMessaging.getInstance().subscribeToTopic("all_users")
            }

            val isDarkModePref by sessionVm.isDarkMode.collectAsState()
            val darkTheme      = isDarkModePref ?: isSystemInDarkTheme()

            Labo05Theme(darkTheme = darkTheme) {
                Navigation()
            }
        }
    }
}
