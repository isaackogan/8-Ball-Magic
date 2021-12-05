package bot.eightball.commands.general.tasks;

import bot.eightball.core.MagicBalls;
import bot.eightball.core.general.IShop;
import bot.eightball.utilities.FileUtils;
import bot.eightball.utilities.Logger;
import bot.eightball.utilities.MiscUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import java.io.IOException;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class RotateStatus implements Runnable, IShop.Schedulable {

    private static final JsonArray statusConfig = getStatusConfig();

    private static JsonArray getStatusConfig() {
        try {
            return (JsonArray) FileUtils.loadJSONFile(MagicBalls.config.STATUS_PATH);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public ScheduledFuture<?> schedule(ScheduledExecutorService ses) {
        return ses.scheduleAtFixedRate(this, 3, MagicBalls.config.STATUS_UPDATE_INTERVAL, TimeUnit.SECONDS);
    }

    private String getAsEscapedString(JsonElement element, boolean toLower) {
        if (element == null)
            return "null";

        String result = element.getAsString();
        return toLower ? result.toLowerCase(Locale.ROOT) : result;

    }

    @Override
    public void run() {

        if (statusConfig == null) {
            Logger.WARN("Unable to load status configuration, failed to change status.");
            return;
        }

        final Random random = new Random();
        JsonObject configChoice = statusConfig.get(random.nextInt(statusConfig.size())).getAsJsonObject();

        // Get values
        final String type = getAsEscapedString(configChoice.get("type"), true);
        final String onlineStatus = getAsEscapedString(configChoice.get("status"), true);
        final String url = getAsEscapedString(configChoice.get("url"), false);
        final String name = getAsEscapedString(configChoice.get("name"), false)
                .replaceAll("%guilds%", MiscUtils.commaFormatNumber(MagicBalls.getGuildCount()))
                .replaceAll("%members%", MiscUtils.commaFormatNumber(MagicBalls.getMemberCount()));

        // Parse Status
        OnlineStatus status = switch (onlineStatus) {
            case "offline" -> OnlineStatus.INVISIBLE;
            case "idle" -> OnlineStatus.IDLE;
            case "dnd", "do_not_disturb" -> OnlineStatus.DO_NOT_DISTURB;
            default -> OnlineStatus.ONLINE;
        };

        Activity activity = switch (type) {
            case "competing" -> Activity.competing(name);
            case "watching" -> Activity.watching(name);
            case "streaming" -> Activity.streaming(name, url);
            case "listening" -> Activity.listening(name);
            default -> Activity.playing(name);
        };

        MagicBalls.updateBotPresence(status, activity);

    }


}
