package bot.eightball.commands.general.tasks;

import bot.eightball.core.MagicBalls;
import bot.eightball.core.general.IShop;
import bot.eightball.utilities.FileUtils;
import com.google.gson.JsonArray;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class AutoHighlight implements Runnable, IShop.Schedulable {

    public static List<String> cachedHighlights = new ArrayList<>();
    private static final Random random = new Random();

    private static JsonArray getStatusConfig() {
        try {
            return (JsonArray) FileUtils.loadJSONFile(MagicBalls.config.STATUS_PATH);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public ScheduledFuture<?> schedule(ScheduledExecutorService ses) {
        return ses.scheduleAtFixedRate(this, MagicBalls.config.HIGHLIGHT_UPDATE_INTERVAL, MagicBalls.config.HIGHLIGHT_UPDATE_INTERVAL, TimeUnit.HOURS);
    }

    private static String getHighlight(int attempts) {
        if (attempts > 5) {
            return null;
        }

        String highlight = cachedHighlights
                .get(random.nextInt(cachedHighlights.size()))
                .replaceAll("`", "")
                .replaceAll("\\*", "")
                .replaceAll("_", "")
                .strip();

        if (highlight.equals("")) {
            return getHighlight(attempts + 1);
        }

        return highlight;

    }

    @Override
    public void run() {
        String highlight = null;

        if (cachedHighlights.size() > 0) {
            highlight = getHighlight(0);
            cachedHighlights.clear();
        }

        if (highlight == null) {
            highlight = "```\nThere was no highlight for today!\n```";
        }

        Guild guild = null;
        TextChannel channel = null;

        try {
            guild = Objects.requireNonNull(MagicBalls.shardManager.getGuildById(MagicBalls.config.HIGHLIGHT_GUILD_ID));
            channel = Objects.requireNonNull(guild.getTextChannelById(MagicBalls.config.HIGHLIGHT_CHANNEL_ID));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (channel == null) {
            return;
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(MagicBalls.getDefaultEmbedColour(guild))
                .setThumbnail(MagicBalls.config.BOT_ICON_URL)
                .setDescription(String.format("""
                        `Today's Question`
                                                
                        %s
                        """, highlight));

        channel.sendMessageEmbeds(embed.build()).queue();
    }

}
