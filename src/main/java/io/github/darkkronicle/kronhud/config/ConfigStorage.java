package io.github.darkkronicle.kronhud.config;

import io.github.darkkronicle.kronhud.gui.hud.ArmorHud;
import io.github.darkkronicle.kronhud.gui.hud.ArrowHud;
import io.github.darkkronicle.kronhud.gui.hud.BossBarHud;
import io.github.darkkronicle.kronhud.gui.hud.CPSHud;
import io.github.darkkronicle.kronhud.gui.hud.CoordsHud;
import io.github.darkkronicle.kronhud.gui.hud.CrossHairHud;
import io.github.darkkronicle.kronhud.gui.hud.FPSHud;
import io.github.darkkronicle.kronhud.gui.hud.ItemUpdateHud;
import io.github.darkkronicle.kronhud.gui.hud.KeystrokeHud;
import io.github.darkkronicle.kronhud.gui.hud.PingHud;
import io.github.darkkronicle.kronhud.gui.hud.PotionsHud;
import io.github.darkkronicle.kronhud.gui.hud.ScoreboardHud;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ConfigStorage {
    // HUD Configuration
    public ArmorHud.Storage armorHudStorage = new ArmorHud.Storage();
    public ItemUpdateHud.Storage itemUpdateHudStorage = new ItemUpdateHud.Storage();
    public CPSHud.CPSStorage cpsHudStorage = new CPSHud.CPSStorage();
    public FPSHud.Storage fpsHudStorage = new FPSHud.Storage();
    public PingHud.Storage pingHudStorage = new PingHud.Storage();
    public PotionsHud.Storage potionsHudStorage = new PotionsHud.Storage();
    public ArrowHud.Storage arrowHudStorage = new ArrowHud.Storage();
    public CrossHairHud.Storage crossHairHudStorage = new CrossHairHud.Storage();
    public KeystrokeHud.Storage keystrokeHudStorage = new KeystrokeHud.Storage();
    public CoordsHud.Storage coordsHudStorage = new CoordsHud.Storage();
    public BossBarHud.Storage bossBarHudStorage = new BossBarHud.Storage();
    public ScoreboardHud.Storage scoreboardHudStorage = new ScoreboardHud.Storage();

    // General Configuration
    public boolean disableVanillaPotionHud = true;
    public boolean disableVanillaVignette = false;
}
