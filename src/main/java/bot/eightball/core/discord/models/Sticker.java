package bot.eightball.core.discord.models;

import java.util.List;

@SuppressWarnings("ClassCanBeRecord")
public class Sticker {

    public final String name;
    public final String description;
    public final List<String> tags;
    public final byte[] file;

    public Sticker(String name, String description, List<String> tags, byte[] file) {
        this.name = name;
        this.description = description;
        this.tags = tags;
        this.file = file;
    }


}
