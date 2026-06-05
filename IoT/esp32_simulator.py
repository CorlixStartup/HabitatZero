import os
import time
import random
import requests
from datetime import datetime

API_URL = os.environ.get("API_URL", "http://localhost:8080/sensores/leitura")
ESTUFA_ID = int(os.environ.get("ESTUFA_ID", "1"))
INTERVALO_ENVIO = int(os.environ.get("INTERVALO_ENVIO", "5"))  # Segundos

def gerar_leitura_mutante():
    """Simula o comportamento dos sensores com variações normais e críticas"""
    sensor = random.choice(["OXIGENIO", "UMIDADE_SOLO", "RADIACAO_EXTERNA", "TEMPERATURA"])

    if sensor == "OXIGENIO":
        # Sorteia valores que podem simular uma queda crítica (abaixo de 19.5%)
        valor = round(random.uniform(18.0, 23.0), 1)
        unidade = "PERCENTUAL"
    elif sensor == "UMIDADE_SOLO":
        # Sorteia valores que podem simular solo seco (abaixo de 30%)
        valor = round(random.uniform(25.0, 70.0), 1)
        unidade = "PERCENTUAL"
    elif sensor == "RADIACAO_EXTERNA":
        # Sorteia valores que podem simular um pico de radiação cósmica (acima de 2.0)
        valor = round(random.uniform(0.1, 3.5), 2)
        unidade = "MSV_HORA"
    else:  # TEMPERATURA
        # Sorteia valores que podem simular temperatura crítica (acima de 40°C)
        valor = round(random.uniform(20.0, 50.0), 1)
        unidade = "CELSIUS"

    payload = {
        "estufaId": ESTUFA_ID,
        "tipoSensor": sensor,
        "valorLeitura": valor,
        "unidade": unidade,
        "timestamp": datetime.utcnow().strftime('%Y-%m-%dT%H:%M:%S')
    }
    return payload

def executar_dispositivo_iot():
    print("[HABITAT ZERO] Dispositivo IoT Inicializado (Modo Simulação Ativo)")
    print(f"Enviando telemetria para {API_URL} a cada {INTERVALO_ENVIO}s...\n")

    while True:
        dados_sensor = gerar_leitura_mutante()

        try:
            response = requests.post(API_URL, json=dados_sensor)

            if response.status_code == 201 or response.status_code == 200:
                print(f"[SUCESSO] {dados_sensor['tipoSensor']}: {dados_sensor['valorLeitura']} {dados_sensor['unidade']} enviado.")
            else:
                print(f"[AVISO] Backend respondeu com Status {response.status_code}: {response.text}")

        except requests.exceptions.ConnectionError:
            print("[ERRO DE CONEXÃO] O Backend Spring Boot está desligado ou a URL está incorreta.")
            
        time.sleep(INTERVALO_ENVIO)

if __name__ == "__main__":
    executar_dispositivo_iot()