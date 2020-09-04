package io.github.darkkronicle.kronhud;

import io.github.darkkronicle.kronhud.config.ConfigHandler;
import io.github.darkkronicle.kronhud.config.ConfigStorage;
import io.github.darkkronicle.kronhud.gui.hud.ArmorHud;
import io.github.darkkronicle.kronhud.gui.hud.ArrowHud;
import io.github.darkkronicle.kronhud.gui.hud.CPSHud;
import io.github.darkkronicle.kronhud.gui.hud.CoordsHud;
import io.github.darkkronicle.kronhud.gui.hud.CrossHairHud;
import io.github.darkkronicle.kronhud.gui.hud.FPSHud;
import io.github.darkkronicle.kronhud.gui.hud.HudManager;
import io.github.darkkronicle.kronhud.gui.hud.ItemUpdateHud;
import io.github.darkkronicle.kronhud.gui.hud.KeystrokeHud;
import io.github.darkkronicle.kronhud.gui.hud.PingHud;
import io.github.darkkronicle.kronhud.gui.hud.PotionsHud;
import io.github.darkkronicle.kronhud.gui.screen.SetScreen;
import io.github.darkkronicle.kronhud.hooks.KronHudHooks;
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
        KeyBinding key = new KeyBinding("kronhud.edithud", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Y, "category.kronhud.keys");
        KeyBindingHelper.registerKeyBinding(key);
        ClientTickEvents.START_CLIENT_TICK.register(s -> {
            if (key.wasPressed()) {
                s.openScreen(new SetScreen());
            }
        });
        KronHudHooks.HUD_RENDER_PRE.register((matrices, delta) -> {
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
        HudRenderCallback.EVENT.register((matrixStack, v) -> hudManager.render(matrixStack));
        setupComplete = true;
    }

}
