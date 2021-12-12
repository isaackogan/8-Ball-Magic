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

    public static boolean isMentalHealthQuestion(String testCase) {
        testCase = testCase.toLowerCase(Locale.ROOT);
        return MagicBalls.config.SUICIDE_KEYWORDS.stream().anyMatch(testCase::contains);
    }

    public static EmbedBuilder getSuicideEmbed() {
        return new EmbedBuilder()
                .setTitle("Hey, you matter.")
                .setDescription(
                        "Nobody experiences perfect mental health all of the time, and you are not alone.\n\n"
                                + "**Reach out** to someone that you trust who will be able to help you navigate through these difficult times. "
                                + "The ideal person to reach out to would be someone who makes you feel safe, you may be able to make a list of "
                                + "adults in your life who you are able to talk to about how you are feeling. If not, please give one of these "
                                + "help lines just one more chance."
                )
                .addField("United States", "Call (800) 273-8255 or Text HOME to 741741", true)
                .addField("United Kingdom", "Call 116-123 or Text SHOUT to 85258", true)
                .addField("Canada", "Call (833) 456-4566 or Text 45645", true)
                .addField("India", "Call +91 80 23655557", true)
                .addField("Japan", "Call 810352869090", true)
                .addField("Other Countries?", "[Click Here](https://en.wikipedia.org/wiki/List_of_suicide_crisis_lines)", true)
                .setColor(MagicBalls.getDefaultEmbedColour(null))
                .setFooter("Please give the help lines a chance. I know you can make it.", MagicBalls.getEmbedThumbnail(null));

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

            EmbedBuilder embed;

            // Mental Health
            if (!isMentalHealthQuestion(question.toLowerCase(Locale.ROOT))) {
                Question responses = getMagicResponse(commandEvent.getGuild(), question.toLowerCase(Locale.ROOT));

                embed = new EmbedBuilder()
                        .setAuthor("\"" + shortenedQuestion + "\"")
                        .setThumbnail(MagicBalls.getEmbedThumbnail(commandEvent.getGuild()))
                        .setColor(MagicBalls.getDefaultEmbedColour(commandEvent.getGuild()))
                        .setDescription("```\n" + responses.allResponses.get(random.nextInt(responses.allResponses.size())) + "\n```");

                if (responses == Question.TOO_SHORT_NOT_QUESTION || responses == Question.GOOD_LENGTH_NOT_QUESTION) {
                    embed.setFooter("Shouldn't you end questions with a question mark?");
                }

            } else {
                embed = getSuicideEmbed();
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
