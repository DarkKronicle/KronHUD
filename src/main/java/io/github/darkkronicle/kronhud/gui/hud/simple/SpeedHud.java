package io.github.darkkronicle.kronhud.gui.hud.simple;

import io.github.darkkronicle.kronhud.config.KronBoolean;
import io.github.darkkronicle.kronhud.config.KronConfig;
import io.github.darkkronicle.kronhud.gui.entry.SimpleTextHudEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Objects;

public class SpeedHud extends SimpleTextHudEntry {

    public static final Identifier ID = new Identifier("kronhud", "speedhud");
    private final static NumberFormat FORMATTER = new DecimalFormat("#0.00");
    private final KronBoolean horizontal = new KronBoolean("horizontal", ID.getPath(), true);

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public String getValue() {
        ClientPlayerEntity player = Objects.requireNonNull(client.player);
        Vec3d vec = player.getVehicle() == null ? player.getVelocity() : player.getVehicle().getVelocity();
        if (client.player.isOnGround() && vec.y < 0) {
            vec = new Vec3d(vec.x, 0, vec.z);
        }
        double speed;
        if (horizontal.getValue()) {
            speed = vec.horizontalLength();
        } else {
            speed = vec.length();
        }
        return FORMATTER.format(speed * 20) + " BPS";
    }

    @Override
    public List<KronConfig<?>> getConfigurationOptions() {
        List<KronConfig<?>> options = super.getConfigurationOptions();
        options.add(horizontal);
        return options;
    }

    @Override
    public String getPlaceholder() {
        return "4.35 BPS";
    }
}
