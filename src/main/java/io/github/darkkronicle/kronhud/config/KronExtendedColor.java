package io.github.darkkronicle.kronhud.config;

import io.github.darkkronicle.darkkore.colors.ExtendedColor;
import io.github.darkkronicle.darkkore.config.options.ExtendedColorOption;

public class KronExtendedColor extends ExtendedColorOption implements KronConfig<ExtendedColor> {

    private final String entryId;

    public KronExtendedColor(String id, String entryId, ExtendedColor defaultValue) {
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
