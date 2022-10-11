package io.github.darkkronicle.kronhud.gui.entry;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.darkkronicle.kronhud.config.KronConfig;
import io.github.darkkronicle.kronhud.config.KronOptionList;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;
import io.github.darkkronicle.kronhud.gui.layout.Justification;
import io.github.darkkronicle.kronhud.util.DrawPosition;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.opengl.GL11;

import java.util.List;

public abstract class SimpleTextHudEntry extends TextHudEntry {

    protected final KronOptionList<Justification> justification = new KronOptionList<>("justification", null, Justification.CENTER);

    public SimpleTextHudEntry() {
        super(53, 13, true);
    }

    protected SimpleTextHudEntry(int width, int height) {
        super(width, height, true);
    }

    protected SimpleTextHudEntry(int width, int height, boolean backgroundAllowed) {
        super(width, height, backgroundAllowed);
    }

    @Override
    public void renderComponent(MatrixStack matrices, float delta) {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableTexture();
        DrawPosition pos = getPos();
        String value = getValue();
        drawString(
                matrices, client.textRenderer, value,
                pos.x() + justification.getValue().getXOffset(value, getWidth() - 4) + 2,
                pos.y() + (Math.round((float) getHeight() / 2)) - 4,
                textColor.getValue().color(), shadow.getValue()
        );
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    @Override
    public void renderPlaceholderComponent(MatrixStack matrices, float delta) {
        DrawPosition pos = getPos();
        String value = getPlaceholder();
        drawString(
                matrices, client.textRenderer, value,
                pos.x() + justification.getValue().getXOffset(value, getWidth() - 4) + 2,
                pos.y() + (Math.round((float) getHeight() / 2)) - 4,
                textColor.getValue().color(), shadow.getValue()
        );
    }

    @Override
    public List<KronConfig<?>> getConfigurationOptions() {
        List<KronConfig<?>> options = super.getConfigurationOptions();
        options.add(justification);
        return options;
    }

    @Override
    public boolean movable() {
        return true;
    }

    public abstract String getValue();

    public abstract String getPlaceholder();

}
