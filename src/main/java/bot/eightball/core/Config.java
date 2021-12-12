package bot.eightball.core;

import bot.eightball.utilities.FileUtils;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;

public class Config {

    public boolean TEST_MODE;
    public boolean SYNC_COMMANDS;
    public String LEGACY_PREFIX;
    public long TEST_GUILD_ID;
    public long OWNER_ID;

    // Hashmap Mappings
    public HashMap<String, Integer> EMBED_COLOUR;
    public HashMap<String, HashMap<String, String>> LINKS;

    // Static Emoji IDs
    public long X_EMOJI;
    public long CHECK_EMOJI;
    public long OFFLINE_EMOJI;
    public long ONLINE_EMOJI;
    public long VERIFIED_EMOJI;
    public long IDLE_EMOJI;

    public String BOT_ICON_URL;
    public long SEARCHING_EMOJI;
    public Long SMALL_CHECKMARK_EMOJI;
    public Long SMALL_X_MARK_EMOJI;
    public Long SMALL_NEUTRAL_EMOJI;

    public Long BOT_LISTING_STATS_UPDATE_INTERVAL;

    public String STATUS_PATH;
    public Long STATUS_UPDATE_INTERVAL;

    public String RESPONSES_TEMPLATE_PATH;

    public Long HIGHLIGHT_UPDATE_INTERVAL;
    public Long HIGHLIGHT_GUILD_ID;
    public Long HIGHLIGHT_CHANNEL_ID;

    public List<String> SUICIDE_KEYWORDS;
    
    public static Config fromYamlFile(String uri) {
        Gson gson = new Gson();
        String json = FileUtils.yamlToJSON(FileUtils.loadYamlFile(uri));
        return gson.fromJson(json, Config.class);
    }

}

