package io.github.darkkronicle.kronhud.gui.component;

import net.minecraft.client.util.math.MatrixStack;

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

    void render(MatrixStack matrices, float delta);

    void renderPlaceholder(MatrixStack matrices, float delta);

    void setHovered(boolean hovered);

}
