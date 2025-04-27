import os, traceback
from fastapi import HTTPException
from sentence_transformers import SentenceTransformer
from dotenv import load_dotenv
from app.services.file_service import FileService
from app.models.rag_models import KnowledgeBaseDocument,QueryModel,FileModel
from app.services.db_service import DBService
from app.utils import db_utils
from app.services.llm_service import LLMService
from app.models.llm_models import LLMRequestModel

load_dotenv()
RAG_EMBEDDINGS_MODEL = os.getenv("RAG_EMBEDDINGS_MODEL")

class RAGService:

    def __init__(self):
        self.model = SentenceTransformer(RAG_EMBEDDINGS_MODEL, local_files_only=True)
    def generate_embeddings(self, chunks:list[str]):
        return self.model.encode(chunks).tolist()
    
    def add_to_knowledge_base(self, files_payload) -> dict:

        file_service = FileService()
        db_service = DBService()
        
        files:list[dict] = files_payload['files']
    
        try:
            preprocess_files = file_service.preprocess_files(files)
        except HTTPException as exp:
            raise exp
        except Exception as exp:
            print(traceback.format_exc())
            raise HTTPException(status_code=500, detail= "Couldn't preprocess uploaded files.")
        
        docs_added_to_chroma = []
        for file in preprocess_files:
            filename = file['filename']
            downloaded_path = file['downloaded_path']
            
            chunks = file_service.get_chunks(file_path=downloaded_path,chunk_size=512, overlap=30)
            embeddings = self.generate_embeddings(chunks)
            
            print(f"{filename} | chunks = {len(chunks)} | embeddings = {len(embeddings)}")
            
            try:
                db_document = KnowledgeBaseDocument(
                    collection_name = files_payload['collection_name'],
                    documents = chunks,
                    filename = filename,
                    embeddings = embeddings
                )
                is_doc_added = db_service.add_to_knowledge_base(db_document)
                if(is_doc_added):
                    file['status'] = "EMBEDDED"
                else:
                    file['status'] = "FAILED"
                file.pop('downloaded_path', None)
            except Exception as exp:
                print(exp)
                file['status'] = "FAILED"
                file.pop('downloaded_path', None)
                continue
        return files
    
    def query_collection(self,collection_name:str, query_model: QueryModel):
        db_service = DBService()
        llm_service = LLMService()
        
        embeddings = self.generate_embeddings([query_model.query])
        
        query_result = db_service.query_collection(embeddings=embeddings, collection_name=collection_name, n_results=query_model.n_results)

        retrieved_context = db_utils.format_db_query_result(query_result)

        llm_request_object = LLMRequestModel(query=query_model.query, context=retrieved_context)
        
        try:
            answer = llm_service.get_rag_response(llm_request_object)
            return answer
        
        except HTTPException as exp:
            raise exp
        except Exception as exp:
            print(traceback.format_exc())
            print("Error occured trying to call llm ", exp)
            raise HTTPException(status_code=500, detail="Error occured trying to call llm")
    
    def get_docs_count(self, collection_name:str) -> int:
        db_service = DBService()
        return db_service.get_docs_count(collection_name)