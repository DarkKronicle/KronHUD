package io.github.darkkronicle.kronhud.gui.hud.item;

import io.github.darkkronicle.kronhud.config.KronConfig;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;
import io.github.darkkronicle.kronhud.gui.entry.TextHudEntry;
import io.github.darkkronicle.kronhud.util.DrawPosition;
import io.github.darkkronicle.kronhud.util.ItemUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.List;

public class ArmorHud extends TextHudEntry {

    public static final Identifier ID = new Identifier("kronhud", "armorhud");

    public ArmorHud() {
        super(20, 100, true);
    }

    @Override
    public void renderComponent(MatrixStack matrices, float delta) {
        DrawPosition pos = getPos();
        int lastY = 2 + (4 * 20);
        renderMainItem(matrices, client.player.getInventory().getMainHandStack(), pos.x() + 2, pos.y() + lastY);
        lastY = lastY - 20;
        for (int i = 0; i <= 3; i++) {
            ItemStack item = client.player.getInventory().armor.get(i);
            renderItem(matrices, item, pos.x() + 2, lastY + pos.y());
            lastY = lastY - 20;
        }
    }

    public void renderItem(MatrixStack matrices, ItemStack stack, int x, int y) {
        ItemUtil.renderGuiItemModel(getScale(), stack, x, y);
        ItemUtil.renderGuiItemOverlay(matrices, client.textRenderer, stack, x, y, null, textColor.getValue().color(), shadow.getValue());
    }

    public void renderMainItem(MatrixStack matrices, ItemStack stack, int x, int y) {
        ItemUtil.renderGuiItemModel(getScale(), stack, x, y);
        String total = String.valueOf(ItemUtil.getTotal(client, stack));
        if (total.equals("1")) {
            total = null;
        }
        ItemUtil.renderGuiItemOverlay(matrices, client.textRenderer, stack, x, y, total, textColor.getValue().color(), shadow.getValue());
    }

    @Override
    public void renderPlaceholderComponent(MatrixStack matrices, float delta) {
        DrawPosition pos = getPos();
        int lastY = 2 + (4 * 20);
        ItemUtil.renderGuiItemModel(getScale(), new ItemStack(Items.GRASS_BLOCK), pos.x() + 2, pos.y() + lastY);
        ItemUtil.renderGuiItemOverlay(matrices, client.textRenderer, new ItemStack(Items.GRASS_BLOCK), pos.x() + 2, pos.y() + lastY, "90",
                textColor.getValue().color(), shadow.getValue()
        );
    }

    @Override
    public boolean movable() {
        return true;
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public List<KronConfig<?>> getConfigurationOptions() {
        List<KronConfig<?>> options = super.getConfigurationOptions();
        return options;
    }

}
