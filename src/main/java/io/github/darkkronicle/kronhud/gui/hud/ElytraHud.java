package io.github.darkkronicle.kronhud.gui.hud;

import io.github.darkkronicle.kronhud.config.*;
import io.github.darkkronicle.kronhud.gui.component.DynamicallyPositionable;
import io.github.darkkronicle.kronhud.gui.entry.TextHudEntry;
import io.github.darkkronicle.kronhud.gui.layout.AnchorPoint;
import io.github.darkkronicle.kronhud.util.ColorUtil;
import io.github.darkkronicle.kronhud.util.DrawPosition;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class ElytraHud extends TextHudEntry implements DynamicallyPositionable {

    public static final Identifier ID = new Identifier("kronhud", "elytrahud");

    private final KronColor firstColor = new KronColor("positivespeed", ID.getPath(), ColorUtil.SELECTOR_GREEN);
    private final KronColor secondColor = new KronColor("negativespeed", ID.getPath(), ColorUtil.SELECTOR_RED);
    private final KronColor fourthColor = new KronColor("zerospeed", ID.getPath(), ColorUtil.GRAY);
    private final KronColor thirdColor = new KronColor("maintextcolor", ID.getPath(), ColorUtil.WHITE);
    private final KronColor fifthColor = new KronColor("distanceColor", ID.getPath(), ColorUtil.SELECTOR_BLUE);

    private Vec3d speed;
    private boolean fallFlying;
    private double bottomBlockY;

    private final KronOptionList<AnchorPoint> anchor = DefaultOptions.getAnchorPoint(AnchorPoint.MIDDLE_MIDDLE);

    public ElytraHud() {
            super(71, 31, false);
            speed = new Vec3d(0, 0, 0);
            fallFlying = false;
    }

    public void updateSpeed(Vec3d velocity, boolean fallFlying, double bottomBlockY) {
        speed = velocity;
        this.fallFlying = fallFlying;
        this.bottomBlockY = bottomBlockY;
    }


    @Override
    public void renderComponent(DrawContext context, float delta) {
        TextRenderer textRenderer = client.textRenderer;

        DrawPosition pos = getPos();

        double verticalSpeed = (double) Math.round(speed.getY() * 2000) /100;
        double horizontalSpeed = (double) Math.round(Math.sqrt(Math.pow(speed.getX(), 2) + Math.pow(speed.getZ(), 2)) * 2000) /100;

        String distanceText = "---";
        if (verticalSpeed < 0) {
            double distancePassed = (double) Math.round(bottomBlockY / Math.abs(verticalSpeed) * horizontalSpeed * 100) /100;

            StringBuilder temp = new StringBuilder(String.valueOf(distancePassed));
            String[] splitted = temp.toString().split("\\.");
            String[] splitted1 = splitted[0].split("");

            int startingSpace = splitted[0].length() % 3;
            temp = new StringBuilder();
            for (int i = 0; i < splitted[0].length(); i++) {
                if (startingSpace == 0) {
                    temp.append(" ");
                    startingSpace = 3;
                } else startingSpace --;
                temp.append(splitted1[i]);
            }
            distanceText = temp.toString()+"."+splitted[1];
        }

        KronColor vspeedColor = verticalSpeed == 0 ? fourthColor : verticalSpeed > 0 ? firstColor : secondColor;
        KronColor hspeedColor = horizontalSpeed > 0 ? firstColor : fourthColor;

        drawString(context, textRenderer, Text.of("VSpeed: "), pos.x()+3, pos.y()+3, thirdColor.getValue().color(), true);
        int currPos = textRenderer.getWidth("VSpeed: ");

        drawString(context, textRenderer, Text.of(String.valueOf(verticalSpeed)), pos.x()+3+currPos, pos.y()+3, vspeedColor.getValue().color(), true);
        currPos += textRenderer.getWidth(String.valueOf(verticalSpeed));

        drawString(context, textRenderer, Text.of(" BPS"), pos.x()+3+currPos, pos.y()+3, thirdColor.getValue().color(), true);

        drawString(context, textRenderer, Text.of("HSpeed: "), pos.x()+3, pos.y()+13, thirdColor.getValue().color(), true);
        currPos = textRenderer.getWidth("HSpeed: ");

        drawString(context, textRenderer, Text.of(String.valueOf(horizontalSpeed)), pos.x()+3+currPos, pos.y()+13, hspeedColor.getValue().color(), true);
        currPos += textRenderer.getWidth(String.valueOf(horizontalSpeed));

        drawString(context, textRenderer, Text.of(" BPS"), pos.x()+3+currPos, pos.y()+13, thirdColor.getValue().color(), true);
        currPos = 0;

        drawString(context, textRenderer, Text.of("Distance: "), pos.x()+3, pos.y()+23, thirdColor.getValue().color(), true);
        currPos = textRenderer.getWidth("Distance: ");

        drawString(context, textRenderer, Text.of(distanceText), pos.x()+3+currPos, pos.y()+23, fifthColor.getValue().color(), true);
        currPos += textRenderer.getWidth(distanceText);

        drawString(context, textRenderer, Text.of(" m"), pos.x()+3+currPos, pos.y()+23, thirdColor.getValue().color(), true);

    }

    @Override
    public void renderPlaceholderComponent(DrawContext context, float delta) {
        TextRenderer textRenderer = client.textRenderer;
        DrawPosition pos = getPos();
        drawString(context, textRenderer, Text.of("VSpeed: "), pos.x()+3, pos.y()+3, thirdColor.getValue().color(), true);
        int currPos = textRenderer.getWidth("VSpeed: ");

        drawString(context, textRenderer, Text.of(String.valueOf(0d)), pos.x()+3+currPos, pos.y()+3, fourthColor.getValue().color(), true);
        currPos += textRenderer.getWidth(String.valueOf(0d));

        drawString(context, textRenderer, Text.of(" BPS"), pos.x()+3+currPos, pos.y()+3, thirdColor.getValue().color(), true);

        drawString(context, textRenderer, Text.of("HSpeed: "), pos.x()+3, pos.y()+13, thirdColor.getValue().color(), true);
        currPos = textRenderer.getWidth("HSpeed: ");

        drawString(context, textRenderer, Text.of(String.valueOf(0d)), pos.x()+3+currPos, pos.y()+13, fourthColor.getValue().color(), true);
        currPos += textRenderer.getWidth(String.valueOf(0d));

        drawString(context, textRenderer, Text.of(" BPS"), pos.x()+3+currPos, pos.y()+13, thirdColor.getValue().color(), true);
        currPos = 0;
    }

    @Override
    public List<KronConfig<?>> getConfigurationOptions() {
        List<KronConfig<?>> options = super.getConfigurationOptions();
        options.add(firstColor);
        options.add(secondColor);
        options.add(thirdColor);
        options.add(fourthColor);
        options.add(fifthColor);

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

    @Override
    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }
}
