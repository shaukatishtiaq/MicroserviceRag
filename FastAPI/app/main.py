from fastapi import FastAPI
from contextlib import asynccontextmanager
from app.api.rag_endpoints import rag_router
from app.messaging.consumer_service import ConsumerService
import os
from dotenv import load_dotenv
load_dotenv()
import threading

file_upload_path = os.getenv("FILE_UPLOAD_PATH")
knowledge_base_path = os.getenv("KNOWLEDGE_BASE_PATH")

def run_messaging_service():
    ConsumerService().start_consumer()

@asynccontextmanager
async def lifespan(app: FastAPI):

    if file_upload_path and not os.path.exists(file_upload_path):
        os.makedirs(file_upload_path)
        print(f"Created directory: {file_upload_path}")

    if knowledge_base_path and not os.path.exists(knowledge_base_path):
        os.makedirs(knowledge_base_path)
        print(f"Created directory: {knowledge_base_path}")

    threading.Thread(target=run_messaging_service,daemon=True).start()
    yield
    
app = FastAPI(lifespan=lifespan)
app.include_router(rag_router, prefix="/rag", tags=["RAG"])
