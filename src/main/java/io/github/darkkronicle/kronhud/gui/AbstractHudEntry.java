package io.github.darkkronicle.kronhud.gui;

import io.github.darkkronicle.darkkore.config.options.Option;
import io.github.darkkronicle.darkkore.gui.Tab;
import io.github.darkkronicle.kronhud.config.DefaultOptions;
import io.github.darkkronicle.kronhud.config.KronBoolean;
import io.github.darkkronicle.kronhud.config.KronConfig;
import io.github.darkkronicle.kronhud.config.KronDouble;
import io.github.darkkronicle.kronhud.gui.component.HudEntry;
import io.github.darkkronicle.kronhud.util.ColorUtil;
import io.github.darkkronicle.kronhud.util.DrawPosition;
import io.github.darkkronicle.kronhud.util.DrawUtil;
import io.github.darkkronicle.kronhud.util.Rectangle;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractHudEntry extends DrawUtil implements HudEntry {

    protected final KronBoolean enabled = DefaultOptions.getEnabled();
    protected final KronDouble scale = DefaultOptions.getScale(this);
    private final KronDouble x = DefaultOptions.getX(this, getDefaultX());
    private final KronDouble y = DefaultOptions.getY(this, getDefaultY());

    private Rectangle trueBounds = null;
    private Rectangle renderBounds = null;
    private DrawPosition truePosition = null;
    private DrawPosition renderPosition;

    @Setter @Getter
    protected int width;
    @Setter @Getter
    protected int height;

    @Setter
    protected boolean hovered = false;
    protected MinecraftClient client = MinecraftClient.getInstance();

    public AbstractHudEntry(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public static int floatToInt(float percent, int max, int offset) {
        return MathHelper.clamp(Math.round((max - offset) * percent), 0, max);
    }

    public static float intToFloat(int current, int max, int offset) {
        return MathHelper.clamp((float) (current) / (max - offset), 0, 1);
    }

    public void init() {}

    public void renderPlaceholderBackground(DrawContext context) {
        if (hovered) {
            fillRect(context, getTrueBounds(), ColorUtil.SELECTOR_BLUE.withAlpha(100));
        } else {
            fillRect(context, getTrueBounds(), ColorUtil.WHITE.withAlpha(50));
        }
        outlineRect(context, getTrueBounds(), ColorUtil.BLACK);
    }

    public int getRawX() {
        return getPos().x();
    }

    public void setX(int x) {
        this.x.setValue((double) intToFloat(x, client.getWindow().getScaledWidth(),
                0
        ));
    }

    public int getRawY() {
        return getPos().y();
    }

    public void setY(int y) {
        this.y.setValue((double) intToFloat(y, client.getWindow().getScaledHeight(),
                0
        ));
    }

    public Rectangle getTrueBounds() {
        return trueBounds;
    }

    /**
     * Gets the hud's bounds when the matrix has already been scaled.
     *
     * @return The bounds.
     */
    public Rectangle getBounds() {
        return renderBounds;
    }

    @Override
    public float getScale() {
        return scale.getValue().floatValue();
    }

    public void scale(DrawContext context) {
        float scale = getScale();
        context.getMatrices().scale(scale, scale, 1);
    }

    @Override
    public DrawPosition getPos() {
        return renderPosition;
    }

    @Override
    public DrawPosition getTruePos() {
        return truePosition;
    }

    @Override
    public void onBoundsUpdate() {
        setBounds();
    }

    public void setBounds() {
        setBounds(getScale());
    }

    @Override
    public int getRawTrueX() {
        return truePosition.x();
    }

    @Override
    public int getRawTrueY() {
        return truePosition.y();
    }

    @Override
    public int getTrueWidth() {
        if (trueBounds == null) {
            return HudEntry.super.getTrueWidth();
        }
        return trueBounds.width();
    }

    @Override
    public int getTrueHeight() {
        if (trueBounds == null) {
            return HudEntry.super.getTrueHeight();
        }
        return trueBounds.height();
    }

    public void setBounds(float scale) {
        if (client.getWindow() == null) {
            truePosition = new DrawPosition(0, 0);
            renderPosition = new DrawPosition(0, 0);
            renderBounds = new Rectangle(0, 0, 1, 1);
            trueBounds = new Rectangle(0, 0, 1, 1);
            return;
        }
        int scaledX = floatToInt(x.getValue().floatValue(), client.getWindow().getScaledWidth(), 0) - offsetTrueWidth();
        int scaledY = floatToInt(y.getValue().floatValue(), client.getWindow().getScaledHeight(), 0) - offsetTrueHeight();
        if (scaledX < 0) {
            scaledX = 0;
        }
        if (scaledY < 0) {
            scaledY = 0;
        }
        int trueWidth = (int) (getWidth() * getScale());
        if (trueWidth < client.getWindow().getScaledWidth() && scaledX + trueWidth > client.getWindow().getScaledWidth()) {
            scaledX = client.getWindow().getScaledWidth() - trueWidth;
        }
        int trueHeight = (int) (getHeight() * getScale());
        if (trueHeight < client.getWindow().getScaledHeight() && scaledY + trueHeight > client.getWindow().getScaledHeight()) {
            scaledY = client.getWindow().getScaledHeight() - trueHeight;
        }
        truePosition = new DrawPosition(scaledX, scaledY);
        renderPosition = truePosition.divide(getScale());
        renderBounds = new Rectangle(renderPosition.x(), renderPosition.y(), getWidth(), getHeight());
        trueBounds = new Rectangle(scaledX, scaledY, (int) (getWidth() * getScale()), (int) (getHeight() * getScale()));
    }

    @Override
    public Tab toTab() {
        // Need to cast KronConfig to Option
        return Tab.ofOptions(getId(), getNameKey(), getConfigurationOptions().stream().map((o -> (Option<?>) o)).collect(Collectors.toList()));
    }

    /**
     * Returns a list of options that should be shown in configuration screens
     * @return List of options
     */
    @Override
    public List<KronConfig<?>> getConfigurationOptions() {
        List<KronConfig<?>> options = new ArrayList<>();
        options.add(enabled);
        options.add(scale);
        return options;
    }

    /**
     * Returns a list of options that should be saved. By default, this includes {@link #getConfigurationOptions()}
     *
     * @return
     */
    @Override
    public List<KronConfig<?>> getSaveOptions() {
        List<KronConfig<?>> options = getConfigurationOptions();
        options.add(x);
        options.add(y);
        return options;
    }

    @Override
    public boolean isEnabled() {
        return enabled.getValue();
    }

    @Override
    public void setEnabled(boolean value) {
        enabled.setValue(value);
    }

}
