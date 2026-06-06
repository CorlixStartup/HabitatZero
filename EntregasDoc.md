# HabitatZero — Documentação Técnica

**FIAP — Engenharia de Software — 4º Ano — Global Solution 2026/1**

> Plataforma full-stack de monitoramento e controle de estufas autônomas para ambientes extremos (colonização lunar/marciana).

---

## 1. Banco de Dados — Diagrama Entidade-Relacionamento

### Diagrama

```
┌──────────────────────────┐
│          COLONO          │
├──────────────────────────┤        ┌──────────────────────────────────────┐
│ id          BIGINT  PK   │        │                ESTUFA                │
│ nome        VARCHAR(100) │  N:1   ├──────────────────────────────────────┤
│ email       VARCHAR(150) ├───────►│ id                   BIGINT  PK      │
│ senha_hash  VARCHAR(255) │        │ nome                 VARCHAR(100)    │
│ cargo       ENUM         │        │ localizacao          VARCHAR(200)    │
│ estufa_id   FK → estufa  │        │ capacidade_m2        DOUBLE          │
└──────────────────────────┘        │ status               ENUM            │
                                    │ threshold_oxigenio_min   DOUBLE      │
                                    │ threshold_umidade_min    DOUBLE      │
                                    │ threshold_radiacao_max   DOUBLE      │
                                    │ threshold_temperatura_max DOUBLE     │
                                    └──────┬─────────────────┬────────────┘
                                           │ 1               │ 1
                              ┌────────────┘                 └────────────┐
                              │ N                                         │ N
              ┌───────────────▼──────────────┐   ┌────────────────────────▼──────┐
              │            PLANTA            │   │        SENSOR_AMBIENTE        │
              ├──────────────────────────────┤   ├───────────────────────────────┤
              │ id             BIGINT  PK    │   │ id            BIGINT  PK      │
              │ nome_cientifico VARCHAR(150) │   │ tipo_sensor   ENUM            │
              │ nome_comum     VARCHAR(100)  │   │ valor_leitura DOUBLE          │
              │ fase_crescimento ENUM        │   │ unidade       ENUM            │
              │ data_plantio   DATE          │   │ timestamp     DATETIME(6)     │
              │ estufa_id      FK → estufa   │   │ estufa_id     FK → estufa     │
              └──────────────────────────────┘   └───────────────────────────────┘
                                                          │ dispara
                                    ┌─────────────────────▼──────────────────┐
                                    │               ALERTA                   │
                                    ├────────────────────────────────────────┤
                                    │ id              BIGINT  PK             │
                                    │ severidade      ENUM                   │
                                    │ mensagem        VARCHAR(255)           │
                                    │ tipo_sensor     ENUM                   │
                                    │ valor_registrado DOUBLE                │
                                    │ criado_em       DATETIME(6)            │
                                    │ resolvido       BIT                    │
                                    │ resolvido_em    DATETIME(6)            │
                                    │ estufa_id       FK → estufa            │
                                    └────────────────────────────────────────┘
```

### Entidades e Relacionamentos

| Entidade | Descrição | Cardinalidade com Estufa |
|---|---|---|
| **ESTUFA** | Estufa autônoma com thresholds de alerta configuráveis por instância | — (entidade central) |
| **COLONO** | Usuário do sistema com cargo e atribuição opcional a uma estufa | N:1 → ESTUFA |
| **PLANTA** | Planta cultivada em uma estufa, rastreada por fase de crescimento | N:1 → ESTUFA |
| **SENSOR_AMBIENTE** | Leitura pontual de um sensor IoT vinculada a uma estufa | N:1 → ESTUFA |
| **ALERTA** | Registro de violação de threshold, gerado automaticamente pelo backend | N:1 → ESTUFA |

### Enums

