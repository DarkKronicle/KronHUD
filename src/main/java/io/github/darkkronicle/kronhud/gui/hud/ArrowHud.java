package io.github.darkkronicle.kronhud.gui.hud;

import fi.dy.masa.malilib.config.IConfigBase;
import io.github.darkkronicle.kronhud.config.KronBoolean;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;
import io.github.darkkronicle.kronhud.util.DrawPosition;
import io.github.darkkronicle.kronhud.util.ItemUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

import java.util.List;

public class ArrowHud extends AbstractHudEntry {
    public static final Identifier ID = new Identifier("kronhud", "arrowhud");
    private int arrows = 0;
    private KronBoolean dynamic = new KronBoolean("dynamic", ID.getPath(), false);

    public ArrowHud() {
        super(20, 30);
    }

    @Override
    public void render(MatrixStack matrices) {
        if (dynamic.getBooleanValue()) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (!(player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof RangedWeaponItem
                    || player.getStackInHand(Hand.OFF_HAND).getItem() instanceof RangedWeaponItem)) {
                return;
            }
        }
        matrices.push();
        scale(matrices);
        DrawPosition pos = getPos();
        if (background.getBooleanValue()) {
            fillRect(matrices, getBounds(), backgroundColor.getColor());
        }
        drawCenteredString(matrices, client.textRenderer, String.valueOf(arrows), new DrawPosition(pos.x() + width / 2,
                pos.y() + height - 10), textColor.getColor(), shadow.getBooleanValue());
        ItemUtil.renderGuiItemModel(matrices, new ItemStack(Items.ARROW), pos.x() + 2, pos.y() + 2);
        matrices.pop();
    }

    @Override
    public boolean tickable() {
        return true;
    }

    @Override
    public void tick() {
        arrows = ItemUtil.getTotal(client, new ItemStack(Items.ARROW)) + ItemUtil.getTotal(client, new ItemStack(Items.TIPPED_ARROW));
    }

    @Override
    public void renderPlaceholder(MatrixStack matrices) {
        matrices.push();
        renderPlaceholderBackground(matrices);
        scale(matrices);
        DrawPosition pos = getPos();
        drawCenteredString(matrices, client.textRenderer, "64", new DrawPosition(pos.x() + width / 2,
                pos.y() + height - 10), textColor.getColor(), shadow.getBooleanValue());
        ItemUtil.renderGuiItemModel(matrices, new ItemStack(Items.ARROW), pos.x() + 2, pos.y() + 2);
        hovered = false;
        matrices.pop();
    }

    @Override
    public void addConfigOptions(List<IConfigBase> options) {
        super.addConfigOptions(options);
        options.add(textColor);
        options.add(shadow);
        options.add(background);
        options.add(backgroundColor);
        options.add(dynamic);
    }

    @Override
    public boolean movable() {
        return true;
    }

    @Override
    public Identifier getId() {
        return ID;
    }

}
