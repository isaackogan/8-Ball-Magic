package bot.eightball.commands.games.eightball.original;

import bot.eightball.commands.general.tasks.AutoHighlight;
import bot.eightball.core.MagicBalls;
import bot.eightball.core.discord.commands.SlashSubCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class EightBallMagic extends SlashSubCommand {

    final Random random = new Random();

    public EightBallMagic(JDA instance) {
        super(instance);
    }

    public Question getMagicResponse(Guild guild, String message) {

        Question questions;

        if (message.contains("gay")) {
            questions = Question.GAY;
        } else if (message.startsWith("who has")) {
            questions = Question.WHO_HAS;
        } else if (message.startsWith("why")) {
            questions = Question.WHY;
        } else if (message.startsWith("who is")) {
            questions = Question.WHO_IS;
        } else {

            boolean testCase = message.strip().replaceAll("\n", "").endsWith("?");

            if (message.length() >= 15) {

                if (testCase) {
                    questions = Question.GOOD_LENGTH_IS_QUESTION;
                } else {
                    questions = Question.GOOD_LENGTH_NOT_QUESTION;
                }

            } else {

                if (testCase) {
                    questions = Question.TOO_SHORT_IS_QUESTION;
                } else {
                    questions = Question.TOO_SHORT_NOT_QUESTION;
                }

            }
        }

        // Add to highlights
        if (questions != Question.TOO_SHORT_NOT_QUESTION) {
            AutoHighlight.cachedHighlights.add(message);
        }

        List<String> dynamicResponses;

        if (guild != null) {
            dynamicResponses = ResponseSQL.getResponseMessages(questions.tableName, guild.getIdLong());
        } else {
            dynamicResponses = new ArrayList<>();
        }

        List<String> responses = questions.staticResponses;
        responses.addAll(dynamicResponses);
        questions.allResponses = responses;

        return questions;

    }

    @Override
    public void run(SlashCommandEvent commandEvent) {
        commandEvent.deferReply().queue(success -> {
            String question = commandEvent.getOptions().get(0).getAsString();
            String shortenedQuestion = question;

            if (question.length() > 230) {
                String firstPart = question.substring(0, 100);
                String lastPart = question.substring(question.length() - 100);
                shortenedQuestion = firstPart + "..." + lastPart;
            }

            Question responses = getMagicResponse(commandEvent.getGuild(), question.toLowerCase(Locale.ROOT));

            EmbedBuilder embed = new EmbedBuilder()
                    .setAuthor("\"" + shortenedQuestion + "\"")
                    .setDescription("```\n" + responses.allResponses.get(random.nextInt(responses.allResponses.size())) + "\n```")
                    .setThumbnail(MagicBalls.getEmbedThumbnail(commandEvent.getGuild()))
                    .setColor(MagicBalls.getDefaultEmbedColour(commandEvent.getGuild()));


            if (responses == Question.TOO_SHORT_NOT_QUESTION || responses == Question.GOOD_LENGTH_NOT_QUESTION) {
                embed.setFooter("Shouldn't you end questions with a question mark?");
            }

            success.editOriginalEmbeds(embed.build()).queue();
        }, failure -> {
        });

    }

    @Override
    public SubcommandData getCommandData() {
        return new SubcommandData("ask", "Ask the Magic 8 Ball a question!")
                .addOption(OptionType.STRING, "question", "What's on your mind?", true);
    }
}
