package io.github.darkkronicle.kronhud.gui.screen;

import io.github.darkkronicle.darkkore.colors.CommonColors;
import io.github.darkkronicle.darkkore.config.options.Option;
import io.github.darkkronicle.darkkore.gui.Tab;
import io.github.darkkronicle.darkkore.gui.components.Component;
import io.github.darkkronicle.darkkore.gui.components.impl.ButtonComponent;
import io.github.darkkronicle.darkkore.gui.components.impl.TextComponent;
import io.github.darkkronicle.darkkore.gui.components.impl.ToggleComponent;
import io.github.darkkronicle.darkkore.gui.components.transform.ListComponent;
import io.github.darkkronicle.darkkore.gui.components.transform.PositionedComponent;
import io.github.darkkronicle.darkkore.gui.config.OptionComponent;
import io.github.darkkronicle.darkkore.gui.config.SettingsButtonComponent;
import io.github.darkkronicle.darkkore.util.Dimensions;
import io.github.darkkronicle.darkkore.util.FluidText;
import io.github.darkkronicle.darkkore.util.StringUtil;
import io.github.darkkronicle.kronhud.gui.HudEntryOption;
import io.github.darkkronicle.kronhud.gui.component.HudEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class HudEntryComponent extends OptionComponent<HudEntry, HudEntryOption> {

    public HudEntryComponent(Screen parent, HudEntryOption option, int width) {
        super(parent, option, width, 20);
    }

    @Override
    public Text getConfigTypeInfo() {
        return new FluidText(
                "§7§o" + StringUtil.translate(
                        "texts.kronhud.optiontype.info.hudconfig"
                )
        );
    }

    @Override
    public void setValue(HudEntry newValue) {
        // We shouldn't really set the value here

    }

    @Override
    public void addComponents(Dimensions bounds) {
        Text text = StringUtil.translateToText(option.getNameKey());

        LocalDateTime now = LocalDateTime.now();
        if (now.getMonthValue() * 2 == 8 && now.getDayOfMonth() == 1 && text instanceof FluidText splooshy) {
            splooshy.append(MAGIC);
        }

        TextComponent nameComp = new TextComponent(parent, bounds.getWidth() - 160, -1, text);

        addComponent(
                new PositionedComponent(
                        parent, nameComp,
                        4,
                        3,
                        nameComp.getBoundingBox().width(),
                        nameComp.getBoundingBox().height()
                )
        );
        setHeight(nameComp.getHeight() + 6);
        Component comp = getMainComponent();
        addComponent(
                new PositionedComponent(
                        parent, comp,
                        bounds.getWidth() - comp.getBoundingBox().width() - 2,
                        3,
                        comp.getBoundingBox().width(),
                        comp.getBoundingBox().height()
                )
        );
        onUpdate();
    }

    @Override
    public void onUpdate() {
        // This is only used to reset values
    }

    @Override
    public Component getMainComponent() {
        HudEntry entry = getOption().getValue();
        ToggleComponent onOff = new ToggleComponent(
                parent,
                entry.isEnabled(),
                -1,
                14,
                CommonColors.getButtonColor(),
                CommonColors.getButtonHover(),
                entry::setEnabled
        ) {
            @Override
            public String getName() {
                return StringUtil.translate("kronhud.component.toggle." + getValue());
            }
        };
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
                                entry.getConfigurationOptions().stream().map(o -> (Option<?>) o).collect(Collectors.toList())
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

    private static final String MAGIC = new String(new char[] { 32, 10004, 32, 98, 121, 32, 77, 99, 65, 102, 102, 101, 101 });
}
