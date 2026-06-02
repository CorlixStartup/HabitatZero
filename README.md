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

Este projeto é dividido em **módulos independentes**, cada um com seu próprio repositório. Este README é compartilhado entre todos eles.

```
habitatzero/
├── backend/      # Backend Spring Boot (este repositório se módulo API)
├── mobile/       # Aplicativo Android Studio
├── iot/          # Firmware ESP32 + simulações de sensores
├── db/           # Scripts SQL (DDL + DML + consultas)
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
│   │    ESP32     │ ──────────────► │       MySQL 8.0        │   │
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

O script completo está em [`db/script.sql`](db/script.sql). Resumo das tabelas:

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

## 🧪 Módulo 4 — Testes (JUnit 5 + Mockito)

### Casos de Teste

A documentação completa está em [`backend/src/test/TEST_CASES.md`](backend/src/test/TEST_CASES.md).

**Testes unitários (JUnit 5 + Mockito) — sem banco de dados:**

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

### Executar os Testes

```bash
# Testes unitários (sem banco)
cd backend && mvn test

# Testes de integração IoT (requer backend + MySQL em execução)
cd IoT && pip install pytest requests && pytest test_integration.py -v
```

---

## 📱 Módulo 5 — Front-end Mobile (Android Studio)

### Telas do Aplicativo

```
┌─────────────────┐    Login    ┌─────────────────────┐   Configurar  ┌──────────────────┐
│   TELA 1        │ ──────────► │     TELA 2          │ ────────────► │    TELA 3        │
│   Login do      │             │  Painel de Controle │               │  Ajustes de      │
│   Colono        │             │  da Estufa          │               │  Clima           │
│                 │             │                     │               │                  │
│ [Email]         │             │ ┌──────────────┐    │               │ O₂:  [slider]    │
│ [Senha]         │             │ │ Estufa Alpha │    │               │ Umid:[slider]    │
│ [ENTRAR]        │             │ │ O₂:  21.2%  ✅│    │               │ Temp:[slider]    │
└─────────────────┘             │ │ Umid: 65%   ✅│    │               │ [SALVAR]         │
                                │ │ Temp: 22°C  ✅│    │               └──────────────────┘
                                │ └──────────────┘    │
                                │ ┌──────────────┐    │
                                │ │ Estufa Beta  │    │
                                │ │ O₂:  18.1%  🔴│    │
                                │ │ ALERTA ATIVO │    │
                                │ └──────────────┘    │
                                └─────────────────────┘
```

### Dependências (build.gradle)

```gradle
dependencies {
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.11.0'
    implementation 'androidx.recyclerview:recyclerview:1.3.2'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'

    // Requisições HTTP para a API
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.12.0'

    // Armazenamento seguro do JWT
    implementation 'androidx.security:security-crypto:1.1.0-alpha06'
}
```

### Estrutura de Navegação

```
LoginActivity
    └── [intent] ──► PainelEstufaActivity (tela principal)
                         └── [intent + estufaId] ──► AjustesClimaActivity
```

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

### Pré-requisitos

- Java 21+
- Maven 3.8+
- MySQL 9.6+
- Android Studio Hedgehog (ou superior)
- Python 3.x (apenas para simulação de sensores)
- Arduino IDE 2.x + board ESP32 (opcional)

### 1. Banco de Dados

```bash
# Criar o banco e executar o script
mysql -u root -p < habitatzero-db/schema.sql
mysql -u root -p habitatzero < habitatzero-db/seed.sql
```

### 2. API Spring Boot

```bash
cd habitatzero-api

# Editar credenciais em src/main/resources/application.properties

# Compilar e executar
./mvnw spring-boot:run

# A API estará disponível em:
# http://localhost:8080
# Swagger: http://localhost:8080/swagger-ui.html
```

### 3. Simulação IoT (sem hardware)

```bash
# O endpoint POST /sensores/leitura é público — não requer autenticação
cd IoT
pip install requests
python3 esp32_simulator.py
```

### 4. Aplicativo Android

```
1. Abrir habitatzero-mobile/ no Android Studio
2. Editar BASE_URL no arquivo RetrofitClient.java com o IP da sua máquina
3. Executar em emulador (API 26+) ou dispositivo físico
```

---

## 👥 Equipe

| Membro | RM | Responsabilidade |
|---|---|---|
| Nome do Dev 1 | RM000001 | Backend — Banco de Dados & Persistência |
| Nome do Dev 2 | RM000002 | Backend — API, Segurança & Integração IoT |
| Nome do Dev 3 | RM000003 | Front-end Mobile Android |
| Nome do Dev 4 | RM000004 | IoT (ESP32) & QA / Testes |

---

## 📦 Estrutura do .zip de Entrega

```
habitatzero-entrega.zip
├── habitatzero-api/          # Código-fonte Spring Boot completo
├── habitatzero-mobile/       # Projeto Android Studio completo
├── habitatzero-iot/          # Firmware ESP32 + simulate_sensors.py
├── habitatzero-db/           # schema.sql + seed.sql + consultas.sql
├── README.md                 # Este arquivo
├── documento.pdf             # Diagrama ER + casos de teste + prints + IoT
└── grupo.txt                 # Nome do grupo, RMs, nomes e link do Vídeo Pitch
```

---

## 📋 Checklist de Entregáveis

- [x] Diagrama ER com 4 entidades e relacionamentos
- [x] Script SQL com `CREATE TABLE`, PKs, FKs e inserts
- [x] Consultas SQL de simulação de uso espacial
- [x] API Spring Boot com 10+ endpoints (GET, POST, PUT, DELETE)
- [x] Arquitetura em camadas: Controller → Service → Repository
- [x] Documentação Swagger em `/swagger-ui.html`
- [x] Plano com 5 casos de teste (cenário, entrada, saída esperada, status)
- [x] Execução de 3+ testes com evidências (logs/prints)
- [x] App Android com 3 telas (Login, Painel, Ajustes de Clima)
- [x] Login com senha BCrypt
- [x] 2+ práticas de segurança (BCrypt + Bean Validation + proteção SQLi/XSS)
- [x] Simulação IoT com 4 sensores (O₂, Umidade, Radiação, Temperatura)
- [x] `README.md` com instruções de execução
- [x] `documento.pdf` com artefatos não-código
- [x] `.zip` com todo o código-fonte
- [x] `grupo.txt` com RMs, nomes e link do Vídeo Pitch
- [x] Vídeo Pitch de até 3 minutos (YouTube/Drive/Vimeo)

---

## 🔗 Conexão com ODS da ONU

Este projeto contribui diretamente para os Objetivos de Desenvolvimento Sustentável:

- **ODS 2** — Fome Zero e Agricultura Sustentável *(produção de alimentos em ambientes extremos)*
- **ODS 9** — Indústria, Inovação e Infraestrutura *(tecnologia espacial aplicada)*
- **ODS 11** — Cidades e Comunidades Sustentáveis *(colonização sustentável fora da Terra)*
- **ODS 13** — Ação Contra a Mudança do Clima *(monitoramento ambiental com dados orbitais)*

---

<div align="center">

**FIAP — Engenharia de Software — 4º Ano — Global Solution 2026/1**

*"Quando ideias ganham propósito, elas têm o poder de transformar realidades."*

</div>
