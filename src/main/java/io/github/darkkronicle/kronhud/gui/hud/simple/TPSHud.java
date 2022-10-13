package io.github.darkkronicle.kronhud.gui.hud.simple;

import io.github.darkkronicle.kronhud.gui.entry.SimpleTextHudEntry;
import net.minecraft.util.Identifier;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class TPSHud extends SimpleTextHudEntry {

    private long lastTick = -1;
    private long lastUpdate = -1;
    private double tps = -1;
    private final static NumberFormat FORMATTER = new DecimalFormat("#0.00");

    public final static Identifier ID = new Identifier("kronhud", "tpshud");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public String getValue() {
        if (tps < 0) {
            return "NaN";
        }
        return FORMATTER.format(tps) + " TPS";
    }

    @Override
    public String getPlaceholder() {
        return "20.00 TPS";
    }

    public void updateTime(long ticks) {
        if (lastTick < 0) {
            lastTick = ticks;
            lastUpdate = System.nanoTime();
            return;
        }

        long time = System.nanoTime();
        // In nano seconds, so 1000000000 in a second
        // Or 1000000 in a millisecond
        double elapsedMilli = (time - lastUpdate) / 1000000d;
        int passedTicks = (int) (ticks - lastTick);
        if (passedTicks > 0) {
            double mspt = elapsedMilli / passedTicks;

            tps = Math.min(1000 / mspt, 20);
        }

        lastTick = ticks;
        lastUpdate = time;

    }
}
