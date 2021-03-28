package io.github.darkkronicle.kronhud.gui.hud;

import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;
import io.github.darkkronicle.polish.util.Colors;
import io.github.darkkronicle.polish.util.DrawPosition;
import io.github.darkkronicle.polish.util.DrawUtil;
import io.github.darkkronicle.polish.util.SimpleColor;
import net.minecraft.client.util.math.MatrixStack;

public abstract class CleanHudEntry extends AbstractHudEntry {

    public CleanHudEntry() {
        super(54, 13);
    }

    protected CleanHudEntry(int width, int height) {
        super(width, height);
    }

    @Override
    public void render(MatrixStack matrices) {
        matrices.push();
        matrices.scale(getS().scale, getS().scale, 1);
        DrawPosition pos = getScaledPos();
        if (getStorage().background) {
            rect(matrices, pos.getX(), pos.getY(), width, height, getStorage().backgroundColor.color());
        }
        drawCenteredString(matrices, client.textRenderer, getValue(), pos.getX() + (Math.round(width) / 2), pos.getY() + (Math.round((float) height / 2)) - 4, getStorage().textColor.color());
        matrices.pop();
    }

    @Override
    public void renderPlaceholder(MatrixStack matrices) {
        matrices.push();
        matrices.scale(getS().scale, getS().scale, 1);
        DrawPosition pos = getScaledPos();
        if (hovered) {
            DrawUtil.rect(matrices, pos.getX(), pos.getY(), width, height, Colors.SELECTOR_BLUE.color().withAlpha(100).color());
        } else {
            rect(matrices, pos.getX(), pos.getY(), width, height, Colors.WHITE.color().withAlpha(50).color());
        }
        outlineRect(matrices, pos.getX(), pos.getY(), width, height, Colors.BLACK.color().color());
        drawCenteredString(matrices, client.textRenderer, getPlaceholder(), pos.getX() + (Math.round(width) / 2), pos.getY() + (Math.round((float) height / 2)) - 4, getStorage().textColor.color());
        matrices.pop();
        hovered = false;
    }

    @Override
    public boolean moveable() {
        return true;
    }

    public abstract String getValue();

    public abstract String getPlaceholder();

    @Override
    public Storage getStorage() {
        // Need to change the datatype so the compiler doesn't get mad.
        return getS();
    }

    public abstract Storage getS();

    public static class Storage extends AbstractStorage {
        SimpleColor textColor;
        SimpleColor backgroundColor;
        boolean background;

        public Storage() {
            x = 1F;
            y = 0F;
            scale = 1;
            textColor = new SimpleColor(255, 255, 255, 255);
            backgroundColor = new SimpleColor(0, 0, 0, 100);
            background = true;
        }
    }

}
