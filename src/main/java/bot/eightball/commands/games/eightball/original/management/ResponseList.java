package bot.eightball.commands.games.eightball.original.management;

import bot.eightball.commands.games.eightball.original.Question;
import bot.eightball.commands.games.eightball.original.ResponseEntry;
import bot.eightball.commands.games.eightball.original.ResponseSQL;
import bot.eightball.core.MagicBalls;
import bot.eightball.utilities.FileUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

public class ResponseList extends ManageResponses {

    public ResponseList(JDA instance) {
        super(instance);
    }

    @Override
    public void manageResponseRun(SlashCommandEvent commandEvent, Question question) {
        commandEvent.deferReply().queue(success -> {
            List<ResponseEntry> categoryResponses = ResponseSQL.getResponseMessageEntries(question.tableName, Objects.requireNonNull(commandEvent.getGuild()).getIdLong());

            // No Responses
            if (categoryResponses.size() < 1) {
                success.editOriginal(String.format("%s You do not have any custom responses in this category!", MagicBalls.getEmojiAsMention(MagicBalls.config.X_EMOJI)))
                        .queue();
                return;
            }

            // Create File
            BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(FileUtils.getFileStream(MagicBalls.config.RESPONSES_TEMPLATE_PATH))));
            StringBuilder out = new StringBuilder();
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    out.append(line);
                }
            } catch (IOException ignore) {
            }
            String guildIconUrl = commandEvent.getGuild().getIconUrl();
            guildIconUrl = (guildIconUrl == null) ? MagicBalls.getEmbedThumbnail(commandEvent.getGuild()) : guildIconUrl;

            String fileString = out.toString()
                    .replaceAll("%embed-icon-url%", MagicBalls.getEmbedThumbnail(commandEvent.getGuild()))
                    .replaceAll("%guild-icon%", guildIconUrl)
                    .replaceAll("%category-name%", question.label)
                    .replaceAll("%guild-name%", commandEvent.getGuild().getName());

            StringBuilder tableData = new StringBuilder();

            for (ResponseEntry response : categoryResponses) {
                tableData.append(String.format(
                        """
                                <tr>
                                    <th>%s</th>
                                    <th>%s</th>
                                </tr>
                                """, response.id, response.message
                ));
            }

            fileString = fileString.replace("%table-data%", tableData);

            // Create Embed
            EmbedBuilder embed = new EmbedBuilder()
                    .setColor(MagicBalls.getDefaultEmbedColour(commandEvent.getGuild()))
                    .setTitle("Generated Response File")
                    .setDescription(
                            """
                                    The attached HTML file contains all of your custom responses in a neatly organized table for you to view!
                                                                        
                                    Open it in a browser (most likely will need to be on your computer) to view all responses and their IDs.
                                    """
                    );

            success
                    .sendFile(fileString.getBytes(StandardCharsets.UTF_8), "responses.html")
                    .addEmbeds(embed.build())
                    .addActionRow(Button.of(ButtonStyle.LINK, "https://fileinfo.com/extension/html", "What are HTML files?"))
                    .queue();

        }, fail -> {
        });


    }

    @Override
    public SubcommandData getCommandData() {
        SubcommandData data = new SubcommandData("list-responses", "List all responses for the /8ball ask command!");

        OptionData category = new OptionData(
                OptionType.STRING, "category", "The auto-response category you want to get the replies for!", true
        );

        int pos = 0;
        for (Question question : Question.values()) {
            pos++;
            category.addChoice(pos + ". " + question.description, question.tableName);
        }

        return data.addOptions(category);
    }
}
