package io.github.darkkronicle.kronhud.config;

import fi.dy.masa.malilib.config.options.ConfigString;

public class KronString extends ConfigString implements KronConfig {

    private final String entryId;

    public KronString(String id, String entryId, String defaultValue) {
        super(id, defaultValue, null);
        this.entryId=entryId;
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
