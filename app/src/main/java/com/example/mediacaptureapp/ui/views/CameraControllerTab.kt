package com.example.mediacaptureapp.ui.views

import android.content.Context
import android.graphics.Bitmap
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import android.graphics.Matrix
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.mediacaptureapp.ImatgesSingleton


@Composable
fun CameraControllerTab() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE
            )
        }
    }
    val lastImage = remember { mutableStateOf<ImageBitmap?>(null) }//ha de recordar el state per modifiacarse quan toca

    //el screen de la camera
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView( //es la vista previa que es pasa a AndroidView per poder treballar en compose
            factory = {
                PreviewView(it).apply {
                    this.controller = controller
                    controller.bindToLifecycle(lifecycleOwner)
                }
            },
            modifier = Modifier.fillMaxSize().padding(16.dp)
        )

        // Botó centrat al mig
        FloatingActionButton(
            onClick = {
                takePhoto(
                    controller = controller,
                    context = context,
                    onPhotoTaken = { image ->
                        ImatgesSingleton.imatges.add(image) // la guarda a la galeria
                        lastImage.value = image.asImageBitmap()//la guarda a la variable que s'acutalitza
                    }
                )
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .size(60.dp),
            containerColor = Color.White,
            contentColor = Color.Black,
            shape = CircleShape

        ) {
            Icon(
                imageVector = Icons.Filled.Camera,
                contentDescription = "Take picture",
                modifier = Modifier.size(40.dp)
            )
        }

        lastImage.value?.let { image ->
            Image(
                bitmap = image,
                contentDescription = "Last photo",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .size(80.dp)
            )
        }
    }
}


private fun takePhoto(
    controller: LifecycleCameraController,
    onPhotoTaken: (Bitmap) -> Unit,
    context: Context
) {
    controller.takePicture(
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)

                val matrix = Matrix().apply {
                    postRotate(image.imageInfo.rotationDegrees.toFloat())//sha de girar
                }
                val rotatedBitmap = Bitmap.createBitmap(
                    image.toBitmap(),
                    0,
                    0,
                    image.width,
                    image.height,
                    matrix,
                    true
                )
                onPhotoTaken(rotatedBitmap)

                Toast.makeText(//no li cal esta a dins d'un @Composable i fa similar a la Sncakbar
                    context,
                    "Foto guardada",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    )
}
