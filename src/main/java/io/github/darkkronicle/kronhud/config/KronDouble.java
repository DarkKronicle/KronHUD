package io.github.darkkronicle.kronhud.config;

import io.github.darkkronicle.darkkore.config.options.DoubleOption;
import io.github.darkkronicle.kronhud.gui.component.HudEntry;
import org.jetbrains.annotations.Nullable;

public class KronDouble extends DoubleOption implements KronConfig<Double> {

    private final String entryId;
    private final HudEntry refreshHud;

    public KronDouble(String id, String entryId, double defaultValue, double min, double max) {
        this(id, entryId, defaultValue, min, max, null);
    }

    public KronDouble(String id, String entryId, double defaultValue, double min, double max, @Nullable HudEntry toRefresh) {
        super(id, entryId, "", defaultValue, min, max);
        this.entryId = entryId;
        this.refreshHud = toRefresh;
    }

    @Override
    public void setValue(Double value) {
        super.setValue(value);
        // Scale has changed!
        if (refreshHud != null) {
            refreshHud.onBoundsUpdate();
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
