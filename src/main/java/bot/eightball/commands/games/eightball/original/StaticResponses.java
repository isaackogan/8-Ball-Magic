package bot.eightball.commands.games.eightball.original;

import bot.eightball.utilities.FileUtils;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

public class StaticResponses {

    public List<String> who_has_question;
    public List<String> why_question;
    public List<String> gay_question;
    public List<String> too_short_not_question;
    public List<String> too_short_is_question;
    public List<String> good_length_not_question;
    public List<String> good_length_is_question;
    public List<String> who_is_question;
    public List<String> dice_roll;

    public static StaticResponses fromJsonFile(String uri) throws IOException {
        Gson gson = new Gson();
        String json = IOUtils.toString(Objects.requireNonNull(FileUtils.getFileStream(uri)), StandardCharsets.UTF_8);
        return gson.fromJson(json, StaticResponses.class);
    }


}
