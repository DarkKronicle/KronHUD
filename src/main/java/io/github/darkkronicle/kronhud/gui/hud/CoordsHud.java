package io.github.darkkronicle.kronhud.gui.hud;

import io.github.darkkronicle.kronhud.KronHUD;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;
import io.github.darkkronicle.polish.api.EntryBuilder;
import io.github.darkkronicle.polish.gui.complexwidgets.EntryButtonList;
import io.github.darkkronicle.polish.gui.screens.BasicConfigScreen;
import io.github.darkkronicle.polish.util.Colors;
import io.github.darkkronicle.polish.util.DrawPosition;
import io.github.darkkronicle.polish.util.DrawUtil;
import io.github.darkkronicle.polish.util.SimpleColor;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class CoordsHud extends AbstractHudEntry {

    public CoordsHud() {
        super(79, 31);
    }

    public static String getZDir(int dir) {
        switch (dir) {
            case 5:
                return "++";
            case 4:
            case 6:
                return "+";
            case 8:
            case 2:
                return "-";
            case 1:
                return "--";
        }
        return "";
    }

    public static String getXDir(int dir) {
        switch (dir) {
            case 3:
                return "++";
            case 2:
            case 4:
                return "+";
            case 6:
            case 8:
                return "-";
            case 7:
                return "--";
        }
        return "";
    }

    /**
     * Get direction. 1 = North, 2 North East, 3 East, 4 South East...
     *
     * @param yaw
     * @return
     */
    public static int getDirection(double yaw) {
        yaw = yaw % 360;
        int plzdontcrash = 0;
        while (yaw < 0) {
            if (plzdontcrash > 10) {
                return 0;
            }
            yaw = yaw + 360;
            plzdontcrash++;
        }
        int[] directions = {0, 23, 68, 113, 158, 203, 248, 293, 338, 360};
        for (int i = 0; i < directions.length; i++) {
            int min = directions[i];
            int max;
            if (i + 1 >= directions.length) {
                max = directions[0];
            } else {
                max = directions[i + 1];
            }
            if (yaw >= min && yaw < max) {
                if (i >= 8) {
                    return 1;
                }
                return i + 1;
            }
        }
        return 0;
    }

    @Override
    public void render(MatrixStack matrices) {
        if (width <= 70) {
            width = 79;
        }
        matrices.push();
        matrices.scale(getStorage().scale, getStorage().scale, 1);
        DrawPosition pos = getScaledPos();
        if (getStorage().background) {
            rect(matrices, pos.getX(), pos.getY(), width, height, getStorage().backgroundColor.color());
        }
        StringBuilder format = new StringBuilder("#");
        if (getStorage().decimalNum > 0) {
            format.append(".");
            for (int i = 0; i < getStorage().decimalNum; i++) {
                format.append("#");
            }
        }
        DecimalFormat df = new DecimalFormat(format.toString());
        df.setRoundingMode(RoundingMode.CEILING);
        double x = client.player.getX();
        double y = client.player.getY();
        double z = client.player.getZ();
        double yaw = client.player.getYaw(0) + 180;
        int dir = getDirection(yaw);
        String direction = getWordedDirection(dir);
        TextRenderer textRenderer = client.textRenderer;
        textRenderer.drawWithShadow(matrices, "X", pos.getX() + 1, pos.getY() + 2, getStorage().firstTextColor.color());
        textRenderer.drawWithShadow(matrices, String.valueOf(df.format(x)), pos.getX() + 11, pos.getY() + 2, getStorage().secondTextColor.color());
        textRenderer.drawWithShadow(matrices, "Y", pos.getX() + 1, pos.getY() + 12, getStorage().firstTextColor.color());
        textRenderer.drawWithShadow(matrices, String.valueOf(df.format(y)), pos.getX() + 11, pos.getY() + 12, getStorage().secondTextColor.color());
        textRenderer.drawWithShadow(matrices, "Z", pos.getX() + 1, pos.getY() + 22, getStorage().firstTextColor.color());
        textRenderer.drawWithShadow(matrices, String.valueOf(df.format(z)), pos.getX() + 11, pos.getY() + 22, getStorage().secondTextColor.color());
        textRenderer.drawWithShadow(matrices, direction, pos.getX() + 60, pos.getY() + 12, getStorage().firstTextColor.color());
        textRenderer.drawWithShadow(matrices, getXDir(dir), pos.getX() + 60, pos.getY() + 2, getStorage().secondTextColor.color());
        textRenderer.drawWithShadow(matrices, getZDir(dir), pos.getX() + 60, pos.getY() + 22, getStorage().secondTextColor.color());
        matrices.pop();
    }

    @Override
    public void renderPlaceholder(MatrixStack matrices) {
        matrices.push();
        matrices.scale(getStorage().scale, getStorage().scale, 1);
        DrawPosition pos = getScaledPos();
        if (hovered) {
            DrawUtil.rect(matrices, pos.getX(), pos.getY(), width, height, Colors.SELECTOR_BLUE.color().withAlpha(100).color());
        } else {
            DrawUtil.rect(matrices, pos.getX(), pos.getY(), width, height, Colors.WHITE.color().withAlpha(50).color());
        }
        DrawUtil.outlineRect(matrices, pos.getX(), pos.getY(), width, height, Colors.BLACK.color().color());
        StringBuilder format = new StringBuilder("#");
        if (getStorage().decimalNum > 0) {
            format.append(".");
            for (int i = 0; i < getStorage().decimalNum; i++) {
                format.append("#");
            }
        }

        DecimalFormat df = new DecimalFormat(format.toString());
        df.setRoundingMode(RoundingMode.FLOOR);
        double x = 109.2325;
        double y = 180.8981;
        double z = -5098.32698;
        double yaw = 180;
        int dir = getDirection(yaw);
        String direction = getWordedDirection(dir);
        TextRenderer textRenderer = client.textRenderer;
        textRenderer.drawWithShadow(matrices, "X", pos.getX() + 1, pos.getY() + 2, getStorage().firstTextColor.color());
        textRenderer.drawWithShadow(matrices, String.valueOf(df.format(x)), pos.getX() + 11, pos.getY() + 2, getStorage().secondTextColor.color());
        textRenderer.drawWithShadow(matrices, "Y", pos.getX() + 1, pos.getY() + 12, getStorage().firstTextColor.color());
        textRenderer.drawWithShadow(matrices, String.valueOf(df.format(y)), pos.getX() + 11, pos.getY() + 12, getStorage().secondTextColor.color());
        textRenderer.drawWithShadow(matrices, "Z", pos.getX() + 1, pos.getY() + 22, getStorage().firstTextColor.color());
        textRenderer.drawWithShadow(matrices, String.valueOf(df.format(z)), pos.getX() + 11, pos.getY() + 22, getStorage().secondTextColor.color());
        textRenderer.drawWithShadow(matrices, direction, pos.getX() + 60, pos.getY() + 12, getStorage().firstTextColor.color());
        textRenderer.drawWithShadow(matrices, getXDir(dir), pos.getX() + 60, pos.getY() + 2, getStorage().secondTextColor.color());
        textRenderer.drawWithShadow(matrices, getZDir(dir), pos.getX() + 60, pos.getY() + 22, getStorage().secondTextColor.color());

        matrices.pop();
        hovered = false;
    }

    public String getWordedDirection(int dir) {
        String direction = "";
        switch (dir) {
            case 1:
                direction = "N";
                break;
            case 2:
                direction = "NE";
                break;
            case 3:
                direction = "E";
                break;
            case 4:
                direction = "SE";
                break;
            case 5:
                direction = "S";
                break;
            case 6:
                direction = "SW";
                break;
            case 7:
                direction = "W";
                break;
            case 8:
                direction = "NW";
                break;
            case 0:
                direction = "?";
                break;
        }
        return direction;
    }

    @Override
    public boolean moveable() {
        return true;
    }

    @Override
    public Storage getStorage() {
        return KronHUD.storage.coordsHudStorage;
    }

    @Override
    public Identifier getID() {
        return new Identifier("kronhud", "coordshud");
    }

    @Override
    public Screen getConfigScreen() {
        EntryBuilder builder = EntryBuilder.create();
        EntryButtonList list = new EntryButtonList((client.getWindow().getScaledWidth() / 2) - 290, (client.getWindow().getScaledHeight() / 2) - 70, 580, 150, 1, false);
        list.addEntry(builder.startToggleEntry(new TranslatableText("option.kronhud.enabled"), getStorage().enabled).setDimensions(20, 10).setSavable(val -> getStorage().enabled = val).build(list));
        list.addEntry(builder.startFloatSliderEntry(new TranslatableText("option.kronhud.scale"), getStorage().scale, 0.2F, 1.5F).setWidth(80).setSavable(val -> getStorage().scale = val).build(list));
        list.addEntry(builder.startToggleEntry(new TranslatableText("option.kronhud.background"), getStorage().background).setSavable(val -> getStorage().background = val).build(list));
        list.addEntry(builder.startColorButtonEntry(new TranslatableText("option.kronhud.backgroundcolor"), getStorage().backgroundColor).setSavable(val -> getStorage().backgroundColor = val).build(list));
        list.addEntry(builder.startColorButtonEntry(new TranslatableText("option.kronhud.coordshud.firstcolor"), getStorage().firstTextColor).setSavable(val -> getStorage().firstTextColor = val).build(list));
        list.addEntry(builder.startColorButtonEntry(new TranslatableText("option.kronhud.coordshud.secondcolor"), getStorage().secondTextColor).setSavable(val -> getStorage().secondTextColor = val).build(list));
        list.addEntry(builder.startIntSliderEntry(new TranslatableText("option.kronhud.coordshud.decimals"), getStorage().decimalNum, 0, 3).setSavable(val -> getStorage().decimalNum = val).setWidth(60).build(list));
        return new BasicConfigScreen(getName(), list, () -> KronHUD.storageHandler.saveDefaultHandling());

    }

    @Override
    public Text getName() {
        return new TranslatableText("hud.kronhud.coordshud");
    }

    public static class Storage extends AbstractStorage {
        public SimpleColor backgroundColor;
        public SimpleColor firstTextColor;
        public SimpleColor secondTextColor;
        public int decimalNum;
        private boolean background;

        public Storage() {
            x = 0.8F;
            y = 0;
            decimalNum = 1;
            backgroundColor = Colors.BLACK.color().withAlpha(100);
            firstTextColor = Colors.SELECTOR_BLUE.color();
            secondTextColor = Colors.WHITE.color();
            background = true;
        }

    }

}
