package bot.eightball.commands.games;

import bot.eightball.core.MagicBalls;
import bot.eightball.core.discord.commands.SlashSubCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class RollDice extends SlashSubCommand {

    private static final Random random = new Random();

    public RollDice(JDA instance) {
        super(instance);
    }

    @Override
    public void run(SlashCommandEvent commandEvent) {
        String reply = MagicBalls.staticResponses.dice_roll.get(random.nextInt(MagicBalls.staticResponses.dice_roll.size()));

        commandEvent.reply(String.format(
                "%s **Rolling Dice...** :game_die:", MagicBalls.getEmojiAsMention(MagicBalls.config.SEARCHING_EMOJI)
        )).queue(success -> {
            EmbedBuilder embed2 = new EmbedBuilder()
                    .setColor(MagicBalls.config.EMBED_COLOUR.get("INVISIBLE"))
                    .setTitle(reply);
            try {
                success.editOriginal(String.format(
                        "%s **Rolled Dice!** :rolling_eyes: :game_die:", MagicBalls.getEmojiAsMention(MagicBalls.config.ONLINE_EMOJI))
                ).setEmbeds(embed2.build()).queueAfter(5, TimeUnit.SECONDS);
            } catch (InsufficientPermissionException e) {
                commandEvent.getUser().openPrivateChannel()
                        .flatMap(channel -> channel.sendMessage(String.format(
                                "%s 8 Ball Magic cannot see this channel! Give it `View Channel` permissions in order to use the dice roll command.",
                                MagicBalls.getEmojiAsMention(MagicBalls.config.X_EMOJI)
                        )))
                        .queue(s -> {
                        }, f -> {
                        });
            }

        }, fail -> {
        });

    }

    @Override
    public SubcommandData getCommandData() {
        return new SubcommandData("roll-dice", "Feeling lucky? Roll a pair of dice!");
    }

}
