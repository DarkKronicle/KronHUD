package io.github.darkkronicle.kronhud.gui.hud;


import fi.dy.masa.malilib.config.IConfigBase;
import io.github.darkkronicle.kronhud.config.KronBoolean;
import io.github.darkkronicle.kronhud.config.KronString;
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

    public static final Identifier ID = new Identifier("kronhud","togglesprint");
    private final KronBoolean toggleSprint = new KronBoolean("toggleSprint", ID.getPath(), false);
    private final KronBoolean toggleSneak = new KronBoolean("toggleSneak", ID.getPath(),false);
    private final KronBoolean randomPlaceholder = new KronBoolean("randomPlaceholder", ID.getPath(),false);
    private final KronString placeholder = new KronString("placeholder", ID.getPath(),"No keys pressed");

    KeyBinding sprintToggle = new KeyBinding("key.toggleSprint", GLFW.GLFW_KEY_K, "category.axolotlclient");
    KeyBinding sneakToggle = new KeyBinding("key.toggleSneak", GLFW.GLFW_KEY_I, "category.axolotlclient");

    public KronBoolean sprintToggled = new KronBoolean("sprintToggled", ID.getPath(),false);
    private boolean sprintWasPressed = false;
    public KronBoolean sneakToggled = new KronBoolean("sneakToggled", ID.getPath(),false);
    private boolean sneakWasPressed = false;

    private final List<String> texts = new ArrayList<>();
    private String text = "";

    public ToggleSprintHud(){
        super(100, 20);
        KeyBindingHelper.registerKeyBinding(sprintToggle);
        KeyBindingHelper.registerKeyBinding(sneakToggle);
    }

    private void loadRandomPlaceholder(){
        try {
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(MinecraftClient.getInstance().getResourceManager().getResourceOrThrow(new Identifier("texts/splashes.txt")).getInputStream(), StandardCharsets.UTF_8)
            );
            String string;
            while((string = bufferedReader.readLine()) != null) {
                string = string.trim();
                if (!string.isEmpty()) {
                    texts.add(string);
                }
            }

            text = texts.get(new Random().nextInt(texts.size()));
        } catch (Exception e){
            text = "";
        }
    }

    private String getRandomPlaceholder(){
        if(Objects.equals(text, "")){
            loadRandomPlaceholder();
        }
        return text;
    }

    @Override
    public String getPlaceholder() {
        return randomPlaceholder.getBooleanValue() ? getRandomPlaceholder() : placeholder.getStringValue();
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
    public String getValue(){

        if(client.options.sneakKey.isPressed())return I18n.translate("texts.kronhud.togglesprint.sneaking_pressed");
        if(client.options.sprintKey.isPressed())return I18n.translate("texts.kronhud.togglesprint.sprinting_pressed");

        if(toggleSneak.getBooleanValue() && sneakToggled.getBooleanValue()){
            return I18n.translate("texts.kronhud.togglesprint.sneaking_toggled");
        }
        if(toggleSprint.getBooleanValue() && sprintToggled.getBooleanValue()){
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
        if(sprintToggle.isPressed() != sprintWasPressed && sprintToggle.isPressed() && toggleSprint.getBooleanValue()){
            sprintToggled.setBooleanValue(!sprintToggled.getBooleanValue());
            sprintWasPressed=sprintToggle.isPressed();
        } else  if(!sprintToggle.isPressed())sprintWasPressed=false;
        if(sneakToggle.isPressed() != sneakWasPressed && sneakToggle.isPressed() && toggleSneak.getBooleanValue()){
            sneakToggled.setBooleanValue(!sneakToggled.getBooleanValue());
            sneakWasPressed=sneakToggle.isPressed();
        } else if(!sneakToggle.isPressed())sneakWasPressed = false;
    }

    @Override
    public void addConfigOptions(List<IConfigBase> options) {
        super.addConfigOptions(options);
        options.add(toggleSprint);
        options.add(toggleSneak);
        options.add(randomPlaceholder);
        options.add(placeholder);
    }

    @Override
    public List<IConfigBase> getAllOptions() {
        List<IConfigBase> options = super.getAllOptions();
        options.add(sprintToggled);
        options.add(sneakToggled);
        return options;
    }
}
