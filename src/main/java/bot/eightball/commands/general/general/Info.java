package bot.eightball.commands.general.general;

import bot.eightball.core.MagicBalls;
import bot.eightball.core.discord.commands.SlashSubCommand;
import bot.eightball.core.discord.interactions.ActionRowBuilder;
import bot.eightball.utilities.MiscUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;

import java.time.format.DateTimeFormatter;

public class Info extends SlashSubCommand {

    User creator;

    public Info(JDA instance) {
        super(instance);
        jda.retrieveUserById(MagicBalls.config.OWNER_ID).queue(user -> creator = user);

    }


    @Override
    public void run(SlashCommandEvent commandEvent) {
        if (creator == null) {
            commandEvent.reply(String.format(
                    "%s Sorry, but this command hasn't loaded yet. Try again later!",
                    MagicBalls.getEmojiAsMention(MagicBalls.config.X_EMOJI))
            ).setEphemeral(true).queue();
            return;
        }

        long guildCount = MagicBalls.getGuildCount();
        long shardCount = jda.getShardInfo().getShardTotal();
        long memberCount = MagicBalls.getMemberCount();

        EmbedBuilder embed = new EmbedBuilder()
                .setDescription("_ _\n_ _")
                .setColor(MagicBalls.getDefaultEmbedColour(commandEvent.getGuild()))
                .setAuthor(String.format("Created by %s \uD83D\uDC51", creator.getAsTag()), null, creator.getEffectiveAvatarUrl());

        String stats = String.format("""
                        ```
                        » Shards : %s
                        » Servers : %s
                        » Users : %s
                        » Ping : %s ms
                        » Created At : %s
                        » Bot Version : %s
                        ```
                        _ _
                        """,
                MiscUtils.commaFormatNumber(shardCount), MiscUtils.commaFormatNumber(guildCount), MiscUtils.commaFormatNumber(memberCount),
                jda.getGatewayPing(), jda.getSelfUser().getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME),
                MagicBalls.version
        );

        Runtime runtime = Runtime.getRuntime();
        final long memoryFree = runtime.freeMemory(), memoryTotal = runtime.totalMemory(), memoryMax = runtime.maxMemory();
        final long memoryUsed = memoryTotal - memoryFree;

        String memoryPercent = String.valueOf(MiscUtils.roundNumber((((memoryUsed / (double) memoryMax)) * 100), 1));

        String memoryUsage = String.format("(%s MB / %s MB)",
                MiscUtils.commaFormatNumber(memoryUsed / 1000000),
                MiscUtils.commaFormatNumber(memoryMax / 1000000)
        );

        String system = String.format("""
                        ```
                        » Platform : %s
                        » Arch : %s
                        » RAM Usage : %s%s %s
                        » System Version : %s
                        ```
                        _ _
                        """,
                System.getProperty("os.name"), System.getProperty("os.arch"), memoryPercent, "%", memoryUsage, System.getProperty("os.version")

        );

        embed.setDescription("Here's some information about this Magic 8 Ball!\n\n_ _")
                .addField("⚙️  Statistics", stats, false)
                .addField("⚙️   System & Hosting", system, false);

        String support = MagicBalls.config.LINKS.get("SUPPORT").get("url");
        String vote = MagicBalls.config.LINKS.get("VOTE").get("url");

        ActionRowBuilder actionRowBuilder = new ActionRowBuilder()
                .addComponents(Button.of(ButtonStyle.LINK, support, "Get Support"))
                .addComponents(Button.of(ButtonStyle.LINK, MagicBalls.getBotInviteLink(jda), "Invite Bot"))
                .addComponents(Button.of(ButtonStyle.LINK, vote, "Vote Now"));

        commandEvent.replyEmbeds(embed.build())
                .addActionRows(actionRowBuilder.build())
                .queue();

    }

    @Override
    public SubcommandData getCommandData() {
        return new SubcommandData("info", "Get information about 8 Ball Magic!");


    }

}
