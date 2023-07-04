package io.github.darkkronicle.kronhud.util;

import io.github.darkkronicle.darkkore.util.Color;
import io.github.darkkronicle.darkkore.util.render.RenderUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class DrawUtil {

    public static void fillRect(DrawContext context, Rectangle rectangle, Color color) {
        RenderUtil.drawRectangle(context, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), color);
    }

    public static void rectOutline(DrawContext context, Rectangle rectangle, Color color) {
        RenderUtil.drawOutline(context, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), color);
    }

    public static void outlineRect(DrawContext context, Rectangle rectangle, Color color) {
        RenderUtil.drawOutline(context, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), color);
    }

    public static void drawCenteredString(DrawContext context, TextRenderer renderer, Text text, int x, int y,
            Color color, boolean shadow) {
        drawCenteredString(context, renderer, text, x, y, color.color(), shadow);
    }

    public static void drawCenteredString(DrawContext context, TextRenderer renderer, Text text, int x, int y,
            int color, boolean shadow) {
        drawString(context, renderer, text, (float) (x - renderer.getWidth(text) / 2), (float) y, color, shadow);
    }

    public static void drawString(DrawContext context, TextRenderer renderer, Text text, float x, float y, int color,
            boolean shadow) {
        context.drawText(renderer, text, (int) x, (int) y, color, shadow);
    }

}
