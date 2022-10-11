package io.github.darkkronicle.kronhud.gui.hud;

import io.github.darkkronicle.darkkore.colors.ExtendedColor;
import io.github.darkkronicle.darkkore.util.Color;
import io.github.darkkronicle.darkkore.util.render.RenderUtil;
import io.github.darkkronicle.kronhud.config.KronBoolean;
import io.github.darkkronicle.kronhud.config.KronColor;
import io.github.darkkronicle.kronhud.config.KronConfig;
import io.github.darkkronicle.kronhud.config.KronExtendedColor;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;
import io.github.darkkronicle.kronhud.hooks.KronHudHooks;
import io.github.darkkronicle.kronhud.util.ColorUtil;
import io.github.darkkronicle.kronhud.util.DrawPosition;
import io.github.darkkronicle.kronhud.util.Rectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class KeystrokeHud extends AbstractHudEntry {
    public static final Identifier ID = new Identifier("kronhud", "keystrokehud");

    private final KronColor pressedTextColor = new KronColor("heldtextcolor", ID.getPath(), new Color(0xFF000000));
    private final KronExtendedColor pressedBackgroundColor = new KronExtendedColor("heldbackgroundcolor", ID.getPath(), new ExtendedColor(0x64FFFFFF, ExtendedColor.ChromaOptions.getDefault()));
    private final KronExtendedColor pressedOutlineColor = new KronExtendedColor("heldoutlinecolor", ID.getPath(), new ExtendedColor(0, ExtendedColor.ChromaOptions.getDefault()));
    private final KronBoolean mouseMovement = new KronBoolean("mousemovement", ID.getPath(), false, this::onMouseMovementOption);
    private ArrayList<Keystroke> keystrokes;
    private final MinecraftClient client;

    private float mouseX = 0;
    private float mouseY = 0;
    private float lastMouseX = 0;
    private float lastMouseY = 0;

    public KeystrokeHud() {
        super(53, 61);
        this.client = MinecraftClient.getInstance();
        KronHudHooks.KEYBIND_CHANGE.register(key -> setKeystrokes());
        KronHudHooks.PLAYER_DIRECTION_CHANGE.register(this::onPlayerDirectionChange);
    }

    public static Optional<String> getMouseKeyBindName(KeyBinding keyBinding) {
        if (keyBinding.getBoundKeyTranslationKey()
                      .equalsIgnoreCase(InputUtil.Type.MOUSE.createFromCode(GLFW.GLFW_MOUSE_BUTTON_1).getTranslationKey())) {
            return Optional.of("LMB");
        } else if (keyBinding.getBoundKeyTranslationKey()
                             .equalsIgnoreCase(InputUtil.Type.MOUSE.createFromCode(GLFW.GLFW_MOUSE_BUTTON_2).getTranslationKey())) {
            return Optional.of("RMB");
        } else if (keyBinding.getBoundKeyTranslationKey()
                             .equalsIgnoreCase(InputUtil.Type.MOUSE.createFromCode(GLFW.GLFW_MOUSE_BUTTON_3).getTranslationKey())) {
            return Optional.of("MMB");
        }
        return Optional.empty();
    }

    public void setKeystrokes() {
        if (client.getWindow() == null) {
            keystrokes = null;
            return;
            // Wait until render is called
        }
        keystrokes = new ArrayList<>();
        DrawPosition pos = getPos();
        // LMB
        keystrokes.add(createFromKey(new Rectangle(0, 36, 26, 17), pos, client.options.attackKey));
        // RMB
        keystrokes.add(createFromKey(new Rectangle(27, 36, 26, 17), pos, client.options.useKey));
        // W
        keystrokes.add(createFromKey(new Rectangle(18, 0, 17, 17), pos, client.options.forwardKey));
        // A
        keystrokes.add(createFromKey(new Rectangle(0, 18, 17, 17), pos, client.options.leftKey));
        // S
        keystrokes.add(createFromKey(new Rectangle(18, 18, 17, 17), pos, client.options.backKey));
        // D
        keystrokes.add(createFromKey(new Rectangle(36, 18, 17, 17), pos, client.options.rightKey));

        // Space
        keystrokes.add(new Keystroke(new Rectangle(0, 54, 53, 7), pos, client.options.jumpKey, (stroke, matrices) -> {
            Rectangle bounds = stroke.bounds;
            Rectangle spaceBounds = new Rectangle(bounds.x() + stroke.offset.x() + 4,
                    bounds.y() + stroke.offset.y() + 2,
                    bounds.width() - 8, 1
            );
            fillRect(matrices, spaceBounds, stroke.getFGColor());
            if (shadow.getValue()) {
                fillRect(matrices, spaceBounds.offset(1, 1),
                        new Color((stroke.getFGColor().color() & 16579836) >> 2 | stroke.getFGColor().color() & -16777216)
                );
            }
        }));
        KeyBinding.unpressAll();
        KeyBinding.updatePressedStates();
    }

    @Override
    public void render(MatrixStack matrices, float delta) {
        matrices.push();
        scale(matrices);
        if (keystrokes == null) {
            setKeystrokes();
        }
        for (Keystroke stroke : keystrokes) {
            stroke.render(matrices);
        }
        if (mouseMovement.getValue()) {
            int spaceY = 62 + getY();
            int spaceX = getX();
            if (background.getValue()) {
                RenderUtil.drawRectangle(matrices, spaceX, spaceY, width, 35, backgroundColor.getValue());
            }
            if (outline.getValue()) {
                RenderUtil.drawOutline(matrices, spaceX, spaceY, width, 35, outlineColor.getValue());
            }

            float calculatedMouseX = (lastMouseX + ((mouseX - lastMouseX) * delta)) - 5;
            float calculatedMouseY = (lastMouseY + ((mouseY - lastMouseY) * delta)) - 5;

            RenderUtil.drawRectangle(matrices, spaceX + (width / 2) - 1, spaceY + 17, 1, 1, ColorUtil.WHITE);

            matrices.translate(calculatedMouseX, calculatedMouseY, 0); // Woah KodeToad, good use of translate

            RenderUtil.drawOutline(
                    matrices,
                    spaceX + (width / 2) - 1,
                    spaceY + 17,
                    11,
                    11,
                    ColorUtil.WHITE
            );
        }
        matrices.pop();
    }

    public void onPlayerDirectionChange(float prevPitch, float prevYaw, float pitch, float yaw) {
        // Implementation credit goes to TheKodeToad
        // This project has the author's approval to use this
        // https://github.com/Sol-Client/Client/blob/main/game/src/main/java/io/github/solclient/client/mod/impl/hud/keystrokes/KeystrokesMod.java
        mouseX += (yaw - prevYaw) / 7F;
        mouseY += (pitch - prevPitch) / 7F;
        // 0, 0 will be the center of the HUD element
        float halfWidth = getWidth() / 2f;
        mouseX = MathHelper.clamp(mouseX, -halfWidth + 4, halfWidth - 4);
        mouseY = MathHelper.clamp(mouseY, -13, 13);
    }

    @Override
    public boolean tickable() {
        return true;
    }

    @Override
    public void tick() {
        DrawPosition pos = getPos();
        if (keystrokes == null) {
            setKeystrokes();
        }
        for (Keystroke stroke : keystrokes) {
            stroke.offset = pos;
        }
        lastMouseX = mouseX;
        lastMouseY = mouseY;
        mouseX *= .75f;
        mouseY *= .75f;
    }

    @Override
    protected boolean getShadowDefault() {
        return false;
    }

    @Override
    public void renderPlaceholder(MatrixStack matrices, float delta) {
        matrices.push();
        renderPlaceholderBackground(matrices);
        renderHud(matrices, delta);
        hovered = false;
        matrices.pop();
    }

    public Keystroke createFromKey(Rectangle bounds, DrawPosition offset, KeyBinding key) {
        String name = getMouseKeyBindName(key).orElse(key.getBoundKeyLocalizedText().getString().toUpperCase());
        if (name.length() > 4) {
            name = name.substring(0, 2);
        }
        return createFromString(bounds, offset, key, name);
    }

    public Keystroke createFromString(Rectangle bounds, DrawPosition offset, KeyBinding key, String word) {
        return new Keystroke(bounds, offset, key, (stroke, matrices) -> {
            Rectangle strokeBounds = stroke.bounds;
            float x = (strokeBounds.x() + stroke.offset.x() + ((float) strokeBounds.width() / 2)) -
                    ((float) client.textRenderer.getWidth(word) / 2);
            float y = strokeBounds.y() + stroke.offset.y() + ((float) strokeBounds.height() / 2) - 4;

            drawString(matrices, client.textRenderer, word, x, y, stroke.getFGColor().color(), shadow.getValue());
        });
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public boolean movable() {
        return true;
    }

    @Override
    public List<KronConfig<?>> getConfigurationOptions() {
        List<KronConfig<?>> options = super.getConfigurationOptions();
        options.add(mouseMovement);
        options.add(textColor);
        options.add(pressedTextColor);
        options.add(shadow);
        options.add(background);
        options.add(backgroundColor);
        options.add(pressedBackgroundColor);
        options.add(outline);
        options.add(outlineColor);
        options.add(pressedOutlineColor);
        return options;
    }

    public void onMouseMovementOption(boolean value) {
        int baseHeight = 61;
        if (value) {
            baseHeight += 36;
        }
        height = baseHeight;
        onSizeUpdate();
    }

    public class Keystroke {
        protected final KeyBinding key;
        protected final KeystrokeRenderer render;
        protected Rectangle bounds;
        protected DrawPosition offset;
        private float start = -1;
        private final int animTime = 100;
        private boolean wasPressed = false;

        public Keystroke(Rectangle bounds, DrawPosition offset, KeyBinding key, KeystrokeRenderer render) {
            this.bounds = bounds;
            this.offset = offset;
            this.key = key;
            this.render = render;
        }

        public void renderStroke(MatrixStack matrices) {
            if (key.isPressed() != wasPressed) {
                start = Util.getMeasuringTimeMs();
            }
            Rectangle rect = bounds.offset(offset);
            if (background.getValue()) {
                fillRect(matrices, rect, getColor());
            }
            if (outline.getValue()) {
                outlineRect(matrices, rect, getOutlineColor());
            }
            if ((Util.getMeasuringTimeMs() - start) / animTime >= 1) {
                start = -1;
            }
            wasPressed = key.isPressed();
        }

        private float getPercentPressed() {
            return start == -1 ? 1 : MathHelper.clamp((Util.getMeasuringTimeMs() - start) / animTime, 0, 1);
        }

        public Color getColor() {
            if (backgroundColor.getValue().getChroma().isActive() || pressedBackgroundColor.getValue().getChroma().isActive()) {
                // Can't blend chroma ATM
                return key.isPressed() ? pressedBackgroundColor.getValue() : backgroundColor.getValue();
            }
            return key.isPressed() ? ColorUtil.blend(backgroundColor.getValue(), pressedBackgroundColor.getValue(),
                    getPercentPressed()
            ) :
                   ColorUtil.blend(
                           pressedBackgroundColor.getValue(),
                           backgroundColor.getValue(),
                           getPercentPressed()
                   );
        }

        public Color getOutlineColor() {
            if (outlineColor.getValue().getChroma().isActive() || pressedOutlineColor.getValue().getChroma().isActive()) {
                // Can't blend chroma ATM
                return key.isPressed() ? pressedOutlineColor.getValue() : outlineColor.getValue();
            }
            return key.isPressed() ? ColorUtil.blend(outlineColor.getValue(), pressedOutlineColor.getValue(),
                    getPercentPressed()
            ) :
                   ColorUtil.blend(
                           pressedOutlineColor.getValue(),
                           outlineColor.getValue(),
                           getPercentPressed()
                   );
        }

        public Color getFGColor() {
            return key.isPressed() ? ColorUtil.blend(textColor.getValue(), pressedTextColor.getValue(), getPercentPressed()) :
                   ColorUtil.blend(
                           pressedTextColor.getValue(),
                           textColor.getValue(),
                           getPercentPressed()
                   );
        }

        public void render(MatrixStack matrices) {
            renderStroke(matrices);
            render.render(this, matrices);
        }

    }

    public interface KeystrokeRenderer {
        void render(Keystroke stroke, MatrixStack matrices);
    }

}
