package io.github.darkkronicle.kronhud.gui.hud;

import io.github.darkkronicle.kronhud.KronHUD;
import io.github.darkkronicle.polish.api.EntryBuilder;
import io.github.darkkronicle.polish.gui.complexwidgets.EntryButtonList;
import io.github.darkkronicle.polish.gui.screens.BasicConfigScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class PingHud extends CleanHudEntry {
    public static final Identifier ID = new Identifier("kronhud", "pinghud");

    public PingHud() {
        // super(x, y, scale);
        super();
    }

    @Override
    public String getValue() {
        PlayerListEntry entry = client.player.networkHandler.getPlayerListEntry(client.player.getUuid());
        if (entry != null) {
            return entry.getLatency() + " ms";
        }
        return "-1 ms";
    }

    @Override
    public String getPlaceholder() {
        return "68 ms";
    }

    @Override
    public Identifier getID() {
        return ID;
    }

    @Override
    public Storage getS() {
        return KronHUD.storage.pingHudStorage;
    }

    @Override
    public Screen getConfigScreen() {
        EntryBuilder builder = EntryBuilder.create();
        EntryButtonList list = new EntryButtonList((client.getWindow().getScaledWidth() / 2) - 290, (client.getWindow().getScaledHeight() / 2) - 70, 580, 150, 1, false);
        list.addEntry(builder.startToggleEntry(new TranslatableText("option.kronhud.enabled"), getStorage().enabled).setDimensions(20, 10).setSavable(val -> getStorage().enabled = val).build(list));
        list.addEntry(builder.startFloatSliderEntry(new TranslatableText("option.kronhud.scale"), getStorage().scale, 0.2F, 1.5F).setWidth(80).setSavable(val -> getStorage().scale = val).build(list));
        list.addEntry(builder.startToggleEntry(new TranslatableText("option.kronhud.background"), getStorage().background).setSavable(val -> getStorage().background = val).build(list));
        list.addEntry(builder.startColorButtonEntry(new TranslatableText("option.kronhud.backgroundcolor"), getStorage().backgroundColor).setSavable(val -> getStorage().backgroundColor = val).build(list));
        list.addEntry(builder.startColorButtonEntry(new TranslatableText("option.kronhud.textcolor"), getStorage().textColor).setSavable(val -> getStorage().textColor = val).build(list));
        return new BasicConfigScreen(getName(), list, () -> KronHUD.storageHandler.saveDefaultHandling());

    }

    @Override
    public Text getName() {
        return new TranslatableText("hud.kronhud.pinghud");
    }
}
