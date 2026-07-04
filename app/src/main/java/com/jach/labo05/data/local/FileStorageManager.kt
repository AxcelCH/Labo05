package com.jach.labo05.data.local

import android.content.Context
import java.io.File

class FileStorageManager(private val context: Context) {

    private val photosDir  = File(context.filesDir, "photos").apply { mkdirs() }
    private val videosDir  = File(context.filesDir, "videos").apply { mkdirs() }
    private val audiosDir  = File(context.filesDir, "audios").apply { mkdirs() }
    private val exportsDir = File(context.filesDir, "exports").apply { mkdirs() }

    fun newPhotoFile(): File =
        File(photosDir, "photo_${System.currentTimeMillis()}.jpg")

    fun newVideoFile(): File =
        File(videosDir, "video_${System.currentTimeMillis()}.mp4")

    fun newAudioFile(extension: String = "m4a"): File =
        File(audiosDir, "audio_${System.currentTimeMillis()}.$extension")

    // Ejercicio 4: archivo destino para exportar el historial como CSV
    fun newExportFile(): File =
        File(exportsDir, "export_${System.currentTimeMillis()}.csv")

    fun deleteFile(path: String): Boolean =
        File(path).takeIf { it.exists() }?.delete() ?: false

    fun fileSize(path: String): Long = File(path).length()
}
