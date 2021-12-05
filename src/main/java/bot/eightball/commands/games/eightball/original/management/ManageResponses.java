package bot.eightball.commands.games.eightball.original.management;

import bot.eightball.commands.games.eightball.original.Question;
import bot.eightball.core.MagicBalls;
import bot.eightball.core.discord.commands.SlashSubCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.Objects;

public abstract class ManageResponses extends SlashSubCommand {

    public ManageResponses(JDA instance) {
        super(instance);
    }

    public boolean hasManageServer(Member member) {
        return member.hasPermission(Permission.MANAGE_SERVER) || member.hasPermission(Permission.ADMINISTRATOR);
    }

    @Override
    public void run(SlashCommandEvent commandEvent) {
        if (!commandEvent.isFromGuild()) {
            commandEvent.reply(String.format("%s Command must be used in a guild!", MagicBalls.getEmojiAsMention(MagicBalls.config.X_EMOJI)))
                    .setEphemeral(true)
                    .queue();
            return;
        }

        if (!hasManageServer(Objects.requireNonNull(commandEvent.getMember()))) {
            commandEvent.reply(String.format("%s You do not have permission to use this command!", MagicBalls.getEmojiAsMention(MagicBalls.config.X_EMOJI)))
                    .setEphemeral(true)
                    .queue();
            return;
        }

        Question question = Question.getQuestion(commandEvent.getOptions().get(0).getAsString());
        if (question == null) {
            commandEvent.reply(String.format("%s Your client sent an invalid menu option!", MagicBalls.getEmojiAsMention(MagicBalls.config.X_EMOJI)))
                    .setEphemeral(true)
                    .queue();
            return;
        }
        manageResponseRun(commandEvent, question);
    }

    public void manageResponseRun(SlashCommandEvent commandEvent, Question question) {
        // Dummy method for overriding
    }


}
