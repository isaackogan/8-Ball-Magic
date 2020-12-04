import discord
from discord.ext import commands
from requests import get
from configuration import config
from asyncio import sleep


class RandomInsult(commands.Cog, name="RandomJoke"):
    def __init__(self, bot):
        self.bot = bot

    @commands.command(aliases=['insult me'])
    async def insult(self, context):

        message = await context.send(f"{config.SEARCHING_EMOJI} **Loading Insult...** :rage:")
        embed = discord.Embed(
            color=config.EMBED_COLOUR_STRD,
            description=get('https://evilinsult.com/generate_insult.php').text
        )
        await sleep(0.5), await message.edit(content=f"{config.ONLINE_EMOJI} **Loaded Insult...** :rage:", embed=embed)


def setup(bot):
    bot.add_cog(RandomInsult(bot))
