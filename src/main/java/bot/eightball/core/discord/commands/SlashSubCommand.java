package bot.eightball.core.discord.commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.List;

public abstract class SlashSubCommand implements ISlashInteraction {

    // JDA
    public final JDA jda;

    // Command Data
    public SubcommandData data;
    public String name;
    public String description;

    public SlashSubCommand(JDA instance) {
        
        // Abstract Attributes
        this.data = this.getCommandData();
        this.name = data.getName();
        this.description = data.getDescription();
        this.jda = instance;

    }

    @Override
    public abstract SubcommandData getCommandData();

    public String getUsage() {
        final SubcommandData commandData = this.data;

        final StringBuilder usage = new StringBuilder(commandData.getName());
        List<OptionData> options = commandData.getOptions();

        for (OptionData option : options) {
            usage.append(" ").append(option.isRequired() ? "<" + option.getName() + ">" : "[" + option.getName() + "]");
        }

        return usage.toString();
    }

    @Override
    public List<Permission> getCommandPermissions() {
        return null;
    }

}
