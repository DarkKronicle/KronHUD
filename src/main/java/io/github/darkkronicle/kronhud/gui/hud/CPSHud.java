package io.github.darkkronicle.kronhud.gui.hud;

import io.github.darkkronicle.darkkore.config.options.Option;
import io.github.darkkronicle.kronhud.config.KronBoolean;
import io.github.darkkronicle.kronhud.config.KronConfig;
import io.github.darkkronicle.kronhud.hooks.KronHudHooks;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.List;

public class CPSHud extends CleanHudEntry {
    public static final Identifier ID = new Identifier("kronhud", "cpshud");

    private final KronBoolean fromKeybindings = new KronBoolean("cpskeybind", ID.getPath(), false);
    private final KronBoolean rmb = new KronBoolean("rightcps", ID.getPath(), false);

    public CPSHud() {
        super();
        KronHudHooks.MOUSE_INPUT.register((window, button, action, mods) -> {
            if (!fromKeybindings.getValue()) {
                if (button == 0) {
                    ClickList.LEFT.click();
                } else if (button == 1) {
                    ClickList.RIGHT.click();
                }
            }
        });
        KronHudHooks.KEYBIND_PRESS.register((key) -> {
            if (fromKeybindings.getValue()) {
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
        if (rmb.getValue()) {
            return ClickList.LEFT.clicks() + " | " + ClickList.RIGHT.clicks() + " CPS";
        } else {
            return ClickList.LEFT.clicks() + " CPS";
        }
    }

    @Override
    public String getPlaceholder() {
        if (rmb.getValue()) {
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
    public List<KronConfig<?>> getOptions() {
        List<KronConfig<?>> options = super.getOptions();
        options.add(fromKeybindings);
        options.add(rmb);
        return options;
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
