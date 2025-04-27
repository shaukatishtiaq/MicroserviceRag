import os, requests, json
from app.models.llm_models import LLMRequestModel
from dotenv import load_dotenv
load_dotenv()

LLM_API_ENDPOINT = os.getenv("LLM_API_ENDPOINT")
LLM_API_TOKEN = os.getenv("LLM_API_TOKEN")
LLM_MODEL_NAME = os.getenv("LLM_MODEL_NAME")

class LLMService:
    
    def get_rag_response(self, request_object: LLMRequestModel) -> str:
        messages = [
            {
                "role": "system",
                "content": """You are a helpful assistant that takes a QUESTION and a CONTEXT and then provides a response for the QUESTION only using the CONTEXT.
                ONLY RETURN THE ANSWER"""
            },
            {
                "role": "user",
                "content": f"""
                    QUESTION = {request_object.query}
                    
                    CONTEXT = {request_object.context}
                    
                    ANSWER = ?
                """
            }
        ]
        headers = {
            "Content-Type": "application/json",
            "Authorization": f"Bearer {LLM_API_TOKEN}"
        }
        body = {
            "model": LLM_MODEL_NAME,
            "messages": messages,
            "frequency_penalty": 0,
            # "response_format": {"type": "json_object"}
        }
        
        response = requests.post(url=LLM_API_ENDPOINT,headers=headers,data=json.dumps(body))
        
        print(f"Response ==> {response}")
        print(f"Response status ==> {response.status_code}")
        print(f"Response Text ==> {response.json()['choices'][0]['message']['content']}")
        
        return response.json()['choices'][0]['message']['content']
        
        