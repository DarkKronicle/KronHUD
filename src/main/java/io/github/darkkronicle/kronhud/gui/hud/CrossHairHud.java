package io.github.darkkronicle.kronhud.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.darkkronicle.kronhud.KronHUD;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;
import io.github.darkkronicle.polish.api.EntryBuilder;
import io.github.darkkronicle.polish.gui.complexwidgets.EntryButtonList;
import io.github.darkkronicle.polish.gui.screens.BasicConfigScreen;
import io.github.darkkronicle.polish.util.DrawPosition;
import io.github.darkkronicle.polish.util.DrawUtil;
import io.github.darkkronicle.polish.util.SimpleColor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.AttackIndicator;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.World;

public class CrossHairHud extends AbstractHudEntry {
    public static final Identifier ID = new Identifier("kronhud", "crosshairhud");


    public CrossHairHud() {
        super(17, 17);
    }

    @Override
    public void render(MatrixStack matrices) {
        matrices.push();
        matrices.scale(getStorage().scale, getStorage().scale, 1);
        DrawPosition pos = getScaledPos();
        SimpleColor color = getColor();
        if (getStorage().type == CrossHairs.DOT) {
            DrawUtil.rect(matrices, pos.getX() + (width / 2) - 2, pos.getY() + (height / 2) - 2, 3, 3, color.color());
        } else if (getStorage().type == CrossHairs.CROSS) {
            DrawUtil.rect(matrices, pos.getX() + (width / 2) - 6, pos.getY() + (height / 2) - 1, 6, 1, color.color());
            DrawUtil.rect(matrices, pos.getX() + (width / 2), pos.getY() + (height / 2) - 1, 5, 1, color.color());
            DrawUtil.rect(matrices, pos.getX() + (width / 2) - 1, pos.getY() + (height / 2) - 6, 1, 6, color.color());
            DrawUtil.rect(matrices, pos.getX() + (width / 2) - 1, pos.getY() + (height / 2), 1, 5, color.color());
        }
        if (this.client.options.attackIndicator == AttackIndicator.CROSSHAIR) {
            float progress = this.client.player.getAttackCooldownProgress(0.0F);
            if (progress != 1.0F) {
                fill(matrices.peek().getModel(), pos.getX() + (width / 2) - 6, pos.getY() + (height / 2) + 9, 11, 1, getStorage().attackIndicatorBG.color());
                fill(matrices.peek().getModel(), pos.getX() + (width / 2) - 6, pos.getY() + (height / 2) + 9, progress * 11, 1, getStorage().attackIndicatorFG.color());
            }
        }
        matrices.pop();
    }

    private static void fill(Matrix4f matrix, float x, float y, float width, float height, int color) {
        float x2 = x + width;
        float y2 = y + height;
        float swap;
        if (x < x2) {
            swap = x;
            x = x2;
            x2 = swap;
        }

        if (y < y2) {
            swap = y;
            y = y2;
            y2 = swap;
        }

        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix, (float) x, (float) y2, 0.0F).color(r, g, b, alpha).next();
        bufferBuilder.vertex(matrix, (float) x2, (float) y2, 0.0F).color(r, g, b, alpha).next();
        bufferBuilder.vertex(matrix, (float) x2, (float) y, 0.0F).color(r, g, b, alpha).next();
        bufferBuilder.vertex(matrix, (float) x, (float) y, 0.0F).color(r, g, b, alpha).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public SimpleColor getColor() {
        HitResult hit = client.crosshairTarget;
        if (hit.getType() == null) {
            return getStorage().basic;
        } else if (hit.getType() == HitResult.Type.ENTITY) {
            return getStorage().entity;
        } else if (hit.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos = ((BlockHitResult) hit).getBlockPos();
            World world = this.client.world;
            if (world.getBlockState(blockPos).createScreenHandlerFactory(world, blockPos) != null) {
                return getStorage().block;
            }
        }
        return getStorage().basic;
    }

    @Override
    public void renderPlaceholder(MatrixStack matrices) {
        // Shouldn't need this...
    }

    @Override
    public boolean moveable() {
        return false;
    }

    @Override
    public Identifier getID() {
        return ID;
    }

    @Override
    public Storage getStorage() {
        return KronHUD.storage.crossHairHudStorage;
    }

    @Override
    public Screen getConfigScreen() {
        EntryBuilder builder = EntryBuilder.create();
        EntryButtonList list = new EntryButtonList((client.getWindow().getScaledWidth() / 2) - 290, (client.getWindow().getScaledHeight() / 2) - 70, 580, 150, 1, false);
        list.addEntry(builder.startToggleEntry(new TranslatableText("option.kronhud.enabled"), getStorage().enabled).setDimensions(20, 10).setSavable(val -> getStorage().enabled = val).build(list));
        list.addEntry(builder.startFloatSliderEntry(new TranslatableText("option.kronhud.scale"), getStorage().scale, 0.2F, 1.5F).setWidth(80).setSavable(val -> getStorage().scale = val).build(list));
        list.addEntry(builder.startColorButtonEntry(new TranslatableText("option.kronhud.crosshairhud.defaultcolor"), getStorage().basic).setSavable(val -> getStorage().basic = val).build(list));
        list.addEntry(builder.startColorButtonEntry(new TranslatableText("option.kronhud.crosshairhud.entitycolor"), getStorage().entity).setSavable(val -> getStorage().entity = val).build(list));
        list.addEntry(builder.startColorButtonEntry(new TranslatableText("option.kronhud.crosshairhud.blockcolor"), getStorage().block).setSavable(val -> getStorage().block = val).build(list));
        list.addEntry(builder.startColorButtonEntry(new TranslatableText("option.kronhud.crosshairhud.attackindicatorfg"), getStorage().attackIndicatorFG).setSavable(val -> getStorage().attackIndicatorFG = val).build(list));
        list.addEntry(builder.startColorButtonEntry(new TranslatableText("option.kronhud.crosshairhud.attackindicatorbg"), getStorage().attackIndicatorBG).setSavable(val -> getStorage().attackIndicatorBG = val).build(list));
        list.addEntry(builder.startDropdownEntry(new TranslatableText("option.kronhud.crosshairhud.type"), getStorage().type).add(CrossHairs.CROSS, "Cross").add(CrossHairs.DOT, "Dot").setSavable(val -> getStorage().type = val).build(list));

        return new BasicConfigScreen(getName(), list, () -> KronHUD.storageHandler.saveDefaultHandling());

    }

    @Override
    public Text getName() {
        return new TranslatableText("hud.kronhud.crosshairhud");
    }

    public enum CrossHairs {
        CROSS,
        DOT
    }

    public static class Storage extends AbstractStorage {
        public CrossHairs type;
        public SimpleColor basic;
        public SimpleColor entity;
        public SimpleColor block;
        public SimpleColor attackIndicatorFG;
        public SimpleColor attackIndicatorBG;

        public Storage() {
            x = 0.5F;
            y = 0.5F;
            scale = 1F;
            enabled = true;

            type = CrossHairs.CROSS;

            basic = new SimpleColor(255, 255, 255, 255);
            entity = new SimpleColor(191, 34, 34, 255);
            block = new SimpleColor(51, 153, 255, 255);
            attackIndicatorFG = new SimpleColor(255, 255, 255, 255);
            attackIndicatorBG = new SimpleColor(0, 0, 0, 255);
        }
    }
}
