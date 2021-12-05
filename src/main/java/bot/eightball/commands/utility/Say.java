package bot.eightball.commands.utility;

import bot.eightball.core.MagicBalls;
import bot.eightball.core.discord.commands.SlashSubCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.Objects;

public class Say extends SlashSubCommand {

    public Say(JDA instance) {
        super(instance);
    }

    public boolean hasManageServer(Member member) {
        return member.hasPermission(Permission.MANAGE_SERVER) || member.hasPermission(Permission.ADMINISTRATOR);
    }

    @Override
    public void run(SlashCommandEvent commandEvent) {

        if (!hasManageServer(Objects.requireNonNull(commandEvent.getMember()))) {
            commandEvent.reply(String.format("%s You do not have permission to use this command!", MagicBalls.getEmojiAsMention(MagicBalls.config.X_EMOJI)))
                    .setEphemeral(true)
                    .queue();
            return;
        }

        String message = commandEvent.getOptions().get(0).getAsString();

        if (message.replaceAll(" ", "").length() < 1) {
            commandEvent.reply(String.format("%s You cannot send an empty message!", MagicBalls.getEmojiAsMention(MagicBalls.config.X_EMOJI)))
                    .setEphemeral(true)
                    .queue();
            return;
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(MagicBalls.getDefaultEmbedColour(commandEvent.getGuild()))
                .setDescription(message);

        commandEvent.replyEmbeds(embed.build()).queue();

    }

    @Override
    public SubcommandData getCommandData() {
        return new SubcommandData("say", "Make the bot say something! (Requires Manage Messages)").addOption(
                OptionType.STRING, "message", "The message for the bot to say", true
        );
    }
}
