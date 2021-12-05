package bot.eightball.core;

import bot.eightball.commands.EightBall;
import bot.eightball.commands.games.eightball.original.StaticResponses;
import bot.eightball.commands.general.general.Legacy;
import bot.eightball.commands.general.tasks.AutoHighlight;
import bot.eightball.commands.general.tasks.BotListing;
import bot.eightball.commands.general.tasks.RotateStatus;
import bot.eightball.core.discord.commands.SlashCommand;
import bot.eightball.core.general.IShop;
import bot.eightball.utilities.Logger;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public class MagicBalls extends ListenerAdapter {

    public static final String version = "2.0.0";
    public static final HashMap<Integer, List<SlashCommand>> commands = new HashMap<>();
    public static final Set<ScheduledFuture<?>> tasks = new HashSet<>();
    public static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);
    public static ShardManager shardManager;
    public static String configPath = "config.yml";
    public static String tokensPath = "tokens.json";
    public static String responsesPath = "responses.json";
    public static Config config;
    public static Tokens tokens;
    public static StaticResponses staticResponses;

    /**
     * Entry Point for Bot
     *
     * @param args Ignored
     * @throws IOException Ignored
     */
    public static void main(String[] args) throws Exception {

        // Start the Bot
        action(PowerAction.START);

        // Accept power actions through Console
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String input = scanner.next();

            // Check if the action matches
            for (PowerAction action : PowerAction.values()) {
                if (action.matches(input)) {
                    // Try to do the action
                    try {
                        action(action);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    /**
     * Method for running power actions
     *
     * @param action Action to complete
     * @throws IOException Failed to complete
     */
    private static void action(PowerAction action) throws IOException {
        switch (action) {

            case START -> {
                Logger.INFO("JDA Starting Up...");

                config = Config.fromYamlFile(configPath);
                tokens = Tokens.fromJsonFile(tokensPath);
                staticResponses = StaticResponses.fromJsonFile(responsesPath);

                DefaultShardManagerBuilder shardManagerBuilder = DefaultShardManagerBuilder
                        .createDefault(tokens.bot)
                        .addEventListeners(new MagicBalls());

                try {
                    shardManager = shardManagerBuilder.build();
                } catch (LoginException e) {
                    e.printStackTrace();
                }


            }

            case STOP -> {

                if (shardManager != null) {
                    shardManager.shutdown();
                    Logger.INFO("JDA Shutting Down...");
                }

                shardManager = null;
                clearTasks();
            }

            case EXIT -> {
                action(PowerAction.STOP);
                System.exit(0);
            }

            case RELOAD -> {
                action(PowerAction.STOP);
                action(PowerAction.START);
            }

        }
    }

    /**
     * Generate Invite Link to Bot
     *
     * @param instance Shard Manager instance
     * @return Invite Link
     */
    public static String getBotInviteLink(JDA instance) {
        return "https://discord.com/api/oauth2/authorize?client_id=" + instance.getSelfUser().getId() + "&permissions=3288853584&scope=bot%20applications.commands";
    }

    /**
     * Get an Emoji as mention (fallback if null is :x:)
     *
     * @param emojiId ID of emoji
     * @return Emoji as a mentionable String
     */
    public static String getEmojiAsMention(long emojiId) {
        Emote emote = shardManager.getEmoteById(emojiId);
        return emote == null ? ":x:" : emote.getAsMention();
    }

    /**
     * Get the member count of the bot by adding the cached guild size of all guilds
     *
     * @return Approximate member count
     */
    public static long getMemberCount() {
        long memberCount = 0;

        for (Guild guild : shardManager.getGuildCache()) {
            memberCount += guild.getMemberCount();
        }

        return memberCount;

    }

    public static long getGuildCount() {
        long count = 0;

        for (JDA shard : shardManager.getShards()) {
            count += shard.getGuildCache().size();
        }

        return count;

    }

    /**
     * Get the default embed colour for a given guild
     *
     * @param guild Guild to inspect
     * @return Embed colour as an int (hex)
     */
    public static int getDefaultEmbedColour(Guild guild) {
        return config.EMBED_COLOUR.get("EIGHTBALL");
    }

    public static int getDefaultEmbedColour() {
        return getDefaultEmbedColour(null);
    }

    public static String getEmbedThumbnail(Guild ignored) {
        return config.BOT_ICON_URL;
    }

    private static void startTasks(List<IShop.Schedulable> tasks) {

        for (IShop.Schedulable task : tasks) {
            Logger.DEBUG("Started task %s", task.getClass().getSimpleName());
            MagicBalls.tasks.add(task.schedule(MagicBalls.scheduler));
        }

    }

    private static void clearTasks() {

        for (ScheduledFuture<?> scheduledFuture : MagicBalls.tasks) {
            scheduledFuture.cancel(true);
        }

        MagicBalls.tasks.clear();

    }

    public static void updateBotPresence(OnlineStatus status, Activity activity) {
        shardManager.setPresence(status, activity);
    }

    /**
     * On ready, register all slash commands
     *
     * @param event Ready status event
     */
    @SuppressWarnings("ArraysAsListWithZeroOrOneArgument")

    public void onReady(@NotNull ReadyEvent event) {

        if (event.getJDA().getShardInfo().getShardId() != shardManager.getShardsTotal() - 1) {
            return;
        }

        // Auto-Tasks
        final List<IShop.Schedulable> tasks = Arrays.asList(
                new RotateStatus(),
                new AutoHighlight(),
                new BotListing()
        );

        startTasks(tasks);

        // Event Listeners
        final List<ListenerAdapter> events = Arrays.asList(
                new Legacy()
        );

        syncListeners(events);

        // Local Shard Commands
        final List<Class<? extends SlashCommand>> commands = Arrays.asList(
                EightBall.class
        );

        syncCommands(commands);

    }

    /**
     * Sync all event listeners in an event listener list
     *
     * @param listeners A list of listener items
     */
    private void syncListeners(List<ListenerAdapter> listeners) {
        listeners.forEach(shardManager::addEventListener);
    }


    private SlashCommand getCommandInstance(Class<? extends SlashCommand> command, JDA shard, Guild guild) {
        try {
            Constructor<?> constructor = command.getConstructor(JDA.class, Guild.class);
            return (SlashCommand) constructor.newInstance(shard, guild);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Sync commands across all shards
     *
     * @param commandClasses Command class-list
     */
    private void syncCommands(List<Class<? extends SlashCommand>> commandClasses) {
        Guild guild = config.TEST_MODE ? shardManager.getGuildById(config.TEST_GUILD_ID) : null;

        // For each shard
        for (JDA shard : shardManager.getShards()) {

            // Sync all commands
            if (MagicBalls.config.SYNC_COMMANDS) {
                // Initialize Shard-Mode
                if (guild == null) {
                    shard.updateCommands().queue(success -> initializeCommands(shard, commandClasses, guild),
                            fail -> Logger.ERROR("Failed to clear old command list in shard-mode!"));
                }
                // Initialize Guild-Mode
                else {
                    guild.updateCommands().queue(success -> initializeCommands(shard, commandClasses, guild),
                            fail -> Logger.ERROR("Failed to clear old command guild-mode!"));
                }
            }
            // Don't sync commands
            else {
                initializeCommands(shard, commandClasses, guild);
            }


        }
    }

    /**
     * Initialize commands in a given shard
     *
     * @param shard          Shard to initialize
     * @param commandClasses Command class list
     * @param guild          Guild (if guild-specific)
     */
    private void initializeCommands(JDA shard, List<Class<? extends SlashCommand>> commandClasses, Guild guild) {
        List<SlashCommand> shardCommands = new ArrayList<>();

        // Load each command
        for (Class<? extends SlashCommand> commandClass : commandClasses) {
            SlashCommand command = this.getCommandInstance(commandClass, shard, guild);

            if (command != null) {
                if (config.SYNC_COMMANDS) command.upsertCommand(shard);
                shardCommands.add(command);
            }
        }

        // Update command list
        MagicBalls.commands.put(shard.getShardInfo().getShardId(), shardCommands);
    }

    /**
     * Enum for defining power actions
     */
    enum PowerAction {
        START("start"),
        STOP("stop"),
        RELOAD("reload", "restart"),
        EXIT("exit", "quit", "kill");

        private final String name;
        private final HashSet<String> aliases;

        PowerAction(String name, String... aliases) {
            this.name = name;
            this.aliases = new HashSet<>(Arrays.asList(aliases));
        }

        public boolean matches(String command) {
            for (String alias : aliases) {
                if (command.equalsIgnoreCase(alias)) {
                    return true;
                }
            }
            return command.equalsIgnoreCase(name);
        }

    }


}






