# rmqp-example

.env file include the number of message you would like to publish .
Message_Count=10 => will publish 10 messages then close the connection

Remember per each step you should run docker-compose build , then change the count in .env if you need then run docker-compose-up

NOTE: for this image we are using python:2.7-onbuild image which is huge image , take a look at Alpine-Artificats for building better size images using alpine os.
