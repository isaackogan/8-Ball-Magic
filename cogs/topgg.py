from discord.ext import commands

import dbl


class TopGG(commands.Cog):

    def __init__(self, bot):
        self.bot = bot
        self.token = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6Ijc1NzA4MTQ1MzgwMzg2NDE0NSIsImJvdCI6dHJ1ZSwiaWF0IjoxNjE1NjU3MzgzfQ.AUazG_siQmGURgEinx6TVRwyx8oQ2FQgaMLCffq8sso'  # set this to your DBL token
        self.dblpy = dbl.DBLClient(self.bot, self.token, autopost=True)  # Autopost will post your guild count every 30 minutes

    @commands.Cog.listener()
    async def on_guild_post(self):
        print("Server count posted successfully")


def setup(bot):
    bot.add_cog(TopGG(bot))