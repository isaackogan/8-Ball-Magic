package bot.eightball.core.discord.commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SlashSubCommandGroup {

    // JDA
    public final JDA jda;
    // Sub Commands
    public Map<String, SlashSubCommand> subCommands = new HashMap<>();
    // Command Data
    public SubcommandGroupData data;
    public String name;
    public String description;

    public SlashSubCommandGroup(JDA instance) {

        // Abstract Attributes
        this.data = this.getCommandData();
        this.name = data.getName();
        this.description = data.getDescription();
        this.jda = instance;

    }

    public void run(SlashCommandEvent commandEvent) {
        SlashSubCommand subCommand = this.subCommands.get(commandEvent.getSubcommandName());
        if (subCommand != null) {
            if (CommandUtils.permissionPredicate(commandEvent, subCommand.getCommandPermissions())) {
                subCommand.run(commandEvent);
            }
        }


    }

    public abstract SubcommandGroupData getCommandData();

    public abstract List<Class<? extends SlashSubCommand>> getSubCommandClasses();

}
