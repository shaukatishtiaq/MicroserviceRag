from pydantic import BaseModel
class FileModel(BaseModel):
    filename: str
    filetype: str
    path: str
    username: str
    status: str

class KnowledgeBaseDocument(BaseModel):
    collection_name: str
    documents: list[str]
    filename: str
    embeddings: list
    metadata: dict[str, str] | None = None

class QueryModel(BaseModel):
    query: str
    n_results: int | None = 4
