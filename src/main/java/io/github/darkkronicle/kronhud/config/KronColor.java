package io.github.darkkronicle.kronhud.config;

import io.github.darkkronicle.darkkore.config.options.IntegerOption;
import io.github.darkkronicle.kronhud.util.Color;

public class KronColor extends IntegerOption implements KronConfig {

    private final String entryId;
    private Color color;

    public KronColor(String id, String entryId, String defaultValue) {
        super(id, entryId, "", Color.parse(defaultValue).color());
        this.color = new Color(this.defaultValue);
        this.entryId = entryId;
    }

    public Color getColor() {
        return color;
    }

    public String getDefaultStringValue() {
        return new Color(defaultValue).toString();
    }

    public void setValueFromString(String value) {
        this.value = (color = Color.parse(value)).color();
    }

    @Override
    public void setValue(Integer value) {
        this.color = new Color(value);
        super.setValue(value);
    }

    public boolean isModified(String newValue) {
        return Color.parse(newValue).color() != defaultValue;
    }

    @Override
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
    public String getComment() {
        return KronConfig.super.getComment();
    }

}
