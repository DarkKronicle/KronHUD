package io.github.darkkronicle.kronhud.config;

import io.github.darkkronicle.darkkore.config.options.DoubleOption;

public class KronDouble extends DoubleOption implements KronConfig<Double> {

    private final String entryId;

    public KronDouble(String id, String entryId, double defaultValue, double min, double max) {
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
