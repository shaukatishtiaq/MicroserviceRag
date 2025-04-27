import chromadb, os
import chromadb.errors
from fastapi import HTTPException, status
from dotenv import load_dotenv
from app.models.rag_models import KnowledgeBaseDocument

load_dotenv()
CHROMA_DB_URL=os.getenv("CHROMA_DB_URL")

class DBService:
    def __init__(self):
        self.client = chromadb.PersistentClient(CHROMA_DB_URL)

    def add_to_knowledge_base(self,db_document: KnowledgeBaseDocument) -> bool:
            self.collection = self.client.get_or_create_collection(db_document.collection_name, metadata={"hnsw:space": "cosine"})
            
            ids = [f"{db_document.filename}_{i}" for i in range(len(db_document.documents))]
            try:
                self.collection.add(
                    documents=db_document.documents,
                    embeddings=db_document.embeddings,
                    metadatas= db_document.metadata if db_document.metadata else None,
                    ids=ids
                )
                print(f"{db_document.filename} chroma insertion completed.")
                print(f"Number of embeddings = {len(db_document.embeddings)}")
                return True
            except Exception as exp:
                print("Error occured when trying to add documents to chroma db. ", exp)
                return False
    def query_collection(self,collection_name:str, embeddings, n_results: int) -> dict:
        try:
            collection = self.client.get_collection(collection_name)
        except chromadb.errors.NotFoundError:
            raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail=f"Collection '{collection_name}' doesn't exist.")
        
        return collection.query(
            query_embeddings=embeddings,
            n_results=n_results,
            include=["documents", "distances","metadatas"]            
        )
        
    def get_docs_count(self, collection_name: str) -> int:
        try:
            collection = self.client.get_collection(collection_name)
            return collection.count()
        except chromadb.errors.NotFoundError as exp:
            print(f"Collection {collection_name} doesn't exist.")
            raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail=f"Collection '{collection_name}' doesn't exist.")
        except Exception as exp:
            print("Error while trying to get collection ", exp)
            raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail="Error while trying to get collection")