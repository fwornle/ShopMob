package com.tanfra.shopmob.smob.ui.planning.lists.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "Settings Screen", fontSize = 20.sp)
    }
}

@Composable
fun Screen2() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "Two Screen", fontSize = 20.sp)
    }
}

@Composable
fun Screen3() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "Three Screen", fontSize = 20.sp)
    }
}
