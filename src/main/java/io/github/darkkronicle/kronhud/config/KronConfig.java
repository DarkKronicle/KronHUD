package io.github.darkkronicle.kronhud.config;

import io.github.darkkronicle.darkkore.config.options.Option;
import net.minecraft.client.resource.language.I18n;

public interface KronConfig<T> extends Option<T> {

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