| Entidade | Campo | Valores |
|---|---|---|
| ESTUFA | `status` | `ATIVA`, `INATIVA`, `MANUTENCAO` |
| COLONO | `cargo` | `AGRONOMISTA`, `ENGENHEIRO`, `MEDICO`, `COMANDANTE`, `TECNICO` |
| PLANTA | `fase_crescimento` | `SEMENTE`, `GERMINACAO`, `CRESCIMENTO`, `MATURACAO`, `COLHEITA` |
| SENSOR_AMBIENTE | `tipo_sensor` | `OXIGENIO`, `UMIDADE_SOLO`, `RADIACAO_EXTERNA`, `TEMPERATURA` |
| SENSOR_AMBIENTE | `unidade` | `PERCENTUAL`, `MSV_HORA`, `CELSIUS` |
| ALERTA | `severidade` | `ATENCAO`, `CRITICO`, `EMERGENCIA` |

### Índices de Performance

```sql
CREATE INDEX idx_sensor_estufa_tipo ON sensor_ambiente (estufa_id, tipo_sensor);
CREATE INDEX idx_sensor_timestamp   ON sensor_ambiente (timestamp);
```

O índice composto `(estufa_id, tipo_sensor)` cobre a query mais frequente do sistema — buscar a última leitura de um tipo específico para uma estufa, executada a cada 5 segundos pelo Dashboard.

---

## 2. Testes — Plano Completo com Evidências

### 2.1 Testes Unitários — Backend (JUnit 5 + Mockito)

**Localização:** `backend/src/test/java/br/com/gs/habitatzero/service/`  
**Como executar:** `cd backend && ./mvnw test`  
**Estratégia:** Cada serviço é testado em isolamento com repositórios mockados via Mockito. Nenhuma instância de banco de dados é necessária.

---

#### TC-01 — Criação de colono com dados válidos

| Campo | Detalhe |
|---|---|
| **Arquivo** | `ColonoServiceTest.java` — `criar_comDadosValidos_deveCriarColono()` |
| **Cenário** | Um novo colono é cadastrado com nome, email, senha forte e cargo. O email não existe no banco. |
| **Entrada** | `ColonoRequest { nome="Joao Silva", email="joao@habitat.com", senha="Senha@123", cargo=AGRONOMISTA }` |
| **Mocks** | `colonoRepository.existsByEmail()` → `false`; `passwordEncoder.encode()` → `"hash_encoded"`; `colonoRepository.save()` → entidade salva com `id=1` |
| **Saída esperada** | `ColonoResponse` não-nulo, `id=1`, `email="joao@habitat.com"`; `colonoRepository.save()` chamado exatamente 1 vez |
| **Status** | ✅ PASS |

**Log de evidência:**
```
ColonoServiceTest > criar_comDadosValidos_deveCriarColono() PASSED
```

---

#### TC-02 — Criação de colono com e-mail duplicado

| Campo | Detalhe |
|---|---|
| **Arquivo** | `ColonoServiceTest.java` — `criar_comEmailExistente_deveDispararBusinessException()` |
| **Cenário** | Tentativa de cadastro com um email já registrado no sistema. |
| **Entrada** | `ColonoRequest { email="duplicado@habitat.com", ... }` |
| **Mocks** | `colonoRepository.existsByEmail("duplicado@habitat.com")` → `true` |
| **Saída esperada** | Lança `BusinessException`; `colonoRepository.save()` **nunca** chamado |
| **Status** | ✅ PASS |

**Log de evidência:**
```
ColonoServiceTest > criar_comEmailExistente_deveDispararBusinessException() PASSED
```

---

#### TC-03 — Criação de estufa com thresholds padrão

| Campo | Detalhe |
|---|---|
| **Arquivo** | `EstufaServiceTest.java` — `criar_comDadosValidos_deveCriarEstufa()` |
| **Cenário** | Nova estufa cadastrada sem especificar thresholds — o sistema deve aplicar os valores padrão. |
| **Entrada** | `EstufaRequest { nome="Estufa Alpha", localizacao="Setor A - Marte", capacidadeM2=500.0 }` |
| **Mocks** | `estufaRepository.save()` → estufa com thresholds padrão injetados pelo `@Builder.Default` |
| **Saída esperada** | `EstufaResponse.thresholdOxigenioMin == 19.5`, `status == ATIVA` |
| **Status** | ✅ PASS |

**Log de evidência:**
```
EstufaServiceTest > criar_comDadosValidos_deveCriarEstufa() PASSED
```

---

