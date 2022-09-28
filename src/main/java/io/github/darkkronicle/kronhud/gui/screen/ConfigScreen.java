package io.github.darkkronicle.kronhud.gui.screen;

import io.github.darkkronicle.darkkore.gui.Tab;
import net.minecraft.client.gui.screen.Screen;

import java.util.List;

public class ConfigScreen extends io.github.darkkronicle.darkkore.gui.ConfigScreen {

    public ConfigScreen(Screen parent, List<Tab> tabs) {
        super(tabs);
        setParent(parent);
//        title = Text.translatable("button.kronhud.configuration");
    }

}
