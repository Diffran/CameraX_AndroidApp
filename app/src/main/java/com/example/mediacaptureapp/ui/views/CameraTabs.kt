package com.example.mediacaptureapp.ui.views

import androidx.compose.runtime.Composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun CameraTabs() {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf(
        Icons.Default.CameraAlt,
        Icons.Filled.Videocam,
        Icons.Filled.Collections
    )

    Column (modifier = Modifier.padding(vertical = 30.dp)
                                .fillMaxWidth()){
        TabRow(selectedTabIndex = selectedTabIndex, modifier = Modifier.fillMaxWidth()) {
            tabTitles.forEachIndexed { index, icon ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    icon = {
                        Icon(imageVector = icon,
                            contentDescription = "",
                            modifier = Modifier.size(30.dp)
                        ) }
                )
            }
        }
        when (selectedTabIndex) {
            0 -> CameraControllerTab()
            1 -> VideoControllerTab()
            2 -> GalleyTab()
        }
    }
}