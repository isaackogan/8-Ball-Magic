package bot.eightball.commands.general.general;

import bot.eightball.core.MagicBalls;
import bot.eightball.core.discord.commands.SlashSubCommand;
import bot.eightball.core.discord.interactions.ActionRowBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;

public class Vote extends SlashSubCommand {

    public Vote(JDA instance) {
        super(instance);
    }

    @Override
    public void run(SlashCommandEvent commandEvent) {

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(MagicBalls.getDefaultEmbedColour(commandEvent.getGuild()))
                .setTitle("Vote for 8 Ball Magic: Maybe your family will love you a little more!");

        ActionRowBuilder actionRowBuilder = new ActionRowBuilder()
                .addComponents(Button.of(ButtonStyle.LINK, MagicBalls.config.LINKS.get("VOTE").get("url"),
                        "Click to Vote")
                );

        commandEvent.replyEmbeds(embed.build())
                .addActionRows(actionRowBuilder.build())
                .queue();

    }

    @Override
    public SubcommandData getCommandData() {
        return new SubcommandData("vote", "Vote for 8 Ball Magic");
    }

}
