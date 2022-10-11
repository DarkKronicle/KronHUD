package io.github.darkkronicle.kronhud.gui;

import io.github.darkkronicle.darkkore.colors.ExtendedColor;
import io.github.darkkronicle.darkkore.config.options.Option;
import io.github.darkkronicle.darkkore.gui.Tab;
import io.github.darkkronicle.kronhud.config.*;
import io.github.darkkronicle.kronhud.util.ColorUtil;
import io.github.darkkronicle.kronhud.util.DrawPosition;
import io.github.darkkronicle.kronhud.util.DrawUtil;
import io.github.darkkronicle.kronhud.util.Rectangle;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;
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
    protected KronColor textColor = new KronColor("textcolor", null, new ExtendedColor(ColorUtil.WHITE, ExtendedColor.ChromaOptions.getDefault()));
    protected KronBoolean shadow = new KronBoolean("shadow", null, getShadowDefault());
    protected KronBoolean background = new KronBoolean("background", null, true);
    protected KronExtendedColor backgroundColor = new KronExtendedColor("backgroundcolor", null, new ExtendedColor(0x64000000, ExtendedColor.ChromaOptions.getDefault()));

    protected KronBoolean outline = new KronBoolean("outline", null, false);
    protected KronExtendedColor outlineColor = new KronExtendedColor("outlinecolor", null, new ExtendedColor(-1, ExtendedColor.ChromaOptions.getDefault()));

    private final KronDouble x = new KronDouble("x", null, getDefaultX(), 0, 1, this);
    private final KronDouble y = new KronDouble("y", null, getDefaultY(), 0, 1, this);

    private Rectangle trueBounds = null;
    private Rectangle renderBounds = null;
    private DrawPosition truePosition = null;
    private DrawPosition renderPosition;

    @Getter
    protected int width;
    @Getter
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

    public void renderHud(MatrixStack matrices, float delta) {
        render(matrices, delta);
    }

    public abstract void render(MatrixStack matrices, float delta);

    public abstract void renderPlaceholder(MatrixStack matrices, float delta);

    public void renderPlaceholderBackground(MatrixStack matrices) {
        if (hovered) {
            fillRect(matrices, getTrueBounds(), ColorUtil.SELECTOR_BLUE.withAlpha(100));
        } else {
            fillRect(matrices, getTrueBounds(), ColorUtil.WHITE.withAlpha(50));
        }
        outlineRect(matrices, getTrueBounds(), ColorUtil.BLACK);
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
        return getPos().x();
    }

    public void setX(int x) {
        this.x.setValue((double) intToFloat(x, client.getWindow().getScaledWidth(),
                Math.round(getWidth() * getScale())
        ));
    }

    public int getY() {
        return getPos().y();
    }

    public void setY(int y) {
        this.y.setValue((double) intToFloat(y, client.getWindow().getScaledHeight(),
                Math.round(getHeight() * getScale())
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

    public Rectangle getTrueBounds() {
        return trueBounds;
    }

    /**
     * Gets the hud's bounds when the matrix has already been scaled.
     *
     * @return The bounds.
     */
    public Rectangle getRenderBounds() {
        return renderBounds;
    }

    public float getScale() {
        return scale.getValue().floatValue();
    }

    public void scale(MatrixStack matrices) {
        matrices.scale(getScale(), getScale(), 1);
    }

    public DrawPosition getPos() {
        return renderPosition;
    }

    public DrawPosition getTruePos() {
        return truePosition;
    }

    public void onSizeUpdate() {
        setBounds();
    }

    public void setBounds() {
        setBounds(getScale());
    }

    public void setBounds(float scale) {
        if (client.getWindow() == null) {
            truePosition = new DrawPosition(0, 0);
            renderPosition = new DrawPosition(0, 0);
            renderBounds = new Rectangle(0, 0, 1, 1);
            trueBounds = new Rectangle(0, 0, 1, 1);
            return;
        }
        int scaledX = floatToInt(x.getValue().floatValue(), client.getWindow().getScaledWidth(),
                Math.round(getWidth() * scale)
        );
        int scaledY = floatToInt(y.getValue().floatValue(), client.getWindow().getScaledHeight(),
                Math.round(getHeight() * scale)
        );
        truePosition = new DrawPosition(scaledX, scaledY);
        renderPosition = truePosition.divide(getScale());
        trueBounds = new Rectangle(scaledX, scaledY, Math.round(getWidth() * getScale()), Math.round(getHeight() * getScale()));
        renderBounds = new Rectangle(renderPosition.x(), renderPosition.y(), getWidth(), getHeight());
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
