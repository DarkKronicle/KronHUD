package io.github.darkkronicle.kronhud;

import com.google.gson.JsonObject;
import io.github.darkkronicle.darkkore.config.ConfigurationManager;
import io.github.darkkronicle.darkkore.intialization.InitializationHandler;
import io.github.darkkronicle.kronhud.config.ConfigHandler;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;
import io.github.darkkronicle.kronhud.gui.hud.*;
import io.github.darkkronicle.kronhud.gui.screen.HudEditScreen;
import lombok.Getter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class KronHUD implements ClientModInitializer {

    public static final String MOD_ID = "kronhud";

    @Override
    public void onInitializeClient() {
        initHuds();
        InitializationHandler.getInstance().registerInitializer(MOD_ID, 0, new InitHandler());
        ConfigurationManager.getInstance().add(ConfigHandler.getInstance());
    }

    public void initHuds() {
        HudManager hudManager = HudManager.getInstance();
        hudManager.add(new ArmorHud());
        hudManager.add(new ArrowHud());
        hudManager.add(new CPSHud());
        hudManager.add(new CrosshairHud());
        hudManager.add(new FPSHud());
        hudManager.add(new IPHud());
        hudManager.add(new SpeedHud());
        hudManager.add(new ItemUpdateHud());
        hudManager.add(new KeystrokeHud());
        hudManager.add(new PingHud());
        hudManager.add(new PotionsHud());
        hudManager.add(new CoordsHud());
        hudManager.add(new BossBarHud());
        hudManager.add(new ScoreboardHud());
        hudManager.add(new PlayerHud());
        hudManager.add(new ActionBarHud());
        hudManager.add(new ToggleSprintHud());
        HudRenderCallback.EVENT.register(hudManager::render);
        hudManager.getEntries().forEach(AbstractHudEntry::init);
    }

}
