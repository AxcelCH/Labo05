package com.jach.labo05.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ── Lab 11: cuerpo del POST /device-tokens/ para registrar el token FCM del dispositivo ──
@Serializable
data class DeviceTokenRequest(
    @SerialName("user_id")   val userId: String?,
    @SerialName("user_name") val userName: String?,
    @SerialName("device_id") val deviceId: String,
    @SerialName("fcm_token") val fcmToken: String
)
