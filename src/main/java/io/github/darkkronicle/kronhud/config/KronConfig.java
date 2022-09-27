package io.github.darkkronicle.kronhud.config;

import net.minecraft.client.resource.language.I18n;

public interface KronConfig {

    String getEntryId();

    String getId();

    default String getTranslationKeyBase() {
        if (getEntryId() == null) {
            return "option.kronhud." + getId();
        } else {
            return "option.kronhud." + getEntryId() + "." + getId();
        }
    }

    default String getName() {
        return I18n.translate(getTranslationKeyBase());
    }

    default String getComment() {
        return I18n.translate(getTranslationKeyBase() + ".comment");
    }


}
