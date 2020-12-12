from configuration import config
from discord.ext import commands
import configuration.config, asyncio, inspect, discord
from discord.ext.commands import has_permissions
from time import strftime

class General(commands.Cog, name="General"):
    def __init__(self, bot):
        self.bot = bot

    @commands.command()
    async def ping(self, context):
        message = await context.send(f"{configuration.config.SEARCHING_EMOJI} **Bot's Ping** :mag_right: `Loading...`")
        await asyncio.sleep(3)
        await message.edit(
            content=f"{configuration.config.ONLINE_EMOJI} **Bot's Ping** :mag_right: `{round(self.bot.latency * 1000)} ms`")

    @commands.command()
    async def poll(self, context):
        command_name = inspect.currentframe().f_code.co_name
        command_pos = context.message.content.find(command_name)
        poll_content = context.message.content[command_pos + len(command_name + " "):]

        embed = discord.Embed(title=f"Discord Poll:", description=f"{poll_content}")
        embed.set_thumbnail(url=self.bot.user.avatar_url)
        message = await context.send(embed=embed)

        await message.add_reaction(config.CHECK_EMOJI)
        await message.add_reaction(config.NEUTRAL_EMOJI)
        await message.add_reaction(config.X_EMOJI)

    @commands.command()
    async def vote(self, context):
        embed = discord.Embed(
            title="Vote to be cool & support this bot!",
            description="[Click here to vote!](https://top.gg/bot/757081453803864145/vote)"
        )
        embed.set_image(url="https://i.imgur.com/9GHtbLL.gif")
        await context.send(embed=embed)

    @commands.command()
    async def invite(self, context):
        embed = discord.Embed(
            title="Invite this bot to your discord server!",
            description="[Click here to invite!](https://discord.com/oauth2/authorize?client_id=757081453803864145&scope=bot&permissions=537259073)"
        )
        embed.set_image(url="https://i.imgur.com/dPSAPN2.gif")
        await context.send(embed=embed)

    @commands.command()
    @has_permissions(manage_messages=True)
    async def say(self, context):
        command_name = inspect.currentframe().f_code.co_name
        command_pos = context.message.content.find(command_name)
        say_message = context.message.content[command_pos + len(command_name + " "):]

        embed = discord.Embed(
            color=config.EMBED_COLOUR_STRD,
            description=say_message
        )

        await context.send(embed=embed)

    @commands.command()
    async def info(self, context):
        embed = discord.Embed(
            title="Bot Information",
            description=":wave: Thanks for checking me out! `I still hate you.`\n_ _"
        )
        embed.add_field(
            name="**Bot Information**",
            value="_ _\n:one: **Vote for me:** [Click here to vote 8 Ball Magic 2020!](https://top.gg/bot/757081453803864145/vote)\n" 
                  ":two: **Invite this bot:** [Click here to invite me, stupid!](https://discord.com/oauth2/authorize?client_id=757081453803864145&scope=bot&permissions=537259073)\n" 
                  ":three: **Join the support server:** [Click here, bloody idiot!](https://discord.gg/ADxgWyh)",
            inline=False
        )
        embed.add_field(
            name="**Creator Information**",
            value="_ _\n<:snapchat:787262440530116618> **Snapchat:** https://snapchat.com/add/isaackogan\n"
                  "<:twitter:787262440370208799> **YouTube:** https://youtube.com/conclusions\n"
                  "<:youtube:787262440470609920> **Twitter:** https://twitter.com/isaacikogan"
        )
        embed.set_thumbnail(url=self.bot.user.avatar_url)
        embed.set_footer(
            text=f"Server: {context.message.guild.name} - {strftime('%D | %I:%M %p (UTC)')}",
            icon_url=context.message.guild.icon_url
        )
        await context.send(embed=embed)


def setup(bot):
    bot.add_cog(General(bot))
