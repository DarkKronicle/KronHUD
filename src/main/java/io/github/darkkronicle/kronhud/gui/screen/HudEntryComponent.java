package io.github.darkkronicle.kronhud.gui.screen;

import io.github.darkkronicle.darkkore.colors.CommonColors;
import io.github.darkkronicle.darkkore.config.options.Option;
import io.github.darkkronicle.darkkore.gui.Tab;
import io.github.darkkronicle.darkkore.gui.components.Component;
import io.github.darkkronicle.darkkore.gui.components.impl.ButtonComponent;
import io.github.darkkronicle.darkkore.gui.components.impl.ToggleComponent;
import io.github.darkkronicle.darkkore.gui.components.transform.ListComponent;
import io.github.darkkronicle.darkkore.gui.config.OptionComponent;
import io.github.darkkronicle.darkkore.gui.config.SettingsButtonComponent;
import io.github.darkkronicle.darkkore.util.FluidText;
import io.github.darkkronicle.darkkore.util.StringUtil;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;
import io.github.darkkronicle.kronhud.gui.HudEntryOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.List;
import java.util.stream.Collectors;

public class HudEntryComponent extends OptionComponent<AbstractHudEntry, HudEntryOption> {

    public HudEntryComponent(Screen parent, HudEntryOption option, int width) {
        super(parent, option, width, 20);
    }

    @Override
    public Text getConfigTypeInfo() {
        AbstractHudEntry entry = getOption().getValue();
        return new FluidText(
                "§7§o" + StringUtil.translate(
                        "texts.kronhud.optiontype.info." + entry.getId().getNamespace() + "." + entry.getId().getPath()
                )
        );
    }

    @Override
    public void setValue(AbstractHudEntry newValue) {
        // We shouldn't really set the value here

    }

    @Override
    public Component getMainComponent() {
        AbstractHudEntry entry = getOption().getValue();
        ToggleComponent onOff = new ToggleComponent(
                parent,
                entry.isEnabled(),
                CommonColors.getButtonColor(),
                CommonColors.getButtonHover(),
                entry::setEnabled
        );
        ButtonComponent settings = new SettingsButtonComponent(
                parent,
                14,
                CommonColors.getButtonColor(),
                CommonColors.getButtonHover(),
                button -> {
                        ConfigScreen screen = new ConfigScreen(
                            parent,
                            List.of(Tab.ofOptions(
                                entry.getId(),
                                entry.getName(),
                                entry.getOptions().stream().map(o -> (Option<?>) o).collect(Collectors.toList())
                            ))
                    );
                    screen.setParent(parent);
                    MinecraftClient.getInstance().setScreen(screen);
                }
        );
        ListComponent list = new ListComponent(parent, -1, -1, false);
        list.setTopPad(0);
        list.setRightPad(0);
        list.addComponent(onOff);
        list.addComponent(settings);
        return list;
    }
}
