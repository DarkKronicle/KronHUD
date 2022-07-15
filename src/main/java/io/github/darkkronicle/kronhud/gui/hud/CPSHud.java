package io.github.darkkronicle.kronhud.gui.hud;

import fi.dy.masa.malilib.config.IConfigBase;
import io.github.darkkronicle.kronhud.config.KronBoolean;
import io.github.darkkronicle.kronhud.hooks.KronHudHooks;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.List;

public class CPSHud extends CleanHudEntry {
    public static final Identifier ID = new Identifier("kronhud", "cpshud");

    private KronBoolean fromKeybindings = new KronBoolean("cpskeybind", ID.getPath(), false);
    private KronBoolean rmb = new KronBoolean("rightcps", ID.getPath(), false);

    public CPSHud() {
        super();
        KronHudHooks.MOUSE_INPUT.register((window, button, action, mods) -> {
            if (!fromKeybindings.getBooleanValue()) {
                if (button == 0) {
                    ClickList.LEFT.click();
                } else if (button == 1) {
                    ClickList.RIGHT.click();
                }
            }
        });
        KronHudHooks.KEYBIND_PRESS.register((key) -> {
            if (fromKeybindings.getBooleanValue()) {
                if (key.equals(client.options.attackKey)) {
                    ClickList.LEFT.click();
                } else if (key.equals(client.options.useKey)) {
                    ClickList.RIGHT.click();
                }
            }
        });
    }

    @Override
    public boolean tickable() {
        return true;
    }

    @Override
    public void tick() {
        ClickList.LEFT.update();
        ClickList.RIGHT.update();
    }

    @Override
    public String getValue() {
        if (rmb.getBooleanValue()) {
            return ClickList.LEFT.clicks() + " | " + ClickList.RIGHT.clicks() + " CPS";
        } else {
            return ClickList.LEFT.clicks() + " CPS";
        }
    }

    @Override
    public String getPlaceholder() {
        if (rmb.getBooleanValue()) {
            return "0 | 0 CPS";
        } else {
            return "0 CPS";
        }
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public void addConfigOptions(List<IConfigBase> options) {
        super.addConfigOptions(options);
        options.add(fromKeybindings);
        options.add(rmb);
    }

    public static class ClickList {

        public static ClickList LEFT = new ClickList();
        public static ClickList RIGHT = new ClickList();
        private List<Long> clicks;

        public ClickList() {
            clicks = new ArrayList<Long>();
        }

        public void update() {
            clicks.removeIf((click) -> Util.getMeasuringTimeMs() - click > 1000);
        }

        public void click() {
            clicks.add(Util.getMeasuringTimeMs());
        }

        public int clicks() {
            return clicks.size();
        }

    }

}
