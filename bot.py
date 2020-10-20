import discord, asyncio, os, platform, sys
from discord.ext.commands import Bot
from discord.ext import commands
import configuration.config
import cogs.eightball

bot = Bot(command_prefix=configuration.config.BOT_PREFIX)
bot.remove_command("help")


@bot.event
async def on_ready():
    bot.loop.create_task(status_task())
    print(f"Logged in as {bot.user.name}")
    print(f"Discord.py API version: {discord.__version__}")
    print(f"Python version: {platform.python_version()}")
    print(f"Running on: {platform.system()} {platform.release()} ({os.name})")


async def status_task():
    while True:
        await bot.change_presence(activity=discord.Game("with her feelings..."))
        await asyncio.sleep(60)
        await bot.change_presence(activity=discord.Game("Fallen#2581"))
        await asyncio.sleep(60)


if __name__ == "__main__":
    for extension in configuration.config.STARTUP_COGS:
        try:
            bot.load_extension(extension)
            extension = extension.replace("cogs.", "")
            print(f"Loaded extension '{extension}'")
        except Exception as e:
            exception = f"{type(e).__name__}: {e}"
            extension = extension.replace("cogs.", "")
            print(f"Failed to load extension {extension}\n{exception}")


@bot.event
async def on_message(message):
    # Ignores if a command is being executed by a bot or by the bot itself
    if message.author == bot.user or message.author.bot:
        return
    else:
        if message.content == configuration.config.BOT_PREFIX[:-1]:
            embed = discord.Embed(
                color=configuration.config.EMBED_COLOUR_STRD,
                description=cogs.eightball.no_context()
            )
            await message.channel.send(embed=embed)
        else:
            await bot.process_commands(message)


@bot.event
async def on_command_error(context, error):
    if discord.ext.commands.errors.CommandNotFound:
        embed = discord.Embed(
            color=configuration.config.EMBED_COLOUR_STRD,
            description=cogs.eightball.read_context(context)
        )
        await context.send(embed=embed)

    else:
        raise error


bot.run(configuration.config.TOKEN)
