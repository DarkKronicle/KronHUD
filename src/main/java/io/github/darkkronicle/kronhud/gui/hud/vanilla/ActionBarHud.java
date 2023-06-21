package io.github.darkkronicle.kronhud.gui.hud.vanilla;

import io.github.darkkronicle.darkkore.util.Color;
import io.github.darkkronicle.kronhud.config.KronBoolean;
import io.github.darkkronicle.kronhud.config.KronConfig;
import io.github.darkkronicle.kronhud.config.KronInteger;
import io.github.darkkronicle.kronhud.gui.entry.TextHudEntry;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class ActionBarHud extends TextHudEntry {

    public static final Identifier ID = new Identifier("kronhud", "actionbarhud");

    public KronInteger timeShown = new KronInteger("timeshown", ID.getPath(), 60, 40, 300);
    public KronBoolean customTextColor = new KronBoolean("customtextcolor", ID.getPath(), false);

    private Text actionBar;
    private int ticksShown;
    private int color;
    private final String placeholder = "Action Bar";

    public ActionBarHud() {
        super(115, 13, false);
    }

    public void setActionBar(Text bar, int color) {
        this.actionBar = bar;
        this.color = color;
    }

    @Override
    public void renderComponent(DrawContext context, float delta) {
        if (ticksShown >= timeShown.getValue()) {
            this.actionBar = null;
        }
        Color vanillaColor = new Color(color);
        if (this.actionBar != null) {

            if (shadow.getValue()) {
                context.drawTextWithShadow(
                        client.textRenderer, actionBar,
                        getPos().x() + Math.round(getWidth() / 2F) - client.textRenderer.getWidth(actionBar) / 2,
                        getPos().y() + 3,
                        customTextColor.getValue() ? (
                                textColor.getValue().alpha() == 255 ?
                                new Color(
                                        textColor.getValue().red(),
                                        textColor.getValue().green(),
                                        textColor.getValue().blue(),
                                        vanillaColor.alpha()
                                ).color() :
                                textColor.getValue().color()
                        ) :
                        color
                );
            } else {

                context.drawText(
                        client.textRenderer, actionBar,
                        getPos().x() + Math.round(getWidth() / 2F) - (client.textRenderer.getWidth(actionBar) / 2),
                        getPos().y() + 3,
                        customTextColor.getValue() ? (
                                textColor.getValue().alpha() == 255 ?
                                new Color(
                                        textColor.getValue().red(),
                                        textColor.getValue().green(),
                                        textColor.getValue().blue(),
                                        vanillaColor.alpha()
                                ).color() :
                                textColor.getValue().color()
                        ) :
                        color, false
                );
            }
            ticksShown++;
        } else {
            ticksShown = 0;
        }
    }

    @Override
    public void renderPlaceholderComponent(DrawContext context, float delta) {
        context.drawText(
                client.textRenderer,
                placeholder,
                getPos().x() + Math.round(getWidth() / 2F) - client.textRenderer.getWidth(placeholder) / 2,
                getPos().y() + 3, -1, false
        );
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public List<KronConfig<?>> getConfigurationOptions() {
        List<KronConfig<?>> options = super.getConfigurationOptions();
        options.add(shadow);
        options.add(timeShown);
        options.add(customTextColor);
        options.add(textColor);
        return options;
    }
}
