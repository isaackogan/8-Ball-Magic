package bot.eightball.commands.general.general;

import bot.eightball.core.MagicBalls;
import bot.eightball.core.discord.commands.SlashSubCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.concurrent.TimeUnit;

public class Ping extends SlashSubCommand {

    public Ping(JDA instance) {
        super(instance);
    }

    @Override
    public void run(SlashCommandEvent commandEvent) {
        commandEvent
                .reply(String.format("%s **Bot's Ping** :mag_right: `Loading...`", MagicBalls.getEmojiAsMention(MagicBalls.config.SEARCHING_EMOJI)))
                .queue(success -> success.editOriginal(
                        String.format("%s **Bot's Ping** :mag_right: `%s ms`", MagicBalls.getEmojiAsMention(MagicBalls.config.ONLINE_EMOJI), this.jda.getGatewayPing())
                ).queueAfter(3, TimeUnit.SECONDS), failure -> {
                });
    }

    @Override
    public SubcommandData getCommandData() {
        return new SubcommandData("ping", "Get the Bot's ping to Discord's servers");
    }

}
