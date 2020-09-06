package io.github.darkkronicle.kronhud.gui.hud;

import io.github.darkkronicle.kronhud.KronHUD;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;
import io.github.darkkronicle.polish.api.EntryBuilder;
import io.github.darkkronicle.polish.gui.complexwidgets.EntryButtonList;
import io.github.darkkronicle.polish.gui.screens.BasicConfigScreen;
import io.github.darkkronicle.polish.util.DrawPosition;
import io.github.darkkronicle.polish.util.DrawUtil;
import io.github.darkkronicle.polish.util.SimpleColor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
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
        matrices.pop();
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

        public Storage() {
            x = 0.5F;
            y = 0.5F;
            scale = 1F;
            enabled = true;

            type = CrossHairs.CROSS;

            basic = new SimpleColor(255, 255, 255, 255);
            entity = new SimpleColor(191, 34, 34, 255);
            block = new SimpleColor(51, 153, 255, 255);
        }
    }
}
