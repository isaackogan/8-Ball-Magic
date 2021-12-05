package bot.eightball.commands;

import bot.eightball.commands.games.CoinFlip;
import bot.eightball.commands.games.GenInsult;
import bot.eightball.commands.games.PenisSize;
import bot.eightball.commands.games.RollDice;
import bot.eightball.commands.games.eightball.original.EightBallMagic;
import bot.eightball.commands.games.eightball.original.ManageResponses;
import bot.eightball.commands.general.general.Info;
import bot.eightball.commands.general.general.Invite;
import bot.eightball.commands.general.general.Ping;
import bot.eightball.commands.general.general.Vote;
import bot.eightball.commands.general.help.Help;
import bot.eightball.commands.utility.Poll;
import bot.eightball.commands.utility.Say;
import bot.eightball.core.discord.commands.SlashCommand;
import bot.eightball.core.discord.commands.SlashSubCommand;
import bot.eightball.core.discord.commands.SlashSubCommandGroup;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class EightBall extends SlashCommand {

    /**
     * Initialize
     *
     * @param instance JDA shard instance
     * @param guild    Optional guild-lock
     */
    public EightBall(JDA instance, Guild guild) {
        super(instance, guild);
        this.setGuildOnly(true);
    }

    @Override
    public void run(SlashCommandEvent commandEvent) {

    }

    @Override
    public List<Class<? extends SlashSubCommandGroup>> getSubCommandGroupClasses() {
        return List.of(
                ManageResponses.class
        );
    }


    @Override
    public List<Class<? extends SlashSubCommand>> getSubCommandClasses() {
        return Arrays.asList(
                Info.class,
                Invite.class,
                Vote.class,
                Ping.class,
                Help.class,
                Say.class,
                Poll.class,
                PenisSize.class,
                CoinFlip.class,
                GenInsult.class,
                RollDice.class,
                EightBallMagic.class
        );
    }


    @Override
    public CommandData getCommandData() {
        return new CommandData("8ball", "All 8ball-related commands");
    }

    @Override
    public void onSelectionMenu(@NotNull SelectionMenuEvent event) {
        Help.onSelectionMenu(event);
    }

}
