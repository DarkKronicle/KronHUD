package io.github.darkkronicle.kronhud.gui.hud;

import io.github.darkkronicle.kronhud.KronHUD;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;
import io.github.darkkronicle.kronhud.util.ItemUtil;
import io.github.darkkronicle.polish.api.EntryBuilder;
import io.github.darkkronicle.polish.gui.complexwidgets.EntryButtonList;
import io.github.darkkronicle.polish.gui.screens.BasicConfigScreen;
import io.github.darkkronicle.polish.util.Colors;
import io.github.darkkronicle.polish.util.DrawPosition;
import io.github.darkkronicle.polish.util.DrawUtil;
import io.github.darkkronicle.polish.util.SimpleColor;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class ArmorHud extends AbstractHudEntry {
    public static final Identifier ID = new Identifier("kronhud", "armorhud");


    public ArmorHud() {
        super(20, 100);
    }

    @Override
    public void render(MatrixStack matrices) {
        matrices.push();
        matrices.scale(getStorage().scale, getStorage().scale, 1);
        DrawPosition pos = getScaledPos();
        if (getStorage().background) {
            DrawableHelper.fill(matrices, pos.getX(), pos.getY(), pos.getX() + width, pos.getY() + height, getStorage().backgroundColor.color());
        }
        int lastY = 2 + (4 * 20);
        renderMainItem(matrices, client.player.inventory.getMainHandStack(), pos.getX() + 2, pos.getY() + lastY);
        lastY = lastY - 20;
        for (int i = 0; i <= 3; i++) {
            ItemStack item = client.player.inventory.armor.get(i);
            renderItem(matrices, item, pos.getX() + 2, lastY + pos.getY());
            lastY = lastY - 20;
        }
        matrices.pop();
    }

    public void renderItem(MatrixStack matrices, ItemStack stack, int x, int y) {
        ItemUtil.renderGuiItemModel(matrices, stack, x, y);
        ItemUtil.renderGuiItemOverlay(matrices, client.textRenderer, stack, x, y, null);
    }

    public void renderMainItem(MatrixStack matrices, ItemStack stack, int x, int y) {
        ItemUtil.renderGuiItemModel(matrices, stack, x, y);
        ItemUtil.renderGuiItemOverlay(matrices, client.textRenderer, stack, x, y, String.valueOf(ItemUtil.getTotal(client, stack)));
    }

    @Override
    public void renderPlaceholder(MatrixStack matrices) {
        matrices.push();
        matrices.scale(getStorage().scale, getStorage().scale, 1);
        DrawPosition pos = getScaledPos();
        if (hovered) {
            DrawUtil.rect(matrices, pos.getX(), pos.getY(), width, height, Colors.SELECTOR_BLUE.color().withAlpha(100).color());
        } else {
            DrawUtil.rect(matrices, pos.getX(), pos.getY(), width, height, Colors.WHITE.color().withAlpha(50).color());
        }
        DrawUtil.outlineRect(matrices, pos.getX(), pos.getY(), width, height, Colors.BLACK.color().color());
        int lastY = 2 + (4 * 20);
        ItemUtil.renderGuiItemModel(matrices, new ItemStack(Items.GRASS_BLOCK), pos.getX() + 2, pos.getY() + lastY);
        ItemUtil.renderGuiItemOverlay(matrices, client.textRenderer, new ItemStack(Items.GRASS_BLOCK), pos.getX() + 2, pos.getY() + lastY, "90");
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
        return KronHUD.storage.armorHudStorage;
    }

    @Override
    public Screen getConfigScreen() {
        EntryBuilder builder = EntryBuilder.create();
        EntryButtonList list = BasicConfigScreen.createButtonList(1);
        list.addEntry(builder.startToggleEntry(new TranslatableText("option.kronhud.enabled"), getStorage().enabled).setDimensions(20, 10).setSavable(val -> getStorage().enabled = val).build(list));
        list.addEntry(builder.startFloatSliderEntry(new TranslatableText("option.kronhud.scale"), getStorage().scale, 0.2F, 1.5F).setWidth(80).setSavable(val -> getStorage().scale = val).build(list));
        list.addEntry(builder.startToggleEntry(new TranslatableText("option.kronhud.background"), getStorage().background).setSavable(val -> getStorage().background = val).build(list));
        list.addEntry(builder.startColorButtonEntry(new TranslatableText("option.kronhud.backgroundcolor"), getStorage().backgroundColor).setSavable(val -> getStorage().backgroundColor = val).build(list));
        return new BasicConfigScreen(getName(), list, () -> KronHUD.storageHandler.saveDefaultHandling());

    }

    @Override
    public Text getName() {
        return new TranslatableText("hud.kronhud.armorhud");
    }

    public static class Storage extends AbstractStorage {
        boolean background;
        SimpleColor backgroundColor;

        public Storage() {
            x = 1F;
            y = 1F;
            scale = 1F;
            enabled = true;
            background = true;
            backgroundColor = new SimpleColor(0, 0, 0, 100);
        }
    }
}
