from pika import credentials, BlockingConnection, ConnectionParameters
from pika.exchange_type import ExchangeType
import json, os
from dotenv import load_dotenv
from app.services.rag_service import RAGService
from app.messaging.producer_service import ProducerService

load_dotenv()

MESSAGING_HOST = os.getenv("MESSAGING_HOST")
MESSAGING_PORT = os.getenv("MESSAGING_PORT")
MESSAGING_USERNAME = os.getenv("MESSAGING_USERNAME")
MESSAGING_PASSWORD = os.getenv("MESSAGING_PASSWORD")
MESSAGING_EXCHANGE_NAME = os.getenv("MESSAGING_EXCHANGE_NAME")
MESSAGING_REQUEST_QUEUE = os.getenv("MESSAGING_REQUEST_QUEUE")
MESSAGING_REQUEST_QUEUE_ROUTING_KEY = os.getenv("MESSAGING_REQUEST_QUEUE_ROUTING_KEY")
MESSAGING_RESPONSE_QUEUE = os.getenv("MESSAGING_RESPONSE_QUEUE")
MESSAGING_RESPONSE_QUEUE_ROUTING_KEY = os.getenv("MESSAGING_RESPONSE_QUEUE_ROUTING_KEY")

class ConsumerService:
    def __init__(self):
        messaging_credentials = credentials.PlainCredentials(MESSAGING_USERNAME, MESSAGING_PASSWORD)
        connection_parameters = ConnectionParameters(MESSAGING_HOST, MESSAGING_PORT, credentials=messaging_credentials)
        connection = BlockingConnection(connection_parameters)
        
        self.channel = connection.channel()
        self.producer = ProducerService(channel=self.channel)
    
    def start_consumer(self):
        self.channel.exchange_declare(MESSAGING_EXCHANGE_NAME,exchange_type=ExchangeType.direct)
        self.channel.queue_declare(MESSAGING_REQUEST_QUEUE, durable=True, exclusive=False, arguments=None)
        self.channel.queue_bind(MESSAGING_REQUEST_QUEUE, MESSAGING_EXCHANGE_NAME, MESSAGING_REQUEST_QUEUE_ROUTING_KEY)
        self.channel.basic_consume(queue=MESSAGING_REQUEST_QUEUE,auto_ack=True, on_message_callback=self.callback)
        
        print('\n\n[*] Waiting for messages.')
        
        self.channel.start_consuming()
        
    def callback(self,ch, method,properties, body):
        rag_service = RAGService()
        
        file_data = json.loads(body)
        print("File data ", file_data)
        print("Files from file data ==>> ", file_data['files'], " ",  type(file_data['files']))
        response = rag_service.add_to_knowledge_base(files_payload=file_data)
        
        self.producer.send_message(MESSAGING_RESPONSE_QUEUE, MESSAGING_EXCHANGE_NAME, MESSAGING_RESPONSE_QUEUE_ROUTING_KEY, response)