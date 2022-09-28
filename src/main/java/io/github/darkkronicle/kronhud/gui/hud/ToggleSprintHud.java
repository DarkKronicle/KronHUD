package io.github.darkkronicle.kronhud.gui.hud;


import io.github.darkkronicle.darkkore.config.options.Option;
import io.github.darkkronicle.kronhud.config.KronBoolean;
import io.github.darkkronicle.kronhud.config.KronConfig;
import io.github.darkkronicle.kronhud.config.KronString;
import lombok.Getter;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class ToggleSprintHud extends CleanHudEntry {

    public static final Identifier ID = new Identifier("kronhud", "togglesprint");
    private final KronBoolean toggleSprint = new KronBoolean("toggleSprint", ID.getPath(), false);
    private final KronBoolean toggleSneak = new KronBoolean("toggleSneak", ID.getPath(), false);
    private final KronBoolean randomPlaceholder = new KronBoolean("randomPlaceholder", ID.getPath(), false);
    private final KronString placeholder = new KronString("placeholder", ID.getPath(), "No keys pressed");

    private final KeyBinding sprintToggle = new KeyBinding("keys.kronhud.toggleSprint", GLFW.GLFW_KEY_K, "keys.category.kronhud.keys");
    private final KeyBinding sneakToggle = new KeyBinding("keys.kronhud.toggleSneak", GLFW.GLFW_KEY_I, "keys.category.kronhud.keys");

    @Getter
    private final KronBoolean sprintToggled = new KronBoolean("sprintToggled", ID.getPath(), false);
    private boolean sprintWasPressed = false;
    @Getter
    private final KronBoolean sneakToggled = new KronBoolean("sneakToggled", ID.getPath(), false);
    private boolean sneakWasPressed = false;

    private final List<String> texts = new ArrayList<>();
    private String text = "";

    public ToggleSprintHud() {
        super(100, 20);
    }

    @Override
    public void init() {
        KeyBindingHelper.registerKeyBinding(sprintToggle);
        KeyBindingHelper.registerKeyBinding(sneakToggle);
    }

    private void loadRandomPlaceholder() {
        try {
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(MinecraftClient.getInstance()
                                                         .getResourceManager()
                                                         .getResourceOrThrow(new Identifier("texts/splashes.txt"))
                                                         .getInputStream(), StandardCharsets.UTF_8)
            );
            String string;
            while ((string = bufferedReader.readLine()) != null) {
                string = string.trim();
                if (!string.isEmpty()) {
                    texts.add(string);
                }
            }

            text = texts.get(new Random().nextInt(texts.size()));
        } catch (Exception e) {
            text = "";
        }
    }

    private String getRandomPlaceholder() {
        if (Objects.equals(text, "")) {
            loadRandomPlaceholder();
        }
        return text;
    }

    @Override
    public String getPlaceholder() {
        return randomPlaceholder.getValue() ? getRandomPlaceholder() : placeholder.getValue();
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
    public String getValue() {

        if (client.options.sneakKey.isPressed()) {
            return I18n.translate("texts.kronhud.togglesprint.sneaking_pressed");
        }
        if (client.options.sprintKey.isPressed()) {
            return I18n.translate("texts.kronhud.togglesprint.sprinting_pressed");
        }

        if (toggleSneak.getValue() && sneakToggled.getValue()) {
            return I18n.translate("texts.kronhud.togglesprint.sneaking_toggled");
        }
        if (toggleSprint.getValue() && sprintToggled.getValue()) {
            return I18n.translate("texts.kronhud.togglesprint.sprinting_toggled");
        }
        return getPlaceholder();
    }

    @Override
    public boolean tickable() {
        return true;
    }

    @Override
    public void tick() {
        if (sprintToggle.isPressed() != sprintWasPressed && sprintToggle.isPressed() && toggleSprint.getValue()) {
            sprintToggled.setValue(!sprintToggled.getValue());
            sprintWasPressed = sprintToggle.isPressed();
        } else if (!sprintToggle.isPressed()) {
            sprintWasPressed = false;
        }
        if (sneakToggle.isPressed() != sneakWasPressed && sneakToggle.isPressed() && toggleSneak.getValue()) {
            sneakToggled.setValue(!sneakToggled.getValue());
            sneakWasPressed = sneakToggle.isPressed();
        } else if (!sneakToggle.isPressed()) {
            sneakWasPressed = false;
        }
    }

    @Override
    public List<KronConfig<?>> getOptions() {
        List<KronConfig<?>> options = super.getOptions();
        options.add(toggleSprint);
        options.add(toggleSneak);
        options.add(randomPlaceholder);
        options.add(placeholder);
        return options;
    }

    @Override
    public List<KronConfig<?>> getAllOptions() {
        List<KronConfig<?>> options = super.getAllOptions();
        options.add(sprintToggled);
        options.add(sneakToggled);
        return options;
    }

}
