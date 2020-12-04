from discord.ext import commands
from configuration.config import *
import inspect, discord, asyncio, random
from configuration.responses import SUGGESTION_REPLIES

cooldown_users = []


class ReplySubmissions(commands.Cog, name="ReplySubmissions"):
    def __init__(self, bot):
        self.bot = bot

    @commands.command()
    async def suggest(self, context):
        # Grabbing the suggestion
        command_name = inspect.currentframe().f_code.co_name
        command_pos = context.message.content.find(command_name)
        suggestion = context.message.content[command_pos+len(command_name + " "):]

        # If they're on cooldown, send an error and end process
        if context.author.id in cooldown_users:
            embed = discord.Embed(
                description=f"{X_EMOJI} You can only send `one` suggestion every `{SUGGESTION_COOLDOWN} seconds`!",
                color=EMBED_COLOUR_ERROR
            )
            embed.set_author(name=f"{context.author.name}#{context.author.discriminator}", icon_url=context.author.avatar_url)
            await context.send(embed=embed)
            return

        # If the suggestion is empty, send the syntax again and end process
        if len(suggestion) == 0:
            embed = discord.Embed(
                description=f"{X_EMOJI} **Usage:** `8ball suggest <content>` (but with your own content).",
                color=EMBED_COLOUR_ERROR
            )
            embed.set_author(name=f"{context.author.name}#{context.author.discriminator}", icon_url=context.author.avatar_url)
            await context.send(embed=embed)
            return

        # If the suggestion is too short, send the proper length and end process
        if len(suggestion) < MIN_SUGGESTION_LENGTH:
            embed = discord.Embed(
                description=f"{X_EMOJI} Too short `(that's what she said)`: Must be at least `{MIN_SUGGESTION_LENGTH} characters` long.",
                color=EMBED_COLOUR_ERROR
            )
            embed.set_author(name=f"{context.author.name}#{context.author.discriminator}",
                             icon_url=context.author.avatar_url)
            await context.send(embed=embed)
            return

        # If the suggestion is valid
        if len(suggestion) >= MIN_SUGGESTION_LENGTH:
            file = open("./configuration/submissions.txt", "r")
            current = file.read()

            file = open("./configuration/submissions.txt", "w")
            file.write(current + f'Received "{suggestion}" from {context.author} ({context.author.id})\n')

            file.close()

            embed = discord.Embed(
                description=f"{CHECK_EMOJI} Your reply submission was successfully sent, `{random.choice(SUGGESTION_REPLIES)}`.",
                color=EMBED_COLOUR_SUCCESS
            )
            embed.set_author(name=f"{context.author.name}#{context.author.discriminator}", icon_url=context.author.avatar_url)
            await context.send(embed=embed)

            cooldown_users.append(context.author.id)
            await asyncio.sleep(SUGGESTION_COOLDOWN)

            for idx, each_id in enumerate(cooldown_users):
                if context.author.id == each_id: del cooldown_users[idx]


def setup(bot):
    bot.add_cog(ReplySubmissions(bot))
