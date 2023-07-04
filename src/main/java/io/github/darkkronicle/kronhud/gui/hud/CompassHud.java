package io.github.darkkronicle.kronhud.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.darkkronicle.darkkore.util.Color;
import io.github.darkkronicle.darkkore.util.render.RenderUtil;
import io.github.darkkronicle.kronhud.config.KronBoolean;
import io.github.darkkronicle.kronhud.config.KronColor;
import io.github.darkkronicle.kronhud.config.KronConfig;
import io.github.darkkronicle.kronhud.config.KronInteger;
import io.github.darkkronicle.kronhud.gui.component.DynamicallyPositionable;
import io.github.darkkronicle.kronhud.gui.entry.TextHudEntry;
import io.github.darkkronicle.kronhud.gui.layout.AnchorPoint;
import io.github.darkkronicle.kronhud.util.DrawPosition;
import io.github.darkkronicle.kronhud.util.DrawUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class CompassHud extends TextHudEntry implements DynamicallyPositionable {

    public final Identifier ID = new Identifier("kronhud", "compasshud");

    private final KronInteger widthOption = new KronInteger("width", ID.getPath(), width, 100, 800, this::updateWidth);

    private final KronColor lookingBox = new KronColor("lookingbox", ID.getPath(), new Color(0x80000000));
    private final KronColor degreesColor = new KronColor("degreescolor", ID.getPath(), new Color(-1));
    private final KronColor majorIndicatorColor = new KronColor("majorindicator", ID.getPath(), new Color(-1));
    private final KronColor minorIndicatorColor = new KronColor("minorindicator", ID.getPath(), new Color(0xCCFFFFFF));
    private final KronColor cardinalColor = new KronColor("cardinalcolor", ID.getPath(), new Color(0xFFFFFFFF));
    private final KronColor semiCardinalColor = new KronColor("semicardinalcolor", ID.getPath(), new Color(0xFFAAAAAA));
    private final KronBoolean invert = new KronBoolean("invert", ID.getPath(), false);
    private final KronBoolean showDegrees = new KronBoolean("showdegrees", ID.getPath(), true);

    private void updateWidth(int newWidth){
        setWidth(newWidth);
        onBoundsUpdate();
    }

    public CompassHud() {
        super(240, 33, false);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public void renderComponent(DrawContext context, float delta) {
        renderCompass(context, delta);
    }

    @Override
    public void renderPlaceholderComponent(DrawContext context, float delta) {
        renderCompass(context, delta);
    }

    public void renderCompass(DrawContext context, float delta) {
        // N = 0
        // E = 90
        // S = 180
        // W = 270
        if (client.player == null) {
            return;
        }
        float halfWidth = width / 2f;
        float degrees = (client.player.getYaw(delta) + 180) % 360;
        if (degrees < 0) {
            degrees += 360;
        }
        float start = degrees - 150 + 360;
//        float end = degrees + 150 + 360;
        int startIndicator = ((int) (start + 8) / 15) * 15;
        int amount = 21;
//        int endIndicator = startIndicator + 15 * amount;
        int dist = width / (amount);
        DrawPosition pos = getPos();
        int x = pos.x();
        int y = pos.y() + 1;
        RenderUtil.drawRectangle(context, pos.x() + (int) halfWidth - 1, pos.y(), 3, 11, lookingBox.getValue());
        if (showDegrees.getValue()) {
            DrawUtil.drawCenteredString(
                    context, client.textRenderer, Text.literal(Integer.toString((int) degrees)), x + (int) halfWidth, y + 20, degreesColor.getValue(),
                    shadow.getValue()
            );
        }
        float shift = (startIndicator - start) / 15f * dist;
        if (invert.getValue()) {
            shift = dist - shift;
        }
        context.getMatrices().translate(shift, 0, 0);
        for (int i = 0; i < amount; i++) {
            int d;
            if (invert.getValue()) {
                d = (startIndicator + ((amount - i - 2) * 15)) % 360;
            } else {
                d = (startIndicator + i * 15) % 360;
            }
            int innerX = x + dist * (i + 1);
            Indicator indicator = getIndicator(d);

            float trueDist;
            if (invert.getValue()) {
                trueDist = ((amount - i) * dist) - shift;
            } else {
                trueDist = ((i + 1) * dist) - shift;
            }
            float targetOpacity = 1 - Math.abs((halfWidth - trueDist)) / halfWidth;
            RenderSystem.setShaderColor(1, 1, 1, targetOpacity);
            if (indicator == Indicator.CARDINAL) {
                // We have to call .color() here so that transparency stays
                RenderUtil.drawRectangle(context, innerX, y, 1, 9, majorIndicatorColor.getValue().color());
                Color color = cardinalColor.getValue();
                color = color.withAlpha((int) (color.alpha() * targetOpacity));
                if (color.alpha() > 0) {
                    DrawUtil.drawCenteredString(
                            context, client.textRenderer, Text.literal(getCardString(indicator, d)), innerX + 1, y + 10, color, shadow.getValue());
                }
            } else if (indicator == Indicator.SEMI_CARDINAL) {
                Color color = semiCardinalColor.getValue();
                color = color.withAlpha((int) (color.alpha() * targetOpacity));
                if (color.alpha() > 0) {
                    DrawUtil.drawCenteredString(
                            context, client.textRenderer, Text.literal(getCardString(indicator, d)), innerX + 1, y + 1, color, shadow.getValue());
                }
            } else {
                // We have to call .color() here so that transparency stays
                RenderUtil.drawRectangle(context, innerX, y, 1, 5, minorIndicatorColor.getValue().color());
            }
        }
        RenderSystem.setShaderColor(1, 1, 1, 1);
        context.getMatrices().translate(-shift, 0, 0);
    }

    private static Indicator getIndicator(int degrees) {
        if (degrees % 90 == 0) {
            return Indicator.CARDINAL;
        }
        if (degrees % 45 == 0) {
            return Indicator.SEMI_CARDINAL;
        }
        return Indicator.SMALL;
    }

    private static String getCardString(Indicator indicator, int degrees) {
        if (indicator == Indicator.CARDINAL) {
            return switch (degrees) {
                case 0 -> "N";
                case 90 -> "E";
                case 180 -> "S";
                case 270 -> "W";
                default -> "NaD";
            };
        }
        return switch (degrees) {
            case 45 -> "NE";
            case 135 -> "SE";
            case 225 -> "SW";
            case 315 -> "NW";
            default -> "NaD";
        };
    }

    @Override
    public List<KronConfig<?>> getConfigurationOptions() {
        List<KronConfig<?>> options = super.getConfigurationOptions();
        options.add(widthOption);
        options.add(showDegrees);
        options.add(invert);
        options.add(lookingBox);
        options.add(degreesColor);
        options.add(cardinalColor);
        options.add(semiCardinalColor);
        options.add(majorIndicatorColor);
        options.add(minorIndicatorColor);
        return options;
    }

    @Override
    public AnchorPoint getAnchor() {
        // Won't be dynamically set
        return AnchorPoint.TOP_MIDDLE;
    }

    private enum Indicator {
        CARDINAL,
        SEMI_CARDINAL,
        SMALL

    }
}
