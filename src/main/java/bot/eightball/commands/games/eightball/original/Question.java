package bot.eightball.commands.games.eightball.original;

import bot.eightball.core.MagicBalls;

import java.util.List;

public enum Question {

    WHO_HAS("who_has_question", MagicBalls.staticResponses.who_has_question, "Start with \"who has\"", "Who Has"),
    WHO_IS("who_is_question", MagicBalls.staticResponses.who_is_question, "Start with \"who is\"", "Who Is"),
    WHY("why_question", MagicBalls.staticResponses.why_question, "Start with \"why\"", "Why"),
    GAY("gay_question", MagicBalls.staticResponses.gay_question, "Include the word \"gay\" in them", "Gay"),
    TOO_SHORT_NOT_QUESTION("too_short_not_question", MagicBalls.staticResponses.too_short_not_question, "Too short and are missing a \"?\"", "Too Short / Missing ?"),
    TOO_SHORT_IS_QUESTION("too_short_is_question", MagicBalls.staticResponses.too_short_is_question, "Too short but have a \"?\"", "Too Short / Has ?"),
    GOOD_LENGTH_NOT_QUESTION("good_length_not_question", MagicBalls.staticResponses.good_length_not_question, "Missing a \"?\" in them", "Good Length / Missing ?"),
    GOOD_LENGTH_IS_QUESTION("good_length_is_question", MagicBalls.staticResponses.good_length_is_question, "Perfect questions!", "Perfect Question");

    public final String tableName;
    public final List<String> staticResponses;
    public final String description;
    public final String label;

    public List<String> allResponses;

    public static Question getQuestion(String tableName) {

        for (Question value : Question.values()) {
            if (value.tableName.equals(tableName)) {
                return value;
            }
        }

        return null;

    }

    Question(String tableName, List<String> staticResponses, String description, String label) {
        this.tableName = tableName;
        this.staticResponses = staticResponses;
        this.description = description;
        this.label = label;
    }


}

