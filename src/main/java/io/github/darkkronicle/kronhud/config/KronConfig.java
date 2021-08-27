package io.github.darkkronicle.kronhud.config;

import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;
import net.minecraft.util.Identifier;

public interface KronConfig {

    String getEntryId();

    String getId();

    default String getTranslationKeyBase() {
        if(getEntryId() == null) {
            return "option.kronhud." + getId();
        }
        else {
            return "option.kronhud." + getEntryId() + "." + getId();
        }
    }

    default String getName() {
        return StringUtils.translate(getTranslationKeyBase());
    }

    default String getComment() {
        return StringUtils.translate(getTranslationKeyBase() + ".comment");
    }


}
