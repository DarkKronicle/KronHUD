package io.github.darkkronicle.kronhud.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.kronhud.config.KronColor;
import io.github.darkkronicle.kronhud.config.KronOptionList;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;
import io.github.darkkronicle.kronhud.util.Color;
import io.github.darkkronicle.kronhud.util.DrawPosition;
import io.github.darkkronicle.kronhud.util.Rectangle;
import lombok.AllArgsConstructor;
import net.minecraft.client.option.AttackIndicator;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.World;

import java.util.List;

public class CrosshairHud extends AbstractHudEntry {
    public static final Identifier ID = new Identifier("kronhud", "crosshairhud");

    private KronOptionList type = new KronOptionList("type", ID.getPath(), Crosshair.CROSS);
    private KronColor defaultColor = new KronColor("defaultcolor", ID.getPath(), "#FFFFFFFF");
    private KronColor entityColor = new KronColor("entitycolor", ID.getPath(), Color.SELECTOR_RED.toString());
    private KronColor containerColor = new KronColor("blockcolor", ID.getPath(), Color.SELECTOR_BLUE.toString());
    private KronColor attackIndicatorBackgroundColor = new KronColor("attackindicatorbg", ID.getPath(),
            "#FF141414");
    private KronColor attackIndicatorForegroundColor = new KronColor("attackindicatorfg", ID.getPath(),
            "#FFFFFFFF");

    public CrosshairHud() {
        super(17, 17);
    }

    @Override
    protected double getDefaultX() {
        return 0.5;
    }

    @Override
    protected float getDefaultY() {
        return 0.5F;
    }

    @Override
    public void render(MatrixStack matrices) {
        matrices.push();
        scale(matrices);
        DrawPosition pos = getPos().subtract(0, -1);
        Color color = getColor();
        if (type.getOptionListValue() == Crosshair.DOT) {
            fillRect(matrices, new Rectangle(pos.x() + (width / 2) - 2, pos.y() + (height / 2) - 2, 3, 3), color);
        } else if (type.getOptionListValue() == Crosshair.CROSS) {
            fillRect(matrices, new Rectangle(pos.x() + (width / 2) - 6, pos.y() + (height / 2) - 1, 6, 1), color);
            fillRect(matrices, new Rectangle(pos.x() + (width / 2), pos.y() + (height / 2) - 1, 5, 1), color);
            fillRect(matrices, new Rectangle(pos.x() + (width / 2) - 1, pos.y() + (height / 2) - 6, 1, 6), color);
            fillRect(matrices, new Rectangle(pos.x() + (width / 2) - 1, pos.y() + (height / 2), 1, 5), color);
        }
        if (this.client.options.attackIndicator == AttackIndicator.CROSSHAIR) {
            float progress = this.client.player.getAttackCooldownProgress(0.0F);
            if (progress != 1.0F) {
                fill(matrices.peek().getPositionMatrix(), pos.x() + (width / 2) - 6, pos.y() + (height / 2) + 9, 11, 1,
                        attackIndicatorBackgroundColor.getColor().color());
                fill(matrices.peek().getPositionMatrix(), pos.x() + (width / 2) - 6, pos.y() + (height / 2) + 9,
                        progress * 11, 1, attackIndicatorForegroundColor.getColor().color());
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
        bufferBuilder.begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix, (float) x, (float) y2, 0.0F).color(r, g, b, alpha).next();
        bufferBuilder.vertex(matrix, (float) x2, (float) y2, 0.0F).color(r, g, b, alpha).next();
        bufferBuilder.vertex(matrix, (float) x2, (float) y, 0.0F).color(r, g, b, alpha).next();
        bufferBuilder.vertex(matrix, (float) x, (float) y, 0.0F).color(r, g, b, alpha).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public Color getColor() {
        HitResult hit = client.crosshairTarget;
        if (hit.getType() == null) {
            return defaultColor.getColor();
        } else if (hit.getType() == HitResult.Type.ENTITY) {
            return entityColor.getColor();
        } else if (hit.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos = ((BlockHitResult) hit).getBlockPos();
            World world = this.client.world;
            if (world.getBlockState(blockPos).createScreenHandlerFactory(world, blockPos) != null) {
                return containerColor.getColor();
            }
        }
        return defaultColor.getColor();
    }

    @Override
    public void renderPlaceholder(MatrixStack matrices) {
        // Shouldn't need this...
    }

    @Override
    public boolean movable() {
        return false;
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public void addConfigOptions(List<IConfigBase> options) {
        super.addConfigOptions(options);
        options.add(type);
        options.add(defaultColor);
        options.add(entityColor);
        options.add(containerColor);
        options.add(attackIndicatorBackgroundColor);
        options.add(attackIndicatorForegroundColor);
    }

    @AllArgsConstructor
    public enum Crosshair implements IConfigOptionListEntry {
        CROSS("cross"),
        DOT("dot");

        private String value;

        @Override
        public String getStringValue() {
            return value;
        }

        @Override
        public String getDisplayName() {
            return StringUtils.translate("option.kronhud." + ID.getPath() + "." + value);
        }

        @Override
        public IConfigOptionListEntry cycle(boolean forwards) {
            int id = this.ordinal();
            if (forwards) {
                id++;
            } else {
                id--;
            }
            if (id >= values().length) {
                id = 0;
            } else if (id < 0) {
                id = values().length - 1;
            }
            return values()[id % values().length];
        }

        @Override
        public IConfigOptionListEntry fromString(String str) {
            for (Crosshair crosshair : values()) {
                if (crosshair.value.equals(str)) {
                    return crosshair;
                }
            }
            return CROSS;
        }

    }

}
