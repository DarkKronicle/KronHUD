package io.github.darkkronicle.kronhud.gui.hud;

import io.github.darkkronicle.kronhud.KronHUD;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;
import io.github.darkkronicle.polish.api.EntryBuilder;
import io.github.darkkronicle.polish.gui.complexwidgets.EntryButtonList;
import io.github.darkkronicle.polish.gui.screens.BasicConfigScreen;
import io.github.darkkronicle.polish.util.Colors;
import io.github.darkkronicle.polish.util.DrawPosition;
import io.github.darkkronicle.polish.util.DrawUtil;
import io.github.darkkronicle.polish.util.SimpleColor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CrossHairHud extends AbstractHudEntry {
    public static final Identifier ID = new Identifier("kronhud", "crosshairhud");


    public CrossHairHud() {
      //  super(0.5F, 0.5F, 17, 17, 1);
        super();
    }

    @Override
    public void render(MatrixStack matrices) {
        matrices.push();
        matrices.scale(getStorage().scale, getStorage().scale, 1);
        DrawPosition pos = getScaledPos();
        SimpleColor color = getColor();
        if (getStorage().type == CrossHairs.DOT) {
            DrawUtil.rect(matrices, pos.getX() + (getStorage().width / 2) - 2, pos.getY() + (getStorage().height / 2) - 2, 3, 3, color.color());
        } else if (getStorage().type == CrossHairs.CROSS) {
            DrawUtil.rect(matrices, pos.getX() + (getStorage().width / 2) - 6, pos.getY() + (getStorage().height / 2) - 1, 6, 1, color.color());
            DrawUtil.rect(matrices, pos.getX() + (getStorage().width / 2), pos.getY() + (getStorage().height / 2) - 1, 5, 1, color.color());
            DrawUtil.rect(matrices, pos.getX() + (getStorage().width / 2) - 1, pos.getY() + (getStorage().height / 2) - 6, 1, 6, color.color());
            DrawUtil.rect(matrices, pos.getX() + (getStorage().width / 2) - 1, pos.getY() + (getStorage().height / 2), 1, 5, color.color());
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
            BlockPos blockPos = ((BlockHitResult)hit).getBlockPos();
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

    public enum CrossHairs {
        CROSS,
        DOT
    }

    @Override
    public Storage getStorage() {
        return KronHUD.storage.crossHairHudStorage;
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
            width = 17;
            height = 17;
            enabled = true;

            type = CrossHairs.CROSS;

            basic = new SimpleColor(255, 255, 255, 255);
            entity = new SimpleColor(191, 34, 34, 255);
            block = new SimpleColor(51, 153, 255, 255);
        }
    }

    @Override
    public Screen getConfigScreen() {
        EntryBuilder builder = EntryBuilder.create();
        EntryButtonList list = new EntryButtonList((client.getWindow().getScaledWidth() / 2) - 290, (client.getWindow().getScaledHeight() / 2) - 70, 580, 150, 1, false);
        list.addEntry(builder.startToggleEntry(new LiteralText("Enabled"), getStorage().enabled).setDimensions(20, 10).setSavable(val -> getStorage().enabled = val).build(list));
        list.addEntry(builder.startFloatSliderEntry(new LiteralText("Scale"), getStorage().scale, 0.2F, 1.5F).setWidth(80).setSavable(val -> getStorage().scale = val).build(list));
        list.addEntry(builder.startColorButtonEntry(new LiteralText("Default Color"), getStorage().basic).setSavable(val -> getStorage().basic = val).build(list));
        list.addEntry(builder.startColorButtonEntry(new LiteralText("Entity Color"), getStorage().entity).setSavable(val -> getStorage().entity = val).build(list));
        list.addEntry(builder.startColorButtonEntry(new LiteralText("Block Color"), getStorage().block).setSavable(val -> getStorage().block = val).build(list));

        return new BasicConfigScreen(new LiteralText("CrossHairHud"), list) {
            @Override
            public void onClose() {
                super.onClose();
                KronHUD.storageHandler.saveDefaultHandling();
            }
        };
    }
    @Override
    public String getName() {
        return "CrossHairHud";
    }
}