#### TC-04 — Busca de estufa por ID inexistente

| Campo | Detalhe |
|---|---|
| **Arquivo** | `EstufaServiceTest.java` — `buscarPorId_comIdInexistente_deveDispararResourceNotFoundException()` |
| **Cenário** | Consulta a uma estufa com ID que não existe no banco. |
| **Entrada** | `id = 999L` |
| **Mocks** | `estufaRepository.findById(999L)` → `Optional.empty()` |
| **Saída esperada** | Lança `ResourceNotFoundException` |
| **Status** | ✅ PASS |

**Log de evidência:**
```
EstufaServiceTest > buscarPorId_comIdInexistente_deveDispararResourceNotFoundException() PASSED
```

---

#### TC-05 — Leitura de sensor dentro dos limites, sem alerta

| Campo | Detalhe |
|---|---|
| **Arquivo** | `SensorServiceTest.java` — `registrarLeitura_comDadosValidos_devePersistirLeitura()` |
| **Cenário** | ESP32 envia leitura de O₂ de 21,0% — acima do threshold mínimo de 19,5%. Nenhum alerta deve ser criado. |
| **Entrada** | `SensorLeituraRequest { estufaId=1, tipoSensor=OXIGENIO, valorLeitura=21.0, unidade=PERCENTUAL }` |
| **Mocks** | `estufaRepository.findById(1L)` → estufa com threshold O₂ = 19,5; `alertaService.avaliarLeituraEDispararAlerta()` → `false` |
| **Saída esperada** | `SensorLeituraResponse.valorLeitura == 21.0`, `alertaDisparado == false`; `sensorRepository.save()` chamado 1 vez |
| **Status** | ✅ PASS |

**Log de evidência:**
```
SensorServiceTest > registrarLeitura_comDadosValidos_devePersistirLeitura() PASSED
```

---

#### TC-06 — Leitura de O₂ abaixo do threshold dispara alerta

| Campo | Detalhe |
|---|---|
| **Arquivo** | `AlertaServiceTest.java` — `avaliarLeitura_comOxigenioAbaixoThreshold_deveCriarAlerta()` |
| **Cenário** | Leitura de O₂ de 17,0% — abaixo do threshold de 19,5%. O serviço de alertas deve criar e persistir um alerta. |
| **Entrada** | `SensorAmbiente { tipoSensor=OXIGENIO, valorLeitura=17.0 }` + `Estufa { thresholdOxigenioMin=19.5 }` |
| **Saída esperada** | Retorna `true`; `alertaRepository.save(Alerta)` chamado exatamente 1 vez |
| **Status** | ✅ PASS |

**Log de evidência:**
```
AlertaServiceTest > avaliarLeitura_comOxigenioAbaixoThreshold_deveCriarAlerta() PASSED
```

---

#### TC-07 — Resolução de alerta ativo

| Campo | Detalhe |
|---|---|
| **Arquivo** | `AlertaServiceTest.java` — `resolverAlerta_comAlertaAtivo_deveMarcarComoResolvido()` |
| **Cenário** | Um alerta ativo (resolvido=false) é marcado como resolvido via `PATCH /alertas/{id}/resolver`. |
| **Entrada** | `alertaId = 42L`; alerta com `resolvido=false` |
| **Mocks** | `alertaRepository.findById(42L)` → alerta ativo; `alertaRepository.save()` → retorna o argumento |
| **Saída esperada** | `AlertaResponse.resolvido == true`; `AlertaResponse.resolvidoEm != null` |
| **Status** | ✅ PASS |

**Log de evidência:**
```
AlertaServiceTest > resolverAlerta_comAlertaAtivo_deveMarcarComoResolvido() PASSED
```

---

#### TC-08 — Login com senha incorreta

| Campo | Detalhe |
|---|---|
| **Arquivo** | `AuthServiceTest.java` — `login_comSenhaInvalida_deveDispararException()` |
| **Cenário** | Colono tenta autenticar com senha errada. O `AuthenticationManager` do Spring Security rejeita a tentativa. |
| **Entrada** | `LoginRequest { email="usuario@habitat.com", senha="senhaErrada" }` |
| **Mocks** | `authenticationManager.authenticate()` lança `BadCredentialsException` |
| **Saída esperada** | `BadCredentialsException` propagada; `colonoRepository.findByEmail()` **nunca** chamado |
| **Status** | ✅ PASS |

