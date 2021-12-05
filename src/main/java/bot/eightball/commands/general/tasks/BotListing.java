package bot.eightball.commands.general.tasks;

import bot.eightball.core.MagicBalls;
import bot.eightball.core.general.IShop;
import bot.eightball.utilities.Logger;
import bot.eightball.utilities.RequestManager;
import net.dv8tion.jda.api.entities.SelfUser;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class BotListing implements Runnable, IShop.Schedulable {

    @Override
    public ScheduledFuture<?> schedule(ScheduledExecutorService ses) {
        return ses.scheduleAtFixedRate(this, 0, MagicBalls.config.BOT_LISTING_STATS_UPDATE_INTERVAL, TimeUnit.HOURS);
    }

    @Override
    public void run() {
        SelfUser selfUser = Objects.requireNonNull(MagicBalls.shardManager.getShardById(0)).getSelfUser();

        // Must be the live version of the bot
        if (!selfUser.isVerified()) {
            return;
        }

        String body = String.format("""
                {
                    "server_count": %s,
                    "shard_count": %s
                }
                """, MagicBalls.getGuildCount(), MagicBalls.shardManager.getShardsTotal());

        try {
            URL url = new URL(String.format("https://top.gg/api/bots/%s/stats", selfUser.getIdLong()));
            CompletableFuture<Integer> future = RequestManager.asyncPostRequest(url, body, MagicBalls.tokens.topGG);
            future.handle((response, error) -> {
                if (response != null) {
                    return response;
                } else {
                    error.printStackTrace();
                    return null;
                }
            }).thenAccept(response -> {
                if (response == null) {
                    Logger.ERROR("Failed to update Top.GG Bot Listing stats");
                } else {
                    Logger.INFO("Submitted update request to TOP.GG with response code <%s>", response);
                }
            });

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

}
