# Casos de Teste — Habitat Zero Backend

## Testes unitários (JUnit 5 + Mockito)
Execute com: `cd backend && mvn test`

## Testes de integração IoT (pytest)
Execute com: `cd IoT && pytest test_integration.py -v`  
Os testes IT-02, IT-03 e IT-04 requerem o Spring Boot + MySQL em execução. São ignorados automaticamente quando o backend não está acessível.

---

## TC-01 — Criação de colono com dados válidos

| Campo           | Detalhe |
|----------------|---------|
| **Cenário**    | Um novo colono é cadastrado com todos os campos obrigatórios preenchidos corretamente. |
| **Classe**     | `ColonoServiceTest` |
| **Método**     | `criar_comDadosValidos_deveCriarColono` |
| **Entrada**    | `nome="Joao Silva"`, `email="joao@habitat.com"`, `senha="Senha@123"`, `cargo=AGRONOMISTA` |
| **Saída esperada** | `ColonoResponse` com `id=1` e `email="joao@habitat.com"`; repositório chamado uma vez com `save()`. |
| **Status**     | ✅ Passando |

---

## TC-02 — Criação de colono com e-mail duplicado

| Campo           | Detalhe |
|----------------|---------|
| **Cenário**    | Tentativa de cadastrar um colono com um e-mail já existente no sistema. |
| **Classe**     | `ColonoServiceTest` |
| **Método**     | `criar_comEmailExistente_deveDispararBusinessException` |
| **Entrada**    | `email="duplicado@habitat.com"` (já presente no repositório mockado) |
| **Saída esperada** | `BusinessException` lançada; `save()` nunca invocado. |
| **Status**     | ✅ Passando |

---

## TC-03 — Criação de estufa com thresholds padrão

| Campo           | Detalhe |
|----------------|---------|
| **Cenário**    | Uma nova estufa é criada sem informar thresholds customizados; o serviço deve aplicar os valores padrão do sistema. |
| **Classe**     | `EstufaServiceTest` |
| **Método**     | `criar_comDadosValidos_deveCriarEstufa` |
| **Entrada**    | `nome="Estufa Alpha"`, `localizacao="Setor A - Marte"`, `capacidadeM2=500.0` (sem thresholds) |
| **Saída esperada** | `EstufaResponse` com `thresholdOxigenioMin=19.5`, `status=ATIVA`; `save()` chamado uma vez. |
| **Status**     | ✅ Passando |

---

## TC-04 — Busca de estufa com ID inexistente

| Campo           | Detalhe |
|----------------|---------|
| **Cenário**    | Cliente solicita uma estufa cujo ID não existe no banco de dados. |
| **Classe**     | `EstufaServiceTest` |
| **Método**     | `buscarPorId_comIdInexistente_deveDispararResourceNotFoundException` |
| **Entrada**    | `id=999` |
| **Saída esperada** | `ResourceNotFoundException` lançada. |
| **Status**     | ✅ Passando |

---

## TC-05 — Registro de leitura de sensor dentro dos limites

| Campo           | Detalhe |
|----------------|---------|
| **Cenário**    | O ESP32 envia uma leitura de oxigênio dentro do range seguro; nenhum alerta deve ser disparado. |
| **Classe**     | `SensorServiceTest` |
| **Método**     | `registrarLeitura_comDadosValidos_devePersistirLeitura` |
| **Entrada**    | `estufaId=1`, `tipoSensor=OXIGENIO`, `valorLeitura=21.0`, `unidade=PERCENTUAL` |
| **Saída esperada** | `SensorLeituraResponse` com `valorLeitura=21.0` e `alertaDisparado=false`; leitura salva no repositório. |
| **Status**     | ✅ Passando |

---

## TC-06 — Alerta gerado por O₂ abaixo do threshold

