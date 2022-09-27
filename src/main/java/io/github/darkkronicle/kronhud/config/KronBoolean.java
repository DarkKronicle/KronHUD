package io.github.darkkronicle.kronhud.config;


import io.github.darkkronicle.darkkore.config.options.BooleanOption;

public class KronBoolean extends BooleanOption implements KronConfig {

    private final String entryId;

    public KronBoolean(String id, String entryId, boolean defaultValue) {
        super(id, entryId, "", defaultValue);
        this.entryId = entryId;
    }

    public String getEntryId() {
        return entryId;
    }

    @Override
    public String getId() {
        return super.getNameKey();
    }

    @Override
    public String getName() {
        return KronConfig.super.getName();
    }

    @Override
    public String getInfoKey() {
        return getComment();
    }

    @Override
    public String getComment() {
        return KronConfig.super.getComment();
    }

}
