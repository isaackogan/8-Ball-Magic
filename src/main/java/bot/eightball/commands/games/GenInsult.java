package bot.eightball.commands.games;

import bot.eightball.core.MagicBalls;
import bot.eightball.core.discord.commands.SlashSubCommand;
import bot.eightball.utilities.RequestManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.net.MalformedURLException;
import java.net.URL;

public class GenInsult extends SlashSubCommand {

    private static URL insultApiURL;

    static {
        try {
            insultApiURL = new URL("https://evilinsult.com/generate_insult.php");
        } catch (MalformedURLException ignore) {

        }
    }

    public GenInsult(JDA instance) {
        super(instance);
    }

    @Override
    public void run(SlashCommandEvent commandEvent) {
        commandEvent.deferReply().queue(success -> RequestManager.asyncGetRequest(insultApiURL).handle((response, error) -> (error != null) ? "Idiot!" : response)
                .thenAccept(response -> {
                    EmbedBuilder embed = new EmbedBuilder()
                            .setTitle("So, you want to be insulted, eh?")
                            .setColor(MagicBalls.getDefaultEmbedColour(commandEvent.getGuild()))
                            .setDescription(response
                                    .replaceAll("&quot;", "\"")
                                    .replaceAll("--&gt;", ">")
                                    .replaceAll("--&lt;", "<")
                            );

                    success.editOriginalEmbeds(embed.build()).queue();
                }), failure -> {
        });
    }

    @Override
    public SubcommandData getCommandData() {
        return new SubcommandData("insult", "Receive a random, brutal insult from yours truly!");
    }

}
