package io.github.darkkronicle.kronhud.gui.hud;

import fi.dy.masa.malilib.config.IConfigBase;
import io.github.darkkronicle.kronhud.config.KronBoolean;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class SpeedHud extends CleanHudEntry {

    public static final Identifier ID = new Identifier("kronhud", "speedhud");
    private final static NumberFormat FORMATTER = new DecimalFormat("#0.00");
    private KronBoolean horizontal = new KronBoolean("horizontal", ID.getPath(), true);

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public String getValue() {
        Vec3d vec = MinecraftClient.getInstance().player.getVelocity();
        double speed;
        if (horizontal.getBooleanValue()) {
            speed = vec.horizontalLength();
        } else {
            speed = vec.length();
        }
        return FORMATTER.format(speed) + "BPT";
    }

    @Override
    public void addConfigOptions(List<IConfigBase> options) {
        super.addConfigOptions(options);
        options.add(horizontal);
    }

    @Override
    public String getPlaceholder() {
        return "0.95 BPT";
    }
}
