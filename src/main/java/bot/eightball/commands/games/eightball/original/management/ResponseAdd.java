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

public class ResponseAdd extends ManageResponses {

    public ResponseAdd(JDA instance) {
        super(instance);
    }

    @Override
    public void manageResponseRun(SlashCommandEvent commandEvent, Question question) {
        String response = commandEvent.getOptions().get(1).getAsString().replace("`", "").strip();

        if (response.length() < 3) {
            commandEvent.reply(String.format("%s Responses must be at least 3 characters long!", MagicBalls.getEmojiAsMention(MagicBalls.config.X_EMOJI)))
                    .setEphemeral(true)
                    .queue();
            return;
        }

        commandEvent.deferReply().queue(success -> doResponseWithPayload(commandEvent, question, success, response), fail -> {
        });

    }

    public void doResponseWithPayload(SlashCommandEvent commandEvent, Question question, InteractionHook deferredReply, String response) {
        boolean result = ResponseSQL.putResponseMessage(
                question.tableName, Objects.requireNonNull(commandEvent.getGuild()).getIdLong(), response
        );

        if (result) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setAuthor(commandEvent.getUser().getName(), null, commandEvent.getUser().getEffectiveAvatarUrl())
                    .setThumbnail(MagicBalls.getEmbedThumbnail(commandEvent.getGuild()))
                    .setTitle(String.format("Successfully added a response in the `%s` category for this Guild's responses!", question.label))
                    .setDescription("```\n" + response + "\n```")
                    .setColor(MagicBalls.config.EMBED_COLOUR.get("SUCCESS"));

            deferredReply.editOriginalEmbeds(embed.build()).queue();

        } else {
            deferredReply.editOriginal(String.format("%s Failed to add response, try again later!", MagicBalls.getEmojiAsMention(MagicBalls.config.X_EMOJI)))
                    .queue();
        }
    }

    @Override
    public SubcommandData getCommandData() {
        SubcommandData data = new SubcommandData("add-response", "Add a response to the /8ball ask command!");

        OptionData category = new OptionData(
                OptionType.STRING, "category", "The auto-response category you want to add responses to!", true
        );

        int pos = 0;
        for (Question question : Question.values()) {
            pos++;
            category.addChoice(pos + ". " + question.description, question.tableName);
        }

        OptionData message = new OptionData(
                OptionType.STRING, "response", "The message you would like the Bot to reply with", true
        );

        return data.addOptions(category, message);
    }

}
