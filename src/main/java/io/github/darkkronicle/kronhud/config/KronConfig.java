package io.github.darkkronicle.kronhud.config;

import net.minecraft.client.resource.language.I18n;

public interface KronConfig {

    static String getTranslationBase(String id, String entry) {
        if (entry == null) {
            return "option.kronhud." + id;
        } else {
            return "option.kronhud." + entry + "." + id;
        }
    }

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
        return getTranslationKeyBase();
    }

    default String getComment() {
        return getTranslationKeyBase() + ".comment";
    }


}
