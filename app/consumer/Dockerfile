FROM python:2.7-alpine
ENV PYTHONPATH /usr/src/app
WORKDIR /usr/src/app
COPY ./ ./
RUN pip install -r requirements.txt
#CMD ["python", "consumer.py", "-p", "5672", "-s", "rabbitmq"]
