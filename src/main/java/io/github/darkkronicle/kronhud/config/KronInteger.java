package io.github.darkkronicle.kronhud.config;

import io.github.darkkronicle.darkkore.config.options.IntegerOption;

import java.util.function.Consumer;

public class KronInteger extends IntegerOption implements KronConfig<Integer> {

    private final String entryId;
    private final Consumer<Integer> callback;

    public KronInteger(String id, String entryId, int defaultValue, int min, int max) {
        this(id, entryId, defaultValue, min, max, null);
    }

    public KronInteger(String id, String entryId, int defaultValue, int min, int max, Consumer<Integer> callback) {
        super(id, entryId, "", defaultValue, min, max);
        this.entryId = entryId;
        this.callback = null;
    }

    @Override
    public void setValue(Integer value) {
        super.setValue(value);
        if (callback != null) {
            callback.accept(value);
        }
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
