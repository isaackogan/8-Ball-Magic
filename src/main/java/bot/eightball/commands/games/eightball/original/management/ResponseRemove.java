package bot.eightball.commands.games.eightball.original.management;

import bot.eightball.commands.games.eightball.original.Question;
import bot.eightball.commands.games.eightball.original.ResponseSQL;
import bot.eightball.core.MagicBalls;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.Objects;

public class ResponseRemove extends ManageResponses {

    public ResponseRemove(JDA instance) {
        super(instance);
    }

    @Override
    public void manageResponseRun(SlashCommandEvent commandEvent, Question question) {
        Long id = commandEvent.getOptions().get(1).getAsLong();
        commandEvent.deferReply().queue(success -> doResponseWithPayload(commandEvent, question, success, id), fail -> {
        });

    }

    public void doResponseWithPayload(SlashCommandEvent commandEvent, Question question, InteractionHook deferredReply, Long id) {
        Boolean result = ResponseSQL.deleteResponseMessage(
                question.tableName, Objects.requireNonNull(commandEvent.getGuild()).getIdLong(), id
        );

        if (result == null) {
            deferredReply.editOriginal(String.format("%s Failed to remove response. Try again later!",
                    MagicBalls.getEmojiAsMention(MagicBalls.config.X_EMOJI))).queue();
        }

        if (Boolean.TRUE.equals(result)) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setAuthor(commandEvent.getUser().getName(), null, commandEvent.getUser().getEffectiveAvatarUrl())
                    .setThumbnail(MagicBalls.getEmbedThumbnail(commandEvent.getGuild()))
                    .setTitle(String.format("Successfully removed response `%s` in the `%s` category from this Guild's responses!", id, question.label))
                    .setColor(MagicBalls.config.EMBED_COLOUR.get("SUCCESS"));

            deferredReply.editOriginalEmbeds(embed.build()).queue();

        } else {
            deferredReply.editOriginal(String.format("%s The response you requested to remove doesn't exist.", MagicBalls.getEmojiAsMention(MagicBalls.config.X_EMOJI)))
                    .queue();
        }
    }

    @Override
    public SubcommandData getCommandData() {
        SubcommandData data = new SubcommandData("remove-response", "Remove a response from the /8ball ask command!");

        OptionData category = new OptionData(
                OptionType.STRING, "category", "The auto-response category you want to remove a responses from!", true
        );

        int pos = 0;
        for (Question questions : Question.values()) {
            pos++;
            category.addChoice(pos + ". " + questions.description, questions.tableName);
        }

        OptionData id = new OptionData(
                OptionType.INTEGER, "id", "The ID of the response you would like to remove", true
        );

        return data.addOptions(category, id);
    }
}
