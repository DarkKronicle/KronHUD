package io.github.darkkronicle.kronhud;

import io.github.darkkronicle.darkkore.config.ConfigurationManager;
import io.github.darkkronicle.darkkore.intialization.InitializationHandler;
import io.github.darkkronicle.kronhud.config.ConfigHandler;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;
import io.github.darkkronicle.kronhud.gui.HudManager;
import io.github.darkkronicle.kronhud.gui.component.HudEntry;
import io.github.darkkronicle.kronhud.gui.hud.*;
import io.github.darkkronicle.kronhud.gui.hud.item.ArmorHud;
import io.github.darkkronicle.kronhud.gui.hud.item.ArrowHud;
import io.github.darkkronicle.kronhud.gui.hud.item.ItemUpdateHud;
import io.github.darkkronicle.kronhud.gui.hud.simple.*;
import io.github.darkkronicle.kronhud.gui.hud.vanilla.ActionBarHud;
import io.github.darkkronicle.kronhud.gui.hud.vanilla.BossBarHud;
import io.github.darkkronicle.kronhud.gui.hud.vanilla.CrosshairHud;
import io.github.darkkronicle.kronhud.gui.hud.vanilla.ScoreboardHud;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

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
        hudManager.add(new IRLTimeHud());
        hudManager.add(new ReachHud());
        hudManager.add(new CompassHud());
        hudManager.add(new TPSHud());
        hudManager.add(new ComboHud());
        HudRenderCallback.EVENT.register(hudManager::render);
        hudManager.getEntries().forEach(HudEntry::init);
    }

}
