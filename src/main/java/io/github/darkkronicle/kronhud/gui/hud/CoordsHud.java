package io.github.darkkronicle.kronhud.gui.hud;

import io.github.darkkronicle.kronhud.config.*;
import io.github.darkkronicle.kronhud.gui.component.DynamicallyPositionable;
import io.github.darkkronicle.kronhud.gui.entry.TextHudEntry;
import io.github.darkkronicle.kronhud.gui.layout.AnchorPoint;
import io.github.darkkronicle.kronhud.util.ColorUtil;
import io.github.darkkronicle.kronhud.util.DrawPosition;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

public class CoordsHud extends TextHudEntry implements DynamicallyPositionable {

    public static final Identifier ID = new Identifier("kronhud", "coordshud");

    private final KronColor firstColor = new KronColor("firsttextcolor", ID.getPath(), ColorUtil.SELECTOR_BLUE);
    private final KronColor secondColor = new KronColor("secondtextcolor", ID.getPath(), ColorUtil.WHITE);
    private final KronInteger decimalPlaces = new KronInteger("decimalplaces", ID.getPath(), 0, 0, 15);
    private final KronBoolean minimal = new KronBoolean("minimal", ID.getPath(), false);

    private final KronOptionList<AnchorPoint> anchor = DefaultOptions.getAnchorPoint(AnchorPoint.MIDDLE_MIDDLE);

    public CoordsHud() {
            super(79, 31, true);
    }

    public static String getZDir(int dir) {
        return switch (dir) {
            case 5 -> "++";
            case 4, 6 -> "+";
            case 8, 2 -> "-";
            case 1 -> "--";
            default -> "";
        };
    }

    public static String getXDir(int dir) {
        return switch (dir) {
            case 3 -> "++";
            case 2, 4 -> "+";
            case 6, 8 -> "-";
            case 7 -> "--";
            default -> "";
        };
    }

