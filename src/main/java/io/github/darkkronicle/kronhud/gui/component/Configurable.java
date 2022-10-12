package io.github.darkkronicle.kronhud.gui.component;

import io.github.darkkronicle.darkkore.gui.Tab;
import io.github.darkkronicle.kronhud.config.KronConfig;

import java.util.List;

public interface Configurable {

    /**
     * Returns a list of options that will be configured
     *
     * @return List of configurable options
     */
    List<KronConfig<?>> getConfigurationOptions();

    /**
     * Returns a list of options that should be saved. By default, this includes {@link #getConfigurationOptions()}
     *
     * @return Options to save within a config
     */
    default List<KronConfig<?>> getSaveOptions() {
        return getConfigurationOptions();
    }

    Tab toTab();

}
