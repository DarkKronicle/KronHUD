package io.github.darkkronicle.kronhud.gui.hud;

import fi.dy.masa.malilib.config.IConfigBase;
import io.github.darkkronicle.kronhud.config.KronColor;
import io.github.darkkronicle.kronhud.config.KronInteger;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;
import io.github.darkkronicle.kronhud.util.Color;
import io.github.darkkronicle.kronhud.util.DrawPosition;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

public class CoordsHud extends AbstractHudEntry {

    public static final Identifier ID = new Identifier("kronhud", "coordshud");

    private KronColor firstColor = new KronColor("firsttextcolor", ID.getPath(), Color.SELECTOR_BLUE.toString());
    private KronColor secondColor = new KronColor("secondtextcolor", ID.getPath(), "#FFFFFFFF");
    private KronInteger decimalPlaces = new KronInteger("decimalplaces", ID.getPath(), 0, 0, 15);

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
        matrices.push();
        scale(matrices);
        DrawPosition pos = getPos();
        if (background.getBooleanValue()) {
            fillRect(matrices, getBounds(), backgroundColor.getColor());
        }
        StringBuilder format = new StringBuilder("#");
        if (decimalPlaces.getIntegerValue() > 0) {
            format.append(".");
            for (int i = 0; i < decimalPlaces.getIntegerValue(); i++) {
                format.append("0");
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
        drawString(matrices, textRenderer, "X", pos.x() + 1, pos.y() + 2, firstColor.getColor().color(),
                shadow.getBooleanValue());
        drawString(matrices, textRenderer, String.valueOf(df.format(x)), pos.x() + 11, pos.y() + 2,
                secondColor.getColor().color(), shadow.getBooleanValue());

        drawString(matrices, textRenderer, "Y", pos.x() + 1, pos.y() + 12, firstColor.getColor().color(),
                shadow.getBooleanValue());
        drawString(matrices, textRenderer, String.valueOf(df.format(y)), pos.x() + 11, pos.y() + 12,
                secondColor.getColor().color(), shadow.getBooleanValue());

        drawString(matrices, textRenderer, "Z", pos.x() + 1, pos.y() + 22, firstColor.getColor().color(),
                shadow.getBooleanValue());
        drawString(matrices, textRenderer, String.valueOf(df.format(z)), pos.x() + 11, pos.y() + 22,
                secondColor.getColor().color(), shadow.getBooleanValue());

        drawString(matrices, textRenderer, direction, pos.x() + 60, pos.y() + 12,
                firstColor.getColor().color(), shadow.getBooleanValue());

        drawString(matrices, textRenderer, getXDir(dir), pos.x() + 60, pos.y() + 2,
                secondColor.getColor().color(), shadow.getBooleanValue());
        textRenderer.drawWithShadow(matrices, getZDir(dir), pos.x() + 60, pos.y() + 22,
                secondColor.getColor().color(), shadow.getBooleanValue());

        matrices.pop();
    }

    @Override
    public void renderPlaceholder(MatrixStack matrices) {
        matrices.push();
        renderPlaceholderBackground(matrices);
        scale(matrices);
        DrawPosition pos = getPos();
        StringBuilder format = new StringBuilder("#");
        if (decimalPlaces.getIntegerValue() > 0) {
            format.append(".");
            for (int i = 0; i < decimalPlaces.getIntegerValue(); i++) {
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
        textRenderer.drawWithShadow(matrices, "X", pos.x() + 1, pos.y() + 2, firstColor.getColor().color());
        textRenderer.drawWithShadow(matrices, String.valueOf(df.format(x)), pos.x() + 11, pos.y() + 2,
                secondColor.getColor().color());
        textRenderer.drawWithShadow(matrices, "Y", pos.x() + 1, pos.y() + 12, firstColor.getColor().color());
        textRenderer.drawWithShadow(matrices, String.valueOf(df.format(y)), pos.x() + 11, pos.y() + 12,
                secondColor.getColor().color());
        textRenderer.drawWithShadow(matrices, "Z", pos.x() + 1, pos.y() + 22, firstColor.getColor().color());
        textRenderer.drawWithShadow(matrices, String.valueOf(df.format(z)), pos.x() + 11, pos.y() + 22,
                secondColor.getColor().color());
        textRenderer.drawWithShadow(matrices, direction, pos.x() + 60, pos.y() + 12,
                firstColor.getColor().color());
        textRenderer.drawWithShadow(matrices, getXDir(dir), pos.x() + 60, pos.y() + 2,
                secondColor.getColor().color());
        textRenderer.drawWithShadow(matrices, getZDir(dir), pos.x() + 60, pos.y() + 22,
                secondColor.getColor().color());

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
    public void addConfigOptions(List<IConfigBase> options) {
    	super.addConfigOptions(options);
    	options.add(background);
    	options.add(backgroundColor);
    	options.add(firstColor);
    	options.add(secondColor);
    	options.add(decimalPlaces);
    }

    @Override
    public boolean movable() {
        return true;
    }

    @Override
    public Identifier getId() {
        return ID;
    }

}
