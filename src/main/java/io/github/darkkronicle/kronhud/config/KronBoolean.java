package io.github.darkkronicle.kronhud.config;


import io.github.darkkronicle.darkkore.config.options.BooleanOption;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class KronBoolean extends BooleanOption implements KronConfig<Boolean> {

    private final String entryId;
    private final Consumer<Boolean> callback;

    public KronBoolean(String id, String entryId, boolean defaultValue) {
        this(id, entryId, defaultValue, null);
    }

    public KronBoolean(String id, String entryId, boolean defaultValue, @Nullable  Consumer<Boolean> callback) {
        super(id, entryId, "", defaultValue);
        this.entryId = entryId;
        this.callback = callback;
    }

    @Override
    public void setValue(Boolean value) {
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
