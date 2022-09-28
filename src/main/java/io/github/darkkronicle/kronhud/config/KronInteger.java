package io.github.darkkronicle.kronhud.config;

import io.github.darkkronicle.darkkore.config.options.IntegerOption;

public class KronInteger extends IntegerOption implements KronConfig {

    private final String entryId;

    public KronInteger(String id, String entryId, int defaultValue, int min, int max) {
        super(id, entryId, "", defaultValue, min, max);
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