**Log de evidência:**
```
AuthServiceTest > login_comSenhaInvalida_deveDispararException() PASSED
```

---

### 2.2 Testes de Integração — IoT (pytest)

**Localização:** `IoT/test_integration.py`  
**Como executar:**
```bash
cd IoT
pip install pytest requests
pytest test_integration.py -v                        # todos (IT-01 roda offline)
pytest test_integration.py -m integration -v         # apenas IT-02 a IT-04 (requer backend)
```

---

#### IT-01 — Payload do simulador tem formato correto

| Campo | Detalhe |
|---|---|
| **Classe** | `TestPayloadFormat` — 3 asserções |
| **Cenário** | Valida o payload gerado por `gerar_leitura_mutante()` antes de qualquer envio. Roda sem servidor. |
| **Entrada** | Chamadas repetidas (30×) à função `gerar_leitura_mutante()` |
| **Saída esperada** | Campos `estufaId`, `tipoSensor`, `valorLeitura`, `unidade`, `timestamp` presentes; `tipoSensor` ∈ `{OXIGENIO, UMIDADE_SOLO, RADIACAO_EXTERNA, TEMPERATURA}`; `unidade` ∈ `{PERCENTUAL, MSV_HORA, CELSIUS}`; timestamp sem sufixo `Z` (incompatível com `LocalDateTime`) |
| **Status** | ✅ PASS |

**Log de evidência:**
```
IoT/test_integration.py::TestPayloadFormat::test_payload_tem_campos_corretos PASSED
IoT/test_integration.py::TestPayloadFormat::test_payload_enums_validos PASSED
IoT/test_integration.py::TestPayloadFormat::test_payload_timestamp_sem_sufixo_z PASSED
```

---

#### IT-02 — API aceita leitura válida (HTTP 200/201)

| Campo | Detalhe |
|---|---|
| **Classe** | `TestApiAceitaLeitura` — requer backend |
| **Cenário** | `POST /sensores/leitura` com payload gerado pelo simulador deve ser aceito sem autenticação. |
| **Entrada** | Payload JSON de `gerar_leitura_mutante()` enviado via `requests.post()` |
| **Saída esperada** | `response.status_code in (200, 201)` |
| **Status** | ✅ PASS |

**Log de evidência:**
```
IoT/test_integration.py::TestApiAceitaLeitura::test_leitura_valida_retorna_sucesso PASSED
```

---

#### IT-03 — API rejeita payload sem campo obrigatório (HTTP 400)

| Campo | Detalhe |
|---|---|
| **Classe** | `TestApiRejeita400` — requer backend |
| **Cenário** | `POST /sensores/leitura` sem `estufaId` deve ser rejeitado com Bean Validation. |
| **Entrada** | Payload de `gerar_leitura_mutante()` com chave `estufaId` removida via `del payload["estufaId"]` |
| **Saída esperada** | `response.status_code == 400` |
| **Status** | ✅ PASS |

**Log de evidência:**
```
IoT/test_integration.py::TestApiRejeita400::test_payload_sem_estufa_id_retorna_400 PASSED
```

---

#### IT-04 — Leitura é persistida e resposta contém ID

| Campo | Detalhe |
|---|---|
| **Classe** | `TestLeituraPersistida` — requer backend |
| **Cenário** | Após um `POST` bem-sucedido, a resposta JSON deve conter `id` (PK gerado) e `estufaId` correto, confirmando que o Hibernate persistiu o registro. |
| **Entrada** | Payload válido de `gerar_leitura_mutante()`; `estufaId = 1` |
| **Saída esperada** | `"id" in response.json()` é `True`; `response.json()["estufaId"] == 1` |
| **Status** | ✅ PASS |

**Log de evidência:**
```
IoT/test_integration.py::TestLeituraPersistida::test_leitura_aparece_no_historico PASSED
```

---

### 2.3 Testes Unitários — Frontend Android (JVM / JUnit 4)

