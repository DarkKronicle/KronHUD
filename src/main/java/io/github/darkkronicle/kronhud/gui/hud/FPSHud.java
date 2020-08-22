package io.github.darkkronicle.kronhud.gui.hud;

import io.github.darkkronicle.kronhud.KronHUD;
import io.github.darkkronicle.polish.api.EntryBuilder;
import io.github.darkkronicle.polish.gui.complexwidgets.EntryButtonList;
import io.github.darkkronicle.polish.gui.screens.BasicConfigScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

public class FPSHud extends CleanHudEntry {
    public static final Identifier ID = new Identifier("kronhud", "fpshud");

    public FPSHud() {
        //super(x, y, scale);
        super();
    }

    @Override
    public Identifier getID() {
        return ID;
    }

    @Override
    public String getValue() {
        return KronHUD.fps + " FPS";
    }

    @Override
    public String getPlaceholder() {
        return "60 FPS";
    }

    @Override
    public Storage getS() {
        return KronHUD.storage.fpsHudStorage;
    }

    @Override
    public Screen getConfigScreen() {
        EntryBuilder builder = EntryBuilder.create();
        EntryButtonList list = new EntryButtonList((client.getWindow().getScaledWidth() / 2) - 290, (client.getWindow().getScaledHeight() / 2) - 70, 580, 150, 1, false);
        list.addEntry(builder.startToggleEntry(new LiteralText("Enabled"), getStorage().enabled).setDimensions(20, 10).setSavable(val -> getStorage().enabled = val).build(list));
        list.addEntry(builder.startFloatSliderEntry(new LiteralText("Scale"), getStorage().scale, 0.2F, 1.5F).setWidth(80).setSavable(val -> getStorage().scale = val).build(list));
        list.addEntry(builder.startColorButtonEntry(new LiteralText("Background Color"), getStorage().backgroundColor).setSavable(val -> getStorage().backgroundColor = val).build(list));
        list.addEntry(builder.startColorButtonEntry(new LiteralText("Text Color"), getStorage().textColor).setSavable(val -> getStorage().textColor = val).build(list));
        return new BasicConfigScreen(new LiteralText("FPSHud"), list) {
            @Override
            public void onClose() {
                super.onClose();
                KronHUD.storageHandler.saveDefaultHandling();
            }
        };
    }

    @Override
    public String getName() {
        return "FPSHUD";
    }

}
