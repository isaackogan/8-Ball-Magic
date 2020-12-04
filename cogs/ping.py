from configuration import config
from discord.ext import commands
import configuration.config, asyncio


class BotPing(commands.Cog, name="BotPing"):
    def __init__(self, bot):
        self.bot = bot

    @commands.command()
    async def ping(self, context):
        message = await context.send(f"{configuration.config.SEARCHING_EMOJI} **Bot's Ping** :mag_right: `Loading...`")
        await asyncio.sleep(3)
        await message.edit(
            content=f"{configuration.config.ONLINE_EMOJI} **Bot's Ping** :mag_right: `{round(self.bot.latency * 1000)} ms`")


def setup(bot):
    bot.add_cog(BotPing(bot))
