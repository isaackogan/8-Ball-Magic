package bot.eightball.commands.general.help;

import bot.eightball.core.MagicBalls;
import bot.eightball.core.discord.commands.SlashCommand;
import bot.eightball.core.discord.commands.SlashSubCommand;
import bot.eightball.core.discord.commands.SlashSubCommandGroup;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Help extends SlashSubCommand {

    public final static int commandExpireMinutes = 5;
    public final static String commandChoiceOption = "command";
    public static final HashMap<String, String> currentMessages = new HashMap<>();


    public Help(JDA instance) {
        super(instance);
    }


    @Override
    public SubcommandData getCommandData() {
        return new SubcommandData("help", "Get help for various bot commands");
    }

    @Override
    public void run(SlashCommandEvent commandEvent) {

        ReplyAction replyAction = new CommandMenu(jda, commandEvent, null, null, null, null).run();

        replyAction.queue(interactionHook -> {
            if (interactionHook == null)
                return;

            // Retrieve Message
            interactionHook.retrieveOriginal().queue(message -> {

                currentMessages.put(message.getId(), commandEvent.getUser().getId());

                try {
                    // Remove components, remove message from cache after X minutes
                    message.editMessageComponents().queueAfter(commandExpireMinutes, TimeUnit.MINUTES,
                            result -> {
                                // Remove
                                try {
                                    currentMessages.remove(message.getId());
                                } catch (NullPointerException ignore) {
                                }
                            },
                            error -> {
                                // Remove Anyways
                                try {
                                    currentMessages.remove(message.getId());
                                } catch (NullPointerException ignore) {
                                }
                            }
                    );
                } catch (InsufficientPermissionException ignored) {
                }


            }, null);

        });


    }

    private static void tryCommandHelp(GenericInteractionCreateEvent commandEvent, String commandName, Message message, JDA jda) {
        for (SlashCommand command : MagicBalls.commands.get(jda.getShardInfo().getShardId())) {
            if (commandName.equals(command.name)) {
                new CommandMenu(jda, commandEvent, message, command, null, null).run();
                return;
            } else {

                for (SlashSubCommand subCommand : command.subCommands.values()) {

                    if (commandName.equals(command.name + subCommand.name)) {
                        new CommandMenu(jda, commandEvent, message, command, subCommand, null).run();
                        return;
                    }

                }

            }

            for (SlashSubCommandGroup subCommandGroup : command.subCommandGroups.values()) {

                for (SlashSubCommand subCommand : subCommandGroup.subCommands.values()) {
                    if (commandName.equals(command.name + subCommandGroup.name + subCommand.name)) {
                        new CommandMenu(jda, commandEvent, message, command, subCommand, subCommandGroup).run();
                        return;
                    }

                }


            }

        }

    }

    public static void onSelectionMenu(@NotNull SelectionMenuEvent event) {

        String owner = currentMessages.get(event.getMessageId());

        // Message is not cached
        if (owner == null) {
            return;
        }

        // Incorrect Owner
        if (!owner.equals(event.getUser().getId())) {
            event.reply(
                    String.format(
                            "%s You do not own this menu and therefore cannot interact with it.",
                            MagicBalls.getEmojiAsMention(MagicBalls.config.X_EMOJI)
                    )
            ).setEphemeral(true).queue();
            return;
        }

        final String componentId = event.getComponentId();
        if (!componentId.equals(CommandMenu.helpMenuComponentId)) {
            return;
        }

        // Get the option choice
        final SelectOption option;
        try {
            option = Objects.requireNonNull(event.getInteraction().getSelectedOptions()).get(0);
        } catch (NullPointerException e) {
            return;
        }

        final String optionValue = option.getValue();
        try {
            tryCommandHelp(event, optionValue, event.getMessage(), event.getJDA());
        } catch (InsufficientPermissionException e) {
            event.getUser().openPrivateChannel()
                    .flatMap(channel -> channel.sendMessage(String.format(
                            "%s 8 Ball Magic cannot see this channel! Give it `View Channel` permissions in order to use the help command.",
                            MagicBalls.getEmojiAsMention(MagicBalls.config.X_EMOJI)
                    )))
                    .queue(s -> {
                    }, f -> {
                    });
        }

    }

}
