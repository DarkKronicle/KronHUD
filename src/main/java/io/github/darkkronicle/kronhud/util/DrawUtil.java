package io.github.darkkronicle.kronhud.util;

import io.github.darkkronicle.darkkore.util.Color;
import io.github.darkkronicle.darkkore.util.render.RenderUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class DrawUtil {

    public static void fillRect(MatrixStack matrices, Rectangle rectangle, Color color) {
        RenderUtil.drawRectangle(matrices, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), color);
    }

    public static void rectOutline(MatrixStack matrices, Rectangle rectangle, Color color) {
        RenderUtil.drawOutline(matrices, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), color);
    }

    public static void outlineRect(MatrixStack matrices, Rectangle rectangle, Color color) {
        RenderUtil.drawOutline(matrices, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), color);
    }

    public static void drawCenteredString(MatrixStack matrices, TextRenderer renderer, Text text, int x, int y,
            Color color, boolean shadow) {
        drawCenteredString(matrices, renderer, text, x, y, color.color(), shadow);
    }

    public static void drawCenteredString(MatrixStack matrices, TextRenderer renderer, Text text, int x, int y,
            int color, boolean shadow) {
        drawString(matrices, renderer, text, (float) (x - renderer.getWidth(text) / 2), (float) y, color, shadow);
    }

    public static void drawString(MatrixStack matrices, TextRenderer renderer, Text text, float x, float y, int color,
            boolean shadow) {
        if (shadow) {
            renderer.drawWithShadow(matrices, text, x, y, color);
        } else {
            renderer.draw(matrices, text, x, y, color);
        }
    }

}
