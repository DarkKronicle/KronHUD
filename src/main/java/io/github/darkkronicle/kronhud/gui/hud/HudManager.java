package io.github.darkkronicle.kronhud.gui.hud;

import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;
import io.github.darkkronicle.kronhud.gui.screen.SetScreen;
import io.github.darkkronicle.polish.util.SimpleRectangle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class HudManager {
    private final HashMap<Identifier, AbstractHudEntry> entries;
    private final MinecraftClient client;
    private boolean placeholder = false;

    public HudManager() {
        this.entries = new HashMap<>();
        client = MinecraftClient.getInstance();
        ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
            for (AbstractHudEntry entry : getEntries()) {
                placeholder = client.currentScreen instanceof SetScreen;
                if (entry.tickable() && entry.isEnabled()) {
                    entry.tick();
                }
            }
        });
    }

    public HudManager add(AbstractHudEntry entry) {
        entries.put(entry.getID(), entry);
        return this;
    }

    public List<AbstractHudEntry> getEntries() {
        if (entries.size() > 0) {
            return new ArrayList<>(entries.values());
        }
        return new ArrayList<>();
    }

    public List<AbstractHudEntry> getMoveableEntries() {
        if (entries.size() > 0) {
            ArrayList<AbstractHudEntry> moves = new ArrayList<>(entries.values());
            moves.removeIf(hud -> !hud.moveable());
            return moves;
        }
        return new ArrayList<>();
    }

    public AbstractHudEntry get(Identifier identifier) {
        return entries.get(identifier);
    }

    public void render(MatrixStack matrices) {
        if (!placeholder) {
            for (AbstractHudEntry hud : getEntries()) {
                if (hud.isEnabled()) {
                    hud.renderHud(matrices);
                }
            }
        }
    }

    public void renderPlaceholder(MatrixStack matrices) {
        for (AbstractHudEntry hud : getEntries()) {
            if (hud.isEnabled()) {
                hud.renderPlaceholder(matrices);
            }
        }
    }

    public Optional<AbstractHudEntry> getEntryXY(int x, int y) {
        for (AbstractHudEntry entry : getMoveableEntries()) {
            if (entry.getX() <= x && entry.getX() + entry.width * entry.getStorage().scale >= x && entry.getY() <= y && entry.getY() + entry.height * entry.getStorage().scale >= y) {
                return Optional.of(entry);
            }
        }
        return Optional.empty();
    }

    public List<SimpleRectangle> getAllBounds() {
        ArrayList<SimpleRectangle> bounds = new ArrayList<>();
        for (AbstractHudEntry entry : getMoveableEntries()) {
            bounds.add(entry.getBounds());
        }
        return bounds;
    }


}
