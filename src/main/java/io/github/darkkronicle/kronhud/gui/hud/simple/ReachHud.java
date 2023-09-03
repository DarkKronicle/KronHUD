package io.github.darkkronicle.kronhud.gui.hud.simple;

import com.google.common.util.concurrent.AtomicDouble;
import io.github.darkkronicle.kronhud.KronHUD;
import io.github.darkkronicle.kronhud.config.KronConfig;
import io.github.darkkronicle.kronhud.config.KronInteger;
import io.github.darkkronicle.kronhud.gui.entry.SimpleTextHudEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.List;

// https://github.com/AxolotlClient/AxolotlClient-mod/blob/4ae2678bfe9e0908be1a7a34e61e689c8005ae0a/src/main/java/io/github/axolotlclient/modules/hud/gui/hud/ReachDisplayHud.java
public class ReachHud extends SimpleTextHudEntry {

    public static final Identifier ID = new Identifier("kronhud", "reachhud");
    private final KronInteger decimalPlaces = new KronInteger("decimalplaces", ID.getPath(), 0, 0, 15);

    private String currentDist;
    private long lastTime = 0;

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public String getValue() {
        if (currentDist == null) {
            return "0 blocks";
        } else if (lastTime + 2000 < Util.getMeasuringTimeMs()) {
            currentDist = null;
            return "0 blocks";
        }
        return currentDist;
    }

    @Override
    public String getPlaceholder() {
        return "2.97 blocks";
    }

    public void updateDistance(double distance) {
        if (distance < 0) {
            return;
        }

        StringBuilder format = new StringBuilder("#");
        if (decimalPlaces.getValue() > 0) {
            format.append(".");
            format.append("0".repeat(Math.max(0, decimalPlaces.getValue())));
        }
        DecimalFormat formatter = new DecimalFormat(format.toString());
        formatter.setRoundingMode(RoundingMode.HALF_UP);
        currentDist = formatter.format(distance) + " blocks";
        lastTime = Util.getMeasuringTimeMs();
    }

    @Override
    public List<KronConfig<?>> getConfigurationOptions() {
        List<KronConfig<?>> options = super.getConfigurationOptions();
        options.add(decimalPlaces);
        options.remove(textColor);
        return options;
    }
}
