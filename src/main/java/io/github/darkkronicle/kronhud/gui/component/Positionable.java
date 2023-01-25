package io.github.darkkronicle.kronhud.gui.component;

import io.github.darkkronicle.kronhud.util.DrawPosition;
import io.github.darkkronicle.kronhud.util.Rectangle;

public interface Positionable {

    /**
     * Returns the draw position when render is unscaled. This does not reflect pixel values
     *
     * @return Position
     */
    default DrawPosition getPos() {
        return new DrawPosition(getRawX(), getRawY());
    }

    /**
     * Returns the draw position that matches the screen positioned. This reflects pixel values
     *
     * @return Position
     */
    default DrawPosition getTruePos() {
        return new DrawPosition(getRawTrueX(), getRawTrueY());
    }

    /**
     * Gets the x value that is stored locally. This may not be correct so use {@link #getX()}
     *
     * @return Unscaled X position
     */
    int getRawX();

    /**
     * Gets the y value of this object. This may not be correct so use {@link #getY()}
     *
     * @return Y Unscaled Y position
     */
    int getRawY();

    /**
     * Gets the x value of this object. This is unscaled so this may not be the exact pixel value
     *
     * @return Unscaled X position
     */
    default int getX() {
        return getRawX();
    }

    /**
     * Gets the y value of this object. This is unscaled so this may not be the exact pixel value
     *
     * @return Y Unscaled Y position
     */
    default int getY() {
        return getRawY();
    }

    /**
     * Gets the x value of this object in pixels (so it's scaled)
     *
     * @return True X position
     */
    default int getTrueX() {
        return getRawTrueX();
    }

    /**
     * Gets the y value of this object in pixels (so it's scaled)
     *
     * @return True Y position
     */
    default int getTrueY() {
        return getRawTrueY();
    }

    /**
     * Gets the x value of this object in pixels (so it's scaled). This may not be accurate so use {@link #getTrueX()}
     *
     * @return True X position
     */
    default int getRawTrueX() {
        return (int) (getX() * getScale());
    }

    /**
     * Gets the y value of this object in pixels (so it's scaled). This may not be accurate so use {@link #getTrueY()}
     *
     * @return True Y position
     */
    default int getRawTrueY() {
        return (int) (getY() * getScale());
    }

    /**
     * Set the raw x value (this does not mean pixels!)
     *
     * @param x Raw x value
     */
    void setX(int x);

    /**
     * Set the true x value, this is equivalent to pixels
     *
     * @param trueX Pixel value of x
     */
    default void setTrueX(int trueX) {
        setX((int) (trueX / getScale()));
    }

    /**
     * Set the raw y value (this does not mean pixels!)
     *
     * @param y Raw y value
     */
    void setY(int y);

    /**
     * Set the true y value, this is equivalent to pixels
     *
     * @param trueY Pixel value of y
     */
    default void setTrueY(int trueY) {
        setX((int) (trueY / getScale()));
    }

    /**
     * The scale to render this object in. By default, it is one
     *
     * @return The scale
     */
    default float getScale() {
        return 1f;
    }

    /**
     * The width of the object, unscaled
     *
     * @return The width of this object
     */
    int getWidth();

    /**
     * The width of the object, scaled. If scale > 1 this is greater than #getWidth. This is the width in pixels basically.
     *
     * @return Scaled width of the object
     */
    default int getTrueWidth() {
        return (int) (getWidth() * getScale());
    }

    /**
     * The height of the object, unscaled
     *
     * @return The height of this object
     */
    int getHeight();

    /**
     * The height of the object, scaled. If scale > 1 this is greater than #getHeight. This is the height in pixels basically.
     *
     * @return Scaled height of the object
     */
    default int getTrueHeight() {
        return (int) (getHeight() * getScale());
    }

    /**
     * Returns the rectangle that represents the boundaries of where this object will be rendered when scaled
     *
     * @return The scaled rectangle
     */
    default Rectangle getBounds() {
        DrawPosition pos = getPos();
        return new Rectangle(pos.x(), pos.y(), getWidth(), getHeight());
    }

    /**
     * Returns the rectangle that represents the boundaries of where this object will be rendered on the screen. This is unscaled
     *
     * @return The unscaled rectangle
     */
    default Rectangle getTrueBounds() {
        DrawPosition pos = getTruePos();
        return new Rectangle(pos.x(), pos.y(), getTrueWidth(), getTrueHeight());
    }

    /**
     * Updated when location/size changes
     */
    default void onBoundsUpdate() {

    }

    /**
     * If this component should be able to move
     *
     * @return Movable option
     */
    default boolean movable() {
        return true;
    }

    /**
     * Sets the raw width (this is unscaled)
     */
    void setWidth(int width);

    /**
     * Sets the raw height (this is unscaled)
     */
    void setHeight(int height);


    /**
     * The height modifier after taking raw position. This is really only used for {@link DynamicallyPositionable}
     *
     * @return The height to shift anchor
     */
    default int offsetHeight() {
        return 0;
    }

    /**
     * The height modifier after taking raw position. This is really only used for {@link DynamicallyPositionable}
     *
     * @return The width to shift anchor
     */
    default int offsetWidth() {
        return 0;
    }

    default int offsetTrueWidth() {
        return (int) (offsetWidth() * getScale());
    }

    default int offsetTrueHeight() {
        return (int) (offsetHeight() * getScale());
    }


}
