package io.github.darkkronicle.kronhud.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.darkkronicle.kronhud.config.KronConfig;
import io.github.darkkronicle.kronhud.config.KronDouble;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

import java.util.List;

public class PlayerHud extends AbstractHudEntry {

    public static final Identifier ID = new Identifier("kronhud", "playerhud");

    private final KronDouble rotation = new KronDouble("rotation", ID.getPath(), 0, 0, 360);

    public PlayerHud() {
        super(62, 94);
    }

    @Override
    public void render(MatrixStack matrices, float delta) {
        matrices.push();
        scale(matrices);
        if (background.getValue() && backgroundColor.getValue().alpha() > 0) {
            fillRect(matrices, getRenderBounds(), backgroundColor.getValue());
        }
        if (outline.getValue() && outlineColor.getValue().alpha() > 0) {
            outlineRect(matrices, getRenderBounds(), outlineColor.getValue());
        }
        renderPlayer(getTruePos().x() + 31 * getScale(), getTruePos().y() + 86 * getScale(), delta);
        matrices.pop();
    }

    @Override
    public void renderPlaceholder(MatrixStack matrices, float delta) {
        matrices.push();
        renderPlaceholderBackground(matrices);
        scale(matrices);
        renderPlayer(getTruePos().x() + 31 * getScale(), getTruePos().y() + 86 * getScale(), 0); // If delta was delta, it would start jittering
        hovered = false;
        matrices.pop();
    }

    public void renderPlayer(double x, double y, float delta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            return;
        }

        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.translate(x, y, 1050);
        matrixStack.scale(1, 1, -1);

        RenderSystem.applyModelViewMatrix();
        MatrixStack nextStack = new MatrixStack();
        nextStack.translate(0, 0, 1000);
        float scale = getScale() * 40;
        nextStack.scale(scale, scale, scale);

        Quaternion quaternion = Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F);

        nextStack.multiply(quaternion);
        // Rotate to whatever is wanted. Also make sure to offset the yaw
        float deltaYaw = client.player.getYaw(delta);
        nextStack.multiply(new Quaternion(new Vec3f(0, 1, 0), deltaYaw - 180 + rotation.getValue().floatValue(), true));

        // Save these to set them back later
        float pastYaw = client.player.getYaw();
        float pastPrevYaw = client.player.prevYaw;

        DiffuseLighting.method_34742();
        EntityRenderDispatcher renderer = client.getEntityRenderDispatcher();
        renderer.setRotation(quaternion);
        renderer.setRenderShadows(false);

        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();

        renderer.render(client.player, 0, 0, 0, 0, delta, nextStack, immediate, 15728880);
        immediate.draw();
        renderer.setRenderShadows(true);
        matrixStack.pop();

        client.player.setYaw(pastYaw);
        client.player.prevYaw = pastPrevYaw;

        RenderSystem.applyModelViewMatrix();
        DiffuseLighting.enableGuiDepthLighting();
    }


    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public boolean movable() {
        return true;
    }

    @Override
    public List<KronConfig<?>> getConfigurationOptions() {
        List<KronConfig<?>> options = super.getConfigurationOptions();
        options.add(rotation);
        options.add(background);
        options.add(backgroundColor);
        options.add(outline);
        options.add(outlineColor);
        return options;
    }
}
