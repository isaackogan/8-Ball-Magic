from configuration.config import PREFIX
from configuration.responses import *
from random import choice
from time import strftime
import discord

cached_replies = ['8ball why does no one love me?']


async def nine_ball(message):
    embed = discord.Embed(
        title="Classic 8 Ball (9ball)"
    )
    embed.set_image(
        url=choice(NINE_BALL)
    )
    await message.channel.send(embed=embed)


class Eightball:
    def __init__(self, context):
        self.context = context
        self.message = self.context.message.content[len(PREFIX + " "):].lower()
        self.valid_length = len(self.message) >= 15
        self.is_question = self.message.endswith("?")

    def starts_with(self, clause):

        clause_length = len(clause)

        # Checking if the clause is there at the start
        if not self.message[:clause_length] == clause:
            return

        # Checking if the clause was just part of another word
        if len(self.message) > clause_length and not self.message[clause_length] == " ":
            return

        # If it passes verification, return True
        return True

    def get_response(self):
        print(f'{strftime("%D %I:%M %p (UTC)")} "{self.message}" ran by {self.context.author} in "{self.context.guild}" ({self.context.guild.id})')
        cached_replies.append(self.message)

        pos, dm = self.message.find('-dm'), False

        if pos != -1:
            self.message = self.message[:pos] + self.message[pos + 3:]
            dm = True

        if self.message == "no context":
            return choice(NO_CONTEXT), dm, None

        elif self.message.replace("?", "") == f"are you gay" or self.message.replace("?", "") == f"you are gay":
            return "Fuck off, you're gay.", dm, "🖕"

        elif "gay" in self.message:
            return choice(GAY), dm, "🏳️‍🌈"

        elif self.starts_with("who has"):
            return choice(WHO_HAS), dm, None

        elif self.starts_with("why"):
            return choice(WHY), dm, None

        elif self.starts_with("who is"):
            return choice(WHO_IS), dm, None

        elif self.valid_length and self.is_question:
            return choice(RESPONSES), dm, None

        elif self.valid_length and not self.is_question:
            return choice(MORE_NO) + '\n\n`End your question question with "?"`', dm, None

        elif not self.valid_length and self.is_question:
            return choice(LESS_YES), dm, None

        elif not self.valid_length and not self.is_question:
            return choice(LESS_NO), dm, None
