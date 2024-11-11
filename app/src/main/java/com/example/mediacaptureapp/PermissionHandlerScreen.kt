package com.example.mediacaptureapp

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.mediacaptureapp.ui.views.CameraTabs

@Composable
fun PermissionHandlerScreen() {
    //el current, remember i tot aixo necessita estar dins duna funcio composable
    val context = LocalContext.current
    var hasAllPermissions by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        )
    }

    val requestPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
        {permisos ->
            hasAllPermissions = permisos.all { it.value }

        }

    LaunchedEffect(key1 = true) {
        if (!hasAllPermissions) {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        }
    }

    if (hasAllPermissions) {
        CameraTabs()
    } else {
        Box(modifier = Modifier.fillMaxSize()
                                 .clickable {
                                     requestPermissionLauncher.launch(
                                         arrayOf(
                                             Manifest.permission.CAMERA,
                                             Manifest.permission.RECORD_AUDIO,
                                             Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                             Manifest.permission.READ_EXTERNAL_STORAGE
                                         )
                                     )
                                 },
            contentAlignment  = Alignment.Center){

            Column(horizontalAlignment = Alignment.CenterHorizontally,
                     verticalArrangement = Arrangement.Center,
                     modifier = Modifier.padding(16.dp)){

                Icon(
                    imageVector  = Icons.Filled.Block,
                    contentDescription  = "",
                    modifier = Modifier.padding(16.dp)
                        .size(48.dp)
                )
                Text( text = "Acepte los permisos para continuar",
                    modifier = Modifier.padding(16.dp)
                )
            }

        }
    }
}