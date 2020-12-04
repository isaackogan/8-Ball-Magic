from configuration import config, responses
from discord.ext import commands
from random import choice
from asyncio import sleep
import discord


class DiceRoll(commands.Cog, name="DiceRoll"):
    def __init__(self, bot):
        self.bot = bot

    @commands.command(aliases=['dice', 'die', 'roll a die', 'roll a dice', 'roll dice', 'roll die'])
    async def roll(self, context):
        message = await context.send(content=f"{config.SEARCHING_EMOJI} **Rolling.** :game_die:")
        await sleep(1.5), await message.edit(content=f"{config.SEARCHING_EMOJI} **Rolling..** :game_die:")
        await sleep(1.5), await message.edit(content=f"{config.SEARCHING_EMOJI} **Rolling...** :game_die:")
        await sleep(2.0), await message.edit(content=f"{config.ONLINE_EMOJI} **Done Rolling! :smirk: :game_die:**", embed=discord.Embed(title=f"Die Result: {choice(responses.DICE)}"))


def setup(bot):
    bot.add_cog(DiceRoll(bot))
