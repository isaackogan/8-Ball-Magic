package bot.eightball.core.discord.commands;

import bot.eightball.core.MagicBalls;
import bot.eightball.utilities.Logger;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SlashCommand extends ListenerAdapter implements ISlashInteraction {

    // JDA
    public final JDA jda;
    private final Guild guild;

    // Subcommands & Groups
    public Map<String, SlashSubCommand> subCommands = new HashMap<>();
    public Map<String, SlashSubCommandGroup> subCommandGroups = new HashMap<>();

    // Command Data
    public CommandData data;
    public String name;
    public String description;
    public boolean guildOnly = false;

    /**
     * Initialize
     *
     * @param instance JDA shard instance
     * @param guild    Optional guild-lock
     */
    public SlashCommand(JDA instance, Guild guild) {

        // Abstract Attributes
        this.data = this.getCommandData();
        this.name = data.getName();
        this.description = data.getDescription();
        this.jda = instance;

        // Supplied Attributes
        this.guild = guild;

        // Add command to listeners
        instance.addEventListener(this);

        // Add subcommands
        List<Class<? extends SlashSubCommand>> subCommands = getSubCommandClasses();
        if (subCommands != null) {

            // Add subcommands
            for (Class<? extends SlashSubCommand> command : subCommands) {

                // Load Command
                try {
                    Constructor<?> constructor = command.getConstructor(JDA.class);
                    SlashSubCommand subCommand = (SlashSubCommand) constructor.newInstance(jda);

                    // Add subcommand data to data variable
                    this.data.addSubcommands(subCommand.getCommandData());

                    // Add subcommand to command subcommands
                    this.subCommands.put(subCommand.name, subCommand);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }

        List<Class<? extends SlashSubCommandGroup>> subCommandGroups = getSubCommandGroupClasses();

        if (subCommandGroups != null) {
            // Add Subcommand Groups
            for (Class<? extends SlashSubCommandGroup> group : subCommandGroups) {

                // Load Subcommand
                try {
                    Constructor<?> constructor = group.getConstructor(JDA.class);
                    SlashSubCommandGroup subCommandGroup = (SlashSubCommandGroup) constructor.newInstance(jda);

                    // Add subcommand to command subcommands
                    this.subCommandGroups.put(subCommandGroup.name, subCommandGroup);

                    // SubCommand Classes
                    List<Class<? extends SlashSubCommand>> subSubCommands = subCommandGroup.getSubCommandClasses();
                    if (subCommands != null) {
                        // Add subcommands
                        for (Class<? extends SlashSubCommand> command : subSubCommands) {

                            // Load Command
                            try {
                                Constructor<?> subConstructor = command.getConstructor(JDA.class);
                                SlashSubCommand subCommand = (SlashSubCommand) subConstructor.newInstance(jda);

                                // Add subcommand data to data variable
                                subCommandGroup.data.addSubcommands(subCommand.getCommandData());

                                // Add subcommand to command subcommands
                                subCommandGroup.subCommands.put(subCommand.name, subCommand);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                    }

                    // Add subcommand data to main command
                    this.data.addSubcommandGroups(subCommandGroup.data);


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

    }

    @Override
    public List<Permission> getCommandPermissions() {
        return null;
    }

    /**
     * Get a command's subcommands
     *
     * @return The subcommands
     */
    public List<Class<? extends SlashSubCommand>> getSubCommandClasses() {
        return null;
    }

    /**
     * Get subcommand GROUPS (lol)
     *
     * @return The subcommand groups
     */
    public List<Class<? extends SlashSubCommandGroup>> getSubCommandGroupClasses() {
        return null;
    }

    public void setGuildOnly(boolean guildOnly) {
        this.guildOnly = guildOnly;
    }

    /**
     * Appropriately dispatch the command
     *
     * @param event Slash event
     */
    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        final String eventName = event.getName();
        final String subCommandGroup = event.getSubcommandGroup();
        final String subCommandName = event.getSubcommandName();

        // Our main command
        if (eventName.equals(this.name)) {

            // Isn't available in DMs
            if (guildOnly && event.getGuild() == null) {
                event.reply(String.format(
                        "%s Sorry, but this command is only available in Guilds!",
                        MagicBalls.getEmojiAsMention(MagicBalls.config.X_EMOJI))
                ).queue();
                return;
            }

            // Normal Command
            if (subCommandGroup == null && subCommandName == null) {
                if (CommandUtils.permissionPredicate(event, this.getCommandPermissions())) {
                    this.run(event);
                }
                return;
            }

            // Subcommand Group
            if (subCommandGroup != null) {
                SlashSubCommandGroup group = this.subCommandGroups.get(subCommandGroup);
                if (group != null) {
                    if (CommandUtils.permissionPredicate(event, this.getCommandPermissions())) {
                        group.run(event);
                    }
                }
                return;

            }

            // Subcommand
            SlashSubCommand subCommand = this.subCommands.get(subCommandName);
            if (subCommand != null) {
                if (CommandUtils.permissionPredicate(event, this.getCommandPermissions())) {
                    subCommand.run(event);
                }
            }

        }
    }

    /**
     * Upsert/Sync command with Discord
     *
     * @param instance JDA instance
     */
    public void upsertCommand(JDA instance) {
        boolean nullGuild = this.guild == null;

        // Upsert command
        if (!nullGuild) guild.upsertCommand(this.data).queue();
        else instance.upsertCommand(this.data).queue();

        Logger.INFO(
                String.format(
                        "[CMD] Command Upsert /%s "
                                + (nullGuild ? "(Global: Shard ID #" + instance.getShardInfo().getShardId() + ")"
                                : "(" + this.guild.getName() + ")"), this.name
                )
        );

    }

    /**
     * Get Command Data
     *
     * @return Command Data
     */
    public abstract CommandData getCommandData();

    /**
     * Get Command Usage
     *
     * @return String usage
     */
    public String getUsage() {
        final CommandData commandData = this.data;

        final StringBuilder usage = new StringBuilder("/" + commandData.getName());
        List<OptionData> options = commandData.getOptions();

        for (OptionData option : options) {
            usage.append(" ").append(option.isRequired() ? "<" + option.getName() + ">" : "[" + option.getName() + "]");
        }

        return usage.toString();
    }


}
