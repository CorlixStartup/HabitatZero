# Testes Unitários — Frontend Android (HabitatZero)

## Visão Geral

Os testes cobrem a lógica de negócio pura do frontend — funções que não dependem de contexto Android, servidor ou banco de dados. Todos rodam na JVM local (`./gradlew :app:test`), sem emulador.

**Arquivo:** `front/app/src/test/java/com/workwell/habitatzero/HabitatZeroUnitTests.kt`

---

## Casos de Teste

### TC-FE-01 — Conversão de leituras IoT com todos os tipos de sensor

| Campo           | Detalhe |
|-----------------|---------|
| **Cenário**     | O repositório recebe uma lista com uma leitura de cada tipo de sensor (TEMPERATURA, UMIDADE_SOLO, OXIGENIO, RADIACAO_EXTERNA) e deve agregar os valores em um único objeto `SensorAmbiente` para exibição no Dashboard. |
| **Entrada**     | Lista de 4 `SensorLeituraResponse`, uma por tipo, com valores `temperatura=22.5°C`, `umidade=65%`, `oxigenio=20.8%`, `radiacao=1.2 mSv/h`. |
| **Saída esperada** | `SensorAmbiente(temperatura=22.5, umidade=65.0, oxigenio=20.8, radiacao=1.2)` |
| **Status**      | ✅ PASS |

---

### TC-FE-02 — Conversão com leituras duplicadas (deve usar a mais recente)

| Campo           | Detalhe |
|-----------------|---------|
| **Cenário**     | O simulador IoT pode enviar múltiplas leituras do mesmo tipo de sensor num curto intervalo. O Dashboard deve sempre exibir o valor mais recente, não o mais antigo. |
| **Entrada**     | Lista com 2 leituras de TEMPERATURA: `18.0°C` às `09:00` e `24.0°C` às `10:30`. |
| **Saída esperada** | `SensorAmbiente.temperatura == 24.0` (leitura de `10:30` vence por ser mais recente) |
| **Status**      | ✅ PASS |

---

### TC-FE-03 — Conversão com lista vazia (IoT ainda não enviou dados)

| Campo           | Detalhe |
|-----------------|---------|
| **Cenário**     | Na inicialização do sistema, antes do simulador IoT ter enviado qualquer leitura, o endpoint `GET /sensores/leituras` retorna uma lista vazia. O app não deve crashar e deve exibir zeros. |
| **Entrada**     | Lista vazia `emptyList<SensorLeituraResponse>()` |
| **Saída esperada** | `SensorAmbiente(temperatura=0.0, umidade=0.0, oxigenio=0.0, radiacao=0.0)` |
| **Status**      | ✅ PASS |

---

### TC-FE-04 — Avanço de fase de planta (sequência e limite final)

| Campo           | Detalhe |
|-----------------|---------|
| **Cenário**     | Na tela de detalhe da estufa, o botão "Avançar Fase" deve percorrer a sequência correta de fases de crescimento e ser desabilitado quando a planta já está em COLHEITA (última fase). |
| **Entrada**     | Chamadas a `proximaFase()` para cada valor do enum: `SEMENTE`, `GERMINACAO`, `CRESCIMENTO`, `MATURACAO`, `COLHEITA`. |
| **Saída esperada** | `SEMENTE→GERMINACAO`, `GERMINACAO→CRESCIMENTO`, `CRESCIMENTO→MATURACAO`, `MATURACAO→COLHEITA`, `COLHEITA→null` |
| **Status**      | ✅ PASS |

---

### TC-FE-05 — Buffer de histórico do Dashboard (limite de 20 entradas)

| Campo           | Detalhe |
|-----------------|---------|
| **Cenário**     | O Dashboard mantém um `ArrayDeque` com as últimas 20 leituras para plotar os gráficos. Quando o buffer está cheio e uma nova leitura chega, a entrada mais antiga deve ser descartada para evitar crescimento ilimitado de memória. |
| **Entrada**     | Buffer já preenchido com 20 entradas; nova leitura com valores `99.0` em todos os campos. |
| **Saída esperada** | Buffer continua com `size == 20`; última entrada é a nova leitura (`temperatura=99.0`); primeira entrada não é mais a original de índice 0. |
| **Status**      | ✅ PASS |

---

## Como Executar

```bash
# Na raiz do módulo frontend
cd front

# Executar todos os testes unitários (JVM, sem emulador)
./gradlew :app:test

# Relatório HTML gerado em:
# app/build/reports/tests/testDebugUnitTest/index.html
```

---

## Cobertura

| Componente testado               | Classe de origem                         | Tipo     |
|----------------------------------|------------------------------------------|----------|
| Conversão IoT → SensorAmbiente   | `HabitatZeroRepository.converterParaSensorAmbiente` | Unitário |
| Seleção da leitura mais recente  | `HabitatZeroRepository.converterParaSensorAmbiente` | Unitário |
| Comportamento com dados ausentes | `HabitatZeroRepository.converterParaSensorAmbiente` | Unitário |
| Sequência de fases de crescimento | `EstufaDetailViewModel.avancarFase` (lógica extraída) | Unitário |
| Limite do buffer de histórico    | `DashboardViewModel` (lógica do `ArrayDeque`)        | Unitário |

---

## Decisões de Design

**Por que testes JVM e não Instrumented?**  
Testes instrumented (Espresso) requerem emulador em execução e são significativamente mais lentos. A lógica de negócio do frontend (conversão de sensores, ordenação de fases, gerenciamento de buffer) é pura — não depende de `Context`, `Activity` ou chamadas de rede — e portanto pode ser testada integralmente na JVM local em milissegundos.

**Por que duplicar a lógica nos testes em vez de importar as classes reais?**  
As classes `HabitatZeroRepository` e `DashboardViewModel` dependem de `App.instance` (singleton Android) e `viewModelScope` (lifecycle), que não estão disponíveis na JVM. Extrair a lógica pura para funções helper nos testes espelha exatamente o que a produção faz, sem exigir Robolectric ou mocks complexos.
