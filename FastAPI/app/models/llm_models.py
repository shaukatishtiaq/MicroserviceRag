from pydantic import BaseModel

class LLMRequestModel(BaseModel):
    query: str
    context: str