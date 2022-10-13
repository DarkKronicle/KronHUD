package io.github.darkkronicle.kronhud.gui.layout;

import io.github.darkkronicle.darkkore.config.options.OptionListEntry;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
public enum CardinalOrder implements OptionListEntry<CardinalOrder> {
    TOP_DOWN("topdown", false, -1),
    DOWN_TOP("downtop", false, 1),
    LEFT_RIGHT("leftright", true, 1),
    RIGHT_LEFT("rightleft", true, -1),
    ;

    private final String key;
    @Getter
    private final boolean xAxis;
    @Getter
    private final int direction;

    @Override
    public List<CardinalOrder> getAll() {
        return List.of(values());
    }

    @Override
    public String getSaveKey() {
        return key;
    }

    @Override
    public String getDisplayKey() {
        return "kronhud.option.anchorpoint.cardinalorder." + key;
    }

    @Override
    public String getInfoKey() {
        return "kronhud.option.anchorpoint.cardinalorder." + key + ".info";
    }
}
