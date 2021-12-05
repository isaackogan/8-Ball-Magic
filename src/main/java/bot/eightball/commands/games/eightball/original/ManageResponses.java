package bot.eightball.commands.games.eightball.original;

import bot.eightball.commands.games.eightball.original.management.ResponseAdd;
import bot.eightball.commands.games.eightball.original.management.ResponseList;
import bot.eightball.commands.games.eightball.original.management.ResponseRemove;
import bot.eightball.core.discord.commands.SlashSubCommand;
import bot.eightball.core.discord.commands.SlashSubCommandGroup;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

import java.util.Arrays;
import java.util.List;

public class ManageResponses extends SlashSubCommandGroup {

    public ManageResponses(JDA instance) {
        super(instance);
    }

    @Override
    public SubcommandGroupData getCommandData() {
        return new SubcommandGroupData("manage", "Manage eight ball magic's /8ball ask responses!");
    }

    @Override
    public List<Class<? extends SlashSubCommand>> getSubCommandClasses() {
        return Arrays.asList(
                ResponseAdd.class,
                ResponseList.class,
                ResponseRemove.class
        );
    }

}
