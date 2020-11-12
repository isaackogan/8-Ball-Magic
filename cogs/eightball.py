import configuration.responses as responses
import random


def no_context():
    return responses.NO_CONTEXT[random.randint(0, len(responses.NO_CONTEXT) - 1)]


def in_message(message, start, stop, string):
    message = message.lower()
    if message[start:stop] == string:
        return True
    else:
        return False


def get_response(response_list):
    return response_list[random.randint(0, len(response_list) - 1)]


def read_context(context):
    print(f'"{context.message.content}" ran by {context.author} in "{context.guild}" ({context.guild.id})')

    message = context.message.content

    is_question = "?" in message
    valid_length = len(message) >= 15

    if in_message(message, 6, 9, "why"):
        return get_response(responses.WHY)

    elif in_message(message, 6, 12, "who is"):
        return get_response(responses.WHO_IS)

    elif valid_length and "gay" in message:
        return get_response(responses.GAY)

    elif not valid_length and is_question:
        return get_response(responses.LESS_YES)

    elif not valid_length and not is_question:
        return get_response(responses.LESS_NO)

    elif valid_length and not is_question:
        return get_response(responses.MORE_NO)

    elif valid_length and is_question:
        return get_response(responses.RESPONSES)
