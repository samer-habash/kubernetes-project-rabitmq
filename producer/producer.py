import pika, logging, sys, argparse
from argparse import RawTextHelpFormatter
from time import sleep
import datetime, generate_message

if __name__ == '__main__':
    examples = sys.argv[0] + " -p 5672 -s rabbitmq"
    parser = argparse.ArgumentParser(formatter_class=RawTextHelpFormatter,
                                 description='Run producer.py',
                                 epilog=examples)
    parser.add_argument('-p', '--port', action='store', dest='port', help='The port to listen on.')
    parser.add_argument('-s', '--server', action='store', dest='server', help='The RabbitMQ server.')
    parser.add_argument('-m', '--message', action='store', dest='message', help='The message to send', required=False)
    parser.add_argument('-r', '--repeat', action='store', dest='repeat', help='Number of times to repeat the message', required=False, default='30')

    args = parser.parse_args()
    if args.port == None:
        print "Missing required argument: -p/--port"
        sys.exit(1)
    if args.server == None:
        print "Missing required argument: -s/--server"
        sys.exit(1)

    # sleep a few seconds to allow RabbitMQ server to come up
    sleep(5)

    logging.basicConfig(level=logging.INFO)
    LOG = logging.getLogger(__name__)
    credentials = pika.PlainCredentials('guest', 'guest')
    parameters = pika.ConnectionParameters(args.server,
                                           int(args.port),
                                           '/',
                                           credentials)
    connection = pika.BlockingConnection(parameters)
    channel = connection.channel()
    q = channel.queue_declare('pc', durable=True)
    q_name = q.method.queue
    

    for i in range(0, int(args.repeat)):
        # Send a message and print it out
        date_now = str(datetime.datetime.now())
        message = generate_message.produce_message()
        message_body = '%s sending content : %s ' % (date_now, message)
        #channel.basic_publish(exchange='', routing_key=q_name, body=message_body)
        #print('Sending producer message : (%s) at %s' % (message, date_now))

        # Turn on delivery confirmations to rabitmq
        channel.confirm_delivery()
        # check delivery
        if channel.basic_publish(exchange='', routing_key=q_name, body=message_body):
            date_now = str(datetime.datetime.now())
            LOG.info(' Producer message (%s) has been delivered at %s' % (message_body, date_now))
        else:
            LOG.warning(' Producer message (%s) has NOT been delivered.' % message_body)
    connection.close()
