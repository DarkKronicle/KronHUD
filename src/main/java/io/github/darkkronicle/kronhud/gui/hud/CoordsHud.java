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
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class CoordsHud extends AbstractHudEntry {
    @Override
    public void render(MatrixStack matrices) {
        matrices.push();
        matrices.scale(getStorage().scale, getStorage().scale, 1);
        DrawPosition pos = getScaledPos();
        rect(matrices, pos.getX(), pos.getY(), getStorage().width, getStorage().height, getStorage().backgroundColor.color());
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
        String direction = getWordedDirection(yaw);
        TextRenderer textRenderer = client.textRenderer;
        textRenderer.drawWithShadow(matrices, "X", pos.getX() + 1, pos.getY() + 2, getStorage().firstTextColor.color());
        textRenderer.drawWithShadow(matrices, String.valueOf(df.format(x)), pos.getX() + 11, pos.getY() + 2, getStorage().secondTextColor.color());
        textRenderer.drawWithShadow(matrices, "Y", pos.getX() + 1, pos.getY() + 12, getStorage().firstTextColor.color());
        textRenderer.drawWithShadow(matrices, String.valueOf(df.format(y)), pos.getX() + 11, pos.getY() + 12, getStorage().secondTextColor.color());
        textRenderer.drawWithShadow(matrices, "Z", pos.getX() + 1, pos.getY() + 22, getStorage().firstTextColor.color());
        textRenderer.drawWithShadow(matrices, String.valueOf(df.format(z)), pos.getX() + 11, pos.getY() + 22, getStorage().secondTextColor.color());
        textRenderer.drawWithShadow(matrices, direction, pos.getX() + 60, pos.getY() + 12, getStorage().firstTextColor.color());

        matrices.pop();
    }

    @Override
    public void renderPlaceholder(MatrixStack matrices) {
        matrices.push();
        matrices.scale(getStorage().scale, getStorage().scale, 1);
        DrawPosition pos = getScaledPos();
        if (hovered) {
            DrawUtil.rect(matrices, pos.getX(), pos.getY(), getStorage().width, getStorage().height, Colors.WHITE.color().withAlpha(150).color());
        } else {
            DrawUtil.rect(matrices, pos.getX(), pos.getY(), getStorage().width, getStorage().height, Colors.WHITE.color().withAlpha(50).color());
        }
        DrawUtil.outlineRect(matrices, pos.getX(), pos.getY(), getStorage().width, getStorage().height, Colors.BLACK.color().color());
        StringBuilder format = new StringBuilder("#");
        if (getStorage().decimalNum > 0) {
            format.append(".");
            for (int i = 0; i < getStorage().decimalNum; i++) {
                format.append("#");
            }
        }

        DecimalFormat df = new DecimalFormat(format.toString());
        df.setRoundingMode(RoundingMode.CEILING);
        double x = 109.2325;
        double y = 180.8981;
        double z = -5098.32698;
        double yaw = 180;
        String direction = getWordedDirection(yaw);
        TextRenderer textRenderer = client.textRenderer;
        textRenderer.drawWithShadow(matrices, "X", pos.getX() + 1, pos.getY() + 2, getStorage().firstTextColor.color());
        textRenderer.drawWithShadow(matrices, String.valueOf(df.format(x)), pos.getX() + 11, pos.getY() + 2, getStorage().secondTextColor.color());
        textRenderer.drawWithShadow(matrices, "Y", pos.getX() + 1, pos.getY() + 12, getStorage().firstTextColor.color());
        textRenderer.drawWithShadow(matrices, String.valueOf(df.format(y)), pos.getX() + 11, pos.getY() + 12, getStorage().secondTextColor.color());
        textRenderer.drawWithShadow(matrices, "Z", pos.getX() + 1, pos.getY() + 22, getStorage().firstTextColor.color());
        textRenderer.drawWithShadow(matrices, String.valueOf(df.format(z)), pos.getX() + 11, pos.getY() + 22, getStorage().secondTextColor.color());
        textRenderer.drawWithShadow(matrices, direction, pos.getX() + 60, pos.getY() + 12, getStorage().firstTextColor.color());
        matrices.pop();
        hovered = false;
    }

    public String getWordedDirection(double yaw) {
        String direction = "";
        switch (getDirection(yaw)) {
            case 1:
                direction = "N";
                break;
            case 2:
                direction = "E";
                break;
            case 3:
                direction = "S";
                break;
            case 4:
                direction = "W";
                break;
            case 0:
                direction = "NaN";
                break;
        }
        return direction;
    }

    /**
     * Get direction. 1 = North, 2 East, 3 South, 4 West
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
        if ((yaw >= 0 && yaw < 45) || (yaw <= 360 && yaw > 315)) {
            return 1;
        } else if (yaw >= 45 && yaw < 135) {
            return 2;
        } else if (yaw >= 135 && yaw < 225) {
            return 3;
        } else if (yaw >= 225 && yaw < 315) {
            return 4;
        }
        return 0;
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
    public String getName() {
        return "CoordsHUD";
    }

    public static class Storage extends AbstractStorage {
        public SimpleColor backgroundColor;
        public SimpleColor firstTextColor;
        public SimpleColor secondTextColor;
        public int decimalNum;

        public Storage() {
            width = 70;
            height = 31;
            decimalNum = 1;
            backgroundColor = Colors.BLACK.color().withAlpha(100);
            firstTextColor = Colors.SELECTOR_BLUE.color();
            secondTextColor = Colors.WHITE.color();
        }

    }

    @Override
    public Screen getConfigScreen() {
        EntryBuilder builder = EntryBuilder.create();
        EntryButtonList list = new EntryButtonList((client.getWindow().getScaledWidth() / 2) - 290, (client.getWindow().getScaledHeight() / 2) - 70, 580, 150, 1, false);
        list.addEntry(builder.startToggleEntry(new LiteralText("Enabled"), getStorage().enabled).setDimensions(20, 10).setSavable(val -> getStorage().enabled = val).build(list));
        list.addEntry(builder.startFloatSliderEntry(new LiteralText("Scale"), getStorage().scale, 0.2F, 1.5F).setWidth(80).setSavable(val -> getStorage().scale = val).build(list));
        list.addEntry(builder.startColorButtonEntry(new LiteralText("Background Color"), getStorage().backgroundColor).setSavable(val -> getStorage().backgroundColor = val).build(list));
        list.addEntry(builder.startColorButtonEntry(new LiteralText("First Text Color"), getStorage().firstTextColor).setSavable(val -> getStorage().firstTextColor = val).build(list));
        list.addEntry(builder.startColorButtonEntry(new LiteralText("Second Text Color"), getStorage().secondTextColor).setSavable(val -> getStorage().secondTextColor = val).build(list));
        list.addEntry(builder.startIntSliderEntry(new LiteralText("Decimals"), getStorage().decimalNum, 0, 3).setSavable(val -> getStorage().decimalNum = val).setWidth(60).build(list));

        return new BasicConfigScreen(new LiteralText(getName()), list) {
            @Override
            public void onClose() {
                super.onClose();
                KronHUD.storageHandler.saveDefaultHandling();
            }
        };
    }

}
