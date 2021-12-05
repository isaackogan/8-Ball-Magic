package bot.eightball.core;

import bot.eightball.utilities.FileUtils;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class Tokens {
    public String bot;
    public MySQL mysql;
    public String topGG;

    public static Tokens fromJsonFile(String uri) throws IOException {
        Gson gson = new Gson();
        String json = IOUtils.toString(Objects.requireNonNull(FileUtils.getFileStream(uri)), StandardCharsets.UTF_8);
        return gson.fromJson(json, Tokens.class);
    }

    public static class MySQL {

        public String host;
        public int port;
        public String user;
        public String password;
        public String db;
        
    }
}
