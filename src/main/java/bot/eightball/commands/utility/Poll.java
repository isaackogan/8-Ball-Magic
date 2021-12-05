package bot.eightball.commands.utility;

import bot.eightball.core.MagicBalls;
import bot.eightball.core.discord.commands.SlashSubCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Poll extends SlashSubCommand {
    public Poll(JDA instance) {
        super(instance);
    }

    @Override
    public void run(SlashCommandEvent commandEvent) {
        String message = commandEvent.getOptions().get(0).getAsString();

        if (message.replaceAll(" ", "").length() < 1) {
            commandEvent.reply(String.format("%s You cannot send an empty poll message!", MagicBalls.config.X_EMOJI))
                    .setEphemeral(true)
                    .queue();
            return;
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Discord Poll")
                .setThumbnail(MagicBalls.config.BOT_ICON_URL)
                .setColor(MagicBalls.getDefaultEmbedColour(commandEvent.getGuild()))
                .setDescription(message);

        commandEvent.replyEmbeds(embed.build()).queue(success -> {
            success.retrieveOriginal().queue(s -> {
                try {
                    s.addReaction(Objects.requireNonNull(jda.getEmoteById(MagicBalls.config.SMALL_CHECKMARK_EMOJI))).queue();
                    s.addReaction(Objects.requireNonNull(jda.getEmoteById(MagicBalls.config.SMALL_NEUTRAL_EMOJI))).queueAfter(300, TimeUnit.MILLISECONDS);
                    s.addReaction(Objects.requireNonNull(jda.getEmoteById(MagicBalls.config.SMALL_X_MARK_EMOJI))).queueAfter(600, TimeUnit.MILLISECONDS);
                } catch (InsufficientPermissionException e) {
                    commandEvent.getUser().openPrivateChannel()
                            .flatMap(channel -> channel.sendMessage(String.format(
                                    "%s 8 Ball Magic is missing permissions for your poll! " +
                                            "Check that it can view the channel and add reactions, then try again.",
                                    MagicBalls.getEmojiAsMention(MagicBalls.config.X_EMOJI)
                            )))
                            .queue(sc -> {
                            }, fl -> {
                            });
                }
            }, f -> {
            });
        }, fail -> {
        });

    }

    @Override
    public SubcommandData getCommandData() {
        return new SubcommandData("poll", "Make a quick poll in your Discord").addOption(
                OptionType.STRING, "question", "The question you want to ask", true
        );
    }
}
