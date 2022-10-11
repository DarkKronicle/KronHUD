package io.github.darkkronicle.kronhud.gui.hud.simple;

import io.github.darkkronicle.kronhud.gui.entry.SimpleTextHudEntry;
import io.github.darkkronicle.kronhud.mixins.AccessorMinecraftClient;
import net.minecraft.util.Identifier;

public class FPSHud extends SimpleTextHudEntry {
    public static final Identifier ID = new Identifier("kronhud", "fpshud");

    public FPSHud() {
        //super(x, y, scale);
        super();
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public String getValue() {
        return AccessorMinecraftClient.getCurrentFps() + " FPS";
    }

    @Override
    public String getPlaceholder() {
        return "60 FPS";
    }

}