**Localização:** `frontend/app/src/test/java/com/workwell/habitatzero/HabitatZeroUnitTests.kt`  
**Como executar:** `cd frontend && ./gradlew :app:test`  
**Estratégia:** Lógica pura extraída da camada de repositório e ViewModel testada na JVM, sem emulador, sem contexto Android.

---

#### TC-FE-01 — Conversão de leituras IoT com todos os tipos de sensor

| Campo | Detalhe |
|---|---|
| **Método** | `TC-FE-01 converter leituras IoT com todos os tipos retorna SensorAmbiente correto` |
| **Cenário** | Repository recebe 4 `SensorLeituraResponse` (um por tipo) e agrega em um único `SensorAmbiente` para o Dashboard. |
| **Entrada** | Lista com `TEMPERATURA=22.5°C`, `UMIDADE_SOLO=65%`, `OXIGENIO=20.8%`, `RADIACAO_EXTERNA=1.2 mSv/h` |
| **Saída esperada** | `SensorAmbiente(temperatura=22.5, umidade=65.0, oxigenio=20.8, radiacao=1.2)` |
| **Status** | ✅ PASS |

**Log de evidência:**
```
HabitatZeroUnitTests > TC-FE-01 converter leituras IoT com todos os tipos retorna SensorAmbiente correto PASSED
```

---

#### TC-FE-02 — Leituras duplicadas do mesmo sensor usa a mais recente

| Campo | Detalhe |
|---|---|
| **Método** | `TC-FE-02 converter leituras duplicadas retorna somente a mais recente` |
| **Cenário** | Dois registros de TEMPERATURA com timestamps diferentes — o Dashboard deve exibir sempre o mais recente. |
| **Entrada** | `TEMPERATURA=18.0°C` às `09:00` e `TEMPERATURA=24.0°C` às `10:30` |
| **Saída esperada** | `SensorAmbiente.temperatura == 24.0` |
| **Status** | ✅ PASS |

**Log de evidência:**
```
HabitatZeroUnitTests > TC-FE-02 converter leituras duplicadas retorna somente a mais recente PASSED
```

---

#### TC-FE-03 — Lista de leituras vazia retorna zeros sem crash

| Campo | Detalhe |
|---|---|
| **Método** | `TC-FE-03 converter lista vazia retorna SensorAmbiente com zeros` |
| **Cenário** | Antes do simulador IoT enviar dados, o endpoint retorna lista vazia. O app não deve crashar. |
| **Entrada** | `emptyList<SensorLeituraResponse>()` |
| **Saída esperada** | `SensorAmbiente(0.0, 0.0, 0.0, 0.0)` |
| **Status** | ✅ PASS |

**Log de evidência:**
```
HabitatZeroUnitTests > TC-FE-03 converter lista vazia retorna SensorAmbiente com zeros PASSED
```

---

#### TC-FE-04 — Avanço de fase de planta: sequência correta e limite final

| Campo | Detalhe |
|---|---|
| **Método** | `TC-FE-04 avancar fase retorna proxima fase correta e null apos COLHEITA` |
| **Cenário** | Botão "Avançar Fase" percorre toda a sequência e fica desabilitado em COLHEITA. |
| **Entrada** | `proximaFase()` chamada para cada valor: SEMENTE, GERMINACAO, CRESCIMENTO, MATURACAO, COLHEITA |
| **Saída esperada** | SEMENTE→GERMINACAO, GERMINACAO→CRESCIMENTO, CRESCIMENTO→MATURACAO, MATURACAO→COLHEITA, COLHEITA→`null` |
| **Status** | ✅ PASS |

**Log de evidência:**
```
HabitatZeroUnitTests > TC-FE-04 avancar fase retorna proxima fase correta e null apos COLHEITA PASSED
```

---

#### TC-FE-05 — Buffer de histórico do Dashboard respeita limite de 20 entradas

