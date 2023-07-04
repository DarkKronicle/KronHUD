package io.github.darkkronicle.kronhud.gui.component;

import net.minecraft.client.gui.DrawContext;

public interface HudEntry extends Identifiable, Configurable, Positionable {

    boolean isEnabled();

    void setEnabled(boolean value);

    default boolean tickable() {
        return false;
    }

    default void tick() {}

    default void init() {}

    default double getDefaultX() {
        return 0;
    }

    default double getDefaultY() {
        return 0;
    }

    void render(DrawContext context, float delta);

    void renderPlaceholder(DrawContext context, float delta);

    void setHovered(boolean hovered);

}
