

Downloading and Installing RabbitMQ with docker

X - > docker run -it --rm --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3.9-management


docker run -d --hostname my-rabbit --name some-rabbit   -p 5672:5672 -p 15672:15672  -e RABBITMQ_DEFAULT_USER=user -e RABBITMQ_DEFAULT_PASS=password rabbitmq:3-management
