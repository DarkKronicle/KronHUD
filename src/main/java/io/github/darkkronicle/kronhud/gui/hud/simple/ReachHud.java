package io.github.darkkronicle.kronhud.gui.hud.simple;

import com.google.common.util.concurrent.AtomicDouble;
import io.github.darkkronicle.kronhud.config.KronConfig;
import io.github.darkkronicle.kronhud.config.KronInteger;
import io.github.darkkronicle.kronhud.gui.entry.SimpleTextHudEntry;
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
        return "3.45 blocks";
    }

    private static Vec3d compareTo(Vec3d compare, Vec3d test, AtomicDouble max) {
        double dist = compare.distanceTo(test);
        if (dist > max.get()) {
            max.set(dist);
            return test;
        }
        return compare;
    }

    public static double getAttackDistance(Entity attacking, Entity receiving) {
        Vec3d camera = attacking.getCameraPosVec(1);
        Vec3d rotation = attacking.getRotationVec(1);

        Vec3d maxPos = receiving.getPos();
        AtomicDouble max = new AtomicDouble(0);

        maxPos = compareTo(camera, maxPos.add(0, 0, receiving.getBoundingBox().maxZ), max);
        maxPos = compareTo(camera, maxPos.add(0, 0, receiving.getBoundingBox().minZ), max);
        maxPos = compareTo(camera, maxPos.add(0, receiving.getBoundingBox().maxY, 0), max);
        maxPos = compareTo(camera, maxPos.add(0, receiving.getBoundingBox().minY, 0), max);
        maxPos = compareTo(camera, maxPos.add(receiving.getBoundingBox().maxX, 0, 0), max);
        maxPos = compareTo(camera, maxPos.add(receiving.getBoundingBox().minX, 0, 0), max);

        // Max reach distance that want to account for
        double d = max.get() + .5;
        Vec3d possibleHits = camera.add(rotation.x * d, rotation.y * d, rotation.z * d);
        Box box = attacking.getBoundingBox().stretch(rotation.multiply(d)).expand(1.0, 1.0, 1.0);


        EntityHitResult result = ProjectileUtil.raycast(attacking, camera, possibleHits, box, entity -> entity.getId() == receiving.getId(), d);
        if (result == null || result.getEntity() == null) {
            // This should not happen...
            return -1;
        }
        return camera.distanceTo(result.getPos());
    }

    public void updateDistance(Entity attacking, Entity receiving) {
        double distance = getAttackDistance(attacking, receiving);
        if (distance < 0) {
            // This should not happen...
            currentDist = "NaN";
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
