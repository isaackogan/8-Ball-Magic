import discord
from discord.ext import commands
import configuration.config as config
from configuration.responses import HELP_MESSAGES
from time import strftime


class HelpCommand(commands.Cog, name="HelpCommand"):
    def __init__(self, bot):
        self.bot = bot

    @commands.command(aliases=["please help me i am going to explode", "i need help", "how do i use you"])
    async def help(self, context):

        # Loading the Command list formatted command list string
        commands_list = ""
        for section in HELP_MESSAGES:
            commands_list += "\n"
            for item in section:
                temp_pos = item.find(";")
                if temp_pos > -1:
                    arg1, arg2 = item[:temp_pos], item[temp_pos + 1:]
                    commands_list += f"• `{config.PREFIX} {arg1}` - {arg2}\n"
                else:
                    print(f"'{item}' in config.yml was not formatted properly.")


        # Creating the help embed
        embed = discord.Embed(
            title=":robot:  BOT HELP » INFORMATION ",
            color=0x202529,
            description=f"_ _\nTo use me, ask a question starting with **{config.PREFIX}** and ending with **?**.\n" + commands_list
                        + "\nConfused? Join the [**support discord**](https://discord.gg/vHE7yJM6fm) for help, to chat, or be stupid.\n\n_ _ "
        )
        embed.set_thumbnail(
            url=self.bot.user.avatar_url
        )
        embed.set_footer(
            text=f"Server: {context.message.guild.name} - {strftime('%D | %I:%M %p (UTC)')}",
            icon_url=context.message.guild.icon_url

        )
        await context.send(embed=embed)


def setup(bot):
    bot.add_cog(HelpCommand(bot))