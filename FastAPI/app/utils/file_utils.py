from dotenv import load_dotenv
import os, re

load_dotenv()

FILE_UPLOAD_PATH = os.getenv("FILE_UPLOAD_PATH")
KNOWLEDGE_BASE_PATH = os.getenv("KNOWLEDGE_BASE_PATH")

def format_filename(filename: str):
    return filename.replace(" ", "_")

def get_save_uploaded_path(filename: str):
    return f"{FILE_UPLOAD_PATH}/{filename}"

def get_knowledge_base_path(filename: str):
    splitted_filename = filename.split(".")
    updated_file_name = "".join(splitted_filename[: len(splitted_filename)-1]) + ".txt"
    
    return f"{KNOWLEDGE_BASE_PATH}/{updated_file_name}"

def clean_text(text):
    text = re.sub(r'\n+', '\n', text)  # Replace multiple newlines with a single one
    text = re.sub(r'\s+', ' ', text)  # Replace multiple spaces with a single space
    text = text.strip()  # Remove leading/trailing spaces
    
    # Additional custom clean-up can go here (e.g., removing page numbers)
    text = re.sub(r'\d+\s*$', '', text)  # Remove page numbers at the end of text
    return text