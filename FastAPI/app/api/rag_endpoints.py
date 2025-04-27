from fastapi import APIRouter, status, HTTPException
from app.models.rag_models import QueryModel
from app.services.rag_service import RAGService
import traceback

rag_router = APIRouter()
rag_service = RAGService()

@rag_router.get("/collections/{collection_name}/count")
async def get_docs_count_in_collection(collection_name: str):
    try:
        docs_count = rag_service.get_docs_count(collection_name)
    
        return {
            "result": docs_count,
            "message": f"The collection count for {collection_name} is {docs_count}"
        }
    except HTTPException as exp:
        raise exp
    except Exception as exp:
         print(traceback.format_exc())
         raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail=f"Error occured.")

@rag_router.post("/collections/{collection_name}")
async def query_collection(collection_name: str, query_model: QueryModel):    
    context = rag_service.query_collection(collection_name = collection_name, query_model=query_model)

    return {
        "result": context
    }