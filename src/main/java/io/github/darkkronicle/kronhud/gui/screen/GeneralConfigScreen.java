package io.github.darkkronicle.kronhud.gui.screen;

import io.github.darkkronicle.kronhud.KronHUD;
import io.github.darkkronicle.kronhud.config.ConfigStorage;
import io.github.darkkronicle.polish.api.EntryBuilder;
import io.github.darkkronicle.polish.gui.complexwidgets.EntryButtonList;
import io.github.darkkronicle.polish.gui.screens.BasicConfigScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;

public class GeneralConfigScreen {

    public static Screen getScreen() {
        MinecraftClient client = MinecraftClient.getInstance();
        EntryBuilder builder = EntryBuilder.create();
        ConfigStorage storage = KronHUD.storage;
        EntryButtonList list = new EntryButtonList((client.getWindow().getScaledWidth() / 2) - 290, (client.getWindow().getScaledHeight() / 2) - 70, 580, 150, 1, false);
        list.addEntry(builder.startToggleEntry(new TranslatableText("option.kronhud.general.vanillapotionhud"), storage.disableVanillaPotionHud).setDimensions(20, 10).setSavable(val -> storage.disableVanillaPotionHud = val).build(list));
        list.addEntry(builder.startToggleEntry(new TranslatableText("option.kronhud.general.vanillavignette"), storage.disableVanillaVignette).setDimensions(20, 10).setSavable(val -> storage.disableVanillaVignette = val).build(list));
        return new BasicConfigScreen(new TranslatableText("option.category.kronhud.general"), list, () -> KronHUD.storageHandler.saveDefaultHandling());

    }

}
