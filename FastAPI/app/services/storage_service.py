from fastapi import HTTPException
import os, traceback
from minio import Minio
from dotenv import load_dotenv
load_dotenv()

storage_endpoint = os.getenv("STORAGE_ENDPOINT")
storage_access_key = os.getenv("STORAGE_ACCESS_KEY")
storage_secret_key = os.getenv("STORAGE_SECRET_KEY")
storage_bucket = os.getenv("STORAGE_BUCKET")

class StorageService:
    def __init__(self):
        
        storage_client = Minio(
            endpoint= storage_endpoint,
            access_key= storage_access_key,
            secret_key= storage_secret_key,
            secure=False
        )
        
        is_bucket_found = storage_client.bucket_exists(storage_bucket)
        if is_bucket_found:
            self.storage_client = storage_client
        else:
            self.error = "Bucket not found."
    
    def get_file(self,storage_path: str):
        try:
            response = self.storage_client.get_object(storage_bucket, storage_path)
            file = response.read()
            return file
        except:
            print(traceback.format_exc())
            raise HTTPException(status_code=404, detail=f"Error getting {storage_path} from bucket.")
        finally:
            response.close()
            response.release_conn()