package io.github.darkkronicle.kronhud.gui.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class IPHud extends CleanHudEntry {

    public static final Identifier ID = new Identifier("kronhud", "iphud");

    public IPHud() {
        super(115, 13);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public String getValue() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.isInSingleplayer()) {
            return "singleplayer";
        }
        if (client.getCurrentServerEntry() == null) {
            return "none";
        }
        return client.getCurrentServerEntry().address;
    }

    @Override
    public String getPlaceholder() {
        return "singleplayer";
    }
}
