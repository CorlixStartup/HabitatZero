<div align="center">

```
██╗  ██╗ █████╗ ██████╗ ██╗████████╗ █████╗ ████████╗    ███████╗███████╗██████╗  ██████╗
██║  ██║██╔══██╗██╔══██╗██║╚══██╔══╝██╔══██╗╚══██╔══╝    ╚══███╔╝██╔════╝██╔══██╗██╔═══██╗
███████║███████║██████╔╝██║   ██║   ███████║   ██║          ███╔╝ █████╗  ██████╔╝██║   ██║
██╔══██║██╔══██║██╔══██╗██║   ██║   ██╔══██║   ██║         ███╔╝  ██╔══╝  ██╔══██╗██║   ██║
██║  ██║██║  ██║██████╔╝██║   ██║   ██║  ██║   ██║        ███████╗███████╗██║  ██║╚██████╔╝
╚═╝  ╚═╝╚═╝  ╚═╝╚═════╝ ╚═╝   ╚═╝   ╚═╝  ╚═╝   ╚═╝        ╚══════╝╚══════╝╚═╝  ╚═╝ ╚═════╝
```

**Simulação de Produção de Alimentos em Ambientes Extremos**

*Colonização Lunar e Marciana — FIAP Global Solution 2026/1*

---

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-9.6-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Android](https://img.shields.io/badge/Android_Studio-Hedgehog-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6.x-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![JUnit](https://img.shields.io/badge/JUnit_5-Mockito-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![ESP32](https://img.shields.io/badge/ESP32-IoT-E7352C?style=for-the-badge&logo=espressif&logoColor=white)

</div>

---

## 📡 Visão Geral

**Habitat Zero** é uma plataforma full stack de monitoramento e controle de estufas autônomas projetadas para operar em ambientes extremos — como uma base lunar ou marciana. O sistema centraliza os dados vitais de cada estufa (níveis de oxigênio, umidade do solo, temperatura e radiação externa), permitindo que colonos gerenciem remotamente as condições de cultivo a partir de um aplicativo mobile Android.

> *"Criar um ambiente capaz de sustentar vida a partir do zero, em um lugar onde as condições naturais são inóspitas."*

### O Problema que Resolvemos

Em uma colônia espacial, falhas nos sistemas de suporte de vida podem ser letais em minutos. Sem monitoramento centralizado e alertas automáticos, um pico de radiação ou uma queda no nível de O₂ pode destruir uma colheita inteira — ou comprometer a atmosfera interna da estufa. O Habitat Zero resolve isso com:

- **Visibilidade centralizada** de todas as estufas em um único painel
- **Alertas automáticos** quando qualquer sensor ultrapassa um limite crítico
- **Comandos seguros e validados** para controle ambiental remoto
- **Rastreabilidade histórica** de todas as leituras e ocorrências

---

## 🗂️ Estrutura do Repositório

```
habitatzero/
├── backend/      # API Spring Boot (Java 21)
├── frontend/     # Aplicativo Android (Kotlin)
├── IoT/          # Firmware ESP32 + simulador Python
├── db/           # Scripts SQL (DDL + DML)
└── README.md     # Este arquivo
```

---

## 🏗️ Arquitetura da Solução

```
┌─────────────────────────────────────────────────────────────────┐
│                         HABITAT ZERO                            │
│                                                                 │
│   ┌──────────────┐    HTTP/REST    ┌────────────────────────┐   │
│   │   Android    │ ◄────────────► │    Spring Boot API     │   │
│   │   Mobile     │                │  (Controller / Service  │   │
│   │   App        │                │     / Repository)       │   │
│   └──────────────┘                └───────────┬────────────┘   │
│                                               │ JPA/Hibernate   │
│   ┌──────────────┐   MQTT / HTTP  ┌───────────▼────────────┐   │
│   │    ESP32     │ ──────────────► │       MySQL            │   │
│   │  (Sensores   │                │  (Estufa / Planta /    │   │
│   │   IoT)       │                │  Sensor / Colono)      │   │
│   └──────────────┘                └────────────────────────┘   │
│                                                                 │
│   [Spring Security + JWT] ──► Todas as rotas protegidas        │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🗃️ Módulo 1 — Banco de Dados (MySQL)

### Diagrama de Entidades

```
┌───────────────────┐         ┌──────────────────────┐
│      COLONO       │         │       ESTUFA          │
├───────────────────┤  N:1    ├──────────────────────┤
│ id (PK)           ├────────►│ id (PK)               │
│ nome              │         │ nome                  │
│ email (UNIQUE)    │         │ localizacao           │
│ senha_hash        │         │ capacidade_m2         │
│ cargo             │         │ status                │
│ estufa_id (FK)    │         └──────────┬───────────┘
└───────────────────┘                    │ 1
                                         │
              ┌──────────────────────────┴──────────────────────┐
              │ N                                               │ N
┌─────────────▼──────────────┐          ┌────────────────────────┐
│          PLANTA             │          │    SENSOR_AMBIENTE     │
├────────────────────────────┤          ├────────────────────────┤
│ id (PK)                    │          │ id (PK)                │
│ nome_cientifico             │          │ tipo_sensor            │
│ fase_crescimento            │          │ valor_leitura          │
│ data_plantio                │          │ unidade                │
│ estufa_id (FK)             │          │ timestamp              │
└────────────────────────────┘          │ estufa_id (FK)         │
                                        └────────────────────────┘
```

### Relacionamentos

| Relação | Cardinalidade | Descrição |
|---|---|---|
| Colono → Estufa | N:1 | Um colono é responsável por uma estufa; uma estufa tem vários colonos |
| Planta → Estufa | N:1 | Uma estufa abriga múltiplas plantas |
| Sensor_Ambiente → Estufa | N:1 | Uma estufa possui múltiplos sensores e leituras |

### Script DDL Principal

O script completo está em [`db/script_habitat_zero.sql`](db/script_habitat_zero.sql). Resumo das tabelas:

| Tabela | Colunas principais |
|--------|-------------------|
| `estufa` | `id`, `nome`, `localizacao`, `capacidade_m2`, `status` (`ATIVA`/`INATIVA`/`MANUTENCAO`), thresholds de O₂, umidade, radiação e temperatura |
| `colono` | `id`, `nome`, `email` (único), `senha_hash`, `cargo` (`AGRONOMISTA`/`COMANDANTE`/`ENGENHEIRO`/`MEDICO`/`TECNICO`), `estufa_id` |
| `planta` | `id`, `nome_cientifico`, `nome_comum`, `fase_crescimento` (`SEMENTE`/`GERMINACAO`/`CRESCIMENTO`/`MATURACAO`/`COLHEITA`), `data_plantio`, `estufa_id` |
| `sensor_ambiente` | `id`, `tipo_sensor` (`OXIGENIO`/`UMIDADE_SOLO`/`RADIACAO_EXTERNA`/`TEMPERATURA`), `valor_leitura`, `unidade` (`PERCENTUAL`/`MSV_HORA`/`CELSIUS`), `timestamp`, `estufa_id` |
| `alerta` | `id`, `severidade` (`ATENCAO`/`CRITICO`/`EMERGENCIA`), `mensagem`, `tipo_sensor`, `valor_registrado`, `criado_em`, `resolvido`, `resolvido_em`, `estufa_id` |

### Consultas de Simulação Espacial

```sql
-- Leituras críticas de O₂ nas últimas 24 horas
SELECT s.estufa_id, e.nome, s.tipo_sensor, s.valor_leitura, s.timestamp
FROM Sensor_Ambiente s
JOIN Estufa e ON s.estufa_id = e.id
WHERE s.tipo_sensor = 'OXIGENIO'
  AND s.valor_leitura < 19.5
  AND s.timestamp >= NOW() - INTERVAL 24 HOUR
ORDER BY s.timestamp DESC;

-- Histórico de umidade por estufa (média diária)
SELECT estufa_id, DATE(timestamp) AS dia, AVG(valor_leitura) AS media_umidade
FROM Sensor_Ambiente
WHERE tipo_sensor = 'UMIDADE_SOLO'
GROUP BY estufa_id, DATE(timestamp)
ORDER BY dia DESC;

-- Plantas em fase de colheita com estufas ativas
SELECT p.nome_cientifico, p.data_plantio, e.nome AS estufa
FROM Planta p
JOIN Estufa e ON p.estufa_id = e.id
WHERE p.fase_crescimento = 'COLHEITA'
  AND e.status = 'ATIVA';
```

---

## ☕ Módulo 2 — API RESTful (Spring Boot)

### Stack Técnica

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 21 | Linguagem principal |
| Spring Boot | 4.x | Framework web |
| Spring Data JPA | 3.x | ORM / persistência |
| Spring Security | 6.x | Autenticação e autorização |
| MySQL Connector | 9.6 | Driver JDBC |
| Springdoc OpenAPI | 2.x | Documentação Swagger |
| Lombok | latest | Redução de boilerplate |
| Bean Validation | 3.x | Validação de inputs |

### Estrutura de Pacotes

```
src/main/java/br/com/gs/habitatzero/
├── config/
│   ├── SecurityConfig.java        # Configuração do Spring Security
│   └── OpenApiConfig.java         # Configuração do Springdoc OpenAPI
├── controller/
│   ├── AuthController.java        # POST /auth/login
│   ├── ColonoController.java      # CRUD de colonos
│   ├── EstufaController.java      # CRUD de estufas
│   ├── PlantaController.java      # CRUD de plantas
│   ├── SensorController.java      # Recebimento de leituras IoT
│   └── AlertaController.java      # Consulta e resolução de alertas
├── dto/
│   ├── request/
│   │   ├── LoginRequest.java
│   │   ├── ColonoRequest.java
│   │   ├── EstufaRequest.java
│   │   ├── PlantaRequest.java
│   │   └── SensorLeituraRequest.java
│   └── response/
│       ├── TokenResponse.java
│       ├── ColonoResponse.java
│       ├── EstufaResponse.java
│       ├── PlantaResponse.java
│       ├── SensorLeituraResponse.java
│       └── AlertaResponse.java
├── entity/
│   ├── Estufa.java
│   ├── Planta.java
│   ├── SensorAmbiente.java
│   ├── Colono.java
│   └── Alerta.java
├── repository/
│   ├── ColonoRepository.java
│   ├── EstufaRepository.java
│   ├── PlantaRepository.java
│   ├── SensorAmbienteRepository.java
│   └── AlertaRepository.java
├── service/
│   ├── AuthService.java
│   ├── ColonoService.java
│   ├── EstufaService.java
│   ├── PlantaService.java
│   ├── SensorService.java
│   └── AlertaService.java         # Lógica de disparo de alertas
└── secutiry/
    ├── JwtUtil.java
    ├── JwtAuthFilter.java
    └── ColonoUserDetailsService.java
```

### Endpoints da API

| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| `POST` | `/auth/login` | Autentica colono, retorna JWT | ❌ |
| `GET` | `/colonos` | Lista todos os colonos | ✅ |
| `GET` | `/colonos/{id}` | Busca colono por ID | ✅ |
| `POST` | `/colonos` | Cadastra novo colono | ✅ |
| `DELETE` | `/colonos/{id}` | Remove colono | ✅ |
| `GET` | `/estufas` | Lista todas as estufas | ✅ |
| `GET` | `/estufas/{id}` | Busca estufa por ID | ✅ |
| `POST` | `/estufas` | Cadastra nova estufa | ✅ |
| `PUT` | `/estufas/{id}` | Atualiza configurações da estufa | ✅ |
| `DELETE` | `/estufas/{id}` | Remove estufa | ✅ |
| `GET` | `/plantas` | Lista plantas em cultivo | ✅ |
| `GET` | `/plantas/{id}` | Busca planta por ID | ✅ |
| `POST` | `/plantas` | Adiciona planta a uma estufa | ✅ |
| `PUT` | `/plantas/{id}` | Atualiza dados da planta | ✅ |
| `DELETE` | `/plantas/{id}` | Remove planta | ✅ |
| `POST` | `/sensores/leitura` | Recebe leitura do ESP32 (IoT) | ❌ |
| `GET` | `/sensores/leituras` | Consulta últimas leituras | ✅ |
| `GET` | `/alertas` | Lista alertas ativos | ✅ |
| `GET` | `/alertas/estufa/{id}` | Lista alertas por estufa | ✅ |
| `PATCH` | `/alertas/{id}/resolver` | Marca alerta como resolvido | ✅ |

> Documentação interativa disponível em: `http://localhost:8080/swagger-ui/index.html`

### Lógica de Alerta — AlertaService

O `AlertaService` avalia cada leitura recebida contra os thresholds configurados por estufa e persiste um `Alerta` quando necessário:

| Sensor | Condição | Severidade |
|--------|----------|-----------|
| `OXIGENIO` | valor < `thresholdOxigenioMin` (padrão 19,5%) | `ATENCAO` / `CRITICO` / `EMERGENCIA` (por desvio) |
| `UMIDADE_SOLO` | valor < `thresholdUmidadeMin` (padrão 30%) | `ATENCAO` |
| `RADIACAO_EXTERNA` | valor > `thresholdRadiacaoMax` (padrão 2,0 mSv/h) | `ATENCAO` / `CRITICO` / `EMERGENCIA` (por múltiplo) |
| `TEMPERATURA` | valor > `thresholdTemperaturaMax` (padrão 40°C) | `CRITICO` |

Os thresholds são personalizáveis por estufa via `PUT /estufas/{id}`. O código completo está em `backend/src/main/java/br/com/gs/habitatzero/service/AlertaService.java`.

### application.properties

```properties
# Banco de dados
spring.datasource.url=jdbc:mysql://localhost:3307/habitat_zero?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# JWT
jwt.secret=HabitatZeroSecretKey2026FIAPGlobalSolution!SuperSegura
jwt.expiration=28800000

# Swagger
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html

# Porta
server.port=8080
```

---

## 🔐 Módulo 3 — Segurança (Spring Security)

### Estratégia de Autenticação

O sistema utiliza autenticação stateless baseada em **JWT (JSON Web Token)**:

```
1. Cliente → POST /auth/login  { email, senha }
2. API valida credenciais no banco (BCrypt.matches)
3. API gera JWT com userId + cargo + expiração (8h)
4. Cliente armazena o token
5. Toda requisição subsequente: Authorization: Bearer <token>
6. JwtAuthFilter valida o token antes de cada controller
```

### Criptografia de Senhas

```java
// Configuração do BCrypt (fator de custo 10 — padrão recomendado)
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(10);
}

// Uso no cadastro:
colono.setSenhaHash(passwordEncoder.encode(senhaRaw));

// Uso na autenticação:
boolean valido = passwordEncoder.matches(senhaRaw, colono.getSenhaHash());
```

### Práticas de Segurança Implementadas

| Prática | Implementação | Proteção |
|---|---|---|
| **Hash de senha** | `BCryptPasswordEncoder` (custo 10) | Exposição de credenciais |
| **JWT stateless** | `jjwt` library, expiração 8h | Sessões hijack |
| **Bean Validation** | `@NotNull`, `@Min`, `@Max`, `@Pattern`, `@Email` | Inputs inválidos |
| **Queries parametrizadas** | JPA/Hibernate por design | SQL Injection |
| **Sanitização de texto** | Remoção de tags HTML em campos livres | XSS |
| **Validação de range físico** | O₂: 0–100%, Temp: -273–500°C | Payload malicioso |

### Exemplo de Validação no DTO

```java
@Data
public class SensorLeituraRequest {

    @NotNull(message = "ID da estufa é obrigatório")
    private Long estufaId;

    @NotNull(message = "Tipo do sensor é obrigatório")
    private SensorAmbiente.TipoSensor tipoSensor;  // OXIGENIO | UMIDADE_SOLO | RADIACAO_EXTERNA | TEMPERATURA

    @NotNull(message = "Valor da leitura é obrigatório")
    @Min(value = 0, message = "Valor de leitura não pode ser negativo")
    @Max(value = 100000, message = "Valor de leitura fora do range físico aceitável")
    private Double valorLeitura;

    @NotNull(message = "Unidade de medida é obrigatória")
    private SensorAmbiente.UnidadeMedida unidade;  // PERCENTUAL | MSV_HORA | CELSIUS

    private LocalDateTime timestamp;  // opcional — usa horário do servidor se omitido
}
```

---

## 🧪 Módulo 4 — Testes (JUnit 5 + Mockito + JVM)

### Casos de Teste — Backend

A documentação completa está em [`backend/src/test/TEST_CASES.md`](backend/src/test/TEST_CASES.md).

**Testes unitários backend (JUnit 5 + Mockito) — sem banco de dados:**

| ID | Cenário | Status |
|---|---|---|
| TC-01 | Criação de colono com dados válidos | ✅ PASS |
| TC-02 | Criação de colono com e-mail duplicado → `BusinessException` | ✅ PASS |
| TC-03 | Criação de estufa com thresholds padrão | ✅ PASS |
| TC-04 | Busca de estufa por ID inexistente → `ResourceNotFoundException` | ✅ PASS |
| TC-05 | Leitura de sensor dentro dos limites — sem alerta | ✅ PASS |
| TC-06 | O₂ abaixo do threshold → alerta criado | ✅ PASS |
| TC-07 | Resolução de alerta ativo → `resolvido=true` | ✅ PASS |
| TC-08 | Login com senha incorreta → `BadCredentialsException` | ✅ PASS |

**Testes de integração IoT (pytest) — requerem backend em execução:**

| ID | Cenário | Status |
|---|---|---|
| IT-01 | Payload do simulador tem campos e enums corretos | ✅ PASS |
| IT-02 | `POST /sensores/leitura` com payload válido → HTTP 200/201 | ✅ PASS |
| IT-03 | `POST /sensores/leitura` sem `estufaId` → HTTP 400 | ✅ PASS |
| IT-04 | Leitura persistida — resposta contém `id` e `estufaId` | ✅ PASS |

### Casos de Teste — Frontend Android

A documentação completa está em [`frontend/TEST_CASES.md`](frontend/TEST_CASES.md).

**Testes unitários frontend (JVM puro) — sem emulador ou banco de dados:**

| ID | Cenário | Status |
|---|---|---|
| TC-FE-01 | Conversão de leituras IoT com todos os tipos → `SensorAmbiente` correto | ✅ PASS |
| TC-FE-02 | Leituras duplicadas do mesmo sensor → usa a mais recente | ✅ PASS |
| TC-FE-03 | Lista de leituras vazia → `SensorAmbiente` com zeros, sem crash | ✅ PASS |
| TC-FE-04 | Avanço de fase de planta → sequência correta, `null` após COLHEITA | ✅ PASS |
| TC-FE-05 | Buffer de histórico do Dashboard → descarta mais antiga ao atingir 20 | ✅ PASS |

### Executar os Testes

```bash
# Testes unitários backend (sem banco)
cd backend && ./mvnw test

# Testes unitários frontend (JVM, sem emulador)
cd frontend && ./gradlew :app:test

# Testes de integração IoT (requer backend + MySQL em execução)
cd IoT && pip install pytest requests && pytest test_integration.py -v
```

---

## 📱 Módulo 5 — Front-end Mobile (Android)

### Stack Técnica

| Tecnologia | Versão | Uso |
|---|---|---|
| Kotlin | — | Linguagem principal |
| Android SDK | API 26–37 | Plataforma mobile |
| AGP | 9.2.1 | Android Gradle Plugin |
| Retrofit 2 | 2.9.0 | Cliente HTTP para a API REST |
| OkHttp | 4.9.3 | Interceptor de logging e JWT |
| Coroutines | 1.7.3 | Requisições assíncronas |
| ViewModel + LiveData | 2.6.2 | Arquitetura MVVM |
| Fragment KTX | 1.6.2 | Single-Activity com Fragments |
| SharedPreferences | — | Persistência local (switches, histórico, token) |
| MPAndroidChart | v3.1.0 | Gráficos de linha para sensores |
| Firebase Messaging | 23.0.0 | Push notifications |
| Material Design 3 | — | UI Components (dark theme) |
| Lottie | 6.0.0 | Animações |

### Arquitetura

O aplicativo segue o padrão **Single-Activity MVVM** com Fragments:

```
MainActivity (host único com BottomNavigationView — 4 abas)
    └── FragmentContainerView
         ├── DashboardFragment  → DashboardViewModel  → Repository → API
         ├── EstufasFragment    → EstufasViewModel    → Repository → API
         ├── AlertasFragment    → AlertasViewModel    → Repository → API
         └── ProfileFragment    → ProfileViewModel    → Repository → API

Telas de detalhe (Activities separadas, abertas por Intent):
    EstufaDetailActivity      → EstufaDetailViewModel      → Repository → API
    ControleClimaticoActivity → ControleClimaticoViewModel → Repository → API
    HistoricoActivity         → SharedPreferences (local)
```

### Telas do Aplicativo

| Tela | Tipo | Descrição | Endpoints usados |
|---|---|---|---|
| **Login** | Activity | Autenticação com email/senha; token JWT e email salvos em SharedPreferences; sessão persiste entre aberturas do app | `POST /auth/login` |
| **Dashboard** | Fragment | Gráficos em tempo real dos 4 sensores, polling a cada 5s, histórico das últimas 20 leituras | `GET /sensores/leituras` |
| **Estufas** | Fragment | Lista de estufas com status colorido, total de plantas e alertas ativos | `GET /estufas` |
| **Alertas** | Fragment | Alertas ativos globais com código de cores por severidade, botão "✓ Resolver" por item | `GET /alertas`, `PATCH /alertas/{id}/resolver` |
| **Perfil** | Fragment | Dados do colono logado (email, iniciais geradas), métricas da missão (sols, eficiência das estufas ao vivo), configurações do sistema, logout com confirmação | `GET /estufas` |
| **Detalhe da Estufa** | Activity | Central de administração da estufa: info, painel de sistemas da colônia (simulado), controles de ventilação/irrigação, gerenciamento de plantas, alertas da estufa | `GET /plantas`, `POST /plantas`, `PUT /plantas/{id}`, `GET /alertas/estufa/{id}`, `PATCH /alertas/{id}/resolver` |
| **Controle Climático** | Activity | Ajuste dos thresholds de temperatura e umidade via sliders | `PUT /estufas/{id}` |
| **Histórico** | Activity | Registro local dos comandos de ventilação/irrigação executados | SharedPreferences |

### Navegação

```
LoginActivity (splash + auto-login se token salvo)
    └── MainActivity (Single-Activity host — 4 abas no Bottom Navigation)
         ├── [tab Dashboard]  ── DashboardFragment    (polling 5s)
         ├── [tab Estufas]    ── EstufasFragment
         │                          └── [tap estufa] ── EstufaDetailActivity
         │                                                  └── [btn thresholds] ── ControleClimaticoActivity
         │                                                  └── [btn histórico]  ── HistoricoActivity
         ├── [tab Alertas]    ── AlertasFragment
         └── [tab Perfil]     ── ProfileFragment
```

### Funcionalidades de Administração

**Reais (chamadas à API):**
- Resolver alertas diretamente do app com animação de remoção
- Listar plantas por estufa com chip de fase colorido
- Avançar fase de crescimento de uma planta (SEMENTE → … → COLHEITA)
- Adicionar nova planta via dialog com formulário
- Ajustar thresholds de alerta da estufa

**Simuladas (feedback local):**
- Painel de Sistemas da Colônia: pressurização, energia solar, link orbital e filtro CO₂ com valores animados a cada 3s
- Ventilação forçada e irrigação automática: toggle com spinner de 1.5s simulando acionamento, confirmação via Snackbar, estado persistido por estufa

### Autenticação JWT

- Token e email salvos em `SharedPreferences` (`HabitatZeroPrefs` → `token`, `user_email`) após login bem-sucedido
- `LoginActivity` verifica o token no `onCreate` — se existir, pula direto para `MainActivity`
- `JWTInterceptor` injeta `Authorization: Bearer <token>` em todas as requisições autenticadas
- `AuthInterceptor` captura HTTP 401, limpa o token e redireciona para login (executado no main thread via `Handler`)

### Configuração do Emulador vs Dispositivo Físico

**Arquivo:** `frontend/app/src/main/java/com/workwell/habitatzero/api/RetrofitClient.kt`

```kotlin
// Emulador Android (10.0.2.2 mapeia para localhost da máquina host)
private const val BASE_URL = "http://10.0.2.2:8080/"

// Dispositivo físico na mesma rede Wi-Fi (substituir pelo IP da máquina)
private const val BASE_URL = "http://192.168.X.X:8080/"
```

### Dependências principais (build.gradle.kts)

```kotlin
dependencies {
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.airbnb.android:lottie:6.0.0")
    implementation("com.google.firebase:firebase-messaging:23.0.0")
}
```

---

## 🐳 Docker — Execução com Contêineres

A forma mais rápida de rodar o backend completo é via Docker Compose. Um único comando sobe o MySQL, a API Spring Boot e o simulador IoT — sem instalar Java, Maven ou MySQL na máquina.

### Pré-requisitos

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (inclui Docker Engine + Compose)

### Subindo tudo

```bash
# Na raiz do projeto (onde está o docker-compose.yml)
docker compose up --build
```

Na primeira execução o Maven baixa as dependências e compila o JAR dentro do contêiner (~3–5 min). As execuções seguintes usam cache e sobem em segundos.

### Serviços e portas

| Serviço | Contêiner | Porta host | Descrição |
|---|---|---|---|
| MySQL 8.4 | `habitatzero-mysql` | `3307` | Banco de dados |
| Spring Boot API | `habitatzero-backend` | `8080` | API REST + Swagger |
| Simulador IoT | `habitatzero-iot` | — | Envia leituras a cada 5s |

**URLs após o `docker compose up`:**
- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

### Sequência de inicialização

O Compose garante a ordem correta via health checks:

```
MySQL (healthy) → Backend Spring Boot (healthy) → Simulador IoT
```

O backend aguarda o MySQL aceitar conexões antes de iniciar. O simulador aguarda o backend estar respondendo na `/api-docs`.

### Comandos úteis

```bash
# Subir em background (sem travar o terminal)
docker compose up --build -d

# Ver logs de todos os serviços
docker compose logs -f

# Ver logs só do backend
docker compose logs -f backend

# Parar tudo (preserva o volume do banco)
docker compose down

# Parar e apagar o banco (reset total)
docker compose down -v

# Rebuild apenas de um serviço
docker compose up --build backend
```

### Conectando o app Android ao backend em contêiner

O backend fica exposto em `localhost:8080` da máquina host. Para o app Android conectar:

**Emulador (AVD):**
```kotlin
// RetrofitClient.kt
private const val BASE_URL = "http://10.0.2.2:8080/"
```

**Dispositivo físico:**
```kotlin
private const val BASE_URL = "http://SEU_IP_LOCAL:8080/"
// Ex: "http://192.168.1.10:8080/"
```

Para descobrir seu IP local: `ipconfig` (Windows) ou `hostname -I` (Linux/macOS).

---

## 🔌 Módulo 6 — IoT (ESP32)

### Sensores e Thresholds

| Sensor | Variável Medida | Unidade | Range Normal | Threshold de Alerta |
|---|---|---|---|---|
| Sensor de O₂ | Percentual de oxigênio interno | % | 19,5 – 23,0 | < 19,5% → **CRÍTICO** |
| Sensor de Umidade | Umidade volumétrica do solo | % | 40 – 70 | < 30% → **ATENÇÃO** |
| Sensor de Radiação | Dose de radiação ionizante externa | mSv/h | 0 – 1,5 | > 2,0 mSv/h → **ATENÇÃO** |
| Sensor de Temperatura | Temperatura interna da estufa | °C | 15 – 40 | > 40°C → **CRÍTICO** |

### Payload JSON Publicado pelo ESP32

```json
{
  "estufaId": 1,
  "tipoSensor": "OXIGENIO",
  "valorLeitura": 18.2,
  "unidade": "PERCENTUAL",
  "timestamp": "2026-06-01T14:30:00"
}
```

Os quatro tipos de sensor suportados e suas unidades:

| `tipoSensor` | `unidade` |
|---|---|
| `OXIGENIO` | `PERCENTUAL` |
| `UMIDADE_SOLO` | `PERCENTUAL` |
| `RADIACAO_EXTERNA` | `MSV_HORA` |
| `TEMPERATURA` | `CELSIUS` |

---

## 🚀 Como Executar o Projeto

> **Recomendado:** Use Docker Compose para subir MySQL + backend + simulador IoT com um único comando — veja a seção [🐳 Docker](#-docker--execução-com-contêineres) acima.
>
> As instruções abaixo descrevem a execução **manual** (sem Docker), útil para desenvolvimento do backend ou quando não se tem Docker disponível.

### Pré-requisitos (execução manual)

| Ferramenta | Versão mínima | Uso |
|---|---|---|
| Java (JDK) | 21 | Compilar e executar o backend |
| Maven | 3.8+ | Build do backend (ou usar o wrapper `./mvnw`) |
| MySQL | 8.0+ (porta 3307) | Banco de dados |
| Android Studio | Hedgehog (2023.1.1) ou superior | Abrir e rodar o projeto Android |
| Python | 3.x | Simulador de sensores IoT (opcional) |

> **Atenção:** O backend está configurado para MySQL na **porta 3307** (não a padrão 3306). Confirme sua instalação do MySQL ou ajuste `application.properties` antes de iniciar.

---

### 1. Banco de Dados (MySQL)

O banco é criado automaticamente pelo Spring Boot no primeiro start (via `createDatabaseIfNotExist=true`). Não é necessário executar o script manualmente para subir o projeto, mas caso queira inicializar com o schema explícito:

```bash
# Conecte no MySQL (ajuste o host/porta conforme necessário)
mysql -u root -p --port=3307

# Dentro do MySQL:
source db/script_habitat_zero.sql
```

**Credenciais padrão** (configuráveis em `backend/src/main/resources/application.properties`):
```
Host:     localhost:3307
Database: habitat_zero
User:     root
Password: root
```

---

### 2. Backend (Spring Boot)

```bash
# Entre na pasta do backend
cd backend

# (Opcional) Edite as credenciais do banco se necessário:
# src/main/resources/application.properties

# Execute o backend com o Maven Wrapper (não requer Maven instalado)
./mvnw spring-boot:run
# No Windows:
mvnw.cmd spring-boot:run
```

O backend estará disponível em:
- **API:** `http://localhost:8080`
- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:8080/api-docs`

> O primeiro start pode ser lento (~30–60s) enquanto o Maven baixa as dependências e o Hibernate cria as tabelas.

---

### 3. Simulador IoT (opcional — sem hardware ESP32)

O endpoint `POST /sensores/leitura` é público (sem autenticação), permitindo simular leituras de sensores sem um ESP32 físico.

```bash
# Entre na pasta de IoT
cd IoT

# Instale a dependência
pip install requests

# Execute o simulador (requer backend em execução)
python esp32_simulator.py
```

O simulador envia leituras aleatórias para todas as estufas cadastradas. Para rodar os testes de integração:

```bash
pip install pytest requests
pytest test_integration.py -v
```

---

### 4. Aplicativo Android (Frontend Mobile)

**Pré-requisito Firebase:** O projeto usa Firebase Cloud Messaging para push notifications. Você precisará de um arquivo `google-services.json` válido:
- Crie um projeto no [Firebase Console](https://console.firebase.google.com/)
- Adicione um app Android com package name `com.workwell.habitatzero`
- Baixe o `google-services.json` gerado
- Coloque o arquivo em `frontend/app/google-services.json`

> **Sem Firebase:** O build ainda funciona se você remover a dependência `firebase-messaging` do `build.gradle.kts` e a classe `MyFirebaseMessagingService` do `AndroidManifest.xml`. As push notifications serão desativadas, mas o app funcionará normalmente.

**Passos:**

1. Abra o Android Studio → `File > Open` → selecione a pasta `frontend/`

2. Aguarde o Gradle sync terminar (pode levar alguns minutos na primeira vez)

3. Configure o endereço do backend em `frontend/app/src/main/java/com/workwell/habitatzero/api/RetrofitClient.kt`:

   ```kotlin
   // Se rodar em emulador (Android Virtual Device):
   private const val BASE_URL = "http://10.0.2.2:8080/"

   // Se rodar em dispositivo físico (substitua pelo IP da sua máquina):
   private const val BASE_URL = "http://192.168.X.X:8080/"
   ```

   Para descobrir o IP da sua máquina:
   - Windows: `ipconfig` → campo "Endereço IPv4"
   - Linux/macOS: `hostname -I`

4. Clique em **Run ▶** (ou `Shift+F10`) para compilar e instalar no emulador/dispositivo

5. O emulador precisa ser **API 26+** (Android 8.0 Oreo). Para criar um AVD: `Tools > Device Manager > Create Virtual Device`

**Credencial de teste:** Crie um colono via `POST /colonos` pelo Swagger antes de fazer login no app:

```json
{
  "nome": "Comandante Silva",
  "email": "silva@habitatzero.br",
  "senha": "Senha@123",
  "cargo": "COMANDANTE"
}
```

---

### 5. Testes

```bash
# Testes unitários do backend (sem banco de dados)
cd backend && ./mvnw test

# Testes unitários do frontend (JVM, sem emulador)
cd frontend && ./gradlew :app:test

# Testes de integração IoT (requer backend + MySQL em execução)
cd IoT && pip install pytest requests && pytest test_integration.py -v
```

---

## 👥 Equipe

<table>
  <tr>
    <th>Nome</th>
    <th>RM</th>
    <th>Turma</th>
  </tr>
  <tr>
    <td>Gabriel Genaro Dalaqua</td>
    <td>551986</td>
    <td>4ESOA</td>
  </tr>
  <tr>
    <td>Alairton Rocha Scabelli </td>
    <td>551454</td>
    <td>4ESOA</td>
  </tr>
  <tr>
    <td>Carolina Nascimento Amorim</td>
    <td>97930</td>
    <td>4ESOA</td>
  </tr>
  <tr>
    <td>Eduardo Marins</td>
    <td>551892</td>
    <td>4ESOA</td>
  </tr>
  <tr>
    <td>Sarah Ribeiro da Silva</td>
    <td>97747</td>
    <td>4ESOA</td>
  </tr>
</table>


---

## 📦 Estrutura do .zip de Entrega

```
habitatzero-entrega.zip
├── backend/                  # Código-fonte Spring Boot completo
├── frontend/                    # Projeto Android Studio completo (Kotlin)
├── IoT/                      # Firmware ESP32 + simulador Python
├── db/                       # script_habitat_zero.sql + consultas
├── README.md                 # Este arquivo
├── documento.pdf             # Diagrama ER + casos de teste + prints + IoT
└── grupo.txt                 # Nome do grupo, RMs, nomes e link do Vídeo Pitch
```

---

<div align="center">

**FIAP — Engenharia de Software — 4º Ano — Global Solution 2026/1**

</div>
