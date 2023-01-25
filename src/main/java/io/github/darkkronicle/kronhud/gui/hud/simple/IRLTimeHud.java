package io.github.darkkronicle.kronhud.gui.hud.simple;

import io.github.darkkronicle.kronhud.config.KronConfig;
import io.github.darkkronicle.kronhud.config.KronString;
import io.github.darkkronicle.kronhud.gui.entry.SimpleTextHudEntry;
import net.minecraft.util.Identifier;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class IRLTimeHud extends SimpleTextHudEntry {
    // https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html

    public static final Identifier ID = new Identifier("kronhud", "irltimehud");

    private DateTimeFormatter formatter = null;
    private boolean error = false;

    private final KronString format = new KronString("dateformat", ID.getPath(), "HH:mm:ss", this::updateDateTimeFormatter);

    @Override
    public Identifier getId() {
        return ID;
    }

    public void updateDateTimeFormatter(String value) {
        try {
            formatter = DateTimeFormatter.ofPattern(value);
            error = false;
        } catch (Exception e) {
            error = true;
            formatter = null;
        }
    }

    @Override
    public String getValue() {
        if (error) {
            return "Error Compiling!";
        }
        if (formatter == null) {
            updateDateTimeFormatter(format.getValue());
            return getValue();
        }
        return formatter.format(LocalDateTime.now());
    }

    @Override
    public String getPlaceholder() {
        if (error) {
            return "Error Compiling!";
        }
        if (formatter == null) {
            updateDateTimeFormatter(format.getValue());
            return getValue();
        }
        return formatter.format(LocalDateTime.of(2020, Month.AUGUST, 22, 14, 28, 32, 1595135));
    }

    @Override
    public List<KronConfig<?>> getConfigurationOptions() {
        List<KronConfig<?>> options = super.getConfigurationOptions();
        options.add(format);
        return options;
    }
}
