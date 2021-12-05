package bot.eightball.commands.general.general;

import bot.eightball.core.MagicBalls;
import bot.eightball.core.discord.interactions.ActionRowBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class Legacy extends ListenerAdapter {

    public static String deleteLegacyMessageButton = "deleteLegacyMessageButton@2021";

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {

        if (!deleteLegacyMessageButton.equals(event.getComponentId())) {
            return;
        }

        event.getMessage().delete().queue(s -> {
        }, f -> {
        });
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        if (!event.isFromGuild()) {
            return;
        }


        // Check that they use our prefix
        if (!event.getMessage().getContentRaw().startsWith(MagicBalls.config.LEGACY_PREFIX)) return;

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Message Commands Removed")
                .setColor(MagicBalls.getDefaultEmbedColour(event.getGuild()))
                .setThumbnail(MagicBalls.getEmbedThumbnail(event.getGuild()))
                .setDescription(
                        """                                                        
                                In order to respect your Guild's privacy, 8 Ball Magic has deprecated all message-based commands.
                                                        
                                In order to use 8 Ball Magic, you will need to use discord's new slash-commands system.
                                Please **re-authenticate** to get started with 8 Ball Magic 2.0.
                                                                
                                Once you do, to ask a question, try `/8ball ask <question>`!
                                                
                                Regards,
                                8 Ball Magic Management
                                """
                );

        ActionRowBuilder builder = new ActionRowBuilder().addComponents(
                Button.of(ButtonStyle.LINK, MagicBalls.getBotInviteLink(event.getJDA()), "Re-Authenticate Bot"),
                Button.of(ButtonStyle.LINK, "https://support-dev.discord.com/hc/en-us/articles/4404772028055", "More Information"),
                Button.of(ButtonStyle.SECONDARY, deleteLegacyMessageButton, "Delete Message")
        );

        // Send the message
        try {
            event.getChannel().sendMessageEmbeds(embed.build()).setActionRows(builder.build())
                    .queue(success -> success.delete().queueAfter(5, TimeUnit.MINUTES, s -> {
                    }, f -> {
                    }), fail -> {
                    });
        } catch (ErrorResponseException ignore) {
            // No permissions in that channel
        }


    }

}
