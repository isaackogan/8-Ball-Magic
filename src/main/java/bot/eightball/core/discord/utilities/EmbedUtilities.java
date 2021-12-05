package bot.eightball.core.discord.utilities;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Locale;

public class EmbedUtilities {

    public static EmbedBuilder messageAsEmbed(String message, int embedColour, String thumbnailURL) {
        return new EmbedBuilder()
                .setTitle(message)
                .setColor(embedColour)
                .setThumbnail(thumbnailURL);
    }

    public static EmbedBuilder messageAsEmbed(String message, int embedColour) {
        return messageAsEmbed(message, embedColour, null);
    }

    public static EmbedBuilder timeFooter(EmbedBuilder builder, Guild guild) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy | h:m a");

        return builder.setFooter(
                guild.getName() + " • " + sdf.format(Date.from(Instant.now())).toUpperCase(Locale.ROOT) + " (UTC)",
                guild.getIconUrl()
        );

    }

}
