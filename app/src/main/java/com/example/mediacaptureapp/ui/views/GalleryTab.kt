package com.example.mediacaptureapp.ui.views

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.mediacaptureapp.ImatgesSingleton
import java.io.File

@Composable
fun GalleyTab(){
    var selectedTabIndex by remember { mutableStateOf(0) } // Controla qué tab está seleccionado

    val tabTitles = listOf(
        "Imatges",
        "Videos"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        TabRow(selectedTabIndex = selectedTabIndex, modifier = Modifier.fillMaxWidth()) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(text = title, fontSize = 16.sp) }
                )
            }
        }

        // Aquí se cambia entre la vista de imágenes y la vista de videos
        when (selectedTabIndex) {
            0 -> {
                GalleryFotosTab()
            }
            1 -> {
                GalleryVideosTab()
            }
        }
    }
}

//FOTOS
@Composable
fun GalleryFotosTab() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if(ImatgesSingleton.imatges.isNotEmpty()){//si el singleton no esta buit mostrara les imatges
            LazyRow(
                modifier = Modifier
                    .height(600.dp)
                    .padding(16.dp)
            ) {
                items(count = ImatgesSingleton.imatges.size, key = { it.hashCode() }) { i ->
                    Image(
                        bitmap = ImatgesSingleton.imatges[i].asImageBitmap(),
                        contentDescription = "imatges guardades",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
        else{
            Text(text = "No s'ha fet cap foto")
        }
    }
}

//VIDEO

@Composable
fun GalleryVideosTab() {
    val context = LocalContext.current
    val videos = getVideoFiles(context)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (videos.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .height(600.dp)
                    .padding(8.dp)
            ) {
                items(videos) { videoFile ->
                    VideoItem(videoFile = videoFile, context = context)
                }
            }
        } else {
            Text(text = "No s'ha enregistrat cap vídeo")
        }
    }
}

@Composable
fun VideoItem(videoFile: File, context: Context) {
    val videoThumbnail = remember { getVideoThumbnail(videoFile) }
    val exoPlayer = remember { ExoPlayer.Builder(context).build() }
    val isPlayerVisible = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .width(350.dp)
            .clickable {
                isPlayerVisible.value = true
            }
            .padding(8.dp)
    ) {
        if (isPlayerVisible.value) {//mostra o el video o la miniatura, depenent si has picat o no a la imatge
            ExoPlayerView(exoPlayer = exoPlayer, videoFile = videoFile)
        } else {
            videoThumbnail?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Video Thumbnail",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun ExoPlayerView(exoPlayer: ExoPlayer, videoFile: File) {
    val context = LocalContext.current
    val uri = Uri.fromFile(videoFile)

    exoPlayer.setMediaItem(MediaItem.fromUri(uri))
    exoPlayer.prepare()
    exoPlayer.play()

    AndroidView(//una view pel composable, sino no pot
        factory = {
            PlayerView(context).apply {
                this.player = exoPlayer
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

fun getVideoThumbnail(videoFile: File): Bitmap? {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(videoFile.absolutePath)
    return retriever.getFrameAtTime(1000000)
}

private fun getVideoFiles(context: Context): List<File> {
    val directory = context.filesDir
    return directory.listFiles { _, name -> name.endsWith(".mp4") }?.toList() ?: emptyList() //les que acaben en .mp4
}


