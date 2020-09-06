package io.github.darkkronicle.kronhud.gui.hud;

import io.github.darkkronicle.kronhud.KronHUD;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;
import io.github.darkkronicle.kronhud.util.ItemUtil;
import io.github.darkkronicle.polish.api.EntryBuilder;
import io.github.darkkronicle.polish.api.ScissorsHelper;
import io.github.darkkronicle.polish.gui.complexwidgets.EntryButtonList;
import io.github.darkkronicle.polish.gui.screens.BasicConfigScreen;
import io.github.darkkronicle.polish.util.Colors;
import io.github.darkkronicle.polish.util.DrawPosition;
import io.github.darkkronicle.polish.util.DrawUtil;
import io.github.darkkronicle.polish.util.SimpleRectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.TextCollector;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static io.github.darkkronicle.kronhud.util.ItemUtil.compare;
import static io.github.darkkronicle.kronhud.util.ItemUtil.getItems;
import static io.github.darkkronicle.kronhud.util.ItemUtil.getTimedItemFromItem;
import static io.github.darkkronicle.kronhud.util.ItemUtil.removeOld;
import static io.github.darkkronicle.kronhud.util.ItemUtil.storageFromItem;

public class ItemUpdateHud extends AbstractHudEntry {
    public static final Identifier ID = new Identifier("kronhud", "itemupdatehud");


    private List<ItemUtil.ItemStorage> oldItems = new ArrayList<>();
    private ArrayList<ItemUtil.TimedItemStorage> removed;
    private ArrayList<ItemUtil.TimedItemStorage> added;

    public ItemUpdateHud() {
        super(200, 80);
        this.client = MinecraftClient.getInstance();
        removed = new ArrayList<>();
        added = new ArrayList<>();
    }

    public void update() {
        this.removed = removeOld(removed, getStorage().timeout);
        this.added = removeOld(added, getStorage().timeout);
        updateAdded();
        updateRemoved();
        oldItems = storageFromItem(getItems(client));
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
        List<ItemUtil.ItemStorage> added = compare(storageFromItem(getItems(client)), oldItems);
        ArrayList<ItemUtil.TimedItemStorage> timedAdded = new ArrayList<>();
        for (ItemUtil.ItemStorage stack : added) {
            timedAdded.add(stack.timed());
        }
        for (ItemUtil.TimedItemStorage stack : timedAdded) {
            if (stack.stack.isEmpty()) {
                continue;
            }
            Optional<ItemUtil.TimedItemStorage> item = getTimedItemFromItem(stack.stack, this.added);
            if (item.isPresent()) {
                item.get().incrementTimes(stack.times);
            } else {
                this.added.add(stack);
            }
        }
        this.added.sort((o1, o2) -> Float.compare(o1.getPassedTime(), o2.getPassedTime()));
    }

    private void updateRemoved() {
        List<ItemUtil.ItemStorage> removed = compare(oldItems, storageFromItem(getItems(client)));
        List<ItemUtil.TimedItemStorage> timed = ItemUtil.untimedToTimed(removed);
        for (ItemUtil.TimedItemStorage stack : timed) {
            if (stack.stack.isEmpty()) {
                continue;
            }
            Optional<ItemUtil.TimedItemStorage> item = getTimedItemFromItem(stack.stack, this.removed);
            if (item.isPresent()) {
                item.get().incrementTimes(stack.times);
            } else {
                this.removed.add(stack);
            }
        }
        this.removed.sort((o1, o2) -> Float.compare(o1.getPassedTime(), o2.getPassedTime()));
    }

