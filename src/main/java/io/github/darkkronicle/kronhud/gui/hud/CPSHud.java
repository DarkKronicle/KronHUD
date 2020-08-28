package io.github.darkkronicle.kronhud.gui.hud;

import com.google.gson.annotations.Expose;
import io.github.darkkronicle.kronhud.KronHUD;
import io.github.darkkronicle.kronhud.hooks.KronHudHooks;
import io.github.darkkronicle.polish.api.EntryBuilder;
import io.github.darkkronicle.polish.gui.complexwidgets.EntryButtonList;
import io.github.darkkronicle.polish.gui.screens.BasicConfigScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.ArrayList;

public class CPSHud extends CleanHudEntry {
    private ArrayList<Long> clicks;
    public static final Identifier ID = new Identifier("kronhud", "cpshud");

    public CPSHud() {
      //  super(x, y, scale);
        super();
        clicks = new ArrayList<>();
        KronHudHooks.MOUSE_INPUT.register((window, button, action, mods) -> {
            if (!getS().clicksCPS) {
                clicks.add(Util.getMeasuringTimeMs());
            }
        });
        KronHudHooks.KEYBIND_PRESS.register((key) -> {
            if (getS().clicksCPS && (key.equals(client.options.keyAttack) || key.equals(client.options.keyUse))) {
                clicks.add(Util.getMeasuringTimeMs());
            }
        });
    }

    @Override
    public boolean tickable() {
        return true;
    }

    @Override
    public void tick() {
        removeOldClicks();
    }

    public void removeOldClicks() {
        long time = Util.getMeasuringTimeMs();
        clicks.removeIf((click) -> time - click > 1000);
    }

    @Override
    public String getValue() {
        return clicks.size() + " CPS";
    }

    @Override
    public String getPlaceholder() {
        return "0 CPS";
    }

    @Override
    public CPSStorage getS() {
        return KronHUD.storage.cpsHudStorage;
    }

    @Override
    public Identifier getID() {
        return ID;
    }

    @Override
    public Screen getConfigScreen() {
        EntryBuilder builder = EntryBuilder.create();
        EntryButtonList list = new EntryButtonList((client.getWindow().getScaledWidth() / 2) - 290, (client.getWindow().getScaledHeight() / 2) - 70, 580, 150, 1, false);
        list.addEntry(builder.startToggleEntry(new LiteralText("Enabled"), getStorage().enabled).setDimensions(20, 10).setSavable(val -> getStorage().enabled = val).build(list));
        list.addEntry(builder.startFloatSliderEntry(new LiteralText("Scale"), getStorage().scale, 0.2F, 1.5F).setWidth(80).setSavable(val -> getStorage().scale = val).build(list));
        list.addEntry(builder.startColorButtonEntry(new LiteralText("Background Color"), getStorage().backgroundColor).setSavable(val -> getStorage().backgroundColor = val).build(list));
        list.addEntry(builder.startColorButtonEntry(new LiteralText("Text Color"), getStorage().textColor).setSavable(val -> getStorage().textColor = val).build(list));
        list.addEntry(builder.startToggleEntry(new LiteralText("CPS based off of keybindings"), getS().clicksCPS).setSavable(val -> getS().clicksCPS = val).build(list));
        return new BasicConfigScreen(new LiteralText("CPSHud"), list) {
            @Override
            public void onClose() {
                super.onClose();
                KronHUD.storageHandler.saveDefaultHandling();
            }
        };
    }

    public static class CPSStorage extends Storage {
        public boolean clicksCPS;

        public CPSStorage() {
            super();
            clicksCPS = false;
        }

    }

    @Override
    public String getName() {
        return "CPSHUD";
    }
}
