package bot.eightball.core.discord.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.BaseCommand;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.List;

public interface ISlashInteraction {

    void run(SlashCommandEvent commandEvent);

    BaseCommand<CommandData> getCommandData();

    List<Permission> getCommandPermissions();

}
