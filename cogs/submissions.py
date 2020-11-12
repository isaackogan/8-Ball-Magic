import discord
from discord.ext import commands
from configuration.config import CHECK_EMOJI, SUGGESTION_COOLDOWN, X_EMOJI, EMBED_COLOUR_SUCCESS, EMBED_COLOUR_ERROR
from configuration.responses import SUGGESTION_REPLIES
from time import strftime
from random import choice
import os, asyncio

cooldown_users = []


class ReplySubmissions(commands.Cog, name="reply_submissions"):
    def __init__(self, bot):
        self.bot = bot

    @commands.command(name="suggest")
    async def reply_submissions(self, context):
        message_content = context.message.content[14:]  # The string from after the command & args
        if len(message_content) >= 5:
            if context.author.id not in cooldown_users:
                file = open("./configuration/submissions.txt", "r")
                current = file.read()

                file = open("./configuration/submissions.txt", "w")
                file.write(current + f'Received "{message_content}" from {context.author} ({context.author.id})\n')

                file.close()

                embed = discord.Embed(
                    description=f"{CHECK_EMOJI} Your reply submission was successfully sent, `{choice(SUGGESTION_REPLIES)}`.",
                    color=EMBED_COLOUR_SUCCESS
                )
                embed.set_author(name=f"{context.author.name}#{context.author.discriminator}",
                                 icon_url=context.author.avatar_url)
                await context.send(embed=embed)

                cooldown_users.append(context.author.id)

                await asyncio.sleep(SUGGESTION_COOLDOWN)

                for idx, each_id in enumerate(cooldown_users):
                    if context.author.id == each_id:
                        cooldown_users[idx] = ""
            else:
                embed = discord.Embed(
                    description=f"{X_EMOJI} You can only send `one` suggestion every `{SUGGESTION_COOLDOWN} seconds`!",
                    color=EMBED_COLOUR_ERROR
                )
                embed.set_author(name=f"{context.author.name}#{context.author.discriminator}",
                                 icon_url=context.author.avatar_url)
                await context.send(embed=embed)
        elif len(message_content) == 0:
            embed = discord.Embed(
                description=f"{X_EMOJI} **Usage:** `8ball suggest <content>` (but with your own content).",
                color=EMBED_COLOUR_ERROR
            )
            embed.set_author(name=f"{context.author.name}#{context.author.discriminator}",
                             icon_url=context.author.avatar_url)
            await context.send(embed=embed)
        else:
            embed = discord.Embed(
                description=f"{X_EMOJI} Too short `(that's what she said)`: Must be at least `5 characters` long.",
                color=EMBED_COLOUR_ERROR
            )
            embed.set_author(name=f"{context.author.name}#{context.author.discriminator}",
                             icon_url=context.author.avatar_url)
            await context.send(embed=embed)


def setup(bot):
    bot.add_cog(ReplySubmissions(bot))
