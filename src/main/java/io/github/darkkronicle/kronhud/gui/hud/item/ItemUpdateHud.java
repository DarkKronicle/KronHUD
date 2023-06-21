package io.github.darkkronicle.kronhud.gui.hud.item;

import io.github.darkkronicle.kronhud.config.KronConfig;
import io.github.darkkronicle.kronhud.config.KronInteger;
import io.github.darkkronicle.kronhud.gui.entry.TextHudEntry;
import io.github.darkkronicle.kronhud.util.ColorUtil;
import io.github.darkkronicle.kronhud.util.DrawPosition;
import io.github.darkkronicle.kronhud.util.ItemUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.TextCollector;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemUpdateHud extends TextHudEntry {
    public static final Identifier ID = new Identifier("kronhud", "itemupdatehud");

    private List<ItemUtil.ItemStorage> oldItems = new ArrayList<>();
    private ArrayList<ItemUtil.TimedItemStorage> removed;
    private ArrayList<ItemUtil.TimedItemStorage> added;

    private KronInteger timeout = new KronInteger("timeout", ID.getPath(), 6, 1, 60);

    public ItemUpdateHud() {
        super(200, 11 * 6 - 2, true);
        removed = new ArrayList<>();
        added = new ArrayList<>();
    }

    public void update() {
        this.removed = ItemUtil.removeOld(removed, timeout.getValue() * 1000);
        this.added = ItemUtil.removeOld(added, timeout.getValue() * 1000);
        updateAdded();
        updateRemoved();
        oldItems = ItemUtil.storageFromItem(ItemUtil.getItems(client));
    }

    @Override
    public boolean tickable() {
        return true;
    }

    @Override
    public void tick() {
        if (client.world != null) {
            update();
        }
    }

    private void updateAdded() {
        List<ItemUtil.ItemStorage> added = ItemUtil.compare(ItemUtil.storageFromItem(ItemUtil.getItems(client)), oldItems);
        ArrayList<ItemUtil.TimedItemStorage> timedAdded = new ArrayList<>();
        for (ItemUtil.ItemStorage stack : added) {
            timedAdded.add(stack.timed());
        }
        for (ItemUtil.TimedItemStorage stack : timedAdded) {
            if (stack.stack.isEmpty()) {
                continue;
            }
            Optional<ItemUtil.TimedItemStorage> item = ItemUtil.getTimedItemFromItem(stack.stack, this.added);
            if (item.isPresent()) {
                item.get().incrementTimes(stack.times);
            } else {
                this.added.add(stack);
            }
        }
        this.added.sort((o1, o2) -> Float.compare(o1.getPassedTime(), o2.getPassedTime()));
    }

    private void updateRemoved() {
        List<ItemUtil.ItemStorage> removed = ItemUtil.compare(oldItems, ItemUtil.storageFromItem(ItemUtil.getItems(client)));
        List<ItemUtil.TimedItemStorage> timed = ItemUtil.untimedToTimed(removed);
        for (ItemUtil.TimedItemStorage stack : timed) {
            if (stack.stack.isEmpty()) {
                continue;
            }
            Optional<ItemUtil.TimedItemStorage> item = ItemUtil.getTimedItemFromItem(stack.stack, this.removed);
            if (item.isPresent()) {
                item.get().incrementTimes(stack.times);
            } else {
                this.removed.add(stack);
            }
        }
        this.removed.sort((o1, o2) -> Float.compare(o1.getPassedTime(), o2.getPassedTime()));
    }

    @Override
    public void renderComponent(DrawContext context, float delta) {
        DrawPosition pos = getPos();
        int lastY = 1;
        int i = 0;
        for (ItemUtil.ItemStorage item : this.added) {
            if (i > 5) {
                return;
            }
            TextCollector message = new TextCollector();
            message.add(Text.literal("+ "));
            message.add(Text.literal("[").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(ColorUtil.DARK_GRAY.color()))));
            message.add(Text.literal(item.times + "").setStyle(Style.EMPTY.withColor(Formatting.WHITE)));
            message.add(Text.literal("] ").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(ColorUtil.DARK_GRAY.color()))));
            message.add(item.stack.getName());
            OrderedText text = Language.getInstance().reorder(message.getCombined());
            if (shadow.getValue()) {
                context.drawTextWithShadow(
                        client.textRenderer, text, pos.x(), pos.y() + lastY, ColorUtil.SELECTOR_GREEN.color()
                );
            } else {
                context.drawTextWithShadow(
                        client.textRenderer, text, pos.x(), pos.y() + lastY, ColorUtil.SELECTOR_GREEN.color()
                );
            }
            lastY = lastY + client.textRenderer.fontHeight + 2;
            i++;
        }
        for (ItemUtil.ItemStorage item : this.removed) {
            if (i > 5) {
                return;
            }
            TextCollector message = new TextCollector();
            message.add(Text.literal("- "));
            message.add(Text.literal("[").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(ColorUtil.DARK_GRAY.color()))));
            message.add(Text.literal(item.times + "").setStyle(Style.EMPTY.withColor(Formatting.WHITE)));
            message.add(Text.literal("] ").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(ColorUtil.DARK_GRAY.color()))));
            message.add(item.stack.getName());
            OrderedText text = Language.getInstance().reorder(message.getCombined());
            if (shadow.getValue()) {
                context.drawTextWithShadow(
                        client.textRenderer, text, pos.x(), pos.y() + lastY, Formatting.RED.getColorValue()
                );
            } else {
                context.drawTextWithShadow(
                        client.textRenderer, text, pos.x(), pos.y() + lastY, Formatting.RED.getColorValue()
                );
            }
            lastY = lastY + client.textRenderer.fontHeight + 2;
            i++;
        }
    }

    @Override
    public void renderPlaceholderComponent(DrawContext context, float delta) {
        DrawPosition pos = getPos();
        TextCollector addM = new TextCollector();
        addM.add(Text.literal("+ "));
        addM.add(Text.literal("[").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(ColorUtil.DARK_GRAY.color()))));
        addM.add(Text.literal("2").setStyle(Style.EMPTY.withColor(Formatting.WHITE)));
        addM.add(Text.literal("] ").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(ColorUtil.DARK_GRAY.color()))));
        addM.add(new ItemStack(Items.DIRT).getName());
        OrderedText addText = Language.getInstance().reorder(addM.getCombined());
        if (shadow.getValue()) {
            context.drawTextWithShadow(
                    client.textRenderer, addText, pos.x(), pos.y(), Formatting.RED.getColorValue()
            );
        } else {
            context.drawTextWithShadow(
                    client.textRenderer, addText, pos.x(), pos.y() + client.textRenderer.fontHeight + 2, Formatting.RED.getColorValue()
            );
        }
        TextCollector removeM = new TextCollector();
        removeM.add(Text.literal("- "));
        removeM.add(Text.literal("[").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(ColorUtil.DARK_GRAY.color()))));
        removeM.add(Text.literal("4").setStyle(Style.EMPTY.withColor(Formatting.WHITE)));
        removeM.add(Text.literal("] ").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(ColorUtil.DARK_GRAY.color()))));
        removeM.add(new ItemStack(Items.GRASS).getName());
        OrderedText removeText = Language.getInstance().reorder(removeM.getCombined());
        if (shadow.getValue()) {
            context.drawTextWithShadow(
                    client.textRenderer, removeText, pos.x(), pos.y() + client.textRenderer.fontHeight + 2,
                    Formatting.RED.getColorValue()
            );
        } else {
            context.drawTextWithShadow(
                    client.textRenderer, removeText, pos.x(), pos.y() + client.textRenderer.fontHeight + 3,
                    Formatting.RED.getColorValue()
            );
        }
    }

    @Override
    public List<KronConfig<?>> getConfigurationOptions() {
        List<KronConfig<?>> options = super.getConfigurationOptions();
        options.add(shadow);
        options.add(timeout);
        return options;
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public boolean movable() {
        return true;
    }

}

