FROM python:2.7-alpine
ENV PYTHONPATH /usr/src/app
RUN apk add --no-cache subversion \
    && svn co https://github.com/samer-habash/kubernetes-project-rabitmq/trunk/app/producer/ /usr/src/app --depth files
WORKDIR /usr/src/app
RUN pip install -r requirements.txt \
    && apk del subversion
#ENTRYPOINT ["python", "producer.py", "-p", "5672", "-s", "rabbitmq"]
# default is 30 message , you can increase/decrease it by passing -r <count>
#CMD ["-r", "30"]
