package com.klodit.almizan.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.klodit.almizan.ui.theme.Navy700

@Composable
fun HomeScreen(innerPadding: PaddingValues) {
    Box(
        modifier         = Modifier.fillMaxSize().padding(innerPadding),
        contentAlignment = Alignment.Center
    ) {
        Text("Home", color = Navy700, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
    }
}