package io.github.darkkronicle.kronhud.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.darkkronicle.darkkore.util.Color;
import lombok.experimental.UtilityClass;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.font.TextRenderer.TextLayerType;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

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
            if (storage.stack.isItemEqual(compare)) {
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
            if (storage.stack.isItemEqual(compare)) {
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

    public static void renderGuiItemOverlay(MatrixStack matrices, TextRenderer text, ItemStack stack, int x, int y,
            String countLabel, int color, boolean shadow, float delta) {
        if (!stack.isEmpty()) {
            matrices.push();
            if (stack.getCount() != 1 || countLabel != null) {
                matrices.translate(0, 0, 200);
                String string = countLabel == null ? String.valueOf(stack.getCount()) : countLabel;
                DrawUtil.drawString(matrices, text, Text.literal(string), (float) (x + 19 - 2 - text.getWidth(string)),
                        (float) (y + 6 + 3), color, shadow);
            }

            if (stack.isItemBarVisible()) {
                RenderSystem.disableDepthTest();
                int step = stack.getItemBarStep();
                int barColor = stack.getItemBarColor();
                int left = x + 2;
                int top = y + 13;
                DrawableHelper.fill(matrices, left, top, left + 13, top + 2, -16777216);
                DrawableHelper.fill(matrices, left, top, left + step, top + 1, barColor | -16777216);
                RenderSystem.enableDepthTest();
            }

            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            float cooldown = player == null ? 0.0F
                    : player.getItemCooldownManager().getCooldownProgress(stack.getItem(), delta);
            if (cooldown > 0.0F) {
                RenderSystem.disableDepthTest();
                int top = y + MathHelper.floor(16 * (1 - cooldown));
                int bottom = top + MathHelper.ceil(16 * cooldown);
                DrawableHelper.fill(matrices, x, top, x + 16, bottom, Integer.MAX_VALUE);
                RenderSystem.enableDepthTest();
            }

            matrices.pop();
        }
    }
}
