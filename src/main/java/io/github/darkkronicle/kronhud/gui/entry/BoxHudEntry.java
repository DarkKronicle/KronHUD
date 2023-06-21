package io.github.darkkronicle.kronhud.gui.entry;

import io.github.darkkronicle.darkkore.colors.ExtendedColor;
import io.github.darkkronicle.kronhud.config.KronBoolean;
import io.github.darkkronicle.kronhud.config.KronConfig;
import io.github.darkkronicle.kronhud.config.KronExtendedColor;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;
import io.github.darkkronicle.kronhud.util.ColorUtil;
import net.minecraft.client.gui.DrawContext;
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
    public void render(DrawContext context, float delta) {
        context.getMatrices().push();
        scale(context);
        if (backgroundAllowed) {
            if (background.getValue() && backgroundColor.getValue().alpha() > 0) {
                fillRect(context, getBounds(), backgroundColor.getValue());
            }
            if (outline.getValue() && outlineColor.getValue().alpha() > 0) {
                outlineRect(context, getBounds(), outlineColor.getValue());
            }
        }
        renderComponent(context, delta);
        context.getMatrices().pop();
    }

    public abstract void renderComponent(DrawContext context, float delta);

    @Override
    public void renderPlaceholder(DrawContext context, float delta) {
        context.getMatrices().push();
        renderPlaceholderBackground(context);
        outlineRect(context, getTrueBounds(), ColorUtil.BLACK);
        scale(context);
        renderPlaceholderComponent(context, delta);
        context.getMatrices().pop();
        hovered = false;
    }

    public abstract void renderPlaceholderComponent(DrawContext context, float delta);

    @Override
    public boolean movable() {
        return true;
    }
}
