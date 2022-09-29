package io.github.darkkronicle.kronhud.gui;

import io.github.darkkronicle.darkkore.config.options.Option;
import io.github.darkkronicle.darkkore.gui.Tab;
import io.github.darkkronicle.darkkore.util.Color;
import io.github.darkkronicle.kronhud.config.KronBoolean;
import io.github.darkkronicle.kronhud.config.KronColor;
import io.github.darkkronicle.kronhud.config.KronConfig;
import io.github.darkkronicle.kronhud.config.KronDouble;
import io.github.darkkronicle.kronhud.util.ColorUtil;
import io.github.darkkronicle.kronhud.util.DrawPosition;
import io.github.darkkronicle.kronhud.util.DrawUtil;
import io.github.darkkronicle.kronhud.util.Rectangle;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractHudEntry extends DrawUtil {

    protected KronBoolean enabled = new KronBoolean("enabled", null, false);
    public KronDouble scale = new KronDouble("scale", null, 1, 0.1F, 2, this);
    protected KronColor textColor = new KronColor("textcolor", null, ColorUtil.WHITE);
    protected KronBoolean shadow = new KronBoolean("shadow", null, getShadowDefault());
    protected KronBoolean background = new KronBoolean("background", null, true);
    protected KronColor backgroundColor = new KronColor("backgroundcolor", null, new Color(0x64000000));
    private final KronDouble x = new KronDouble("x", null, getDefaultX(), 0, 1, this);
    private final KronDouble y = new KronDouble("y", null, getDefaultY(), 0, 1, this);

    private Rectangle scaledBounds = null;
    private Rectangle unscaledBounds = null;
    private DrawPosition scaledPosition = null;
    private DrawPosition unscaledPosition;

    public int width;
    public int height;
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

    public void renderHud(MatrixStack matrices) {
        render(matrices);
    }

    public abstract void render(MatrixStack matrices);

    public abstract void renderPlaceholder(MatrixStack matrices);

    public void renderPlaceholderBackground(MatrixStack matrices) {
        if (hovered) {
            fillRect(matrices, getScaledBounds(), ColorUtil.SELECTOR_BLUE.withAlpha(100));
        } else {
            fillRect(matrices, getScaledBounds(), ColorUtil.WHITE.withAlpha(50));
        }
        outlineRect(matrices, getScaledBounds(), ColorUtil.BLACK);
    }

    public abstract Identifier getId();

    public abstract boolean movable();

    public boolean tickable() {
        return false;
    }

    public void tick() {
    }

    public void setXY(int x, int y) {
        setX(x);
        setY(y);
    }

    public int getX() {
        return getScaledPos().x();
    }

    public void setX(int x) {
        this.x.setValue((double) intToFloat(x, client.getWindow().getScaledWidth(),
                Math.round(width * getScale())
        ));
    }

    public int getY() {
        return getScaledPos().y();
    }

    public void setY(int y) {
        this.y.setValue((double) intToFloat(y, client.getWindow().getScaledHeight(),
                Math.round(height * getScale())
        ));
    }

    protected double getDefaultX() {
        return 0;
    }

    protected float getDefaultY() {
        return 0;
    }

    protected boolean getShadowDefault() {
        return true;
    }

    public Rectangle getScaledBounds() {
        return scaledBounds;
    }

    /**
     * Gets the hud's bounds when the matrix has already been scaled.
     *
     * @return The bounds.
     */
    public Rectangle getBounds() {
        return unscaledBounds;
    }

    public float getScale() {
        return scale.getValue().floatValue();
    }

    public void scale(MatrixStack matrices) {
        matrices.scale(getScale(), getScale(), 1);
    }

    public DrawPosition getPos() {
        return unscaledPosition;
    }

    public DrawPosition getScaledPos() {
        return scaledPosition;
    }

    public void setBounds() {
        setBounds(getScale());
    }

    public void setBounds(float scale) {
        if (client.getWindow() == null) {
            scaledPosition = new DrawPosition(0, 0);
            unscaledPosition = new DrawPosition(0, 0);
            unscaledBounds = new Rectangle(0, 0, 1, 1);
            scaledBounds = new Rectangle(0, 0, 1, 1);
            return;
        }
        int scaledX = floatToInt(x.getValue().floatValue(), client.getWindow().getScaledWidth(),
                Math.round(width * scale)
        );
        int scaledY = floatToInt(y.getValue().floatValue(), client.getWindow().getScaledHeight(),
                Math.round(height * scale)
        );
        scaledPosition = new DrawPosition(scaledX, scaledY);
        unscaledPosition = scaledPosition.divide(getScale());
        scaledBounds = new Rectangle(getX(), getY(), Math.round(width * getScale()), Math.round(height * getScale()));
        unscaledBounds = new Rectangle(getX(), getY(), width, height);
    }

    public Tab getOptionWrapper() {
        // Need to cast KronConfig to Option
        return Tab.ofOptions(getId(), getNameKey(), getConfigurationOptions().stream().map((o -> (Option<?>) o)).collect(Collectors.toList()));
    }

    public List<KronConfig<?>> getConfigurationOptions() {
        List<KronConfig<?>> options = new ArrayList<>();
        options.add(enabled);
        options.add(scale);
        return options;
    }

    public List<KronConfig<?>> getSaveOptions() {
        List<KronConfig<?>> options = getConfigurationOptions();
        options.add(x);
        options.add(y);
        return options;
    }

    public boolean isEnabled() {
        return enabled.getValue();
    }

    public String getNameKey() {
        return "hud." + getId().getNamespace() + "." + getId().getPath();
    }

    public String getInfoKey() {
        return "hud." + getId().getNamespace() + "." + getId().getPath() + ".info";
    }

    public String getName() {
        return Text.translatable(getNameKey()).getString();
    }

    public void setEnabled(boolean value) {
        enabled.setValue(value);
    }

    public void init() {}
}