| Campo | Detalhe |
|---|---|
| **Método** | `TC-FE-05 buffer de historico descarta entrada mais antiga ao atingir limite de 20` |
| **Cenário** | `ArrayDeque` com capacidade de 20 leituras — ao receber a 21ª, a mais antiga é descartada. |
| **Entrada** | Buffer cheio com 20 entradas + nova leitura com todos os valores `99.0` |
| **Saída esperada** | `buffer.size == 20`; `buffer.last() == novaLeitura`; primeira entrada não é mais a original |
| **Status** | ✅ PASS |

**Log de evidência:**
```
HabitatZeroUnitTests > TC-FE-05 buffer de historico descarta entrada mais antiga ao atingir limite de 20 PASSED
```

---

## 3. IoT — Lógica dos Sensores e Influência no Sistema

### Visão Geral da Integração

```
ESP32 (ou simulador Python)
        │
        │  POST /sensores/leitura  (sem autenticação)
        ▼
  SensorController
        │
        ▼
  SensorService.registrarLeitura()
        ├── persiste SensorAmbiente no banco
        └── chama AlertaService.avaliarLeituraEDispararAlerta()
                    │
                    ├── compara valor com threshold da estufa
                    ├── se violado → cria Alerta no banco
                    └── retorna alertaDisparado: true/false
```

O campo `alertaDisparado` está presente na resposta do `POST /sensores/leitura`, permitindo que o ESP32 ou o simulador saiba imediatamente se a leitura gerou um alerta — sem precisar consultar um endpoint adicional.

---

### Sensor 1 — Oxigênio (O₂)

| Atributo | Valor |
|---|---|
| **`tipoSensor`** | `OXIGENIO` |
| **Unidade** | `PERCENTUAL` (%) |
| **Threshold padrão** | `thresholdOxigenioMin = 19.5%` |
| **Range simulado** | 18,0% – 23,0% |
| **Condição de alerta** | `valorLeitura < thresholdOxigenioMin` |

**Lógica de severidade:**
```
desvio = thresholdOxigenioMin - valorLeitura
desvio ≥ 5%  →  EMERGENCIA   (ex: O₂ = 14.5% com threshold 19.5%)
desvio ≥ 2%  →  CRITICO      (ex: O₂ = 17.0%)
desvio < 2%  →  ATENCAO      (ex: O₂ = 18.8%)
```

**Influência no sistema:** O oxigênio é o sensor mais crítico da colônia. Uma queda abaixo de 19,5% compromete a respiração humana e a sobrevivência das plantas. O sistema escalona automaticamente a severidade proporcionalmente ao desvio, permitindo resposta graduada — de atenção monitorada a evacuação de emergência.

---

### Sensor 2 — Umidade do Solo

| Atributo | Valor |
|---|---|
| **`tipoSensor`** | `UMIDADE_SOLO` |
| **Unidade** | `PERCENTUAL` (%) |
| **Threshold padrão** | `thresholdUmidadeMin = 30.0%` |
| **Range simulado** | 25,0% – 70,0% |
| **Condição de alerta** | `valorLeitura < thresholdUmidadeMin` |

**Lógica de severidade:**
```
qualquer desvio abaixo do threshold  →  ATENCAO
```

**Influência no sistema:** Solo abaixo de 30% de umidade compromete a absorção de nutrientes pelas raízes. O alerta de `ATENCAO` sinaliza ao colono que é necessário acionar a irrigação manualmente pelo app (controle simulado) ou verificar o sistema de irrigação automática da estufa.

---

### Sensor 3 — Radiação Externa

| Atributo | Valor |
|---|---|
| **`tipoSensor`** | `RADIACAO_EXTERNA` |
| **Unidade** | `MSV_HORA` (mSv/h) |
| **Threshold padrão** | `thresholdRadiacaoMax = 2.0 mSv/h` |
| **Range simulado** | 0,1 – 3,5 mSv/h |
| **Condição de alerta** | `valorLeitura > thresholdRadiacaoMax` |

**Lógica de severidade:**
```
valor ≥ 3×threshold  →  EMERGENCIA   (ex: 6.0 mSv/h)
valor ≥ 2×threshold  →  CRITICO      (ex: 4.0 mSv/h)
valor > threshold    →  ATENCAO      (ex: 2.5 mSv/h)
```

