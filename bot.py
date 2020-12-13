import discord, os, platform, asyncio
from discord.ext.commands import AutoShardedBot
import configuration.config as config
from discord.utils import find
from cogs.eightball import Eightball
from time import strftime
from cogs.eightball import cached_replies
from random import choice
from cogs.eightball import nine_ball

bot = AutoShardedBot(command_prefix=config.PREFIX + " ")
bot.remove_command("help")


@bot.event
async def on_ready():
    bot.loop.create_task(status_task())
    bot.loop.create_task(daily_question())
    print("-" * 40)
    print(f"Logged in as {bot.user.name}, bot is in {len(bot.guilds)} Guilds!")
    print(f"Discord.py API version: {discord.__version__} | Python version {platform.python_version()}")
    print(f"Running on: {platform.system()} {platform.release()} ({os.name})")
    print("change this part")
    print("-" * 40)


async def status_task():
    while True:
        await bot.change_presence(activity=discord.Game("Ask me a question!"))
        await asyncio.sleep(60)
        await bot.change_presence(activity=discord.Game("🎄🎄🎄🎄"))
        await asyncio.sleep(60)
        await bot.change_presence(activity=discord.Game("🕎✡️🕎✡️"))
        await asyncio.sleep(60)
        await bot.change_presence(activity=discord.Game("8ball vote"))
        await asyncio.sleep(60)


async def daily_question():
    send_channel = bot.get_channel(config.DAILY_POST_CHANNEL)

    while True:
        await asyncio.sleep(1)
        if strftime('%I:%M:%S:%p') == "05:00:00:PM":
            embed = discord.Embed(
                color=config.EMBED_COLOUR_STRD,
                description=f"`Today's Question`\n\n{choice(cached_replies)}"
            )
            embed.set_thumbnail(url=bot.user.avatar_url)
            await send_channel.send(embed=embed)
            cached_replies.clear()


@bot.event
async def on_guild_join(guild):
    general = find(lambda x: 'general' in x.name, guild.text_channels)
    if general and general.permissions_for(guild.me).send_messages:
        await general.send('Hello {}!'.format(guild.name))
    print(f"Joined guild {guild.name}, total Guilds: {len(bot.guilds)}")


@bot.event
async def on_guild_leave(guild):
    print(f"Left guild {guild.name}, total Guilds: {len(bot.guilds)}")


if __name__ == "__main__":
    print("-" * 40)
    for extension in config.STARTUP_COGS:
        try:
            bot.load_extension(extension)
            extension = extension.replace("cogs.", "")
            print(f"Loaded extension '{extension}'")
        except Exception as ex:
            exception = f"{type(ex).__name__}: {ex}"
            extension = extension.replace("cogs.", "")
            print(f"Failed to load extension {extension}\n{exception}")

    bot_commands = []

    for command in bot.commands:
        bot_commands.append(f"{command}")

    command_aliases = []

    for command_name in bot_commands:
        try:
            command_aliases += AutoShardedBot.get_command(bot, command_name).aliases
        except:
            pass

    bot_commands += command_aliases


@bot.event
async def on_message(message):
    # Ignores Bot Messages
    if message.author == bot.user or message.author.bot:
        return

    # Separates 9-ball messages
    if message.content.find("9ball") == 0:
        await nine_ball(message)

    # Ignores non-8Ball messages
    if not message.content[:len(config.PREFIX)].lower() == config.PREFIX:
        return

    # If no context is given
    if message.content == config.PREFIX:
        message.content = config.PREFIX + " " + "no context"

    await bot.process_commands(message)


@bot.event
async def on_command_error(context, error):
    # Main 8Ball responses
    if discord.ext.commands.errors.CommandNotFound:
        current = Eightball(context)
        response = current.get_response()

        response_content = response[0]
        response_dm_bool = response[1]
        response_reaction = response[2]

        embed = discord.Embed(
            color=config.EMBED_COLOUR_STRD,
            description=response_content
        )

        if response_dm_bool:
            bot_message = await context.author.send(embed=embed)
            if response_reaction is not None: await bot_message.add_reaction(response_reaction)

        else:
            await context.send(embed=embed)
            if response_reaction is not None: await context.message.add_reaction(response_reaction)

    # Throws error if not an 8Ball Response
    else:
        raise error

token_file = open("bot_token.txt")
bot.run(token_file.read())
