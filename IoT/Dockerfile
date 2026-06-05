FROM python:3.12-slim
WORKDIR /app
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt
COPY esp32_simulator.py .
CMD ["python", "-u", "esp32_simulator.py"]
