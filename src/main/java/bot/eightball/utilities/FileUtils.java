package bot.eightball.utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.apache.commons.io.IOUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.LinkedHashMap;

public class FileUtils {

    public static String getRelativeURI(String uri) {
        return Paths.get("").toAbsolutePath() + uri;
    }

    public static InputStream getFileStream(String uri) {
        final String realURI = System.getProperty("user.dir") + "/" + uri;

        try {
            return new FileInputStream(realURI);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static Object loadYamlFile(String uri) {
        Yaml yaml = new Yaml();
        InputStream inputStream = getFileStream(uri);
        return yaml.load(inputStream);

    }

    public static String yamlToJSON(Object yaml) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(yaml, LinkedHashMap.class);
    }

    public static JsonElement loadJSONFile(String uri) throws IOException {
        Gson gson = new Gson();
        String json = IOUtils.toString(FileUtils.getFileStream(uri), StandardCharsets.UTF_8);
        return gson.fromJson(json, JsonElement.class);
    }


}
