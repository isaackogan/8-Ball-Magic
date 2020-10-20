import discord
from discord.ext import commands
import configuration.config


class ServerInfo(commands.Cog, name="serverinfo"):
    def __init__(self, bot):
        self.bot = bot

    @commands.command(pass_context=True, aliases=['server', 'sv'])
    async def serverinfo(self, context):
        server = context.message.guild
        roles = [x.id for x in server.roles]
        current_number = 0

        role_length = len(roles)

        roles_formatted = ""

        if role_length > configuration.config.CMD_SERVER_ROLE_LIMIT:
            roles = roles[:configuration.config.CMD_SERVER_ROLE_LIMIT]
            roles_formatted = f"`Displaying the first {configuration.config.CMD_SERVER_ROLE_LIMIT} roles.`"

        for entry in roles:
            current_number += 1
            if current_number != 1:
                roles_formatted += f"\n<@&{entry}>"

        roles = roles_formatted

        channels = len(server.channels)
        time = str(server.created_at)
        time = time.split(" ")
        time = time[0]

        embed = discord.Embed(
            title=f"{server}",
            color=configuration.config.EMBED_COLOUR_STRD
        )
        embed.set_thumbnail(
            url=server.icon_url
        )
        embed.add_field(
            name="Server Owner",
            value=f"<@{server.owner_id}>",
        )
        embed.add_field(
            name="Member Count",
            value=f"`{server.member_count}`",
            inline=False
        )
        embed.add_field(
            name="Text/Voice Channels",
            value=f"`{channels}`",
            inline=False
        )
        embed.add_field(
            name="Date ",
            value=f"`{time}`",
        )
        embed.add_field(
            name=f"Roles (Server Total: {role_length})",
            value=roles,
            inline=False
        )
        await context.send(embed=embed)


def setup(bot):
    bot.add_cog(ServerInfo(bot))