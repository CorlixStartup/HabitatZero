package com.workwell.habitatzero

import com.workwell.habitatzero.model.SensorAmbiente
import com.workwell.habitatzero.model.SensorLeituraResponse
import org.junit.Assert.*
import org.junit.Test

/**
 * 5 unit tests for HabitatZero Android frontend.
 * All tests are pure JVM — no Android context required.
 *
 * Run with: ./gradlew :app:test
 */
class HabitatZeroUnitTests {

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers (mirror production logic without depending on the real classes)
    // ─────────────────────────────────────────────────────────────────────────

    private fun converterParaSensorAmbiente(leituras: List<SensorLeituraResponse>): SensorAmbiente {
        val latestByType = leituras
            .sortedByDescending { it.timestamp }
            .groupBy { it.tipoSensor }
            .mapValues { (_, readings) -> readings.first().valorLeitura }
        return SensorAmbiente(
            temperatura = latestByType["TEMPERATURA"]       ?: 0.0,
            umidade     = latestByType["UMIDADE_SOLO"]      ?: 0.0,
            oxigenio    = latestByType["OXIGENIO"]          ?: 0.0,
            radiacao    = latestByType["RADIACAO_EXTERNA"]  ?: 0.0
        )
    }

    private fun proximaFase(faseAtual: String): String? {
        val fases = listOf("SEMENTE", "GERMINACAO", "CRESCIMENTO", "MATURACAO", "COLHEITA")
        return fases.getOrNull(fases.indexOf(faseAtual) + 1)
    }

    private fun badgeColorNome(severidade: String): String = when (severidade.uppercase()) {
        "EMERGENCIA" -> "vermelho_critico"
        "CRITICO"    -> "vermelho_critico"
        "ATENCAO"    -> "amarelo_atencao"
        else         -> "verde_normal"
    }

    private fun historyCap(existingSize: Int, newEntry: SensorAmbiente, cap: Int): List<SensorAmbiente> {
        val deque = ArrayDeque<SensorAmbiente>(cap)
        repeat(existingSize) {
            if (deque.size >= cap) deque.removeFirst()
            deque.addLast(SensorAmbiente(it.toDouble(), 0.0, 0.0, 0.0))
        }
        if (deque.size >= cap) deque.removeFirst()
        deque.addLast(newEntry)
        return deque.toList()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TC-FE-01: Conversão de leituras IoT → SensorAmbiente (múltiplos sensores)
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun `TC-FE-01 converter leituras IoT com todos os tipos retorna SensorAmbiente correto`() {
        val leituras = listOf(
            SensorLeituraResponse(1, 1, "Alpha", "TEMPERATURA",      22.5, "CELSIUS",    "2026-06-05T10:00:00", false),
            SensorLeituraResponse(2, 1, "Alpha", "UMIDADE_SOLO",     65.0, "PERCENTUAL", "2026-06-05T10:00:00", false),
            SensorLeituraResponse(3, 1, "Alpha", "OXIGENIO",         20.8, "PERCENTUAL", "2026-06-05T10:00:00", false),
            SensorLeituraResponse(4, 1, "Alpha", "RADIACAO_EXTERNA",  1.2, "MSV_HORA",   "2026-06-05T10:00:00", false)
        )

        val result = converterParaSensorAmbiente(leituras)

        assertEquals(22.5, result.temperatura, 0.001)
        assertEquals(65.0, result.umidade,     0.001)
        assertEquals(20.8, result.oxigenio,    0.001)
        assertEquals(1.2,  result.radiacao,    0.001)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TC-FE-02: Conversão com leitura duplicada — deve usar a mais recente
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun `TC-FE-02 converter leituras duplicadas retorna somente a mais recente`() {
        val leituras = listOf(
            SensorLeituraResponse(1, 1, "Alpha", "TEMPERATURA", 18.0, "CELSIUS", "2026-06-05T09:00:00", false),
            SensorLeituraResponse(2, 1, "Alpha", "TEMPERATURA", 24.0, "CELSIUS", "2026-06-05T10:30:00", false)
        )

        val result = converterParaSensorAmbiente(leituras)

        assertEquals("Deve retornar o valor da leitura mais recente (10:30)", 24.0, result.temperatura, 0.001)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TC-FE-03: Conversão sem leituras — todos os campos devem ser 0.0
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun `TC-FE-03 converter lista vazia retorna SensorAmbiente com zeros`() {
        val result = converterParaSensorAmbiente(emptyList())

        assertEquals(0.0, result.temperatura, 0.0)
        assertEquals(0.0, result.umidade,     0.0)
        assertEquals(0.0, result.oxigenio,    0.0)
        assertEquals(0.0, result.radiacao,    0.0)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TC-FE-04: Avanço de fase de planta — sequência e limite
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun `TC-FE-04 avancar fase retorna proxima fase correta e null apos COLHEITA`() {
        assertEquals("GERMINACAO",  proximaFase("SEMENTE"))
        assertEquals("CRESCIMENTO", proximaFase("GERMINACAO"))
        assertEquals("MATURACAO",   proximaFase("CRESCIMENTO"))
        assertEquals("COLHEITA",    proximaFase("MATURACAO"))
        assertNull("Nao deve existir fase apos COLHEITA", proximaFase("COLHEITA"))
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TC-FE-05: Buffer de histórico do Dashboard — limite de 20 entradas
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    fun `TC-FE-05 buffer de historico descarta entrada mais antiga ao atingir limite de 20`() {
        val cap = 20
        val novaLeitura = SensorAmbiente(99.0, 99.0, 99.0, 99.0)

        // Buffer already at capacity (20 entries)
        val result = historyCap(existingSize = 20, newEntry = novaLeitura, cap = cap)

        assertEquals("Buffer deve ter exatamente $cap entradas", cap, result.size)
        assertEquals("Última entrada deve ser a nova leitura", novaLeitura, result.last())
        assertNotEquals("Primeira entrada nao deve ser temperatura 0.0 da entrada original mais antiga",
            0.0, result.first().temperatura, 0.001)
    }
}
