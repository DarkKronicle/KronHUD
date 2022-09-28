package io.github.darkkronicle.kronhud.gui.hud;

import io.github.darkkronicle.kronhud.config.KronBoolean;
import io.github.darkkronicle.kronhud.config.KronConfig;
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
    private KronBoolean allArrowTypes = new KronBoolean("allArrowTypes", ID.getPath(), false);
    private ItemStack currentArrow = new ItemStack(Items.ARROW);

    public ArrowHud() {
        super(20, 30);
    }

    @Override
    public void render(MatrixStack matrices) {
        if (dynamic.getValue()) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (!(
                    player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof RangedWeaponItem
                            || player.getStackInHand(Hand.OFF_HAND).getItem() instanceof RangedWeaponItem
            )) {
                return;
            }
        }
        matrices.push();
        scale(matrices);

        DrawPosition pos = getPos();
        if (background.getValue()) {
            fillRect(matrices, getBounds(), backgroundColor.getValue());
        }
        drawCenteredString(
                matrices, client.textRenderer, String.valueOf(arrows), new DrawPosition(pos.x() + width / 2, pos.y() + height - 10),
                textColor.getValue(), shadow.getValue()
        );
        ItemUtil.renderGuiItemModel(matrices, currentArrow, pos.x() + 2, pos.y() + 2);
        matrices.pop();
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
    public void renderPlaceholder(MatrixStack matrices) {
        matrices.push();
        renderPlaceholderBackground(matrices);
        scale(matrices);
        DrawPosition pos = getPos();
        drawCenteredString(
                matrices, client.textRenderer, "64", new DrawPosition(pos.x() + width / 2, pos.y() + height - 10), textColor.getValue(),
                shadow.getValue()
        );
        ItemUtil.renderGuiItemModel(matrices, new ItemStack(Items.ARROW), pos.x() + 2, pos.y() + 2);
        hovered = false;
        matrices.pop();
    }

    @Override
    public List<KronConfig<?>> getOptions() {
        List<KronConfig<?>> options = super.getOptions();
        options.add(textColor);
        options.add(shadow);
        options.add(background);
        options.add(backgroundColor);
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
