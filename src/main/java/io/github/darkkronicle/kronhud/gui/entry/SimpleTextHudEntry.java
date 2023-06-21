package io.github.darkkronicle.kronhud.gui.entry;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.darkkronicle.darkkore.util.Color;
import io.github.darkkronicle.kronhud.config.DefaultOptions;
import io.github.darkkronicle.kronhud.config.KronConfig;
import io.github.darkkronicle.kronhud.config.KronInteger;
import io.github.darkkronicle.kronhud.config.KronOptionList;
import io.github.darkkronicle.kronhud.gui.component.DynamicallyPositionable;
import io.github.darkkronicle.kronhud.gui.layout.AnchorPoint;
import io.github.darkkronicle.kronhud.gui.layout.Justification;
import io.github.darkkronicle.kronhud.util.DrawPosition;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import org.lwjgl.opengl.GL11;

import java.util.List;

public abstract class SimpleTextHudEntry extends TextHudEntry implements DynamicallyPositionable {

    protected final KronOptionList<Justification> justification = new KronOptionList<>("justification", null, Justification.CENTER);
    protected final KronOptionList<AnchorPoint> anchor = DefaultOptions.getAnchorPoint(AnchorPoint.TOP_MIDDLE);

    private final KronInteger minWidth = new KronInteger("minwidth", null, 53, 1, 300);

    public SimpleTextHudEntry() {
        this(53, 13, true);
    }

    protected SimpleTextHudEntry(int width, int height) {
        this(width, height, true);
    }

    protected SimpleTextHudEntry(int width, int height, boolean backgroundAllowed) {
        super(width, height, backgroundAllowed);
        this.minWidth.setDefaultValue(width);
    }

    @Override
    public void renderComponent(DrawContext context, float delta) {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
        DrawPosition pos = getPos();
        String value = getValue();

        int valueWidth = client.textRenderer.getWidth(value);
        int elementWidth = valueWidth + 4;

        int min = minWidth.getValue();
        if (elementWidth < min) {
            if (width != min) {
                setWidth(min);
                onBoundsUpdate();
            }
        } else if (elementWidth != width) {
            setWidth(elementWidth);
            onBoundsUpdate();
        }
        drawString(
                context, client.textRenderer, Text.literal(value),
                pos.x() + justification.getValue().getXOffset(valueWidth, getWidth() - 4) + 2,
                pos.y() + (Math.round((float) getHeight() / 2)) - 4,
                getTextColor().color(), shadow.getValue()
        );
        RenderSystem.disableBlend();
    }

    public Color getTextColor() {
        return textColor.getValue();
    }

    @Override
    public void renderPlaceholderComponent(DrawContext context, float delta) {
        DrawPosition pos = getPos();
        String value = getPlaceholder();
        drawString(
                context, client.textRenderer, Text.literal(value),
                pos.x() + justification.getValue().getXOffset(value, getWidth() - 4) + 2,
                pos.y() + (Math.round((float) getHeight() / 2)) - 4,
                textColor.getValue().color(), shadow.getValue()
        );
    }

    @Override
    public List<KronConfig<?>> getConfigurationOptions() {
        List<KronConfig<?>> options = super.getConfigurationOptions();
        options.add(justification);
        options.add(anchor);
        options.add(minWidth);
        return options;
    }

    @Override
    public boolean movable() {
        return true;
    }

    public abstract String getValue();

    public abstract String getPlaceholder();

    @Override
    public AnchorPoint getAnchor() {
        return anchor.getValue();
    }
}
