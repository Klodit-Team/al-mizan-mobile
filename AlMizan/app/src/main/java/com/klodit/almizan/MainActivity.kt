package com.klodit.almizan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.klodit.almizan.navigation.NavGraph
import com.klodit.almizan.navigation.MainNavGraph
import com.klodit.almizan.ui.theme.AlMizanTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AlMizanTheme {
                val navController = rememberNavController()
                //NavGraph(navController = navController)

                MainNavGraph(navController = navController)
            }
        }
    }
}