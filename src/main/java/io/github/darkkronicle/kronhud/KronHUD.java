package io.github.darkkronicle.kronhud;

import io.github.darkkronicle.kronhud.config.ConfigHandler;
import io.github.darkkronicle.kronhud.config.ConfigStorage;
import io.github.darkkronicle.kronhud.gui.hud.*;
import io.github.darkkronicle.kronhud.gui.screen.SetScreen;
import lombok.Getter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class KronHUD implements ClientModInitializer {
    public static HudManager hudManager;
    public static ConfigStorage storage;
    public static ConfigHandler storageHandler;
    public static int fps = 0;
    private boolean rendered = false;
    @Getter
    private boolean setupComplete;


    @Override
    public void onInitializeClient() {
        KeyBinding key = new KeyBinding("keys.kronhud.edithud", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Y, "keys.category.kronhud.keys");
        KeyBindingHelper.registerKeyBinding(key);
        ClientTickEvents.START_CLIENT_TICK.register(s -> {
            if (key.wasPressed()) {
                s.openScreen(SetScreen.getScreen());
            }
            if (!rendered) {
                setHud();
                rendered = true;
            }
        });
    }

    public void setHud() {
        storageHandler = new ConfigHandler();
        hudManager = new HudManager();
        hudManager.add(new ArmorHud());
        hudManager.add(new ArrowHud());
        hudManager.add(new CPSHud());
        hudManager.add(new CrossHairHud());
        hudManager.add(new FPSHud());
        hudManager.add(new ItemUpdateHud());
        hudManager.add(new KeystrokeHud());
        hudManager.add(new PingHud());
        hudManager.add(new PotionsHud());
        hudManager.add(new CoordsHud());
        hudManager.add(new BossBarHud());
        hudManager.add(new ScoreboardHud());
        HudRenderCallback.EVENT.register((matrixStack, v) -> hudManager.render(matrixStack));
        setupComplete = true;
    }

}
