package com.jach.labo05

import android.app.Application
import com.jach.labo05.data.local.FileStorageManager
import com.jach.labo05.data.local.DemoDataDatabase
import com.jach.labo05.data.repository.AudioRepository
import com.jach.labo05.data.repository.GpsRepository
import com.jach.labo05.data.repository.MediaRepository
import com.jach.labo05.data.session.SessionManager

class DemoDataApp : Application() {

    val database     by lazy { DemoDataDatabase.getInstance(this) }
    val fileStorage  by lazy { FileStorageManager(this) }
    val sessionManager by lazy { SessionManager(this) }

    val gpsRepository by lazy {
        GpsRepository(database.gpsGoogleDao(), database.gpsSensorsDao())
    }
    val mediaRepository by lazy {
        MediaRepository(database.mediaDao(), fileStorage)
    }
    val audioRepository by lazy {
        AudioRepository(database.audioDao(), fileStorage)
    }
}
