package io.github.darkkronicle.kronhud.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.ConfigColor;
import fi.dy.masa.malilib.config.options.ConfigInteger;
import fi.dy.masa.malilib.config.options.ConfigTypeWrapper;
import fi.dy.masa.malilib.util.Color4f;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.kronhud.util.Color;


public class KronColor extends ConfigInteger implements KronConfig {

    private String entryId;
    private Color color;

    public KronColor(String id, String entryId, String defaultValue) {
        super(id, Color.parse(defaultValue).color(), null);
        this.color = new Color(this.defaultValue);
        this.entryId = entryId;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String getStringValue() {
        return color.toString();
    }

    @Override
    public String getDefaultStringValue() {
        return new Color(defaultValue).toString();
    }

    @Override
    public void setValueFromString(String value) {
        this.value = (color = Color.parse(value)).color();
    }

    @Override
    public void setIntegerValue(int value) {
        this.color = new Color(value);
        super.setIntegerValue(value);
    }

    @Override
    public boolean isModified(String newValue) {
        return Color.parse(newValue).color() != defaultValue;
    }

    @Override
    public void setValueFromJsonElement(JsonElement element)
    {
        try {
            if (element.isJsonPrimitive()) {
                value = this.getClampedValue((color = Color.parse(element.getAsString())).color());
            } else {
                MaLiLib.logger.warn("Failed to set config value for '{}' from the JSON element '{}'", this.getName(), element);
            }
        } catch (Exception e) {
            MaLiLib.logger.warn("Failed to set config value for '{}' from the JSON element '{}'", this.getName(), element, e);
        }
    }

    @Override
    public JsonElement getAsJsonElement() {
        return new JsonPrimitive(getStringValue());
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

    public class Wrapper extends ConfigTypeWrapper {

        public Wrapper(IConfigBase wrappedConfig) {
            super(null, wrappedConfig);
        }


    }

}