**Influência no sistema:** Em ambientes lunares e marcianos, picos de radiação cósmica são previsíveis mas fatais em doses acumuladas. A severidade em múltiplos do threshold reflete que o perigo cresce de forma não-linear: 2× o limite seguro já representa risco direto ao DNA celular das plantas e à saúde dos colonos.

---

### Sensor 4 — Temperatura Interna

| Atributo | Valor |
|---|---|
| **`tipoSensor`** | `TEMPERATURA` |
| **Unidade** | `CELSIUS` (°C) |
| **Threshold padrão** | `thresholdTemperaturaMax = 40.0°C` |
| **Range simulado** | 20,0°C – 50,0°C |
| **Condição de alerta** | `valorLeitura > thresholdTemperaturaMax` |

**Lógica de severidade:**
```
qualquer leitura acima do threshold  →  CRITICO
```

**Influência no sistema:** Temperaturas acima de 40°C causam desnaturação de proteínas nas plantas e risco de hipertermia para os colonos. Diferente dos outros sensores, temperatura recebe sempre `CRITICO` — não existe "atenção" para superaquecimento dentro de uma estufa pressurizável, onde o calor acumulado escalona rapidamente.

---

### Tabela Resumo — Thresholds e Severidades

| Sensor | Condição | Severidade | Mensagem gerada |
|---|---|---|---|
| `OXIGENIO` | valor < min, desvio ≥ 5% | `EMERGENCIA` | `"O₂ abaixo do limite: X% (mínimo: Y%)"` |
| `OXIGENIO` | valor < min, desvio ≥ 2% | `CRITICO` | `"O₂ abaixo do limite: X% (mínimo: Y%)"` |
| `OXIGENIO` | valor < min, desvio < 2% | `ATENCAO` | `"O₂ abaixo do limite: X% (mínimo: Y%)"` |
| `UMIDADE_SOLO` | valor < min | `ATENCAO` | `"Umidade do solo crítica: X% (mínimo: Y%)"` |
| `RADIACAO_EXTERNA` | valor ≥ 3× max | `EMERGENCIA` | `"Radiação externa elevada: X mSv/h (máximo: Y mSv/h)"` |
| `RADIACAO_EXTERNA` | valor ≥ 2× max | `CRITICO` | `"Radiação externa elevada: X mSv/h (máximo: Y mSv/h)"` |
| `RADIACAO_EXTERNA` | valor > max | `ATENCAO` | `"Radiação externa elevada: X mSv/h (máximo: Y mSv/h)"` |
| `TEMPERATURA` | valor > max | `CRITICO` | `"Temperatura acima do limite: X°C (máximo: Y°C)"` |

### Personalização por Estufa

Todos os thresholds são configuráveis individualmente por estufa via `PUT /estufas/{id}`. O app Android expõe isso na tela **Controle Climático** — o colono pode ajustar `thresholdTemperaturaMax` e `thresholdUmidadeMin` com sliders, e a mudança é imediatamente refletida na avaliação das próximas leituras IoT.

---

### Implementação com Hardware Físico

O projeto inclui o firmware real em `IoT/FirmwareReal.cpp`, escrito para o **ESP32** (microcontrolador Espressif com Wi-Fi integrado). Abaixo está a especificação completa do hardware necessário para substituir o simulador Python por um dispositivo físico.

#### Microcontrolador

| Componente | Modelo recomendado | Justificativa |
|---|---|---|
| **MCU** | ESP32 DevKit V1 (ou ESP32-WROOM-32) | Wi-Fi 802.11 b/g/n nativo, ADC de 12 bits (0–4095), suporta HTTP sem shields adicionais |

#### Sensores Físicos

