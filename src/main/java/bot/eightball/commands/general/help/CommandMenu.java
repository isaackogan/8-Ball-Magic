package bot.eightball.commands.general.help;

import bot.eightball.core.MagicBalls;
import bot.eightball.core.discord.commands.SlashCommand;
import bot.eightball.core.discord.commands.SlashSubCommand;
import bot.eightball.core.discord.commands.SlashSubCommandGroup;
import bot.eightball.core.discord.interactions.ActionRowBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;

import java.util.List;

public class CommandMenu {

    /**
     * Custom ID for Help Menu
     */
    public static final String helpMenuComponentId = "helpMenuCommandSelection";
    /**
     * Action Row Components
     */
    private static final ActionRowBuilder selectMenu = generateCommandSelectMenu();
    /**
     * Main Menu Embed
     */
    private static final EmbedBuilder mainMenu = getMainMenuEmbed();
    /**
     * Interaction event for the menu (Union[Component, SlashCommand])
     */
    public final GenericInteractionCreateEvent interactionEvent;
    /**
     * JDA instance
     */
    public final JDA jda;
    /**
     * Message (Optional)
     */
    final Message message;
    /**
     * Command we're getting the info of
     */
    final SlashCommand command;

    /**
     * Command SubCommand
     */
    final SlashSubCommand subCommand;

    /**
     * SubCommand Grouo
     */
    final SlashSubCommandGroup subCommandGroup;

    /**
     * Constructor: Build Command Menu
     *
     * @param instance         JDA instance
     * @param interactionEvent Interaction to reply to
     * @param message          Original message (if editing message)
     * @param command          Command to display
     */
    public CommandMenu(
            JDA instance,
            GenericInteractionCreateEvent interactionEvent,
            Message message,
            SlashCommand command,
            SlashSubCommand subCommand,
            SlashSubCommandGroup subCommandGroup
    ) {
        this.subCommandGroup = subCommandGroup;
        this.interactionEvent = interactionEvent;
        this.subCommand = subCommand;
        this.jda = instance;
        this.message = message;
        this.command = command;
    }

    /**
     * Create action row containing all the commands we need
     *
     * @return Action row builder with our commands
     */
    private static ActionRowBuilder generateCommandSelectMenu() {
        final SelectionMenu.Builder menu = SelectionMenu.create(helpMenuComponentId)
                .setPlaceholder("Choose a command to get help with...");

        for (SlashCommand command : MagicBalls.commands.get(0)) {
            if (command.subCommands.size() < 1 && command.subCommandGroups.size() < 1) {
                menu.addOption(command.getUsage(), command.name);
            } else {
                for (SlashSubCommand subCommand : command.subCommands.values()) {
                    menu.addOption(String.format("/%s %s", command.name, subCommand.getUsage()), command.name + subCommand.name);
                }
                for (SlashSubCommandGroup subCommandGroup : command.subCommandGroups.values()) {
                    for (SlashSubCommand subCommand : subCommandGroup.subCommands.values()) {
                        menu.addOption(String.format(
                                "/%s %s %s", command.name, subCommandGroup.name, subCommand.getUsage()
                        ), command.name + subCommandGroup.name + subCommand.name);


                    }
                }
            }


        }

        return new ActionRowBuilder().addComponents(menu.build());
    }

    /**
     * Create the main menu static embed
     *
     * @return Main Menu Embed
     */
    private static EmbedBuilder getMainMenuEmbed() {
        return new EmbedBuilder()
                .setDescription("_ _\nFor help with a specific command, `check out the dropdown menu.`")
                .setThumbnail(MagicBalls.config.BOT_ICON_URL)
                .setColor(MagicBalls.getDefaultEmbedColour())
                .setAuthor("8 Ball Magic Help » Main Menu");
    }

    /**
     * Get the command menu for the specified command
     *
     * @return Command Embed
     */
    private EmbedBuilder getCommandEmbed(SlashSubCommand subCommand, SlashSubCommandGroup subCommandGroup) {

        // Create Base Embed
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(MagicBalls.getDefaultEmbedColour())
                .setThumbnail(MagicBalls.config.BOT_ICON_URL);


        if (subCommand == null) {
            embedBuilder.setAuthor(String.format("8 Ball Magic Help » /%s Command", command.name))
                    .setTitle(command.getUsage())
                    .setDescription(command.description + "\n\n_ _");
        } else {
            if (subCommandGroup == null) {
                embedBuilder.setAuthor(String.format("8 Ball Magic Help » /%s %s Command", command.name, subCommand.name))
                        .setTitle("/" + command.name + " " + subCommand.getUsage())
                        .setDescription(subCommand.description + "\n\n_ _");
            } else {
                embedBuilder.setAuthor(String.format("8 Ball Magic Help » /%s %s %s Command", command.name, subCommandGroup.name, subCommand.name))
                        .setTitle("/" + command.name + " " + subCommandGroup.name + " " + subCommand.getUsage())
                        .setDescription(subCommand.description + "\n\n_ _");
            }
        }

        // Add command options
        List<OptionData> optionDataList = (subCommand == null) ? command.data.getOptions() : subCommand.data.getOptions();
        for (int i = 0; i < optionDataList.size(); i++) {
            OptionData current = optionDataList.get(i);

            embedBuilder.addField(
                    String.format("Command Option %s", i + 1),
                    String.format("""
                            **Name:** `%s`
                            **Description:** `%s`
                                                        
                            _ _
                            """, current.getName(), current.getDescription())
                    , false
            );

        }

        return embedBuilder;

    }

    /**
     * Run the command menu
     */
    public ReplyAction run() {

        // Get either main menu or command embed
        EmbedBuilder embedBuilder = (command == null) ? mainMenu : getCommandEmbed(subCommand, subCommandGroup);

        // Edit the message & reply to event OR reply with new message
        if (message == null) {
            return interactionEvent.replyEmbeds(embedBuilder.build()).addActionRows(selectMenu.build());
        } else {
            message.editMessageEmbeds(embedBuilder.build()).queue();
            ((GenericComponentInteractionCreateEvent) interactionEvent).deferEdit().queue();
        }

        return null;

    }

}
