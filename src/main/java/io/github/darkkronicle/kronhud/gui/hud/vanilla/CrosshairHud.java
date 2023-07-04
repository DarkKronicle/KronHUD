package io.github.darkkronicle.kronhud.gui.hud.vanilla;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.darkkronicle.darkkore.colors.ExtendedColor;
import io.github.darkkronicle.darkkore.config.options.OptionListEntry;
import io.github.darkkronicle.darkkore.util.Color;
import io.github.darkkronicle.darkkore.util.render.RenderUtil;
import io.github.darkkronicle.kronhud.config.KronBoolean;
import io.github.darkkronicle.kronhud.config.KronConfig;
import io.github.darkkronicle.kronhud.config.KronExtendedColor;
import io.github.darkkronicle.kronhud.config.KronOptionList;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;
import io.github.darkkronicle.kronhud.gui.component.DynamicallyPositionable;
import io.github.darkkronicle.kronhud.gui.layout.AnchorPoint;
import io.github.darkkronicle.kronhud.util.ColorUtil;
import io.github.darkkronicle.kronhud.util.DrawPosition;
import io.github.darkkronicle.kronhud.util.Rectangle;
import lombok.AllArgsConstructor;
import net.minecraft.block.AbstractChestBlock;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.AttackIndicator;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;

import java.util.List;

public class CrosshairHud extends AbstractHudEntry implements DynamicallyPositionable {
    public static final Identifier ID = new Identifier("kronhud", "crosshairhud");
    private static final Identifier ICONS_TEXTURE = new Identifier("textures/gui/icons.png");

    private final KronOptionList<Crosshair> type = new KronOptionList<>("type", ID.getPath(), Crosshair.CROSS);
    private final KronBoolean showInF5 = new KronBoolean("showInF5", ID.getPath(), false);
    private final KronExtendedColor defaultColor = new KronExtendedColor("defaultcolor", ID.getPath(), new ExtendedColor(ColorUtil.WHITE, ExtendedColor.ChromaOptions.getDefault()));
    private final KronExtendedColor entityColor = new KronExtendedColor("entitycolor", ID.getPath(), new ExtendedColor(ColorUtil.SELECTOR_RED, ExtendedColor.ChromaOptions.getDefault()));
    private final KronExtendedColor containerColor = new KronExtendedColor("blockcolor", ID.getPath(), new ExtendedColor(ColorUtil.SELECTOR_BLUE, ExtendedColor.ChromaOptions.getDefault()));
    private final KronExtendedColor attackIndicatorBackgroundColor = new KronExtendedColor("attackindicatorbg", ID.getPath(), new ExtendedColor(new Color(0xFF141414), ExtendedColor.ChromaOptions.getDefault()));
    private final KronExtendedColor attackIndicatorForegroundColor = new KronExtendedColor("attackindicatorfg", ID.getPath(), new ExtendedColor(ColorUtil.WHITE, ExtendedColor.ChromaOptions.getDefault()));

    public CrosshairHud() {
        super(15, 15);
    }

    @Override
    public double getDefaultX() {
        return 0.5;
    }

    @Override
    public double getDefaultY() {
        return 0.5F;
    }

