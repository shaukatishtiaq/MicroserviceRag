from fastapi import UploadFile, HTTPException
from dotenv import load_dotenv
from app.utils import file_utils
import os
import pymupdf
import pathlib
from app.models.rag_models import FileModel
from app.services.storage_service import StorageService

load_dotenv()
FILE_UPLOAD_PATH = os.getenv("FILE_UPLOAD_PATH")
KNOWLEDGE_BASE_PATH = os.getenv("KNOWLEDGE_BASE_PATH")

class FileService:
    
    def move_file_to_knowledge_base(self, file:dict) -> str:
        storage_service = StorageService()
        
        knowledge_base_file_path = file_utils.get_knowledge_base_path(file['filename'])
        filetype = file['filetype']
        
        file_data = storage_service.get_file(file['path'])
        
        if filetype == "pdf":
            with pymupdf.open(stream=file_data) as pdf_file:
                content = chr(12).join([page.get_text() for page in pdf_file])
                pathlib.Path(knowledge_base_file_path).write_bytes(content.encode())
            file['status'] = "DOWNLOADED FROM BUCKET"
            file['downloaded_path'] = knowledge_base_file_path
            return file
            # Delete file from bucket
           
        
        elif filetype == "txt":
            with open(knowledge_base_file_path, "wb") as txt_file:
                content = file_data
                txt_file.write(content)
            file['status'] = "DOWNLOADED FROM BUCKET"
            file['downloaded_path'] = knowledge_base_file_path
            return file
            # Delete file from bucket         
    
    def preprocess_files(self, files: list[dict]) -> list[str]:

        for file in files:
            preprocess_file = self.move_file_to_knowledge_base(file)
            file = preprocess_file
        
        return files
    def get_chunks(self,file_path,chunk_size=512, overlap=30):
        
        with open(file_path, 'r',encoding='utf-8') as txt_file:
            content = txt_file.read()

        chunks = []

        start = 0
        while start < len(content):
            end = start + chunk_size
            current_chunk = content[start:end]
            cleaned_chunk = file_utils.clean_text(current_chunk)
            chunks.append(cleaned_chunk)
            
            start = end - overlap

        return chunks