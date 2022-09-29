package io.github.darkkronicle.kronhud.util;

import io.github.darkkronicle.darkkore.util.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.opengl.GL11;

// Cannot use @UtilityClass annotation. Class can't be marked as final.
public class DrawUtil {

    public static void fillRect(MatrixStack matrices, Rectangle rectangle, Color color) {
        fillRect(matrices, rectangle.x(), rectangle.y(), rectangle.width(),
                rectangle.height(),
                color.color());
    }

    private static void fillRect(MatrixStack matrices, int x, int y, int width, int height, int color) {
        DrawableHelper.fill(matrices, x, y, x + width, y + height, color);
    }

    public static void outlineRect(MatrixStack matrices, Rectangle rectangle, Color color) {
        outlineRect(matrices, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), color.color());
    }

    private static void outlineRect(MatrixStack matrices, int x, int y, int width, int height, int color) {
        fillRect(matrices, x, y, 1, height, color);
        fillRect(matrices, x + width - 1, y, 1, height, color);
        fillRect(matrices, x, y, width, 1, color);
        fillRect(matrices, x, y + height - 1, width, 1, color);
    }


    public static void drawCenteredString(MatrixStack matrices, TextRenderer renderer,
                                          String text, int x, int y,
                                          Color color, boolean shadow) {
        drawCenteredString(matrices, renderer, text, x, y, color.color(), shadow);
    }


    public static void drawCenteredString(MatrixStack matrices, TextRenderer renderer,
                                          String text, int x, int y,
                                          int color, boolean shadow) {
        drawString(matrices, renderer, text, (float)(x - renderer.getWidth(text) / 2),
                (float) y,
                color, shadow);
    }

    public static void drawString(MatrixStack matrices, TextRenderer renderer, String text, float x, float y,
                                  int color, boolean shadow) {
        if(shadow) {
            renderer.drawWithShadow(matrices, text, x, y, color);
        }
        else {
            renderer.draw(matrices, text, x, y, color);
        }
    }

    public static void applyScissor(Rectangle scissor) {
        Window window = MinecraftClient.getInstance().getWindow();
        double scale = window.getScaleFactor();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor((int) (scissor.x() * scale), (int) ((window.getScaledHeight() - scissor.height() - scissor.y()) * scale), (int) (scissor.width() * scale), (int) (scissor.height() * scale));
    }

    public static void removeScissors() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

}
