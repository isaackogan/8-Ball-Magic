import discord
from discord.ext import commands
import configuration.config
from time import strftime


class Help(commands.Cog, name="help"):
    def __init__(self, bot):
        self.bot = bot

    @commands.command(name="help")
    async def help(self, context):
        commands_list = ""

        for item in configuration.config.HELP_MESSAGES:
            temp_pos = item.find(";")
            if temp_pos > -1:
                arg1 = item[:temp_pos]
                arg2 = item[temp_pos + 1:]

                commands_list += f"• `{configuration.config.BOT_PREFIX}{arg1}` - {arg2}\n"

            else:
                print(f"'{item}' in config.yml was not formatted properly.")

        # Note that commands made only for the owner of the bot are not listed here.
        embed = discord.Embed(
            title=":robot:  BOT HELP » INFORMATION",
            color=0x202529,
            description=f"_ _\nAsk a question starting with **{configuration.config.BOT_PREFIX[:-1]}** and ending with **?**.\n\n" + commands_list + "\n_ _"
        )
        embed.set_thumbnail(
            url=self.bot.user.avatar_url
        )
        embed.set_footer(
            text=f"Server: {context.message.guild.name} - {strftime('%D | %I:%M %p (EST)')}",
            icon_url=context.message.guild.icon_url
        )
        await context.send(embed=embed)


def setup(bot):
    bot.add_cog(Help(bot))
