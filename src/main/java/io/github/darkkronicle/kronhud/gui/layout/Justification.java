package io.github.darkkronicle.kronhud.gui.layout;

import io.github.darkkronicle.darkkore.config.options.OptionListEntry;
import lombok.AllArgsConstructor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.List;

@AllArgsConstructor
public enum Justification implements OptionListEntry<Justification> {
    LEFT("left"),
    CENTER("center"),
    RIGHT("right")
    ;

    private final String key;

    @Override
    public List<Justification> getAll() {
        return List.of(values());
    }

    @Override
    public String getSaveKey() {
        return key;
    }

    @Override
    public String getDisplayKey() {
        return "kronhud.option.justification." + key;
    }

    @Override
    public String getInfoKey() {
        return "kronhud.option.justification." + key + ".info";
    }


    public int getXOffset(Text text, int width) {
        if (this == LEFT) {
            return 0;
        }
        return getXOffset(MinecraftClient.getInstance().textRenderer.getWidth(text), width);
    }

    public int getXOffset(String text, int width) {
        if (this == LEFT) {
            return 0;
        }
        return getXOffset(MinecraftClient.getInstance().textRenderer.getWidth(text), width);
    }

    public int getXOffset(int textWidth, int width) {
        if (this == LEFT) {
            return 0;
        }
        if (this == RIGHT) {
            return width - textWidth;
        }
        return (width - textWidth) / 2;
    }
}
