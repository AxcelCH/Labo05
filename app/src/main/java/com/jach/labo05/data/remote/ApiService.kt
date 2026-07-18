package com.jach.labo05.data.remote

import com.jach.labo05.data.remote.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("{projectSlug}/auth/register")
    suspend fun register(
        @Path("projectSlug") projectSlug: String,
        @Body request: RegisterRequest
    ): Response<Unit>

    @POST("{projectSlug}/auth/login")
    suspend fun login(
        @Path("projectSlug") projectSlug: String,
        @Body request: LoginRequest
    ): Response<TokenResponse>

    @POST("{projectSlug}/auth/google")
    suspend fun loginWithGoogle(
        @Path("projectSlug") projectSlug: String,
        @Body request: GoogleLoginRequest
    ): Response<TokenResponse>

    // ── Lab 9 · Parte 1: recuperar el user_id (UUID) del usuario autenticado ──
    @GET("{projectSlug}/auth/me")
    suspend fun me(
        @Path("projectSlug") projectSlug: String,
        @Header("Authorization") token: String
    ): Response<UserMeResponse>

    @POST("{projectSlug}/auth/refresh-token")
    suspend fun refreshToken(
        @Path("projectSlug") projectSlug: String,
        @Body request: RefreshTokenRequest
    ): Response<TokenResponse>

    // ── Lab 9 · Parte 2: subir un punto GPS al backend ──
    @POST("{projectSlug}/geo-events-orm/")
    suspend fun createGeoEventORM(
        @Path("projectSlug") projectSlug: String,
        @Header("Authorization") token: String?,
        @Body request: GeoEventRequest
    ): Response<GeoEventResponse>

    // ── Lab 9 · Parte 3: consultar los puntos GPS ya subidos ──
    @GET("{projectSlug}/geo-events-orm/")
    suspend fun listGeoEventsORM(
        @Path("projectSlug") projectSlug: String,
        @Header("Authorization") token: String?,
        @Query("user_id") userId: String? = null,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): Response<List<GeoEventResponse>>
}
