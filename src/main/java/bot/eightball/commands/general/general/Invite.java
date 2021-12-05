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

public class Invite extends SlashSubCommand {


    public Invite(JDA instance) {
        super(instance);
    }

    @Override
    public void run(SlashCommandEvent commandEvent) {

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(MagicBalls.getDefaultEmbedColour(commandEvent.getGuild()))
                .setImage("https://i.imgur.com/dPSAPN2.gif")
                .setTitle("Invite this bot to your discord server!");

        ActionRowBuilder actionRowBuilder = new ActionRowBuilder()
                .addComponents(Button.of(ButtonStyle.LINK, MagicBalls.getBotInviteLink(jda), "Click to invite the bot, idiot."));

        commandEvent.replyEmbeds(embed.build())
                .addActionRows(actionRowBuilder.build())
                .queue();

    }

    @Override
    public SubcommandData getCommandData() {
        return new SubcommandData("invite", "Invite the bot to your Guild!");


    }

}
