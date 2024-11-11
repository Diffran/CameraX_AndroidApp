package com.example.mediacaptureapp

import android.graphics.Bitmap

object ImatgesSingleton {//object es una instancia unica, vamos un Singleton
    val imatges = mutableListOf<Bitmap>()//Bitmap es la class que representa Imatges a Android
}