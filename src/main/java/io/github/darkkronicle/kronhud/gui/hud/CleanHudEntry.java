package io.github.darkkronicle.kronhud.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.darkkronicle.kronhud.config.KronConfig;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;
import io.github.darkkronicle.kronhud.util.DrawPosition;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.opengl.GL11;

import java.util.List;

public abstract class CleanHudEntry extends AbstractHudEntry {

    public CleanHudEntry() {
        super(53, 13);
    }

    protected CleanHudEntry(int width, int height) {
        super(width, height);
    }

    @Override
    public void render(MatrixStack matrices, float delta) {
        matrices.push();
        scale(matrices);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableTexture();
        DrawPosition pos = getPos();
        if (background.getValue() && backgroundColor.getValue().alpha() > 0) {
            fillRect(matrices, getRenderBounds(), backgroundColor.getValue());
        }
        if (outline.getValue() && outlineColor.getValue().alpha() > 0) {
            outlineRect(matrices, getRenderBounds(), outlineColor.getValue());
        }
        drawCenteredString(
                matrices, client.textRenderer, getValue(),
                pos.x() + (Math.round(getWidth()) / 2),
                pos.y() + (Math.round((float) getHeight() / 2)) - 4,
                textColor.getValue(), shadow.getValue()
        );
        matrices.pop();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    @Override
    public void renderPlaceholder(MatrixStack matrices, float delta) {
        matrices.push();
        renderPlaceholderBackground(matrices);
        scale(matrices);
        DrawPosition pos = getPos();
        drawCenteredString(
                matrices, client.textRenderer, getPlaceholder(),
                pos.x() + (Math.round(getWidth()) / 2),
                pos.y() + (Math.round((float) getHeight() / 2)) - 4,
                textColor.getValue(), shadow.getValue()
        );
        matrices.pop();
        hovered = false;
    }

    @Override
    public List<KronConfig<?>> getConfigurationOptions() {
        List<KronConfig<?>> options = super.getConfigurationOptions();
        options.add(textColor);
        options.add(shadow);
        options.add(background);
        options.add(backgroundColor);
        options.add(outline);
        options.add(outlineColor);
        return options;
    }

    @Override
    public boolean movable() {
        return true;
    }

    public abstract String getValue();

    public abstract String getPlaceholder();

}
