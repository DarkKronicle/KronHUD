package io.github.darkkronicle.kronhud.gui.hud.item;

import io.github.darkkronicle.kronhud.config.KronBoolean;
import io.github.darkkronicle.kronhud.config.KronConfig;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;
import io.github.darkkronicle.kronhud.gui.entry.BoxHudEntry;
import io.github.darkkronicle.kronhud.gui.entry.TextHudEntry;
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

public class ArrowHud extends TextHudEntry {

    public static final Identifier ID = new Identifier("kronhud", "arrowhud");
    private int arrows = 0;
    private KronBoolean dynamic = new KronBoolean("dynamic", ID.getPath(), false);
    private KronBoolean allArrowTypes = new KronBoolean("allArrowTypes", ID.getPath(), false);
    private ItemStack currentArrow = new ItemStack(Items.ARROW);

    public ArrowHud() {
        super(20, 30, true);
    }

    @Override
    public void render(MatrixStack matrices, float delta) {
        if (dynamic.getValue()) {
            ClientPlayerEntity player = client.player;
            if (!(
                    player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof RangedWeaponItem
                            || player.getStackInHand(Hand.OFF_HAND).getItem() instanceof RangedWeaponItem
            )) {
                return;
            }
        }
        super.render(matrices, delta);
    }

    @Override
    public void renderComponent(MatrixStack matrices, float delta) {
        DrawPosition pos = getPos();
        drawCenteredString(
                matrices, client.textRenderer, String.valueOf(arrows), pos.x() + getWidth() / 2, pos.y() + getHeight() - 10,
                textColor.getValue(), shadow.getValue()
        );
        ItemUtil.renderGuiItemModel(getScale(), currentArrow, pos.x() + 2, pos.y() + 2);
    }

    @Override
    public boolean tickable() {
        return true;
    }

    @Override
    public void tick() {
        if (allArrowTypes.getValue()) {
            arrows = ItemUtil.getTotal(client, new ItemStack(Items.ARROW)) + ItemUtil.getTotal(client, new ItemStack(Items.TIPPED_ARROW))
                    + ItemUtil.getTotal(client, new ItemStack(Items.SPECTRAL_ARROW));
        } else {
            arrows = ItemUtil.getTotal(client, currentArrow);
        }
        if (client.player == null) {
            return;
        }
        if (!allArrowTypes.getValue()) {
            currentArrow = client.player.getArrowType(Items.BOW.getDefaultStack());
        } else {
            currentArrow = new ItemStack(Items.ARROW);
        }
    }

    @Override
    public void renderPlaceholderComponent(MatrixStack matrices, float delta) {
        DrawPosition pos = getPos();
        drawCenteredString(
                matrices, client.textRenderer, "64", pos.x() + getWidth() / 2, pos.y() + getHeight() - 10, textColor.getValue(),
                shadow.getValue()
        );
        ItemUtil.renderGuiItemModel(getScale(), new ItemStack(Items.ARROW), pos.x() + 2, pos.y() + 2);
    }

    @Override
    public List<KronConfig<?>> getConfigurationOptions() {
        List<KronConfig<?>> options = super.getConfigurationOptions();
        options.add(dynamic);
        options.add(allArrowTypes);
        return options;
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