    @Override
    public void render(MatrixStack matrices) {
        matrices.push();
        matrices.scale(getStorage().scale, getStorage().scale, 1);
        DrawPosition pos = getScaledPos();
        ScissorsHelper.INSTANCE.addScissor(new SimpleRectangle(getX(), getY(), width, height));
        int lastY = 1;
        for (ItemUtil.ItemStorage item : this.added) {
            if (lastY > height) {
                break;
            }
            TextCollector message = new TextCollector();
            message.add(new LiteralText("+ "));
            message.add(new LiteralText("[").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(Colors.DARKGRAY.color().color()))));
            message.add(new LiteralText(item.times + "").setStyle(Style.EMPTY.withColor(Formatting.WHITE)));
            message.add(new LiteralText("] ").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(Colors.DARKGRAY.color().color()))));
            message.add(item.stack.getName());
            OrderedText text = Language.getInstance().reorder(message.getCombined());
            client.textRenderer.drawWithShadow(matrices, text, pos.getX(), pos.getY() + lastY, Colors.SELECTOR_GREEN.color().color());
            lastY = lastY + client.textRenderer.fontHeight + 2;
        }
        for (ItemUtil.ItemStorage item : this.removed) {
            if (lastY > height) {
                break;
            }
            TextCollector message = new TextCollector();
            message.add(new LiteralText("- "));
            message.add(new LiteralText("[").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(Colors.DARKGRAY.color().color()))));
            message.add(new LiteralText(item.times + "").setStyle(Style.EMPTY.withColor(Formatting.WHITE)));
            message.add(new LiteralText("] ").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(Colors.DARKGRAY.color().color()))));
            message.add(item.stack.getName());
            OrderedText text = Language.getInstance().reorder(message.getCombined());
            client.textRenderer.drawWithShadow(matrices, text, pos.getX(), pos.getY() + lastY, Formatting.RED.getColorValue());
            lastY = lastY + client.textRenderer.fontHeight + 2;
        }
        ScissorsHelper.INSTANCE.removeLastScissor();
        matrices.pop();
    }

    @Override
    public void renderPlaceholder(MatrixStack matrices) {
        matrices.push();
        matrices.scale(getStorage().scale, getStorage().scale, 1);
        DrawPosition pos = getScaledPos();
        if (hovered) {
            DrawUtil.rect(matrices, pos.getX(), pos.getY(), width, height, Colors.SELECTOR_BLUE.color().withAlpha(100).color());
        } else {
            rect(matrices, pos.getX(), pos.getY(), width, height, Colors.WHITE.color().withAlpha(50).color());
        }
        outlineRect(matrices, pos.getX(), pos.getY(), width, height, Colors.BLACK.color().color());
        TextCollector addM = new TextCollector();
        addM.add(new LiteralText("+ "));
        addM.add(new LiteralText("[").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(Colors.DARKGRAY.color().color()))));
        addM.add(new LiteralText("2").setStyle(Style.EMPTY.withColor(Formatting.WHITE)));
        addM.add(new LiteralText("] ").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(Colors.DARKGRAY.color().color()))));
        addM.add(new ItemStack(Items.DIRT).getName());
        OrderedText text = Language.getInstance().reorder(addM.getCombined());
        client.textRenderer.drawWithShadow(matrices, text, pos.getX(), pos.getY() + 1, Colors.SELECTOR_GREEN.color().color());
        TextCollector message = new TextCollector();
        message.add(new LiteralText("- "));
        message.add(new LiteralText("[").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(Colors.DARKGRAY.color().color()))));
        message.add(new LiteralText("4").setStyle(Style.EMPTY.withColor(Formatting.WHITE)));
        message.add(new LiteralText("] ").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(Colors.DARKGRAY.color().color()))));
        message.add(new ItemStack(Items.GRASS).getName());
        OrderedText addT = Language.getInstance().reorder(message.getCombined());
        client.textRenderer.drawWithShadow(matrices, addT, pos.getX(), pos.getY() + client.textRenderer.fontHeight + 3, Formatting.RED.getColorValue());
        hovered = false;
        matrices.pop();
    }


    @Override
    public Identifier getID() {
        return ID;
    }

    @Override
    public boolean moveable() {
        return true;
    }

    @Override
    public Storage getStorage() {
        return KronHUD.storage.itemUpdateHudStorage;
    }

    @Override
    public Screen getConfigScreen() {
        EntryBuilder builder = EntryBuilder.create();
        EntryButtonList list = new EntryButtonList((client.getWindow().getScaledWidth() / 2) - 290, (client.getWindow().getScaledHeight() / 2) - 70, 580, 150, 1, false);
        list.addEntry(builder.startToggleEntry(new TranslatableText("option.kronhud.enabled"), getStorage().enabled).setDimensions(20, 10).setSavable(val -> getStorage().enabled = val).build(list));
        list.addEntry(builder.startFloatSliderEntry(new TranslatableText("option.kronhud.scale"), getStorage().scale, 0.2F, 1.5F).setWidth(80).setSavable(val -> getStorage().scale = val).build(list));
        return new BasicConfigScreen(getName(), list, () -> KronHUD.storageHandler.saveDefaultHandling());

    }

    @Override
    public Text getName() {
        return new TranslatableText("hud.kronhud.itemupdatehud");
    }

    public static class Storage extends AbstractStorage {
        int timeout;

        public Storage() {
            x = 0.2F;
            y = 0F;
            scale = 1;
            timeout = 6000;
        }
    }

}

