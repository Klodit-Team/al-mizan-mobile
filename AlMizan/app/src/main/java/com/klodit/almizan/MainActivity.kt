package com.klodit.almizan

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.klodit.almizan.navigation.MainNavGraph
import com.klodit.almizan.navigation.NavGraph
import com.klodit.almizan.ui.dashboard.DashboardScreen
import com.klodit.almizan.ui.theme.AlMizanTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AlMizanTheme {
              /*  var isAuthenticated by remember { mutableStateOf(false) }

                if (isAuthenticated) {
                    MainNavGraph()
                } else {
                    NavGraph(onAuthSuccess = { isAuthenticated = true })
                } */

                DashboardScreen()
            }
        }
    }
}