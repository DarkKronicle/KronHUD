package io.github.darkkronicle.kronhud.config;

import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigBooleanHotkeyed;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;
import net.minecraft.util.Identifier;

public class KronBoolean extends ConfigBoolean implements KronConfig {

    private String entryId;

    public KronBoolean(String id, String entryId, boolean defaultValue) {
        super(id, defaultValue, null);
        this.entryId = entryId;
    }

    public String getEntryId() {
        return entryId;
    }

    @Override
    public String getId() {
        return super.getName();
    }

    @Override
    public String getName() {
        return KronConfig.super.getName();
    }

    @Override
    public String getComment() {
        return KronConfig.super.getComment();
    }

}
