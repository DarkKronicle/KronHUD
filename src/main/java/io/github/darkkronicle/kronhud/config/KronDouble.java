package io.github.darkkronicle.kronhud.config;

import fi.dy.masa.malilib.config.options.ConfigDouble;
import fi.dy.masa.malilib.config.options.ConfigInteger;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;

public class KronDouble extends ConfigDouble implements KronConfig {

    private String entryId;

    public KronDouble(String id, String entryId, double defaultValue, double min, double max) {
        super(id, defaultValue, min, max, null);
        this.entryId = entryId;
    }

    @Override
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
