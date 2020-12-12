import configuration.config, discord
from discord.ext import commands
from time import strftime


class ServerInfo(commands.Cog, name="ServerInfo"):
    def __init__(self, bot):
        self.bot = bot

    @commands.command(aliases=['sv'])
    async def server(self, context):
        sv = context.message.guild
        roles = [x.id for x in sv.roles]

        formatted_roles = ""

        if len(roles) > configuration.config.CMD_SERVER_ROLE_LIMIT:
            roles = roles[:configuration.config.CMD_SERVER_ROLE_LIMIT]
            formatted_roles = f"`Displaying the first {configuration.config.CMD_SERVER_ROLE_LIMIT} roles.`"

        for idx, entry in enumerate(roles):
            # Omitting the first entry, printing the rest
            if idx:
                formatted_roles += f"\n<@&{entry}>"

        embed = discord.Embed(
            title=f"Server - {sv}",
            color=0x202529
        )
        embed.set_thumbnail(
            url=sv.icon_url
        )
        embed.add_field(
            name="Owner",
            value=f"<@{sv.owner_id}>",
        )
        embed.add_field(
            name="Creation Date",
            value=f"`{str(sv.created_at).split(' ')[0]}`",
        )
        embed.add_field(
            name="Member Count",
            value=f"`{sv.member_count}`",
            inline=False
        )
        embed.add_field(
            name="Text/Voice Channels",
            value=f"`{len(sv.channels)}`",
            inline=False
        )
        embed.add_field(
            name="Server Region",
            value=f"`{str(context.guild.region).capitalize()}`",
            inline=False
        )
        embed.add_field(
            name=f"Roles (Server Total: {len(roles)})",
            value=formatted_roles,
            inline=False
        )
        embed.set_footer(
            text=f"Server: {context.message.guild.name} - {strftime('%D | %I:%M %p (UTC)')}",
            icon_url=context.message.guild.icon_url
        )
        await context.send(embed=embed)


def setup(bot):
    bot.add_cog(ServerInfo(bot))
