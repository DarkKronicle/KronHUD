package io.github.darkkronicle.kronhud.gui.hud;

import io.github.darkkronicle.kronhud.KronHUD;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;
import io.github.darkkronicle.kronhud.hooks.KronHudHooks;
import io.github.darkkronicle.polish.api.EntryBuilder;
import io.github.darkkronicle.polish.gui.complexwidgets.EntryButtonList;
import io.github.darkkronicle.polish.gui.screens.BasicConfigScreen;
import io.github.darkkronicle.polish.util.ColorUtil;
import io.github.darkkronicle.polish.util.Colors;
import io.github.darkkronicle.polish.util.DrawPosition;
import io.github.darkkronicle.polish.util.EasingFunctions;
import io.github.darkkronicle.polish.util.SimpleColor;
import io.github.darkkronicle.polish.util.SimpleRectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Optional;

public class KeystrokeHud extends AbstractHudEntry {
    public static final Identifier ID = new Identifier("kronhud", "keystrokehud");

    private ArrayList<Keystroke> keystrokes;
    private MinecraftClient client;

    public KeystrokeHud() {
        super(54, 61);
        this.client = MinecraftClient.getInstance();
        KronHudHooks.KEYBIND_CHANGE.register(key -> setKeystrokes());

    }

    public void setKeystrokes() {
        keystrokes = new ArrayList<>();
        DrawPosition scaledPos = getScaledPos();
        // LMB
        keystrokes.add(createFromKey(new SimpleRectangle(0, 36, 26, 17), scaledPos, client.options.keyAttack));
        // RMB
        keystrokes.add(createFromKey(new SimpleRectangle(27, 36, 26, 17), scaledPos, client.options.keyUse));
        // W
        keystrokes.add(createFromKey(new SimpleRectangle(18, 0, 17, 17), scaledPos, client.options.keyForward));
        // A
        keystrokes.add(createFromKey(new SimpleRectangle(0, 18, 17, 17), scaledPos, client.options.keyLeft));
        // S
        keystrokes.add(createFromKey(new SimpleRectangle(18, 18, 17, 17), scaledPos, client.options.keyBack));
        // D
        keystrokes.add(createFromKey(new SimpleRectangle(36, 18, 17, 17), scaledPos, client.options.keyRight));

        // Space
        keystrokes.add(new Keystroke(new SimpleRectangle(0, 54, 53, 7), scaledPos, client.options.keyJump, (stroke, matrices) -> {
            SimpleRectangle bounds = stroke.bounds;
            rect(matrices, bounds.x() + stroke.offset.getX()+ 2, bounds.y() + stroke.offset.getY() + 2, bounds.width() - 4, 1, stroke.getColor(true).withAlpha(150).color());
        }, getStorage().unselected, getStorage().selected));
        KeyBinding.unpressAll();
        KeyBinding.updatePressedStates();
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


    @Override
    public void render(MatrixStack matrices) {
        matrices.push();
        matrices.scale(getStorage().scale, getStorage().scale, 1);
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
        DrawPosition pos = getScaledPos();
        for (Keystroke stroke : keystrokes) {
            stroke.offset = pos;
        }
    }

    @Override
    public void renderPlaceholder(MatrixStack matrices) {
        matrices.push();
        matrices.scale(getStorage().scale, getStorage().scale, 1);
        DrawPosition pos = getScaledPos();
        if (hovered) {
            rect(matrices, pos.getX(), pos.getY(), width, height, Colors.WHITE.color().withAlpha(150).color());
        } else {
            rect(matrices, pos.getX(), pos.getY(), width, height, Colors.WHITE.color().withAlpha(50).color());
        }
        outlineRect(matrices, pos.getX(), pos.getY(), width, height, Colors.BLACK.color().color());
        matrices.scale(1 / getStorage().scale, 1 / getStorage().scale, 1);
        renderHud(matrices);
        hovered = false;
        matrices.pop();
    }

    public Keystroke createFromKey(SimpleRectangle bounds, DrawPosition offset, KeyBinding key) {
        String name = getMouseKeyBindName(key).orElse(key.getBoundKeyLocalizedText().asString().toUpperCase());
        if (name.length() > 4) {
            name = name.substring(0, 2);
        }
        return createFromString(bounds, offset, key, name);
    }

    public Keystroke createFromString(SimpleRectangle bounds, DrawPosition offset, KeyBinding key, String word) {
        return new Keystroke(bounds, offset, key, (stroke, matrices) -> {
            SimpleRectangle strokeBound = stroke.bounds;
            client.textRenderer.draw(matrices, word, (strokeBound.x() + stroke.offset.getX() + ((float)strokeBound.width() / 2 + 1)) - ((float) client.textRenderer.getWidth(word) / 2), strokeBound.y() + stroke.offset.getY() + ((float) strokeBound.height() / 2) - 4, stroke.getColor(true).withAlpha(150).color());
        }, getStorage().unselected, getStorage().selected);
    }

    public static class Keystroke {
        public SimpleRectangle bounds;
        public DrawPosition offset;
        public final KeyBinding key;
        public final KeystrokeRender render;
        private float start = -1;
        private int animTime = 100;
        private EasingFunctions ease = EasingFunctions.Types.SINE_IN;
        private SimpleColor unselected;
        private SimpleColor selected;
        public float percent = 1;
        private boolean wasPressed = false;

        public Keystroke(SimpleRectangle bounds, DrawPosition offset, KeyBinding key, KeystrokeRender render, SimpleColor unselected, SimpleColor selected) {
            this.bounds = bounds;
            this.offset = offset;
            this.key = key;
            this.render = render;
            this.selected = selected;
            this.unselected = unselected;
        }

        public void setPos(int x, int y) {
            bounds = new SimpleRectangle(x, y, bounds.width(), bounds.height());
        }

        public void setDimensions(int width, int height) {
            bounds = new SimpleRectangle(bounds.x(), bounds.y(), width, height);
        }

        public void setBounds(int x, int y, int width, int height) {
            bounds = new SimpleRectangle(x, y, width, height);
        }

        public void renderStroke(MatrixStack matrices) {
            if (key.isPressed() != wasPressed) {
                start = Util.getMeasuringTimeMs();
            }
            rect(matrices, bounds.x() + offset.getX(), bounds.y() + offset.getY(), bounds.width(), bounds.height(), getColor().color());
            if ((Util.getMeasuringTimeMs() - start) / animTime >= 1) {
                start = -1;
            }
            wasPressed = key.isPressed();
        }

        public SimpleColor getColor() {
            return getColor(false);
        }

        public SimpleColor getColor(boolean invert) {
            percent = start == -1 ? 1 : (float) ease.apply(MathHelper.clamp((Util.getMeasuringTimeMs() - start) / animTime, 0, 1));
            if (invert) {
                return key.isPressed() ? ColorUtil.blend(selected, unselected, percent) : ColorUtil.blend(unselected, selected, percent);
            }
            return key.isPressed() ? ColorUtil.blend(unselected, selected, percent) : ColorUtil.blend(selected, unselected, percent);
        }

        public void render(MatrixStack matrices) {
            renderStroke(matrices);
            render.render(this, matrices);
        }

        public interface KeystrokeRender {
            void render(Keystroke stroke, MatrixStack matrices);
        }
    }

    @Override
    public Identifier getID() {
        return ID;
    }

    @Override
    public boolean moveable() {
        return true;
    }

    @Override
    public Storage getStorage() {
        return KronHUD.storage.keystrokeHudStorage;
    }

    public static class Storage extends AbstractStorage {
        public SimpleColor unselected;
        public SimpleColor selected;

        public Storage() {
            x = 0F;
            y = 0F;
            scale = 1;
            unselected = new SimpleColor(0, 0, 0, 100);
            selected = new SimpleColor(255, 255, 255, 150);
        }
    }

    @Override
    public Screen getConfigScreen() {
        EntryBuilder builder = EntryBuilder.create();
        EntryButtonList list = new EntryButtonList((client.getWindow().getScaledWidth() / 2) - 290, (client.getWindow().getScaledHeight() / 2) - 70, 580, 150, 1, false);
        list.addEntry(builder.startToggleEntry(new LiteralText("Enabled"), getStorage().enabled).setDimensions(20, 10).setSavable(val -> getStorage().enabled = val).build(list));
        list.addEntry(builder.startFloatSliderEntry(new LiteralText("Scale"), getStorage().scale, 0.2F, 1.5F).setWidth(80).setSavable(val -> getStorage().scale = val).build(list));
        list.addEntry(builder.startColorButtonEntry(new LiteralText("Unselected Color"), getStorage().unselected).setSavable(val -> getStorage().unselected = val).build(list));
        list.addEntry(builder.startColorButtonEntry(new LiteralText("Selected Color"), getStorage().selected).setSavable(val -> getStorage().selected = val).build(list));

        return new BasicConfigScreen(new LiteralText(getName()), list, () -> KronHUD.storageHandler.saveDefaultHandling());

    }

    @Override
    public String getName() {
        return "KeystrokeHUD";
    }
}
