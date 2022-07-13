package io.github.darkkronicle.kronhud.gui.hud;

import fi.dy.masa.malilib.config.IConfigBase;
import io.github.darkkronicle.kronhud.config.KronColor;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;
import io.github.darkkronicle.kronhud.hooks.KronHudHooks;
import io.github.darkkronicle.kronhud.util.Color;
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

    private KronColor pressedTextColor = new KronColor("heldtextcolor", ID.getPath(), "#FF000000");
    private KronColor pressedBackgroundColor = new KronColor( "heldbackgroundcolor", ID.getPath(), "#64FFFFFF");
    private ArrayList<Keystroke> keystrokes;
    private final MinecraftClient client;

    public KeystrokeHud() {
        super(53, 61);
        this.client = MinecraftClient.getInstance();
        KronHudHooks.KEYBIND_CHANGE.register(key -> setKeystrokes());
    }

    public static Optional<String> getMouseKeyBindName(KeyBinding keyBinding) {
        if (keyBinding.getBoundKeyTranslationKey().equalsIgnoreCase(InputUtil.Type.MOUSE.createFromCode(GLFW.GLFW_MOUSE_BUTTON_1).getTranslationKey())) {
            return Optional.of("LMB");
        } else if (keyBinding.getBoundKeyTranslationKey().equalsIgnoreCase(InputUtil.Type.MOUSE.createFromCode(GLFW.GLFW_MOUSE_BUTTON_2).getTranslationKey())) {
            return Optional.of("RMB");
        } else if (keyBinding.getBoundKeyTranslationKey().equalsIgnoreCase(InputUtil.Type.MOUSE.createFromCode(GLFW.GLFW_MOUSE_BUTTON_3).getTranslationKey())) {
            return Optional.of("MMB");
        }
        return Optional.empty();
    }

    public void setKeystrokes() {
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
                    bounds.width() - 8, 1);
            fillRect(matrices, spaceBounds, stroke.getFGColor());
            if (shadow.getBooleanValue()) {
                fillRect(matrices, spaceBounds.offset(1, 1),
                        new Color((stroke.getFGColor().color() & 16579836) >> 2 | stroke.getFGColor().color() & -16777216));
            }
        }));
        KeyBinding.unpressAll();
        KeyBinding.updatePressedStates();
    }

    @Override
    public void render(MatrixStack matrices) {
        matrices.push();
        scale(matrices);
        if (keystrokes == null) {
            setKeystrokes();
        }
        for (Keystroke stroke : keystrokes) {
            stroke.render(matrices);
        }
        matrices.pop();
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
    }

    @Override
    protected boolean getShadowDefault() {
        return false;
    }

    @Override
    public void renderPlaceholder(MatrixStack matrices) {
        matrices.push();
        renderPlaceholderBackground(matrices);
        renderHud(matrices);
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

            drawString(matrices, client.textRenderer, word, x, y, stroke.getFGColor().color(), shadow.getBooleanValue());
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
    public void addConfigOptions(List<IConfigBase> options) {
        super.addConfigOptions(options);
        options.add(textColor);
        options.add(pressedTextColor);
        options.add(shadow);
        options.add(background);
        options.add(backgroundColor);
        options.add(pressedBackgroundColor);
    }

    public class Keystroke {
        public final KeyBinding key;
        public final KeystrokeRenderer render;
        public Rectangle bounds;
        public DrawPosition offset;
        private float start = -1;
        private final int animTime = 100;
        private boolean wasPressed = false;

        public Keystroke(Rectangle bounds, DrawPosition offset, KeyBinding key, KeystrokeRenderer render) {
            this.bounds = bounds;
            this.offset = offset;
            this.key = key;
            this.render = render;
        }

        public void setPos(int x, int y) {
            bounds = new Rectangle(x, y, bounds.width(), bounds.height());
        }

        public void setDimensions(int width, int height) {
            bounds = new Rectangle(bounds.x(), bounds.y(), width, height);
        }

        public void setBounds(int x, int y, int width, int height) {
            bounds = new Rectangle(x, y, width, height);
        }

        public void renderStroke(MatrixStack matrices) {
            if (key.isPressed() != wasPressed) {
                start = Util.getMeasuringTimeMs();
            }
            if (background.getBooleanValue()) {
                fillRect(matrices, bounds.offset(offset),
                        getColor());
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
            return key.isPressed() ? Color.blend(backgroundColor.getColor(), pressedBackgroundColor.getColor(),
                    getPercentPressed()) :
                    Color.blend(pressedBackgroundColor.getColor(),
                    backgroundColor.getColor(),
                    getPercentPressed());
        }

        public Color getFGColor() {
            return key.isPressed() ? Color.blend(textColor.getColor(), pressedTextColor.getColor(), getPercentPressed()) :
                    Color.blend(pressedTextColor.getColor(),
                            textColor.getColor(),
                            getPercentPressed());
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
