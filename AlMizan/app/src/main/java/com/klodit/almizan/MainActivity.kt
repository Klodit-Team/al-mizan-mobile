package com.klodit.almizan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.klodit.almizan.navigation.NavGraph
import com.klodit.almizan.ui.auth.LoginScreen
import com.klodit.almizan.ui.auth.VerificationScreen
import com.klodit.almizan.ui.auth.RegistrationStep1Screen

import com.klodit.almizan.ui.theme.AlMizanTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AlMizanTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}