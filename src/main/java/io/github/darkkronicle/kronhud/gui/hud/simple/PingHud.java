package io.github.darkkronicle.kronhud.gui.hud.simple;

import io.github.darkkronicle.kronhud.gui.entry.SimpleTextHudEntry;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;

public class PingHud extends SimpleTextHudEntry {
    public static final Identifier ID = new Identifier("kronhud", "pinghud");

    public PingHud() {
        super();
    }

    @Override
    public String getValue() {
        PlayerListEntry entry = client.player.networkHandler.getPlayerListEntry(client.player.getUuid());
        if (entry != null) {
            return entry.getLatency() + " ms";
        }
        return "0 ms";
    }

    @Override
    public String getPlaceholder() {
        return "68 ms";
    }

    @Override
    public Identifier getId() {
        return ID;
    }
}
