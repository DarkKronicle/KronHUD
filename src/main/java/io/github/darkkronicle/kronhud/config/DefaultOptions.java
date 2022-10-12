package io.github.darkkronicle.kronhud.config;

import io.github.darkkronicle.kronhud.gui.component.HudEntry;
import io.github.darkkronicle.kronhud.gui.layout.AnchorPoint;
import lombok.experimental.UtilityClass;

/**
 * A utility class to make creating similar options easier
 */
@UtilityClass
public class DefaultOptions {

    public static KronDouble getX(HudEntry toRefresh, double defaultX) {
        return new KronDouble("x", null, defaultX, 0, 1, toRefresh);
    }

    public static KronDouble getY(HudEntry toRefresh, double defaultY) {
        return new KronDouble("y", null, defaultY, 0, 1, toRefresh);
    }

    public static KronDouble getScale(HudEntry toRefresh) {
        return new KronDouble("scale", null, 1, 0.1, 2, toRefresh);
    }

    public static KronBoolean getEnabled() {
        return new KronBoolean("enabled", null, false);
    }

    public static KronOptionList<AnchorPoint> getAnchorPoint() {
        return getAnchorPoint(AnchorPoint.TOP_LEFT);
    }

    public static KronOptionList<AnchorPoint> getAnchorPoint(AnchorPoint defaultValue) {
        return new KronOptionList<>("anchorpoint", null, defaultValue);
    }

}
