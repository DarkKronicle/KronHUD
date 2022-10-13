package io.github.darkkronicle.kronhud.gui.hud.simple;

import io.github.darkkronicle.kronhud.gui.entry.SimpleTextHudEntry;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class ComboHud extends SimpleTextHudEntry {

    public static final Identifier ID = new Identifier("kronhud", "combohud");

    private long lastTime = 0;
    private int target = -1;
    private int count = 0;

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public String getValue() {
        if (count == 0) {
            return "0 hits";
        }
        if (lastTime + 2000 < Util.getMeasuringTimeMs()) {
            count = 0;
            return "0 hits";
        }
        if (count == 1) {
            return "1 hit";
        }
        return count + " hits";
    }

    @Override
    public String getPlaceholder() {
        return "3 hits";
    }

    public void onEntityAttack(Entity attacked) {
        target = attacked.getId();
    }

    public void onEntityDamage(Entity entity) {
        if (entity.getId() == client.player.getId()) {
            target = -1;
            count = 0;
            return;
        }
        if (entity.getId() == target) {
            count++;
            lastTime = Util.getMeasuringTimeMs();
        }
    }

}
