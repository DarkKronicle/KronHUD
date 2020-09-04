package io.github.darkkronicle.kronhud.gui;

import io.github.darkkronicle.polish.util.DrawPosition;
import io.github.darkkronicle.polish.util.DrawUtil;
import io.github.darkkronicle.polish.util.SimpleRectangle;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public abstract class AbstractHudEntry extends DrawUtil {
    public int width;
    public int height;
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

    public boolean isEnabled() {
        return getStorage().enabled;
    }

    public void renderHud(MatrixStack matrices) {
        if (client == null) {
            client = MinecraftClient.getInstance();
        }
        render(matrices);
    }

    public abstract void render(MatrixStack matrices);

    public abstract void renderPlaceholder(MatrixStack matrices);

    public abstract Identifier getID();

    public abstract boolean moveable();

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
        return floatToInt(getStorage().x, client.getWindow().getScaledWidth(), Math.round(width * getStorage().scale));
    }

    public void setX(int x) {
        getStorage().x = intToFloat(x, client.getWindow().getScaledWidth(), Math.round(width * getStorage().scale));

    }

    public int getY() {
        return floatToInt(getStorage().y, client.getWindow().getScaledHeight(), Math.round(height * getStorage().scale));
    }

    public void setY(int y) {
        getStorage().y = intToFloat(y, client.getWindow().getScaledHeight(), Math.round(height * getStorage().scale));
    }

    public SimpleRectangle getBounds() {
        return new SimpleRectangle(getX(), getY(), Math.round(width * getStorage().scale), Math.round(height * getStorage().scale));
    }

    public DrawPosition getScaledPos() {
        return getScaledPos(getStorage().scale);
    }

    public DrawPosition getScaledPos(float scale) {
        int scaledX = Math.round(getX() / scale);
        int scaledY = Math.round(getY() / scale);
        return new DrawPosition(scaledX, scaledY);
    }

    public abstract <E extends AbstractStorage> E getStorage();

    public Screen getConfigScreen() {
        return null;
    }

    public abstract Text getName();

    public static class AbstractStorage {
        public float x = 0;
        public float y = 0;
        public float scale = 1;
        public boolean enabled = true;
    }

}
