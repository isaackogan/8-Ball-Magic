import discord
from discord.ext import commands
import configuration.config
from time import strftime
from random import choice
from asyncio import sleep
from configuration import responses


class RollSomeDice(commands.Cog, name="dice"):
    def __init__(self, bot):
        self.bot = bot

    @commands.command(pass_context=True, aliases=['dice', 'roll', 'rolldice', 'roll a die', 'roll dice', 'roll a dice'])
    async def roll_dice(self, context):
        message = await context.send(
            content=f"{configuration.config.SEARCHING_EMOJI} **Rolling.** :game_die:")
        await sleep(1.5)
        await message.edit(content=f"{configuration.config.SEARCHING_EMOJI} **Rolling..** :game_die:")
        await sleep(1.5)
        await message.edit(content=f"{configuration.config.SEARCHING_EMOJI} **Rolling...** :game_die:")
        await sleep(2.0)
        await message.edit(content=f"{configuration.config.ONLINE_EMIJI} **Done Rolling! :smirk: :game_die:**",
                           embed=discord.Embed(
                               title=f"Die Result: {choice(responses.DICE)}", ))


def setup(bot):
    bot.add_cog(RollSomeDice(bot))
