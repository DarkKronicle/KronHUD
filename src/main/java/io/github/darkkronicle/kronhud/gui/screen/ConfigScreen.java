package io.github.darkkronicle.kronhud.gui.screen;

import io.github.darkkronicle.kronhud.KronHUD;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;
import io.github.darkkronicle.polish.gui.complexwidgets.ToggleModuleList;
import io.github.darkkronicle.polish.gui.complexwidgets.ToggleModuleWidget;
import io.github.darkkronicle.polish.gui.screens.ToggleModuleScreen;
import io.github.darkkronicle.polish.util.WidgetManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

public class ConfigScreen {

    public static Screen getScreen() {
        ToggleModuleList modules = ToggleModuleScreen.createButtonList();
        MinecraftClient client = MinecraftClient.getInstance();
        modules.addEntry(new ToggleModuleWidget(0, 0, new TranslatableText("option.category.kronhud.general"), false, (button) -> {
            modules.save();
            client.openScreen(GeneralConfigScreen.getScreen());
        }, false), (aBoolean -> nothing()));
        for (AbstractHudEntry entry : KronHUD.hudManager.getEntries()) {
            modules.addEntry(new ToggleModuleWidget(0, 0, entry.getName(), entry.getStorage().enabled, (button) -> {
                modules.save();
                KronHUD.storageHandler.saveDefaultHandling();
                client.openScreen(entry.getConfigScreen());
            }, true), aBoolean -> entry.getStorage().enabled = aBoolean);
        }
        return new ToggleModuleScreen(new TranslatableText("button.kronhud.configuration"), modules, () -> KronHUD.storageHandler.saveDefaultHandling());
    }

    public static void nothing() {}

}