    @Override
    public void render(DrawContext context, float delta) {
        if (!client.options.getPerspective().isFirstPerson() && !showInF5.getValue()) {
            return;
        }

        context.getMatrices().push();
        scale(context);
        DrawPosition pos = getPos().subtract(0, -1);
        Color color = getColor();
        AttackIndicator indicator = this.client.options.getAttackIndicator().getValue();

        if (type.getValue() == Crosshair.DOT) {
            fillRect(context, new Rectangle(pos.x() + (getWidth() / 2) - 2, pos.y() + (getHeight() / 2) - 2, 3, 3), color);
        } else if (type.getValue() == Crosshair.CROSS) {
            fillRect(context, new Rectangle(pos.x() + (getWidth() / 2) - 6, pos.y() + (getHeight() / 2) - 1, 6, 1), color);
            fillRect(context, new Rectangle(pos.x() + (getWidth() / 2), pos.y() + (getHeight() / 2) - 1, 5, 1), color);
            fillRect(context, new Rectangle(pos.x() + (getWidth() / 2) - 1, pos.y() + (getHeight() / 2) - 6, 1, 6), color);
            fillRect(context, new Rectangle(pos.x() + (getWidth() / 2) - 1, pos.y() + (getHeight() / 2), 1, 5), color);
        } else if (type.getValue() == Crosshair.DIRECTION) {
            Camera camera = this.client.gameRenderer.getCamera();
            MatrixStack matrixStack = RenderSystem.getModelViewStack();
            matrixStack.push();
            matrixStack.translate(getRawX() + ((float) getWidth() / 2), getRawY() + ((float) getHeight() / 2), 0);
            matrixStack.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(camera.getPitch()));
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw()));
            matrixStack.scale(-getScale(), -getScale(), getScale());
            RenderSystem.applyModelViewMatrix();
            RenderSystem.renderCrosshair(10);
            matrixStack.pop();
            RenderSystem.applyModelViewMatrix();
        } else if (type.getValue() == Crosshair.TEXTURE) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);

            // Draw crosshair
            RenderSystem.setShaderColor(
                    (float) color.red() / 255, (float) color.green() / 255, (float) color.blue() / 255, (float) color.alpha() / 255);
            context.drawTexture(
                    ICONS_TEXTURE, (int) (((client.getWindow().getScaledWidth() / getScale()) - 15) / 2),
                    (int) (((client.getWindow().getScaledHeight() / getScale()) - 15) / 2), 0, 0, 15, 15
            );
            RenderSystem.setShaderColor(1, 1, 1, 1);

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
                    context.drawTexture(ICONS_TEXTURE, x, y, 68, 94, 16, 16);
                } else if (progress < 1.0F) {
                    int k = (int) (progress * 17.0F);
                    context.drawTexture(ICONS_TEXTURE, x, y, 36, 94, 16, 4);
                    context.drawTexture(ICONS_TEXTURE, x, y, 52, 94, k, 4);
                }
            }
        }
        if (indicator == AttackIndicator.CROSSHAIR) {
            float progress = this.client.player.getAttackCooldownProgress(0.0F);
            if (progress != 1.0F) {
                RenderUtil.drawRectangle(
                        context, pos.x() + (getWidth() / 2) - 6, pos.y() + (getHeight() / 2) + 9, 11, 1,
                        attackIndicatorBackgroundColor.getValue()
                );
                RenderUtil.drawRectangle(
                        context, pos.x() + (getWidth() / 2) - 6, pos.y() + (getHeight() / 2) + 9,
                        (int) (progress * 11), 1, attackIndicatorForegroundColor.getValue()
                );
            }
        }
        context.getMatrices().pop();
    }

    public Color getColor() {
        HitResult hit = client.crosshairTarget;
        if (hit.getType() == null) {
            return defaultColor.getValue();
        } else if (hit.getType() == HitResult.Type.ENTITY) {
            return entityColor.getValue();
        } else if (hit.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos = ((BlockHitResult) hit).getBlockPos();
            World world = this.client.world;
            if (
                    world.getBlockState(blockPos).createScreenHandlerFactory(world, blockPos) != null
                    || world.getBlockState(blockPos).getBlock() instanceof AbstractChestBlock<?>
            ) {
                return containerColor.getValue();
            }
        }
        return defaultColor.getValue();
    }

    @Override
    public void renderPlaceholder(DrawContext context, float delta) {
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
    public List<KronConfig<?>> getConfigurationOptions() {
        List<KronConfig<?>> options = super.getConfigurationOptions();
        options.add(type);
        options.add(showInF5);
        options.add(defaultColor);
        options.add(entityColor);
        options.add(containerColor);
        options.add(attackIndicatorBackgroundColor);
        options.add(attackIndicatorForegroundColor);
        return options;
    }

    @Override
    public AnchorPoint getAnchor() {
        return AnchorPoint.MIDDLE_MIDDLE;
    }

    @AllArgsConstructor
    public enum Crosshair implements OptionListEntry<Crosshair> {
        CROSS("cross"),
        DOT("dot"),
        DIRECTION("direction"),
        TEXTURE("texture");

        private final String value;

        @Override
        public String getDisplayKey() {
            return "option.kronhud." + ID.getPath() + "." + value;
        }

        @Override
        public String getInfoKey() {
            return "option.kronhud." + ID.getPath() + ".info." + value;
        }

        @Override
        public List<Crosshair> getAll() {
            return List.of(values());
        }

        @Override
        public String getSaveKey() {
            return value;
        }

    }

}
