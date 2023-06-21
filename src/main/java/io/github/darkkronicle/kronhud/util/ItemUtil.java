package io.github.darkkronicle.kronhud.util;

import lombok.experimental.UtilityClass;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@UtilityClass
public class ItemUtil {

    public List<ItemStorage> storageFromItem(List<ItemStack> items) {
        ArrayList<ItemStorage> storage = new ArrayList<>();
        for (ItemStack item : items) {
            if (item.isEmpty()) {
                continue;
            }
            Optional<ItemStorage> s = getItemFromItem(item, storage);
            if (s.isPresent()) {
                ItemUtil.ItemStorage store = s.get();
                store.incrementTimes(item.getCount());
            } else {
                storage.add(new ItemUtil.ItemStorage(item, item.getCount()));
            }
        }
        return storage;
    }

    public List<ItemStack> getItems(MinecraftClient client) {
        ArrayList<ItemStack> items = new ArrayList<>();
        if (client.player == null) {
            return null;
        }
        items.addAll(client.player.getInventory().armor);
        items.addAll(client.player.getInventory().offHand);
        items.addAll(client.player.getInventory().main);
        return items;
    }

    public ArrayList<ItemUtil.TimedItemStorage> removeOld(List<ItemUtil.TimedItemStorage> list, int time) {
        ArrayList<ItemUtil.TimedItemStorage> stored = new ArrayList<>();
        for (ItemUtil.TimedItemStorage storage : list) {
            if (storage.getPassedTime() <= time) {
                stored.add(storage);
            }
        }
        return stored;
    }

    public static List<ItemUtil.TimedItemStorage> untimedToTimed(List<ItemStorage> list) {
        ArrayList<TimedItemStorage> timed = new ArrayList<>();
        for (ItemStorage stack : list) {
            timed.add(stack.timed());
        }
        return timed;
    }

    public Optional<ItemUtil.ItemStorage> getItemFromItem(ItemStack item, List<ItemUtil.ItemStorage> list) {
        ItemStack compare = item.copy();
        compare.setCount(1);
        for (ItemUtil.ItemStorage storage : list) {
            if (storage.stack.isOf(compare.getItem())) {
                return Optional.of(storage);
            }
        }
        return Optional.empty();
    }

    public Optional<ItemUtil.TimedItemStorage> getTimedItemFromItem(ItemStack item,
            List<ItemUtil.TimedItemStorage> list) {
        ItemStack compare = item.copy();
        compare.setCount(1);
        for (ItemUtil.TimedItemStorage storage : list) {
            if (storage.stack.isOf(compare.getItem())) {
                return Optional.of(storage);
            }
        }
        return Optional.empty();
    }

    public int getTotal(MinecraftClient client, ItemStack stack) {
        List<ItemStack> item = ItemUtil.getItems(client);
        if (item == null || item.isEmpty()) {
            return 0;
        }
        List<ItemUtil.ItemStorage> items = ItemUtil.storageFromItem(item);
        Optional<ItemUtil.ItemStorage> stor = ItemUtil.getItemFromItem(stack, items);
        return stor.map(itemStorage -> itemStorage.times).orElse(0);
    }

    /**
     * Compares two ItemStorage Lists.
     * If list1.get(1) is 10, and list2 is 5, it will return 5.
     * Will return nothing if negative...
     *
     * @param list1 one to be based off of
     * @param list2 one to compare to
     * @return
     */
    public List<ItemStorage> compare(List<ItemStorage> list1, List<ItemStorage> list2) {
        ArrayList<ItemStorage> list = new ArrayList<>();
        for (ItemStorage current : list1) {
            Optional<ItemStorage> optional = getItemFromItem(current.stack, list2);
            if (optional.isPresent()) {
                ItemStorage other = optional.get();
                if (current.times - other.times <= 0) {
                    continue;
                }
                list.add(new ItemStorage(other.stack.copy(), current.times - other.times));
            } else {
                list.add(current.copy());
            }
        }
        return list;
    }

    public void renderGuiQuad(BufferBuilder buffer, int x, int y, int width, int height, int red, int green, int blue,
            int alpha) {
        buffer.begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(x, y, 0.0D).color(red, green, blue, alpha).next();
        buffer.vertex(x, y + height, 0.0D).color(red, green, blue, alpha).next();
        buffer.vertex(x + width, y + height, 0.0D).color(red, green, blue, alpha).next();
        buffer.vertex(x + width, y, 0.0D).color(red, green, blue, alpha).next();
        Tessellator.getInstance().draw();
    }

    public static class ItemStorage {

        public final ItemStack stack;
        public int times;

        public ItemStorage(ItemStack stack) {
            this(stack, 1);
        }

        public ItemStorage(ItemStack stack, int times) {
            ItemStack copy = stack.copy();
            copy.setCount(1);
            this.stack = copy;
            this.times = times;
        }

        public void incrementTimes(int num) {
            times = times + num;
        }

        public ItemStorage copy() {
            return new ItemStorage(stack.copy(), times);
        }

        public TimedItemStorage timed() {
            return new TimedItemStorage(stack, times);
        }
    }

    public static class TimedItemStorage extends ItemStorage {

        public float start;

        public TimedItemStorage(ItemStack stack) {
            this(stack, 1);
        }

        public TimedItemStorage(ItemStack stack, int times) {
            super(stack, times);
            this.start = Util.getMeasuringTimeMs();
        }

        public float getPassedTime() {
            return Util.getMeasuringTimeMs() - start;
        }

        @Override
        public void incrementTimes(int num) {
            super.incrementTimes(num);
            refresh();
        }

        public void refresh() {
            start = Util.getMeasuringTimeMs();
        }
    }
}
