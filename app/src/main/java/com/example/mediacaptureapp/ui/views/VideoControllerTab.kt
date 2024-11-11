package com.example.mediacaptureapp.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.provider.MediaStore
import android.widget.Toast
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Recording
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.camera.view.video.AudioConfig
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.io.File



private var recording: Recording? = null
val videoThumbnail = mutableStateOf<Bitmap?>(null)


@Composable
fun VideoControllerTab() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(
                CameraController.VIDEO_CAPTURE
            )
        }
    }
    val isRecording = remember { mutableStateOf(false)}//controla l'estat de la grabació


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
                if (isRecording.value) {
                    isRecording.value = false
                } else {
                    isRecording.value = true
                }
                startRecording(controller,context)//crida al metode que graba
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
                imageVector =  if (isRecording.value) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                contentDescription = "Take video",
                modifier = Modifier.size(40.dp)
            )
        }

        // Mostrar la miniatura
        videoThumbnail.value?.let { thumbnail ->
            Image(
                bitmap = thumbnail.asImageBitmap(),//aixo ho converteix a imatge, sino no ho pot mostrar el compose
                contentDescription = "Video Thumbnail",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .size(80.dp)
            )
        }
    }
}

@SuppressLint("MissingPermission")
private fun startRecording(
    controller: LifecycleCameraController,
    context: Context
){
    if(recording != null){//si esta graban i piques para la grabacio i la converteix al tipus d'output que hagis configurat
        recording?.stop()
        recording = null
        return
    }

    val outputFile = File(context.filesDir, "el_meu_video_${System.currentTimeMillis()}.mp4")
    recording = controller.startRecording(
        FileOutputOptions.Builder(outputFile).build(),//el tipus d'output, en el meu cas el File
        AudioConfig.create(true), //perque pilli l'audio, se li ha de treure el permission
        ContextCompat.getMainExecutor(context),
    ){
        event ->
        when(event){
            is VideoRecordEvent.Finalize -> {
                if(event.hasError()) {
                    recording?.close()
                    recording = null

                    Toast.makeText(//es com la snackbar pero no necessita anar en un @Composable
                        context,
                        "Error al grabar el video",
                        Toast.LENGTH_LONG
                    ).show()
                }else{
                    //thumbnail
                    val thumbnail = getVideoThumbnail(outputFile.absolutePath)//metode privat per veure la thumbnail
                    videoThumbnail.value = thumbnail

                    Toast.makeText(
                        context,
                        "Video guardat",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}

private fun getVideoThumbnail(filePath: String): Bitmap? {//per veure la thumbnail
    return ThumbnailUtils.createVideoThumbnail(
        filePath,
        MediaStore.Images.Thumbnails.MINI_KIND
    )
}



