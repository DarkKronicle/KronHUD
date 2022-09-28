package io.github.darkkronicle.kronhud.config;


import io.github.darkkronicle.darkkore.config.options.BooleanOption;

public class KronBoolean extends BooleanOption implements KronConfig {

    private final String entryId;

    public KronBoolean(String id, String entryId, boolean defaultValue) {
        super(id, entryId, "", defaultValue);
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
