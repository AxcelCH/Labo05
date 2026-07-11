package com.jach.labo05.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenRequest(
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("device_id")     val deviceId: String
)
