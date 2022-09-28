package io.github.darkkronicle.kronhud.config;

import io.github.darkkronicle.darkkore.config.options.StringOption;

public class KronString extends StringOption implements KronConfig {

    private final String entryId;

    public KronString(String id, String entryId, String defaultValue) {
        super(id, "", "", defaultValue);
        this.entryId = entryId;
    }

    @Override
    public String getEntryId() {
        return entryId;
    }

    @Override
    public String getId() {
        return super.getKey();
    }

    @Override
    public String getNameKey() {
        return KronConfig.super.getName();
    }

    @Override
    public String getInfoKey() {
        return KronConfig.super.getComment();
    }

}
