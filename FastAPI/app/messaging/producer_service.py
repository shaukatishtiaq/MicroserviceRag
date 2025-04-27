from pika import BlockingConnection
from pika.exchange_type import ExchangeType
from pika.spec import BasicProperties
import json

class ProducerService:
    def __init__(self, channel):
        self.channel = channel
        
    def send_message(self, queue_name:str, exchange_name:str, routing_key:str, message):
        self.channel.exchange_declare(exchange=exchange_name,exchange_type=ExchangeType.direct)
        self.channel.queue_declare(queue_name,durable=True, exclusive=False, arguments=None)
        self.channel.queue_bind(queue=queue_name,exchange=exchange_name,routing_key=routing_key)
        
        props = BasicProperties("application/json",delivery_mode=2)
        
        self.channel.basic_publish(exchange=exchange_name,routing_key=routing_key,body=json.dumps(message), properties=props)