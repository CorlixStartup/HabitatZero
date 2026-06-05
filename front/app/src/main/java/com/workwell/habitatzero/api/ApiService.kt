package com.workwell.habitatzero.api

import com.workwell.habitatzero.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body loginDTO: LoginDTO): Response<TokenDTO>

    @GET("estufas")
    suspend fun listarEstufas(): Response<List<Estufa>>

    @GET("estufas/{id}")
    suspend fun buscarEstufa(@Path("id") id: Long): Response<Estufa>

    @PUT("estufas/{id}")
    suspend fun atualizarEstufa(
        @Path("id") id: Long,
        @Body estufa: EstufaRequest
    ): Response<Estufa>

    @GET("sensores/leituras")
    suspend fun getSensores(@Query("estufaId") estufaId: Long? = null): Response<List<SensorLeituraResponse>>

    @GET("alertas")
    suspend fun listarAlertas(): Response<List<Alerta>>

    @GET("alertas/estufa/{estufaId}")
    suspend fun listarAlertasPorEstufa(@Path("estufaId") estufaId: Long): Response<List<Alerta>>

    @PATCH("alertas/{id}/resolver")
    suspend fun resolverAlerta(@Path("id") id: Long): Response<Alerta>
}
