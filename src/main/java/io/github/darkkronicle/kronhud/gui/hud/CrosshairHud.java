package io.github.darkkronicle.kronhud.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.kronhud.config.KronBoolean;
import io.github.darkkronicle.kronhud.config.KronColor;
import io.github.darkkronicle.kronhud.config.KronOptionList;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;
import io.github.darkkronicle.kronhud.util.Color;
import io.github.darkkronicle.kronhud.util.DrawPosition;
import io.github.darkkronicle.kronhud.util.Rectangle;
import lombok.AllArgsConstructor;
import net.minecraft.block.AbstractChestBlock;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.option.AttackIndicator;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

import java.util.List;

public class CrosshairHud extends AbstractHudEntry {
    public static final Identifier ID = new Identifier("kronhud", "crosshairhud");

    private KronOptionList type = new KronOptionList("type", ID.getPath(), Crosshair.CROSS);
    private KronBoolean showInF5 = new KronBoolean("showInF5", ID.getPath(), false);
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
        if(!client.options.getPerspective().isFirstPerson() && !showInF5.getBooleanValue())return;

        matrices.push();
        scale(matrices);
        DrawPosition pos = getPos().subtract(0, -1);
        Color color = getColor();
        AttackIndicator indicator = this.client.options.getAttackIndicator().getValue();

        if (type.getOptionListValue() == Crosshair.DOT) {
            fillRect(matrices, new Rectangle(pos.x() + (width / 2) - 2, pos.y() + (height / 2) - 2, 3, 3), color);
        } else if (type.getOptionListValue() == Crosshair.CROSS) {
            fillRect(matrices, new Rectangle(pos.x() + (width / 2) - 6, pos.y() + (height / 2) - 1, 6, 1), color);
            fillRect(matrices, new Rectangle(pos.x() + (width / 2), pos.y() + (height / 2) - 1, 5, 1), color);
            fillRect(matrices, new Rectangle(pos.x() + (width / 2) - 1, pos.y() + (height / 2) - 6, 1, 6), color);
            fillRect(matrices, new Rectangle(pos.x() + (width / 2) - 1, pos.y() + (height / 2), 1, 5), color);
        } else if (type.getOptionListValue() == Crosshair.DIRECTION) {
            Camera camera = this.client.gameRenderer.getCamera();
            MatrixStack matrixStack = RenderSystem.getModelViewStack();
            matrixStack.push();
            matrixStack.translate(getX() + ((float) width / 2), getY() + ((float) height / 2), 0);
            matrixStack.multiply(Vec3f.NEGATIVE_X.getDegreesQuaternion(camera.getPitch()));
            matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(camera.getYaw()));
            matrixStack.scale(-getScale(), -getScale(), getScale());
            RenderSystem.applyModelViewMatrix();
            RenderSystem.renderCrosshair(10);
            matrixStack.pop();
            RenderSystem.applyModelViewMatrix();
        } else if (type.getOptionListValue() == Crosshair.TEXTURE) {
            RenderUtils.bindTexture(DrawableHelper.GUI_ICONS_TEXTURE);

            // Draw crosshair
            RenderUtils.color((float) color.red() / 255, (float) color.green() / 255, (float) color.blue() / 255, (float) color.alpha() / 255);
            client.inGameHud.drawTexture(matrices, (int) (((client.getWindow().getScaledWidth() / getScale()) - 15) / 2), (int) (((client.getWindow().getScaledHeight() / getScale()) - 15) / 2), 0, 0, 15, 15);
            RenderUtils.color(1, 1, 1, 1);

            // Draw attack indicator
            if (indicator == AttackIndicator.CROSSHAIR) {
                float progress = this.client.player.getAttackCooldownProgress(0.0F);

                // Whether a cross should be displayed under the indicator
                boolean targetingEntity = false;
                if (this.client.targetedEntity != null && this.client.targetedEntity instanceof LivingEntity
                        && progress >= 1.0F) {
                    targetingEntity = this.client.player.getAttackCooldownProgressPerTick() > 5.0F;
                    targetingEntity &= this.client.targetedEntity.isAlive();
                }

                int x = (int) ((client.getWindow().getScaledWidth() / getScale()) / 2 - 8);
                int y = (int) ((client.getWindow().getScaledHeight() / getScale()) / 2 - 7 + 16);

                if (targetingEntity) {
                    client.inGameHud.drawTexture(matrices, x, y, 68, 94, 16, 16);
                } else if (progress < 1.0F) {
                    int k = (int)(progress * 17.0F);
                    client.inGameHud.drawTexture(matrices, x, y, 36, 94, 16, 4);
                    client.inGameHud.drawTexture(matrices, x, y, 52, 94, k, 4);
                }
            }
        }
        if (indicator == AttackIndicator.CROSSHAIR) {
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
        bufferBuilder.vertex(matrix, x, y2, 0.0F).color(r, g, b, alpha).next();
        bufferBuilder.vertex(matrix, x2, y2, 0.0F).color(r, g, b, alpha).next();
        bufferBuilder.vertex(matrix, x2, y, 0.0F).color(r, g, b, alpha).next();
        bufferBuilder.vertex(matrix, x, y, 0.0F).color(r, g, b, alpha).next();
        BufferRenderer.drawWithShader(bufferBuilder.end());
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
            if (world.getBlockState(blockPos).createScreenHandlerFactory(world, blockPos) != null || world.getBlockState(blockPos).getBlock() instanceof AbstractChestBlock<?>) {
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
        options.add(showInF5);
        options.add(defaultColor);
        options.add(entityColor);
        options.add(containerColor);
        options.add(attackIndicatorBackgroundColor);
        options.add(attackIndicatorForegroundColor);
    }

    @AllArgsConstructor
    public enum Crosshair implements IConfigOptionListEntry {
        CROSS("cross"),
        DOT("dot"),
        DIRECTION("direction"),
        TEXTURE("texture");

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
