import requests, random


def generate_sequence_number():
    value = random.randint(0, 999999)
    return value


def generate_random_word():
    grab_words = requests.get("http://svnweb.freebsd.org/csrg/share/dict/words?view=co&content-type=text/plain").content.splitlines()
    word_content = " ".join([grab_words[random.randrange(0, len(grab_words))] for i in range(3)])
    return word_content

def produce_message():
    words = generate_random_word()
    seq_num = generate_sequence_number()
    message_output = ' sequence_number:' + str(seq_num) + ', content: ' + words
    return message_output
