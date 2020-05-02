FROM python:2.7-alpine
ENV PYTHONPATH /usr/src/app
WORKDIR /usr/src/app
RUN apk add --no-cache git \
    && git clone  
COPY ./ ./
RUN pip install -r requirements.txt
ENTRYPOINT ["python", "producer.py", "-p", "5672", "-s", "rabbitmq"]
# default is 30 message , you can increase/decrease it by passing -r <count>
CMD ["-r", "30"]
