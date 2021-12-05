package bot.eightball.core.discord.commands;

import bot.eightball.core.MagicBalls;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommandUtils {
    public static boolean permissionPredicate(SlashCommandEvent event, List<Permission> permissions) {
        if (permissions == null) {
            return true;
        }

        String X_EMOJI = MagicBalls.getEmojiAsMention(MagicBalls.config.X_EMOJI);
        Member member = event.getMember();

        if (member == null) {
            event.reply(
                    String.format("%s Error retrieving Guild information. Commands requiring permissions cannot be used in DMs.", X_EMOJI)
            ).setEphemeral(true).queue();
            return false;
        }

        if (!member.hasPermission(permissions)) {
            Set<String> required = new HashSet<>();

            for (Permission p : permissions)
                required.add(String.format("`%s`", p.getName()));

            event.reply(
                    String.format(
                            "%s You do not have permission for this command. Be sure you have the following permissions: %s",
                            X_EMOJI, String.join(",", required)
                    )
            ).setEphemeral(true).queue();
            return false;
        }
        return true;
    }
}
