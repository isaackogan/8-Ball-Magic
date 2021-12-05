package bot.eightball.core.discord.interactions;

import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ActionRowBuilder {

    List<Component> components = new ArrayList<>();
    private boolean disableButtons;

    public ActionRowBuilder() {
        this.disableButtons = false;
    }

    public ActionRowBuilder(boolean disableButtons) {
        this.disableButtons = disableButtons;
    }

    public boolean isDisableButtons() {
        return disableButtons;
    }

    public ActionRowBuilder setDisableButtons(boolean disableButtons) {
        this.disableButtons = disableButtons;
        return this;
    }

    public ActionRow build() {
        if (disableButtons) {
            List<Component> newComponents = new ArrayList<>();

            for (Component c : components) {
                if (c.getType() == Component.Type.BUTTON) {
                    Button t = (Button) c;

                    if (t.getStyle().equals(ButtonStyle.LINK)) {
                        newComponents.add(t);
                    } else {
                        newComponents.add(t.asDisabled());
                    }


                } else {
                    newComponents.add(c);

                }

            }

            components = newComponents;

        }

        return ActionRow.of(components);
    }

    public ActionRowBuilder addComponents(Component... component) {
        this.components.addAll(Arrays.stream(component).collect(Collectors.toList()));
        return this;
    }

}
