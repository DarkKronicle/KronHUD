package io.github.darkkronicle.kronhud.gui.component;

import io.github.darkkronicle.darkkore.util.StringUtil;
import net.minecraft.util.Identifier;

/**
 * An interface that represents an object that contains an Identifier, as well as ways to translate itself into a
 */
public interface Identifiable {

    /**
     * Returns a unique identifier for this object
     *
     * @return The identifier
     */
    Identifier getId();

    /**
     * Gets the display name key
     *
     * @return The display name key
     */
    default String getNameKey() {
        return "hud." + getId().getNamespace() + "." + getId().getPath();
    }

    /**
     * Gets the info key to render on hover
     *
     * @return Info key
     */
    default String getInfoKey() {
        return "hud." + getId().getNamespace() + "." + getId().getPath() + ".info";
    }

    /**
     * The translated name of the object
     *
     * @return String containing the name
     */
    default String getName() {
        return StringUtil.translate(getNameKey());
    }

}
