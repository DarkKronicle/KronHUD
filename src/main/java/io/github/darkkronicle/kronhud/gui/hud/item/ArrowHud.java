package io.github.darkkronicle.kronhud.gui.hud.item;

import io.github.darkkronicle.darkkore.util.render.RenderUtil;
import io.github.darkkronicle.kronhud.config.KronBoolean;
import io.github.darkkronicle.kronhud.config.KronConfig;
import io.github.darkkronicle.kronhud.gui.entry.TextHudEntry;
import io.github.darkkronicle.kronhud.util.DrawPosition;
import io.github.darkkronicle.kronhud.util.ItemUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.text.Text;
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
    public void render(DrawContext context, float delta) {
        if (dynamic.getValue()) {
            ClientPlayerEntity player = client.player;
            if (!(
                    player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof RangedWeaponItem
                            || player.getStackInHand(Hand.OFF_HAND).getItem() instanceof RangedWeaponItem
            )) {
                return;
            }
        }
        super.render(context, delta);
    }

    @Override
    public void renderComponent(DrawContext context, float delta) {
        DrawPosition pos = getPos();
        drawCenteredString(
                context, client.textRenderer, Text.literal(Integer.toString(arrows)), pos.x() + getWidth() / 2, pos.y() + getHeight() - 10,
                textColor.getValue(), shadow.getValue()
        );
        RenderUtil.drawItem(context, currentArrow, pos.x() + 2, pos.y() + 2);
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
            currentArrow = client.player.getProjectileType(Items.BOW.getDefaultStack());
        } else {
            currentArrow = new ItemStack(Items.ARROW);
        }
    }

    @Override
    public void renderPlaceholderComponent(DrawContext context, float delta) {
        DrawPosition pos = getPos();
        drawCenteredString(
                context, client.textRenderer, Text.literal("64"), pos.x() + getWidth() / 2, pos.y() + getHeight() - 10, textColor.getValue(),
                shadow.getValue()
        );
        RenderUtil.drawItem(context, new ItemStack(Items.ARROW), pos.x() + 2, pos.y() + 2);
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