| Variável medida | Sensor recomendado | Pino no ESP32 | Saída elétrica | Observações |
|---|---|---|---|---|
| **Oxigênio (O₂)** | Grove Oxygen Sensor (ME2-O2-Ф20) | `GPIO 34` (ADC1_CH6) | Analógica 0–3,3 V | Saída proporcional à concentração. Conversão: `O₂% = (leitura / 4095.0) × 25.0` conforme datasheet |
| **Umidade do Solo** | Sensor Capacitivo de Umidade v2.0 | `GPIO 35` (ADC1_CH7) | Analógica 0–3,3 V | Capacitivo (não enferruja). Inversamente proporcional: solo seco → valor alto (~3200), solo molhado → valor baixo (~1500). Requer calibração por substrato |
| **Radiação** | Módulo Geiger-Müller SEN0463 (DFRobot) | `GPIO 32` (ADC1_CH4) | Pulsos digitais / tensão analógica convertida | Fornece taxa de dose em µSv/h. Divisão por 1000 para converter para mSv/h antes de enviar à API |
| **Temperatura** | DS18B20 (sonda impermeável) | `GPIO 4` (OneWire) | Digital (protocolo 1-Wire) | ±0,5°C de precisão, à prova d'água. Para uso em estufa, a versão impermeável é preferível ao DHT22 |

> **Por que GPIO34 e GPIO35?** Esses pinos são somente entrada (input-only) no ESP32, o que os torna ideais para leitura analógica — não há risco de configuração acidental como saída que danificaria o ADC.

#### Diagrama de Conexão

```
                    ┌─────────────────────┐
                    │       ESP32          │
                    │                     │
  ME2-O2 ──────────┤ GPIO34 (ADC)        │
  Capacitivo Solo ──┤ GPIO35 (ADC)        │
  Geiger SEN0463 ───┤ GPIO32 (ADC)        │
  DS18B20 ──────────┤ GPIO4  (1-Wire)     │
                    │                     │
                    │ 3V3 ────────────────┼──► VCC sensores
                    │ GND ────────────────┼──► GND sensores
                    │                     │
                    │ Wi-Fi (interno)     │
                    └─────────┬───────────┘
                              │ HTTP POST
                              ▼
                  POST /sensores/leitura
                  (Spring Boot API)
```

#### Lógica do Firmware (`FirmwareReal.cpp`)

O firmware executa um ciclo simples a cada 10 segundos:

1. **Lê** cada sensor via `analogRead()` ou protocolo 1-Wire
2. **Converte** o valor bruto (0–4095 para ADC de 12 bits) para a unidade física usando a fórmula do datasheet de cada componente
3. **Monta** o payload JSON com `ArduinoJson`
4. **Envia** via `HTTPClient` ao endpoint `POST /sensores/leitura` da API
5. **Verifica** o campo `alertaDisparado` na resposta — se `true`, acende um LED vermelho no hardware (feedback local imediato, independente do app)

```cpp
// Trecho do loop principal (FirmwareReal.cpp)
void loop() {
    enviarLeitura("OXIGENIO",         lerSensorOxigenio(),   "PERCENTUAL");
    enviarLeitura("UMIDADE_SOLO",     lerSensorUmidade(),    "PERCENTUAL");
    enviarLeitura("RADIACAO_EXTERNA", lerSensorRadiacao(),   "MSV_HORA");
    enviarLeitura("TEMPERATURA",      lerSensorTemperatura(), "CELSIUS");
    delay(10000); // aguarda 10 segundos
}
```

A função `enviarLeitura()` monta o JSON, faz o `POST` e loga o resultado na Serial (115200 baud) para depuração com o Monitor Serial do Arduino IDE.

#### Configuração para Uso Físico

1. Instale o suporte ao ESP32 no Arduino IDE via Board Manager: `https://dl.espressif.com/dl/package_esp32_index.json`
2. Instale as bibliotecas: **ArduinoJson** (v6+) e **DallasTemperature** (para DS18B20) via Library Manager
3. Edite `FirmwareReal.cpp` com suas credenciais de rede e IP do servidor:
```cpp
const char* ssid       = "NOME_DA_REDE_WIFI";
const char* password   = "SENHA_WIFI";
const char* serverName = "http://192.168.X.X:8080/sensores/leitura";
const int   ESTUFA_ID  = 1; // ID da estufa cadastrada no banco
```
4. Selecione a placa **ESP32 Dev Module** e a porta COM correta
5. Faça o upload (⚡ Upload no Arduino IDE)

O endpoint `POST /sensores/leitura` não requer autenticação, portanto o ESP32 se comunica diretamente com a API sem necessidade de gerenciar tokens JWT.
