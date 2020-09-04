package io.github.darkkronicle.kronhud.gui.hud;

import io.github.darkkronicle.kronhud.KronHUD;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;
import io.github.darkkronicle.kronhud.util.ItemUtil;
import io.github.darkkronicle.polish.api.EntryBuilder;
import io.github.darkkronicle.polish.gui.complexwidgets.EntryButtonList;
import io.github.darkkronicle.polish.gui.screens.BasicConfigScreen;
import io.github.darkkronicle.polish.util.Colors;
import io.github.darkkronicle.polish.util.DrawPosition;
import io.github.darkkronicle.polish.util.SimpleColor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class ArrowHud extends AbstractHudEntry {
    public static final Identifier ID = new Identifier("kronhud", "arrowhud");
    private int arrows = 0;


    public ArrowHud() {
        super(20, 30);
    }

    @Override
    public void render(MatrixStack matrices) {
        matrices.push();
        matrices.scale(getStorage().scale, getStorage().scale, 1);
        DrawPosition pos = getScaledPos();
        rect(matrices, pos.getX(), pos.getY(), width, height, getStorage().backgroundColor.color());
        drawCenteredString(matrices, client.textRenderer, String.valueOf(arrows), pos.getX() + width / 2, pos.getY() + height - 10, getStorage().textColor.color());
        ItemUtil.renderGuiItemModel(matrices, new ItemStack(Items.ARROW), pos.getX() + 2, pos.getY() + 2);
        matrices.pop();
    }

    @Override
    public boolean tickable() {
        return true;
    }

    @Override
    public void tick() {
        arrows = ItemUtil.getTotal(client, new ItemStack(Items.ARROW));
    }

    @Override
    public void renderPlaceholder(MatrixStack matrices) {
        matrices.push();
        matrices.scale(getStorage().scale, getStorage().scale, 1);
        DrawPosition pos = getScaledPos();
        if (hovered) {
            rect(matrices, pos.getX(), pos.getY(), width, height, Colors.WHITE.color().withAlpha(150).color());
        } else {
            rect(matrices, pos.getX(), pos.getY(), width, height, Colors.WHITE.color().withAlpha(50).color());
        }
        outlineRect(matrices, pos.getX(), pos.getY(), width, height, Colors.BLACK.color().color());
        drawCenteredString(matrices, client.textRenderer, "64", pos.getX() + width / 2, pos.getY() + height - 10, getStorage().textColor.color());
        ItemUtil.renderGuiItemModel(matrices, new ItemStack(Items.ARROW), pos.getX() + 2, pos.getY() + 2);
        hovered = false;
        matrices.pop();
    }

    @Override
    public boolean moveable() {
        return true;
    }

    @Override
    public Identifier getID() {
        return ID;
    }

    @Override
    public Storage getStorage() {
        return KronHUD.storage.arrowHudStorage;
    }

    @Override
    public Screen getConfigScreen() {
        EntryBuilder builder = EntryBuilder.create();
        EntryButtonList list = new EntryButtonList((client.getWindow().getScaledWidth() / 2) - 290, (client.getWindow().getScaledHeight() / 2) - 70, 580, 150, 1, false);
        list.addEntry(builder.startToggleEntry(new TranslatableText("option.kronhud.enabled"), getStorage().enabled).setDimensions(20, 10).setSavable(val -> getStorage().enabled = val).build(list));
        list.addEntry(builder.startFloatSliderEntry(new TranslatableText("option.kronhud.scale"), getStorage().scale, 0.2F, 1.5F).setWidth(80).setSavable(val -> getStorage().scale = val).build(list));
        list.addEntry(builder.startColorButtonEntry(new TranslatableText("option.kronhud.backgroundcolor"), getStorage().backgroundColor).setSavable(val -> getStorage().backgroundColor = val).build(list));
        list.addEntry(builder.startColorButtonEntry(new TranslatableText("option.kronhud.textcolor"), getStorage().textColor).setSavable(val -> getStorage().textColor = val).build(list));

        return new BasicConfigScreen(getName(), list, () -> KronHUD.storageHandler.saveDefaultHandling());

    }

    @Override
    public Text getName() {
        return new TranslatableText("hud.kronhud.arrowhud");
    }

    public static class Storage extends AbstractStorage {
        SimpleColor backgroundColor;
        SimpleColor textColor;

        public Storage() {
            x = 0.5F;
            y = 0F;
            scale = 1F;
            enabled = true;
            backgroundColor = new SimpleColor(0, 0, 0, 100);
            textColor = new SimpleColor(255, 255, 255, 255);
        }
    }
}
