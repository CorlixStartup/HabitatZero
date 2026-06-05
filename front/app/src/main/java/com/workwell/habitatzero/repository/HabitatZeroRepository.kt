package com.workwell.habitatzero.repository

import com.workwell.habitatzero.App
import com.workwell.habitatzero.api.RetrofitClient
import com.workwell.habitatzero.model.Alerta
import com.workwell.habitatzero.model.Estufa
import com.workwell.habitatzero.model.EstufaRequest
import com.workwell.habitatzero.model.LoginDTO
import com.workwell.habitatzero.model.SensorAmbiente
import com.workwell.habitatzero.model.SensorLeituraResponse
import com.workwell.habitatzero.model.TokenDTO
import retrofit2.Response

class HabitatZeroRepository {

    private val api by lazy { RetrofitClient.getApi(App.instance) }

    suspend fun login(loginDTO: LoginDTO): Response<TokenDTO> {
        return api.login(loginDTO)
    }

    suspend fun listarEstufas(): Response<List<Estufa>> {
        return api.listarEstufas()
    }

    suspend fun buscarEstufa(id: Long): Response<Estufa> {
        return api.buscarEstufa(id)
    }

    suspend fun atualizarEstufa(id: Long, request: EstufaRequest): Response<Estufa> {
        return api.atualizarEstufa(id, request)
    }

    suspend fun buscarSensores(estufaId: Long? = null): Response<List<SensorLeituraResponse>> {
        return api.getSensores(estufaId)
    }

    fun converterParaSensorAmbiente(leituras: List<SensorLeituraResponse>): SensorAmbiente {
        val latestByType = leituras
            .sortedByDescending { it.timestamp }
            .groupBy { it.tipoSensor }
            .mapValues { (_, readings) -> readings.first().valorLeitura }

        return SensorAmbiente(
            temperatura = latestByType["TEMPERATURA"] ?: 0.0,
            umidade = latestByType["UMIDADE_SOLO"] ?: 0.0,
            oxigenio = latestByType["OXIGENIO"] ?: 0.0,
            radiacao = latestByType["RADIACAO_EXTERNA"] ?: 0.0
        )
    }

    suspend fun listarAlertas(): Response<List<Alerta>> {
        return api.listarAlertas()
    }

    suspend fun listarAlertasPorEstufa(estufaId: Long): Response<List<Alerta>> {
        return api.listarAlertasPorEstufa(estufaId)
    }

    suspend fun resolverAlerta(id: Long): Response<Alerta> {
        return api.resolverAlerta(id)
    }
}
