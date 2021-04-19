package io.github.darkkronicle.kronhud.gui.hud;

import io.github.darkkronicle.kronhud.KronHUD;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;
import io.github.darkkronicle.kronhud.hooks.KronHudHooks;
import io.github.darkkronicle.polish.api.EntryBuilder;
import io.github.darkkronicle.polish.gui.complexwidgets.EntryButtonList;
import io.github.darkkronicle.polish.gui.screens.BasicConfigScreen;
import io.github.darkkronicle.polish.util.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Optional;

public class KeystrokeHud extends AbstractHudEntry {
    public static final Identifier ID = new Identifier("kronhud", "keystrokehud");

    private ArrayList<Keystroke> keystrokes;
    private final MinecraftClient client;

    public KeystrokeHud() {
        super(54, 61);
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
            rect(matrices, bounds.x() + stroke.offset.getX() + 2, bounds.y() + stroke.offset.getY() + 2, bounds.width() - 4, 1, stroke.getFGColor().color());
        }, getStorage().unselected, getStorage().selected, getStorage().unselectedFG, getStorage().selectedFG));
        KeyBinding.unpressAll();
        KeyBinding.updatePressedStates();
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
        if (keystrokes == null) {
            setKeystrokes();
        }
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
            DrawUtil.rect(matrices, pos.getX(), pos.getY(), width, height, Colors.SELECTOR_BLUE.color().withAlpha(100).color());
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
            SimpleRectangle strokeBounds = stroke.bounds;
            client.textRenderer.draw(matrices, word, (strokeBounds.x() + stroke.offset.getX() + ((float) strokeBounds.width() / 2)) - ((float) client.textRenderer.getWidth(word) / 2), strokeBounds.y() + stroke.offset.getY() + ((float) strokeBounds.height() / 2) - 4, stroke.getFGColor().color());
        }, getStorage().unselected, getStorage().selected, getStorage().unselectedFG, getStorage().selectedFG);
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

    @Override
    public Screen getConfigScreen() {
        EntryBuilder builder = EntryBuilder.create();
        EntryButtonList list = new EntryButtonList((client.getWindow().getScaledWidth() / 2) - 290, (client.getWindow().getScaledHeight() / 2) - 70, 580, 150, 1, false);
        list.addEntry(builder.startToggleEntry(new TranslatableText("option.kronhud.enabled"), getStorage().enabled).setDimensions(20, 10).setSavable(val -> getStorage().enabled = val).build(list));
        list.addEntry(builder.startFloatSliderEntry(new TranslatableText("option.kronhud.scale"), getStorage().scale, 0.2F, 1.5F).setWidth(80).setSavable(val -> getStorage().scale = val).build(list));
        list.addEntry(builder.startColorButtonEntry(new TranslatableText("option.kronhud.keystrokehud.unselectedcolor"), getStorage().unselected).setSavable(val -> getStorage().unselected = val).build(list));
        list.addEntry(builder.startColorButtonEntry(new TranslatableText("option.kronhud.keystrokehud.selectedcolor"), getStorage().selected).setSavable(val -> getStorage().selected = val).build(list));
        list.addEntry(builder.startColorButtonEntry(new TranslatableText("option.kronhud.keystrokehud.unselectedfgcolor"), getStorage().unselectedFG).setSavable(val -> getStorage().unselectedFG = val).build(list));
        list.addEntry(builder.startColorButtonEntry(new TranslatableText("option.kronhud.keystrokehud.selectedfgcolor"), getStorage().selectedFG).setSavable(val -> getStorage().selectedFG = val).build(list));

        return new BasicConfigScreen(getName(), list, () -> {
            KronHUD.storageHandler.saveDefaultHandling();
            setKeystrokes();
        });

    }

    @Override
    public Text getName() {
        return new TranslatableText("hud.kronhud.keystrokehud");
    }

    public static class Keystroke {
        public final KeyBinding key;
        public final KeystrokeRender render;
        public SimpleRectangle bounds;
        public DrawPosition offset;
        public float percent = 1;
        private float start = -1;
        private final int animTime = 100;
        private final EasingFunctions ease = EasingFunctions.Types.SINE_IN;
        private final SimpleColor unselected;
        private final SimpleColor selected;
        private final SimpleColor unselectedFG;
        private final SimpleColor selectedFG;
        private boolean wasPressed = false;

        public Keystroke(SimpleRectangle bounds, DrawPosition offset, KeyBinding key, KeystrokeRender render, SimpleColor unselected, SimpleColor selected, SimpleColor unselectedFG, SimpleColor selectedFG) {
            this.bounds = bounds;
            this.offset = offset;
            this.key = key;
            this.render = render;
            this.selected = selected;
            this.unselected = unselected;
            this.unselectedFG = unselectedFG;
            this.selectedFG = selectedFG;
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
            percent = start == -1 ? 1 : (float) ease.apply(MathHelper.clamp((Util.getMeasuringTimeMs() - start) / animTime, 0, 1));
            return key.isPressed() ? ColorUtil.blend(unselected, selected, percent) : ColorUtil.blend(selected, unselected, percent);
        }

        public SimpleColor getFGColor() {
            percent = start == -1 ? 1 : (float) ease.apply(MathHelper.clamp((Util.getMeasuringTimeMs() - start) / animTime, 0, 1));
            return key.isPressed() ? ColorUtil.blend(unselectedFG, selectedFG, percent) : ColorUtil.blend(selectedFG, unselectedFG, percent);
        }

        public void render(MatrixStack matrices) {

            renderStroke(matrices);
            render.render(this, matrices);
        }

        public interface KeystrokeRender {
            void render(Keystroke stroke, MatrixStack matrices);
        }
    }

    public static class Storage extends AbstractStorage {
        public SimpleColor unselected;
        public SimpleColor selected;
        public SimpleColor unselectedFG;
        public SimpleColor selectedFG;

        public Storage() {
            x = 0F;
            y = 0F;
            scale = 1;
            unselected = new SimpleColor(0, 0, 0, 100);
            selected = new SimpleColor(255, 255, 255, 150);
            unselectedFG = new SimpleColor(16777215);
            selectedFG = new SimpleColor(4210752);
        }
    }
}
