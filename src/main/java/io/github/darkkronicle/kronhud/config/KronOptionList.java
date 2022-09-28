package io.github.darkkronicle.kronhud.config;

import io.github.darkkronicle.darkkore.config.options.ListOption;
import io.github.darkkronicle.darkkore.config.options.Option;
import io.github.darkkronicle.darkkore.config.options.OptionListEntry;

public class KronOptionList<T extends OptionListEntry<T>> extends ListOption<T> implements KronConfig<T> {

    private final String entryId;

    public KronOptionList(String id, String entryId, T defaultValue) {
        super(id, entryId, "", defaultValue);
        this.entryId = entryId;
    }

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