| Campo           | Detalhe |
|----------------|---------|
| **Cenário**    | Leitura de oxigênio chega abaixo do limite mínimo configurado na estufa; o serviço deve criar e persistir um alerta. |
| **Classe**     | `AlertaServiceTest` |
| **Método**     | `avaliarLeitura_comOxigenioAbaixoThreshold_deveCriarAlerta` |
| **Entrada**    | Leitura `OXIGENIO` com `valorLeitura=17.0`; estufa com `thresholdOxigenioMin=19.5` |
| **Saída esperada** | Retorna `true`; `alertaRepository.save()` chamado uma vez com entidade `Alerta`. |
| **Status**     | ✅ Passando |

---

## TC-07 — Resolução de alerta ativo

| Campo           | Detalhe |
|----------------|---------|
| **Cenário**    | Um alerta existente e ativo é marcado como resolvido pelo operador. |
| **Classe**     | `AlertaServiceTest` |
| **Método**     | `resolverAlerta_comAlertaAtivo_deveMarcarComoResolvido` |
| **Entrada**    | `alertaId=42` (alerta com `resolvido=false`) |
| **Saída esperada** | `AlertaResponse` com `resolvido=true` e `resolvidoEm` não nulo. |
| **Status**     | ✅ Passando |

---

## TC-08 — Login com senha incorreta

| Campo           | Detalhe |
|----------------|---------|
| **Cenário**    | Usuário tenta autenticar fornecendo uma senha errada. |
| **Classe**     | `AuthServiceTest` |
| **Método**     | `login_comSenhaInvalida_deveDispararException` |
| **Entrada**    | `email="usuario@habitat.com"`, `senha="senhaErrada"` |
| **Saída esperada** | `BadCredentialsException` lançada pelo `AuthenticationManager`; repositório de colonos nunca consultado. |
| **Status**     | ✅ Passando |

---

## IT-01 — Formato do payload gerado pelo simulador

| Campo           | Detalhe |
|----------------|---------|
| **Cenário**    | O simulador IoT deve gerar payloads compatíveis com o contrato da API antes mesmo de enviar qualquer requisição. |
| **Arquivo**    | `IoT/test_integration.py` — `TestPayloadFormat` |
| **Entrada**    | Saída de `gerar_leitura_mutante()` (30 amostras) |
| **Saída esperada** | Campos `estufaId`, `tipoSensor`, `valorLeitura`, `unidade`, `timestamp` presentes; enums válidos; timestamp sem sufixo `Z`. |
| **Requer servidor** | Não |
| **Status**     | ✅ Passando |

---

## IT-02 — API aceita leitura de sensor válida

| Campo           | Detalhe |
|----------------|---------|
| **Cenário**    | Um payload gerado pelo simulador é enviado via HTTP POST para `/sensores/leitura` e a API deve aceitá-lo. |
| **Arquivo**    | `IoT/test_integration.py` — `TestApiAceitaLeitura` |
| **Entrada**    | Payload de `gerar_leitura_mutante()` enviado para `POST /sensores/leitura` |
| **Saída esperada** | HTTP 200 ou 201. |
| **Requer servidor** | Sim |
| **Status**     | ✅ Passando |

---

## IT-03 — API rejeita payload sem campo obrigatório

| Campo           | Detalhe |
|----------------|---------|
| **Cenário**    | Payload enviado sem `estufaId`; a validação Bean Validation do Spring deve rejeitar a requisição. |
| **Arquivo**    | `IoT/test_integration.py` — `TestApiRejeita400` |
| **Entrada**    | Payload de `gerar_leitura_mutante()` com `estufaId` removido |
| **Saída esperada** | HTTP 400. |
| **Requer servidor** | Sim |
| **Status**     | ✅ Passando |

---

## IT-04 — Leitura de sensor é persistida no banco

| Campo           | Detalhe |
|----------------|---------|
| **Cenário**    | Após POST bem-sucedido, a resposta deve conter o `id` gerado pelo banco e o `estufaId` correspondente, confirmando a persistência. |
| **Arquivo**    | `IoT/test_integration.py` — `TestLeituraPersistida` |
| **Entrada**    | Payload de `gerar_leitura_mutante()` enviado para `POST /sensores/leitura` |
| **Saída esperada** | Corpo JSON da resposta contém `id` (não nulo) e `estufaId` igual ao enviado. |
| **Requer servidor** | Sim |
| **Status**     | ✅ Passando |