    /**
     * Get direction. 1 = North, 2 North East, 3 East, 4 South East...
     *
     * @param yaw
     * @return
     */
    public static int getDirection(double yaw) {
        yaw %= 360;

        if (yaw < 0) {
            yaw += 360;
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
    public void renderComponent(MatrixStack matrices, float delta) {
        DrawPosition pos = getPos();
        StringBuilder format = new StringBuilder("#");
        if (decimalPlaces.getValue() > 0) {
            format.append(".");
            format.append("0".repeat(Math.max(0, decimalPlaces.getValue())));
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

        if (minimal.getValue()) {
            int currPos = pos.x() + 1;
            String separator = ", ";
            drawString(
                    matrices, textRenderer, "XYZ: ",
                    currPos, pos.y() + 2,
                    firstColor.getValue().color(), shadow.getValue()
            );
            currPos += textRenderer.getWidth("XYZ: ");
            drawString(
                    matrices, textRenderer, String.valueOf(df.format(x)),
                    currPos, pos.y() + 2,
                    secondColor.getValue().color(), shadow.getValue()
            );
            currPos += textRenderer.getWidth(String.valueOf(df.format(x)));
            drawString(
                    matrices, textRenderer, separator,
                    currPos, pos.y() + 2,
                    firstColor.getValue().color(), shadow.getValue()
            );
            currPos += textRenderer.getWidth(separator);
            drawString(
                    matrices, textRenderer, String.valueOf(df.format(y)),
                    currPos, pos.y() + 2,
                    secondColor.getValue().color(), shadow.getValue()
            );
            currPos += textRenderer.getWidth(String.valueOf(df.format(y)));
            drawString(
                    matrices, textRenderer, separator,
                    currPos, pos.y() + 2,
                    firstColor.getValue().color(), shadow.getValue()
            );
            currPos += textRenderer.getWidth(separator);
            drawString(
                    matrices, textRenderer, String.valueOf(df.format(z)),
                    currPos, pos.y() + 2,
                    secondColor.getValue().color(), shadow.getValue()
            );
            currPos += textRenderer.getWidth(String.valueOf(df.format(z)));
            int width = currPos - pos.x() + 2;
            boolean changed = false;
            if (getWidth() != width) {
                setWidth(width);
                changed = true;
            }
            if (getHeight() != 11) {
                setHeight(11);
                changed = true;
            }
            if (changed) {
                onBoundsUpdate();
            }
        } else {
            drawString(
                    matrices, textRenderer, "X",
                    pos.x() + 1, pos.y() + 2,
                    firstColor.getValue().color(), shadow.getValue()
            );
            drawString(
                    matrices, textRenderer, String.valueOf(df.format(x)),
                    pos.x() + 11, pos.y() + 2,
                    secondColor.getValue().color(), shadow.getValue()
            );

            drawString(
                    matrices, textRenderer, "Y",
                    pos.x() + 1, pos.y() + 12,
                    firstColor.getValue().color(), shadow.getValue()
            );
            drawString(
                    matrices, textRenderer, String.valueOf(df.format(y)),
                    pos.x() + 11, pos.y() + 12,
                    secondColor.getValue().color(), shadow.getValue()
            );

            drawString(
                    matrices, textRenderer, "Z",
                    pos.x() + 1, pos.y() + 22,
                    firstColor.getValue().color(), shadow.getValue()
            );

            drawString(
                    matrices, textRenderer, String.valueOf(df.format(z)), pos.x() + 11, pos.y() + 22, secondColor.getValue().color(),
                    shadow.getValue()
            );

            drawString(
                    matrices, textRenderer, direction,
                    pos.x() + 60, pos.y() + 12,
                    firstColor.getValue().color(), shadow.getValue()
            );

            drawString(
                    matrices, textRenderer, getXDir(dir),
                    pos.x() + 60, pos.y() + 2,
                    secondColor.getValue().color(), shadow.getValue()
            );
            textRenderer.drawWithShadow(
                    matrices, getZDir(dir),
                    pos.x() + 60, pos.y() + 22,
                    secondColor.getValue().color(), shadow.getValue()
            );
            boolean changed = false;
            if (getWidth() != 79) {
                setWidth(79);
                changed = true;
            }
            if (getHeight() != 31) {
                setHeight(31);
                changed = true;
            }
            if (changed) {
                onBoundsUpdate();
            }
        }
    }

    @Override
    public void renderPlaceholderComponent(MatrixStack matrices, float delta) {
        DrawPosition pos = getPos();
        StringBuilder format = new StringBuilder("#");
        if (decimalPlaces.getValue() > 0) {
            format.append(".");
            format.append("#".repeat(Math.max(0, decimalPlaces.getValue())));
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

        if (minimal.getValue()) {
            int currPos = pos.x() + 1;
            String separator = ", ";

            textRenderer.drawWithShadow(matrices, "XYZ: ", currPos, pos.y() + 2, firstColor.getValue().color());
            currPos += textRenderer.getWidth("XYZ: ");
            textRenderer.drawWithShadow(matrices, String.valueOf(df.format(x)), currPos, pos.y() + 2, secondColor.getValue().color(), shadow.getValue());
            currPos += textRenderer.getWidth(String.valueOf(df.format(x)));
            textRenderer.drawWithShadow(matrices, separator, currPos, pos.y() + 2, firstColor.getValue().color(), shadow.getValue());
            currPos += textRenderer.getWidth(separator);
            textRenderer.drawWithShadow(matrices, String.valueOf(df.format(y)), currPos, pos.y() + 2, secondColor.getValue().color(), shadow.getValue());
            currPos += textRenderer.getWidth(String.valueOf(df.format(y)));
            textRenderer.drawWithShadow(matrices, separator, currPos, pos.y() + 2, firstColor.getValue().color(), shadow.getValue() );
            currPos += textRenderer.getWidth(separator);
            textRenderer.drawWithShadow(matrices, String.valueOf(df.format(z)), currPos, pos.y() + 2, secondColor.getValue().color(), shadow.getValue());
            currPos += textRenderer.getWidth(String.valueOf(df.format(z)));

            int width = currPos - pos.x() + 2;
            boolean changed = false;
            if (getWidth() != width) {
                setWidth(width);
                changed = true;
            }
            if (getHeight() != 11) {
                setHeight(11);
                changed = true;
            }
            if (changed) {
                onBoundsUpdate();
            }
        } else {
            textRenderer.drawWithShadow(matrices, "X", pos.x() + 1, pos.y() + 2, firstColor.getValue().color());
            textRenderer.drawWithShadow(matrices, String.valueOf(df.format(x)), pos.x() + 11, pos.y() + 2, secondColor.getValue().color());
            textRenderer.drawWithShadow(matrices, "Y", pos.x() + 1, pos.y() + 12, firstColor.getValue().color());
            textRenderer.drawWithShadow(matrices, String.valueOf(df.format(y)), pos.x() + 11, pos.y() + 12, secondColor.getValue().color());
            textRenderer.drawWithShadow(matrices, "Z", pos.x() + 1, pos.y() + 22, firstColor.getValue().color());
            textRenderer.drawWithShadow(matrices, String.valueOf(df.format(z)), pos.x() + 11, pos.y() + 22, secondColor.getValue().color());
            textRenderer.drawWithShadow(matrices, direction, pos.x() + 60, pos.y() + 12, firstColor.getValue().color());
            textRenderer.drawWithShadow(matrices, getXDir(dir), pos.x() + 60, pos.y() + 2, secondColor.getValue().color());
            textRenderer.drawWithShadow(matrices, getZDir(dir), pos.x() + 60, pos.y() + 22, secondColor.getValue().color());
        }
    }

    public String getWordedDirection(int dir) {
        return switch (dir) {
            case 1 -> "N";
            case 2 -> "NE";
            case 3 -> "E";
            case 4 -> "SE";
            case 5 -> "S";
            case 6 -> "SW";
            case 7 -> "W";
            case 8 -> "NW";
            case 0 -> "?";
            default -> "";
        };
    }

    @Override
    public List<KronConfig<?>> getConfigurationOptions() {
        List<KronConfig<?>> options = super.getConfigurationOptions();
        options.add(firstColor);
        options.add(secondColor);
        options.add(decimalPlaces);
        options.add(minimal);
        return options;
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public AnchorPoint getAnchor() {
        return anchor.getValue();
    }

}
