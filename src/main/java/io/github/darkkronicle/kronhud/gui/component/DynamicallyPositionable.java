package io.github.darkkronicle.kronhud.gui.component;

import io.github.darkkronicle.kronhud.gui.layout.AnchorPoint;

/**
 * Represents an object that width/height can change, and it can react accordingly
 */
public interface DynamicallyPositionable extends Positionable {

    /**
     * Get the direction that this object is anchored in
     *
     * @return {@link AnchorPoint} that represents where the object is anchored in
     */
    AnchorPoint getAnchor();

    @Override
    default int getX() {
        return getAnchor().getX(getRawX(), getWidth());
    }

    @Override
    default int getY() {
        return getAnchor().getY(getRawY(), getHeight());
    }

    @Override
    default int getTrueX() {
        return getAnchor().getX(getRawTrueX(), getTrueWidth());
    }

    @Override
    default int getTrueY() {
        return getAnchor().getY(getRawTrueY(), getTrueHeight());
    }

    @Override
    default int offsetWidth() {
        return getAnchor().offsetWidth(getWidth());
    }

    @Override
    default int offsetHeight() {
        return getAnchor().offsetHeight(getHeight());
    }

}
