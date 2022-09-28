package io.github.darkkronicle.kronhud.config;

import io.github.darkkronicle.darkkore.config.options.ColorOption;
import io.github.darkkronicle.darkkore.util.Color;

public class KronColor extends ColorOption implements KronConfig {

    private final String entryId;

    public KronColor(String id, String entryId, Color defaultValue) {
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
