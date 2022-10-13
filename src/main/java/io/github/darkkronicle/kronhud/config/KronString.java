package io.github.darkkronicle.kronhud.config;

import io.github.darkkronicle.darkkore.config.options.StringOption;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class KronString extends StringOption implements KronConfig<String> {

    private final String entryId;
    private final Consumer<String> callback;

    public KronString(String id, String entryId, String defaultValue) {
        this(id, entryId, defaultValue, null);
    }

    public KronString(String id, String entryId, String defaultValue, @Nullable Consumer<String> callback) {
        super(id, "", "", defaultValue);
        this.entryId = entryId;
        this.callback = callback;
    }

    @Override
    public void setValue(String value) {
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
