FROM python:3.10-slim

WORKDIR /service

COPY requirements.txt /service/requirements.txt

RUN pip install --no-cache-dir -r requirements.txt

COPY app /service/app
COPY embeddings_model /service/embeddings_model

EXPOSE 8081

CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]