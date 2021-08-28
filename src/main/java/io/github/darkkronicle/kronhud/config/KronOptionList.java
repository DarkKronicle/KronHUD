package io.github.darkkronicle.kronhud.config;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.config.options.ConfigDouble;
import fi.dy.masa.malilib.config.options.ConfigOptionList;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;

public class KronOptionList extends ConfigOptionList implements KronConfig {

    private String entryId;

    public KronOptionList(String id, String entryId, IConfigOptionListEntry defaultValue) {
        super(id, defaultValue, null);
        this.entryId = entryId;
    }

    public String getEntryId() {
        return entryId;
    }

    @Override
    public String getId() {
        return super.getName();
    }

    @Override
    public String getName() {
        return KronConfig.super.getName();
    }

    @Override
    public String getComment() {
        return KronConfig.super.getComment();
    }

}
