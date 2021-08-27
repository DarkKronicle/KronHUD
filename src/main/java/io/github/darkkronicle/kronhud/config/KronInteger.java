package io.github.darkkronicle.kronhud.config;

import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigInteger;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;

public class KronInteger extends ConfigInteger implements KronConfig {

    private String entryId;

    public KronInteger(String id, String entryId, int defaultValue, int min, int max) {
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
