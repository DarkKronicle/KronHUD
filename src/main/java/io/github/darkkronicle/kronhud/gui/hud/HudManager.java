package io.github.darkkronicle.kronhud.gui.hud;

import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;
import io.github.darkkronicle.kronhud.gui.screen.HudEditScreen;
import io.github.darkkronicle.kronhud.util.Rectangle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class HudManager {
    private final Map<Identifier, AbstractHudEntry> entries;
    private final MinecraftClient client;

    public HudManager() {
        this.entries = new LinkedHashMap<>();
        client = MinecraftClient.getInstance();
        ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
            for (AbstractHudEntry entry : getEntries()) {
                if (entry.tickable() && entry.isEnabled()) {
                    entry.tick();
                }
            }
        });
    }

    public HudManager add(AbstractHudEntry entry) {
        entries.put(entry.getId(), entry);
        return this;
    }

    public List<AbstractHudEntry> getEntries() {
        if (entries.size() > 0) {
            return new ArrayList<>(entries.values());
        }
        return Collections.emptyList();
    }

    public List<AbstractHudEntry> getMoveableEntries() {
        if (entries.size() > 0) {
            return entries.values().stream().filter((entry) -> entry.isEnabled() && entry.movable()).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public AbstractHudEntry get(Identifier identifier) {
        return entries.get(identifier);
    }

    public void render(MatrixStack matrices) {
        if (!(client.currentScreen instanceof HudEditScreen) && !client.options.debugEnabled) {
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
            Rectangle bounds = entry.getScaledBounds();
            if (bounds.x() <= x && bounds.x() + bounds.width() >= x && bounds.y() <= y && bounds.y() + bounds.height() >= y) {
                return Optional.of(entry);
            }
        }
        return Optional.empty();
    }

    public List<Rectangle> getAllBounds() {
        ArrayList<Rectangle> bounds = new ArrayList<>();
        for (AbstractHudEntry entry : getMoveableEntries()) {
            bounds.add(entry.getScaledBounds());
        }
        return bounds;
    }


}
