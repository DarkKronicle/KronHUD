package io.github.darkkronicle.kronhud.gui.hud;

import io.github.darkkronicle.kronhud.KronHUD;
import io.github.darkkronicle.kronhud.hooks.KronHudHooks;
import io.github.darkkronicle.polish.api.EntryBuilder;
import io.github.darkkronicle.polish.gui.complexwidgets.EntryButtonList;
import io.github.darkkronicle.polish.gui.screens.BasicConfigScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.ArrayList;

public class CPSHud extends CleanHudEntry {
    public static final Identifier ID = new Identifier("kronhud", "cpshud");
    private final ArrayList<Long> clicks;

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
        list.addEntry(builder.startToggleEntry(new TranslatableText("option.kronhud.enabled"), getStorage().enabled).setDimensions(20, 10).setSavable(val -> getStorage().enabled = val).build(list));
        list.addEntry(builder.startFloatSliderEntry(new TranslatableText("option.kronhud.scale"), getStorage().scale, 0.2F, 1.5F).setWidth(80).setSavable(val -> getStorage().scale = val).build(list));
        list.addEntry(builder.startColorButtonEntry(new TranslatableText("option.kronhud.backgroundcolor"), getStorage().backgroundColor).setSavable(val -> getStorage().backgroundColor = val).build(list));
        list.addEntry(builder.startColorButtonEntry(new TranslatableText("option.kronhud.textcolor"), getStorage().textColor).setSavable(val -> getStorage().textColor = val).build(list));
        list.addEntry(builder.startToggleEntry(new TranslatableText("option.kronhud.cpshud.cpskeybind"), getS().clicksCPS).setSavable(val -> getS().clicksCPS = val).build(list));
        return new BasicConfigScreen(getName(), list, () -> KronHUD.storageHandler.saveDefaultHandling());

    }

    @Override
    public Text getName() {
        return new TranslatableText("hud.kronhud.cpshud");
    }

    public static class CPSStorage extends Storage {
        public boolean clicksCPS;

        public CPSStorage() {
            super();
            clicksCPS = false;
        }

    }
}
