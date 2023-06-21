package io.github.darkkronicle.kronhud.gui;

import io.github.darkkronicle.kronhud.gui.component.HudEntry;
import io.github.darkkronicle.kronhud.gui.screen.HudEditScreen;
import io.github.darkkronicle.kronhud.util.Rectangle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class HudManager {

    private final static HudManager INSTANCE = new HudManager();

    public static HudManager getInstance() {
        return INSTANCE;
    }

    private final Map<Identifier, HudEntry> entries;
    private final MinecraftClient client;

    private HudManager() {
        this.entries = new LinkedHashMap<>();
        client = MinecraftClient.getInstance();
        ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
            for (HudEntry entry : getEntries()) {
                if (entry.tickable() && entry.isEnabled()) {
                    entry.tick();
                }
            }
        });
    }

    public void refreshAllBounds() {
        for (HudEntry entry : getEntries()) {
            entry.onBoundsUpdate();
        }
    }

    public HudManager add(HudEntry entry) {
        entries.put(entry.getId(), entry);
        return this;
    }

    public List<HudEntry> getEntriesSorted() {
        List<HudEntry> entryList = getEntries();
        entryList.sort(Comparator.comparing(hudEntry -> hudEntry.getId().toString()));
        return entryList;
    }

    public List<HudEntry> getEntries() {
        if (entries.size() > 0) {
            return new ArrayList<>(entries.values());
        }
        return Collections.emptyList();
    }

    public List<HudEntry> getMoveableEntries() {
        if (entries.size() > 0) {
            return entries.values().stream().filter((entry) -> entry.isEnabled() && entry.movable()).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public HudEntry get(Identifier identifier) {
        return entries.get(identifier);
    }

    public void render(DrawContext context, float delta) {
        if (!(client.currentScreen instanceof HudEditScreen) && !client.options.debugEnabled) {
            for (HudEntry hud : getEntries()) {
                if (hud.isEnabled()) {
                    hud.render(context, delta);
                }
            }
        }
    }

    public void renderPlaceholder(DrawContext context, float delta) {
        for (HudEntry hud : getEntries()) {
            if (hud.isEnabled()) {
                hud.renderPlaceholder(context, delta);
            }
        }
    }

    public Optional<HudEntry> getEntryXY(int x, int y) {
        for (HudEntry entry : getMoveableEntries()) {
            Rectangle bounds = entry.getTrueBounds();
            if (bounds.x() <= x && bounds.x() + bounds.width() >= x && bounds.y() <= y && bounds.y() + bounds.height() >= y) {
                return Optional.of(entry);
            }
        }
        return Optional.empty();
    }

    public List<Rectangle> getAllBounds() {
        ArrayList<Rectangle> bounds = new ArrayList<>();
        for (HudEntry entry : getMoveableEntries()) {
            bounds.add(entry.getTrueBounds());
        }
        return bounds;
    }
}
