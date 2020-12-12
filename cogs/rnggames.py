from configuration import config, responses
from discord.ext import commands
from random import choice, randint
from asyncio import sleep
import discord


class RNGGames(commands.Cog, name="RNGGames"):
    def __init__(self, bot):
        self.bot = bot

    @commands.command(aliases=['dice', 'die', 'roll a dice', 'roll dice', 'roll die'])
    async def roll(self, context):
        message = await context.send(content=f"{config.SEARCHING_EMOJI} **Rolling.** :game_die:")
        await sleep(1.5), await message.edit(content=f"{config.SEARCHING_EMOJI} **Rolling..** :game_die:")
        await sleep(1.5), await message.edit(content=f"{config.SEARCHING_EMOJI} **Rolling...** :game_die:")
        await sleep(2.0), await message.edit(content=f"{config.ONLINE_EMOJI} **Done Rolling! :smirk: :game_die:**", embed=discord.Embed(title=f"Die Result: {choice(responses.DICE)}"))

    @commands.command(aliases=['pp', 'pp size', 'ppsize'])
    async def howlongismypp(self, context):
        embed = discord.Embed(
            color=config.EMBED_COLOUR_STRD,
            description=f"8{'=' * randint(1, 32)}D"
        )
        await context.send(embed=embed)

    @commands.command(aliases=['flip', 'coin', 'coin flip', 'flip a coin', 'coin toss'])
    async def coinflip(self, context):
        coin = choice(["https://i.imgur.com/RM8GQWE.png", "https://i.imgur.com/3jn9USF.png"])
        message = await context.send(content=f"{config.SEARCHING_EMOJI} **Flipping.** :coin:")
        await sleep(1.5), await message.edit(content=f"{config.SEARCHING_EMOJI} **Flipping..** :coin:")
        await sleep(1.5), await message.edit(content=f"{config.SEARCHING_EMOJI} **Flipping...** :coin:")
        embed = discord.Embed()
        embed.set_thumbnail(url=coin)
        await sleep(2.0), await message.edit(content=f"{config.ONLINE_EMOJI} **Flipped Coin! :smirk: :coin:**", embed=embed)



def setup(bot):
    bot.add_cog(RNGGames(bot))
