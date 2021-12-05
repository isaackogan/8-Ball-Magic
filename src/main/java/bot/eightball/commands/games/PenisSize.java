package bot.eightball.commands.games;

import bot.eightball.core.MagicBalls;
import bot.eightball.core.discord.commands.SlashSubCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.Collections;
import java.util.Random;

public class PenisSize extends SlashSubCommand {
    private static final Random rand = new Random();

    public PenisSize(JDA instance) {
        super(instance);
    }

    public String generateRandomPenis() {
        return "8" + String.join("", Collections.nCopies(rand.nextInt(35), "=")) + "D";
    }

    @Override
    public void run(SlashCommandEvent commandEvent) {

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("So, you want to know your PP size?")
                .setColor(MagicBalls.getDefaultEmbedColour(commandEvent.getGuild()))
                .setDescription(generateRandomPenis());

        commandEvent.replyEmbeds(embed.build()).queue();

    }

    @Override
    public SubcommandData getCommandData() {
        return new SubcommandData("pp-size", "Get the size of your long john. Your schlong. Enough euphemisms... your 8==D.");
    }
}
