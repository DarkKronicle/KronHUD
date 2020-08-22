package io.github.darkkronicle.kronhud.gui;

import io.github.darkkronicle.polish.util.DrawPosition;
import io.github.darkkronicle.polish.util.DrawUtil;
import io.github.darkkronicle.polish.util.SimpleRectangle;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public abstract class AbstractHudEntry extends DrawUtil {
    @Setter
    protected boolean hovered = false;
    protected MinecraftClient client = MinecraftClient.getInstance();

//    public AbstractHudEntry(float x, float y, int width, int height, float scale) {
//        this(x, y, width, height, scale, true);
//    }
//
//    public AbstractHudEntry(int x, int y, int width, int height, float scale) {
//        this(x, y, width, height, scale, true);
//    }
//
//    public AbstractHudEntry(int x, int y, int width, int height, float scale, boolean enabled) {
//        this(intToFloat(x, MinecraftClient.getInstance().getWindow().getScaledWidth(), width), intToFloat(y, MinecraftClient.getInstance().getWindow().getScaledHeight(), height), width, height, scale, enabled);
//    }
//
//    public AbstractHudEntry(float x, float y, int width, int height, float scale, boolean enabled) {
//        getStorage().x = x;
//        getStorage().y = y;
//        getStorage().width = width;
//        getStorage().height = height;
//        getStorage().scale = scale;
//        getStorage().enabled = enabled;
//    }

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

    public void setX(int x) {
        getStorage().x = intToFloat(x, client.getWindow().getScaledWidth(), getStorage().width);

    }

    public void setY(int y) {
        getStorage().y = intToFloat(y, client.getWindow().getScaledHeight(), getStorage().height);
    }

    public static int floatToInt(float percent, int max, int offset) {
        return MathHelper.clamp(Math.round((max - offset) * percent), 0, max);
    }

    public static float intToFloat(int current, int max, int offset) {
        return MathHelper.clamp((float) (current) / (max - offset), 0, 1);
    }

    public int getX() {
        return floatToInt(getStorage().x, client.getWindow().getScaledWidth(), getStorage().width);
    }

    public int getY() {
        return floatToInt(getStorage().y, client.getWindow().getScaledHeight(), getStorage().height);
    }

    public SimpleRectangle getBounds() {
        return new SimpleRectangle(getX(), getY(), Math.round(getStorage().width * getStorage().scale), Math.round(getStorage().height * getStorage().scale));
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

    public static class AbstractStorage {
        public float x = 0;
        public float y = 0;
        public int width = 30;
        public int height = 30;
        public float scale = 1;
        public boolean enabled = true;
    }

    public Screen getConfigScreen() {
        return null;
    }

    public abstract String getName();

}
