package io.github.darkkronicle.kronhud.gui;

import io.github.darkkronicle.darkkore.config.impl.ConfigObject;
import io.github.darkkronicle.darkkore.config.options.BasicOption;
import io.github.darkkronicle.kronhud.config.KronConfig;

import java.util.Optional;

public class HudEntryOption extends BasicOption<AbstractHudEntry> {

    public HudEntryOption(AbstractHudEntry value) {
        this(value.getNameKey(), value.getInfoKey(), value);
    }

    public HudEntryOption(String displayName, String hoverName, AbstractHudEntry value) {
        super(value.getId().toString(), displayName, hoverName, value);
        setValue(value);
    }

    @Override
    public void save(ConfigObject config) {
        AbstractHudEntry entry = getValue();
        ConfigObject obj = config.createNew();
        for (KronConfig<?> option : entry.getSaveOptions()) {
            option.save(obj);
        }
        config.set(entry.getId().toString(), obj);
    }

    @Override
    public void load(ConfigObject config) {
        AbstractHudEntry entry = getValue();
        Optional<ConfigObject> nested = config.getOptional(entry.getId().toString());
        if (nested.isEmpty()) {
            return;
        }
        for (KronConfig<?> option : value.getSaveOptions()) {
            option.load(nested.get());
        }
    }

}
