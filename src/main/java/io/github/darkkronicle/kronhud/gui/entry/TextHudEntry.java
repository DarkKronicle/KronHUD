package io.github.darkkronicle.kronhud.gui.entry;

import io.github.darkkronicle.darkkore.colors.ExtendedColor;
import io.github.darkkronicle.kronhud.config.KronBoolean;
import io.github.darkkronicle.kronhud.config.KronColor;
import io.github.darkkronicle.kronhud.config.KronConfig;
import io.github.darkkronicle.kronhud.util.ColorUtil;

import java.util.List;

public abstract class TextHudEntry extends BoxHudEntry {

    protected KronColor textColor = new KronColor("textcolor", null, new ExtendedColor(ColorUtil.WHITE, ExtendedColor.ChromaOptions.getDefault()));
    protected KronBoolean shadow = new KronBoolean("shadow", null, getShadowDefault());

    protected boolean getShadowDefault() {
        return true;
    }

    public TextHudEntry(int width, int height, boolean backgroundAllowed) {
        super(width, height, backgroundAllowed);
    }

    @Override
    public List<KronConfig<?>> getConfigurationOptions() {
        List<KronConfig<?>> options = super.getConfigurationOptions();
        options.add(textColor);
        options.add(shadow);
        return options;
    }

}
