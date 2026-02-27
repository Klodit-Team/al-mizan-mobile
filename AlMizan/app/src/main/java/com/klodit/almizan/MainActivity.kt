package com.klodit.almizan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.klodit.almizan.ui.auth.LoginScreen
import com.klodit.almizan.ui.theme.AlMizanTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AlMizanTheme {
                LoginScreen()   // ← just show the login screen for now
            }
        }
    }
}