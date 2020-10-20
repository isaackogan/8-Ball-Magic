import os, sys, discord, platform, random, aiohttp, json
import asyncio
from discord.ext import commands
import configuration.config


class Ping(commands.Cog, name="ping"):
    def __init__(self, bot):
        self.bot = bot

    @commands.command(name="ping")
    async def ping(self, context):
        print(context)
        message = await context.send(f"{configuration.config.SEARCHING_EMOJI} **Bot's Ping** :mag_right: `Loading...`")
        await asyncio.sleep(3)
        await message.edit(
            content=f"{configuration.config.ONLINE_EMIJI} **Bot's Ping** :mag_right: `{round(self.bot.latency * 1000)} ms`")


def setup(bot):
    bot.add_cog(Ping(bot))
