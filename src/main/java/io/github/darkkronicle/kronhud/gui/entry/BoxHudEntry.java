package io.github.darkkronicle.kronhud.gui.entry;

import io.github.darkkronicle.darkkore.colors.ExtendedColor;
import io.github.darkkronicle.kronhud.config.KronBoolean;
import io.github.darkkronicle.kronhud.config.KronConfig;
import io.github.darkkronicle.kronhud.config.KronExtendedColor;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;
import io.github.darkkronicle.kronhud.util.ColorUtil;
import net.minecraft.client.util.math.MatrixStack;

import java.util.List;

public abstract class BoxHudEntry extends AbstractHudEntry {

    private final boolean backgroundAllowed;

    protected KronBoolean background = new KronBoolean("background", null, true);
    protected KronExtendedColor backgroundColor = new KronExtendedColor("backgroundcolor", null, new ExtendedColor(0x64000000, ExtendedColor.ChromaOptions.getDefault()));

    protected KronBoolean outline = new KronBoolean("outline", null, false);
    protected KronExtendedColor outlineColor = new KronExtendedColor("outlinecolor", null, new ExtendedColor(-1, ExtendedColor.ChromaOptions.getDefault()));

    public BoxHudEntry(int width, int height, boolean backgroundAllowed) {
        super(width, height);
        this.backgroundAllowed = backgroundAllowed;
        if (!backgroundAllowed) {
            background = null;
            backgroundColor = null;
            outline = null;
            outlineColor = null;
        }
    }

    @Override
    public List<KronConfig<?>> getConfigurationOptions() {
        List<KronConfig<?>> options = super.getConfigurationOptions();
        if (backgroundAllowed) {
            options.add(background);
            options.add(backgroundColor);
            options.add(outline);
            options.add(outlineColor);
        }
        return options;
    }

    @Override
    public void render(MatrixStack matrices, float delta) {
        matrices.push();
        scale(matrices);
        if (backgroundAllowed) {
            if (background.getValue() && backgroundColor.getValue().alpha() > 0) {
                fillRect(matrices, getRenderBounds(), backgroundColor.getValue());
            }
            if (outline.getValue() && outlineColor.getValue().alpha() > 0) {
                outlineRect(matrices, getRenderBounds(), outlineColor.getValue());
            }
        }
        renderComponent(matrices, delta);
        matrices.pop();
    }

    public abstract void renderComponent(MatrixStack matrices, float delta);

    @Override
    public void renderPlaceholder(MatrixStack matrices, float delta) {
        matrices.push();
        renderPlaceholderBackground(matrices);
        outlineRect(matrices, getTrueBounds(), ColorUtil.BLACK);
        scale(matrices);
        renderPlaceholderComponent(matrices, delta);
        matrices.pop();
        hovered = false;
    }

    public abstract void renderPlaceholderComponent(MatrixStack matrices, float delta);

    @Override
    public boolean movable() {
        return true;
    }
}
