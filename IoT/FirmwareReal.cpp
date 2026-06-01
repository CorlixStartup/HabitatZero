#include <WiFi.h>
#include <HTTPClient.h>
#include <ArduinoJson.h> // Necessário instalar via Gerenciador de Bibliotecas

// Configurações da Rede Sem Fio
const char* ssid     = "NOME_REDE_WIFI";
const char* password = "SENHA_WIFI";

// URL Endpoint da API Spring
// Nota: IP da máquina na rede local (ex: 192.168.1.50)
const char* serverName = "http://SEU_IP_LOCAL:8080/sensores/leitura";

// Definição dos Pinos Analógicos
const int PINO_O2      = 34; // GPIO34 (ADC1_CH6)
const int PINO_UMIDADE = 35; // GPIO35 (ADC1_CH7)
const int PINO_RADIACAO = 32; // GPIO32 (Mede a tensão convertida do módulo)

const int ESTUFA_ID = 1; // Identificador desta unidade de cultivo

void setup() {
  Serial.begin(115200);
  
  pinMode(PINO_O2, INPUT);
  pinMode(PINO_UMIDADE, INPUT);
  pinMode(PINO_RADIACAO, INPUT);

  WiFi.begin(ssid, password);
  Serial.print("Conectando ao Wi-Fi");
  while(WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("\nConectado com sucesso à rede!");
}

float lerSensorOxigenio() {
  int leituraRaw = analogRead(PINO_O2);
  // Conversão matemática baseada no datasheet do fabricante (Ex: 0V = 0%, 3.3V = 25%)
  float porcentagemO2 = (leituraRaw / 4095.0) * 25.0; 
  return porcentagemO2;
}

float lerSensorUmidade() {
  int leituraRaw = analogRead(PINO_UMIDADE);
  // Sensores capacitivos operam de forma inversa: solo seco dá valor alto, solo úmido valor baixo
  // Calibração típica: 3200 (Totalmente Seco) a 1500 (Totalmente Molhado)
  int valorSeco = 3200;
  int valorMolhado = 1500;
  
  float porcentagemUmidade = map(leituraRaw, valorSeco, valorMolhado, 0, 100);
  
  // Garante que o valor fique estritamente dentro do range de 0 a 100%
  if(porcentagemUmidade > 100) porcentagemUmidade = 100;
  if(porcentagemUmidade < 0) porcentagemUmidade = 0;
  
  return porcentagemUmidade;
}

float lerSensorRadiacao() {
  int leituraRaw = analogRead(