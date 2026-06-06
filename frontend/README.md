# HabitatZero

![compileSdk](https://img.shields.io/badge/compileSdk-37-blue)
![minSdk](https://img.shields.io/badge/minSdk-26-blue)
![targetSdk](https://img.shields.io/badge/targetSdk-37-blue)
![Kotlin](https://img.shields.io/badge/Kotlin-2.4.0-purple)
![License](https://img.shields.io/badge/license-MIT-lightgrey)

Aplicativo Android nativo para **monitoramento e controle de estufas agrícolas**. Conecta-se a um backend Spring Boot via HTTP, exibe leituras em tempo real de sensores (temperatura, umidade, oxigênio, radiação), permite ajustar parâmetros climáticos remotamente e recebe alertas push via Firebase Cloud Messaging quando os valores saem dos limites seguros.

---

## Screenshots

<!-- Adicione screenshots após executar o app -->
<!--
| Dashboard | Controle Climático | Alertas |
|-----------|-------------------|---------|
| ![dashboard](docs/screen_dashboard.png) | ![climate](docs/screen_climate.png) | ![alerts](docs/screen_alerts.png) |
-->

---

## Arquitetura

O projeto segue o padrão **MVVM + Repository**:

```
LoginActivity / DashboardActivity / ...
        │  observa LiveData
        ▼
   ViewModel (viewModelScope + coroutines)
        │  chama
        ▼
   HabitatZeroRepository
        │  delega para
        ├─► RetrofitClient → API REST (Spring Boot)
        └─► AppDatabase (Room) → persistência local
```

- **Camada UI** — Activities observam `LiveData` exposta pelos ViewModels. Nenhuma lógica de negócio vive nas Activities.
- **Camada ViewModel** — Chama o repositório e expõe `LiveData<T>` para a UI. Usa `viewModelScope` + coroutines para operações assíncronas.
- **Camada Repository** — Fonte única de verdade; abstrai Retrofit (remoto) e Room (local).
- **Camada de dados** — Entidades Room: `HistoricoItem`, `ClimaConfig`. DAOs usam `suspend functions`.

---

## Tech Stack

| Componente | Biblioteca / Versão |
|---|---|
| Linguagem | Kotlin 2.4.0 |
| Min SDK | 26 (Android 8.0 Oreo) |
| Target/Compile SDK | 37 |
| HTTP Client | Retrofit 2.9.0 + OkHttp logging-interceptor 4.9.3 |
| Parsing JSON | Gson (converter-gson 2.9.0) |
| Banco local | Room 2.6.1 |
| Async | Kotlin Coroutines 1.7.3 |
| ViewModel / LiveData | Lifecycle 2.6.2 |
| Push Notifications | Firebase Cloud Messaging 23.0.0 |
| Gráficos | MPAndroidChart v3.1.0 |
| Animações | Lottie 6.0.0 |
| UI | Material Components 1.14.0 (Material 3 DayNight) |
| Build System | Gradle (Kotlin DSL) |

---

## Pré-requisitos

- **Java 11** (configurado como `sourceCompatibility` e `targetCompatibility` em `app/build.gradle.kts`)
- **Android Studio Hedgehog (2023.1.1) ou mais recente**
- **Android SDK 37** instalado via SDK Manager
- **Emulador ou dispositivo físico rodando API 26+**
- **Backend HabitatZero rodando** (Spring Boot, porta padrão 8080)
- **Projeto Firebase** com um app Android registrado (para FCM)

---

## Como Configurar e Rodar

### 1. Clonar o Repositório

```bash
git clone https://github.com/seu-usuario/HabitatZero.git
cd HabitatZero
```

### 2. Abrir no Android Studio

- Abra o Android Studio.
- Escolha **File → Open** e selecione a pasta `HabitatZero` (a que contém `settings.gradle.kts`).
- Aguarde o Gradle sync concluir. Se falhar, verifique se o Android SDK 37 está instalado em **SDK Manager → SDK Platforms**.

### 3. Configurar o Firebase (`google-services.json`)

1. No [Firebase Console](https://console.firebase.google.com/), abra seu projeto.
2. Vá em **Configurações do projeto → Seus apps** e faça o download de `google-services.json`.
3. Coloque o arquivo em:
   ```
   app/google-services.json
   ```
4. O plugin `google-services` (já declarado em `build.gradle.kts`) o detectará automaticamente no próximo sync.

> Sem esse arquivo o build falhará porque o plugin `com.google.gms.google-services` é obrigatório.

### 4. Configurar a URL do Backend

Abra `app/src/main/java/com/workwell/habitatzero/api/RetrofitClient.kt` e atualize `BASE_URL`:

| Cenário | Valor |
|---|---|
| Emulador Android → backend no localhost | `"http://10.0.2.2:8080/"` (padrão já configurado) |
| Dispositivo físico → máquina na mesma rede | `"http://192.168.x.x:8080/"` (substitua pelo IP da sua máquina) |

> **Atenção:** `android:usesCleartextTraffic="true"` está ativo no `AndroidManifest.xml` para facilitar o desenvolvimento local. Remova ou restrinja essa configuração antes de publicar em produção.

### 5. Rodar o Backend

O app Android depende de um backend Spring Boot. Inicie-o antes de abrir o app:

```bash
# Na raiz do projeto backend
./mvnw spring-boot:run
# ou, se usar Gradle
./gradlew bootRun
```

O backend deve estar acessível na `BASE_URL` configurada no passo anterior.

### 6. Build e Instalação

```bash
# Apenas gerar o APK de debug
./gradlew assembleDebug

# Build + instalar diretamente no dispositivo/emulador conectado
./gradlew installDebug
```

O APK gerado fica em `app/build/outputs/apk/debug/app-debug.apk`.

### 7. Rodar os Testes

```bash
# Testes unitários (JVM)
./gradlew test

# Testes instrumentados (requer dispositivo ou emulador conectado)
./gradlew connectedAndroidTest
```

Fontes dos testes:
- `app/src/test/` — testes unitários
- `app/src/androidTest/` — testes instrumentados / Espresso

---

## Estrutura do Projeto

```
HabitatZero/
├── app/
│   ├── build.gradle.kts
│   ├── google-services.json          ← baixe do Firebase Console (não versionado)
│   ├── proguard-rules.pro
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           └── java/com/workwell/habitatzero/
│               ├── App.kt                          # Application subclass (singleton)
│               ├── MyFirebaseMessagingService.kt   # FCM — token refresh e recepção de mensagens
│               ├── adapter/
│               │   ├── AlertaAdapter.kt
│               │   ├── EstufaAdapter.kt
│               │   └── HistoricoAdapter.kt
│               ├── api/
│               │   ├── ApiService.kt               # Interface Retrofit
│               │   ├── AuthInterceptor.kt          # 401 → redireciona para Login
│               │   ├── JWTInterceptor.kt           # Injeta Bearer token em todas as requisições
│               │   └── RetrofitClient.kt           # Singleton OkHttp + Retrofit
│               ├── data/
│               │   ├── AppDatabase.kt              # Singleton Room
│               │   ├── ClimaConfig.kt              # Entidade Room
│               │   ├── ClimaConfigDao.kt           # DAO (suspend functions)
│               │   ├── HistoricoDao.kt             # DAO (suspend functions)
│               │   └── HistoricoItem.kt            # Entidade Room
│               ├── model/
│               │   ├── Alerta.kt
│               │   ├── Estufa.kt
│               │   ├── LoginDTO.kt
│               │   ├── SensorAmbiente.kt
│               │   └── TokenDTO.kt
│               ├── repository/
│               │   └── HabitatZeroRepository.kt
│               ├── ui/
│               │   ├── AlertasActivity.kt
│               │   ├── ControleClimaticoActivity.kt
│               │   ├── DashboardActivity.kt
│               │   ├── EstufasActivity.kt
│               │   ├── HistoricoActivity.kt
│               │   ├── LoginActivity.kt
│               │   ├── MainActivity.kt
│               │   └── SplashActivity.kt
│               └── viewmodel/
│                   ├── ControleClimaticoViewModel.kt
│                   ├── DashboardViewModel.kt
│                   ├── DashboardViewModelFactory.kt
│                   ├── EstufasViewModel.kt
│                   └── LoginViewModel.kt
├── gradle/
│   └── libs.versions.toml            # Catálogo de versões de dependências
├── build.gradle.kts
├── gradle.properties
├── settings.gradle.kts
└── README.md
```

---

## Funcionalidades

- **Monitoramento em tempo real** — Dashboard consulta `/sensores` a cada 5 segundos e atualiza os cards de temperatura, umidade, oxigênio e radiação com gráficos de linha animados.
- **Alertas visuais** — Cards mudam de cor (verde = normal, vermelho = crítico) com base em limites configurados. Leituras fora do range disparam notificações locais e vibração.
- **Controle climático** — Operadores definem temperatura alvo (15–35 °C) e umidade alvo (30–90 %) via sliders, ativam ventilação e irrigação com switches, e persistem as configurações localmente (Room) e remotamente (API).
- **Gestão de estufas** — Tela de Estufas lista as unidades cadastradas no backend via `/estufas`.
- **Histórico de configurações** — `HistoricoActivity` lê os registros salvos no banco Room local.
- **Notificações push** — Firebase Cloud Messaging entrega alertas disparados pelo backend via `MyFirebaseMessagingService`. O token FCM é sincronizado com o backend após o login.
- **Autenticação JWT** — Login envia credenciais para `/auth/login`, armazena o token retornado em `SharedPreferences` e o anexa como header `Bearer` em todas as requisições. Uma resposta HTTP 401 limpa o token e redireciona para `LoginActivity`.
- **Modo escuro** — Tema segue a configuração do sistema via `Theme.Material3.DayNight`.

---

## Endpoints da API

| Método | Caminho | Descrição |
|--------|---------|-----------|
| `POST` | `/auth/login` | Autenticar; retorna token JWT |
| `POST` | `/auth/token-fcm` | Registrar token FCM do dispositivo |
| `GET` | `/estufas` | Listar todas as estufas |
| `GET` | `/sensores` | Leitura atual dos sensores |
| `PUT` | `/estufas/{id}/clima` | Atualizar parâmetros climáticos da estufa `{id}` |

---

## Limitações Conhecidas / Notas de Desenvolvimento

- **`AlertasActivity` usa dados mock** — a integração com um endpoint real de alertas ainda não foi implementada. Veja o TODO em `AlertasActivity.kt` com os passos necessários.
- **Gráficos exibem apenas a leitura mais recente** — `atualizarGraficos()` cria um dataset de único ponto a cada poll. Um buffer circular é necessário para gráficos com histórico.
- **URL base hardcoded** — `RetrofitClient.kt` usa `http://10.0.2.2:8080/` por padrão. Ajuste para o IP correto ao testar em dispositivo físico.
- **Cleartext traffic ativo** — `android:usesCleartextTraffic="true"` facilita o desenvolvimento com backend local. Deve ser removido antes de um build de produção.
- **Cobertura de testes** — os testes atuais são apenas esqueletos (`ExampleUnitTest`, `ExampleInstrumentedTest`). ViewModels, repositório e DAOs ainda carecem de cobertura.

---

## Contribuindo

Contribuições são bem-vindas. Siga os passos:

1. Faça um fork do repositório.
2. Crie um branch de feature: `git checkout -b feature/minha-feature`.
3. Faça commit das suas mudanças: `git commit -m "feat: descrição da feature"`.
4. Faça push do branch: `git push origin feature/minha-feature`.
5. Abra um Pull Request descrevendo a mudança e como testá-la.

Siga as [convenções de código Kotlin](https://kotlinlang.org/docs/coding-conventions.html) e garanta que `./gradlew test` passe antes de submeter.
