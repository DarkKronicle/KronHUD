package io.github.darkkronicle.kronhud.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.darkkronicle.kronhud.config.KronBoolean;
import io.github.darkkronicle.kronhud.config.KronConfig;
import io.github.darkkronicle.kronhud.config.KronDouble;
import io.github.darkkronicle.kronhud.gui.entry.BoxHudEntry;
import io.github.darkkronicle.kronhud.hooks.KronHudHooks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public class PlayerHud extends BoxHudEntry {

    public static final Identifier ID = new Identifier("kronhud", "playerhud");

    private final KronDouble rotation = new KronDouble("rotation", ID.getPath(), 0, 0, 360);
    private final KronBoolean dynamicRotation = new KronBoolean("dynamicrotation", ID.getPath(), true);

    private float lastYawOffset = 0;
    private float yawOffset = 0;
    private float lastYOffset = 0;
    private float yOffset = 0;


    public PlayerHud() {
        super(62, 94, true);
        KronHudHooks.PLAYER_DIRECTION_CHANGE.register(this::onPlayerDirectionChange);
    }

    @Override
    public void renderComponent(MatrixStack matrices, float delta) {
        renderPlayer(getTruePos().x() + 31 * getScale(), getTruePos().y() + 86 * getScale(), delta);
    }

    @Override
    public void renderPlaceholderComponent(MatrixStack matrices, float delta) {
        renderPlayer(getTruePos().x() + 31 * getScale(), getTruePos().y() + 86 * getScale(), 0); // If delta was delta, it would start jittering
    }

    public void renderPlayer(double x, double y, float delta) {
        if (client.player == null) {
            return;
        }

        float scale = getScale() * 40;
        if (client.player.hasVehicle()) {
            Entity vehicle = client.player.getVehicle();
            if (vehicle.getType() == EntityType.HORSE
                    || vehicle.getType() == EntityType.SKELETON_HORSE
                    || vehicle.getType() == EntityType.ZOMBIE_HORSE
                    || vehicle.getType() == EntityType.DONKEY
                    || vehicle.getType() == EntityType.MULE
            ) { // horses are too big normally, need to be scaled and moved to center
                float scaleSub = scale * 0.25f;
                y -= 86 * scaleSub / 40.0;
                scale -= scaleSub;
            }
            if (vehicle.getType() == EntityType.PIG) { // pigs are too big too, but only slightly
                float scaleSub = scale * 0.1f;
                y -= 86 * scaleSub / 40.0;
                scale -= scaleSub;
            }
        }

        float lerpY = (lastYOffset + ((yOffset - lastYOffset) * delta));

        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.translate(x, y - lerpY, 1050);
        matrixStack.scale(1, 1, -1);

        RenderSystem.applyModelViewMatrix();
        MatrixStack nextStack = new MatrixStack();
        nextStack.translate(0, 0, 1000);
        nextStack.scale(scale, scale, scale);
        Quaternionf quaternion = RotationAxis.POSITIVE_Z.rotationDegrees(180);

        nextStack.multiply(quaternion);
        // Rotate to whatever is wanted. Also make sure to offset the yaw
        float deltaYaw = client.player.getYaw(delta);
        if (dynamicRotation.getValue()) {
            deltaYaw -= (lastYawOffset + ((yawOffset - lastYawOffset) * delta));
        }
        nextStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(deltaYaw - 180 + rotation.getValue().floatValue()));

        // Save these to set them back later
        float pastYaw = client.player.getYaw();
        float pastPrevYaw = client.player.prevYaw;

        DiffuseLighting.method_34742();
        EntityRenderDispatcher renderer = client.getEntityRenderDispatcher();
        renderer.setRotation(quaternion);
        renderer.setRenderShadows(false);

        Entity player = client.player;
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        if (player.hasVehicle()) {
            Entity vehicle = client.player.getVehicle();
            if (vehicle.getType() == EntityType.BOAT || vehicle.getType() == EntityType.CHEST_BOAT) { // boats are special for some reason
                renderer.render(
                        vehicle,
                        vehicle.getX() - player.getX(),
                        vehicle.getY() - player.getY(),
                        vehicle.getZ() - player.getZ(),
                        vehicle.prevYaw,
                        delta,
                        nextStack,
                        immediate,
                        15728880
                );
                for (Entity otherRider : vehicle.getPassengerList()) {
                    if (otherRider == player) {
                        continue;
                    }
                    renderer.render(
                            otherRider,
                            otherRider.getX() - player.getX(),
                            otherRider.getY() - player.getY(),
                            otherRider.getZ() - player.getZ(),
                            otherRider.prevYaw,
                            delta,
                            nextStack,
                            immediate,
                            15728880
                    );
                }
                renderer.render(player, 0, 0, 0, -pastYaw, delta, nextStack, immediate, 15728880);
            } else { // every other vehicle allows the player to rotate their body
                renderer.render(
                        vehicle,
                        vehicle.getX() - player.getX(),
                        vehicle.getY() - player.getY(),
                        vehicle.getZ() - player.getZ(),
                        0,
                        delta,
                        nextStack,
                        immediate,
                        15728880
                );
                for (Entity otherRider : vehicle.getPassengerList()) {
                    if (otherRider == player) {
                        continue;
                    }
                    renderer.render(
                            otherRider,
                            otherRider.getX() - player.getX(),
                            otherRider.getY() - player.getY(),
                            otherRider.getZ() - player.getZ(),
                            0,
                            delta,
                            nextStack,
                            immediate,
                            15728880
                    );
                }
                renderer.render(player, 0, 0, 0, pastYaw, delta, nextStack, immediate, 15728880);
            }
        } else {
            renderer.render(player, 0, 0, 0, 0, delta, nextStack, immediate, 15728880);
        }
        immediate.draw();
        renderer.setRenderShadows(true);
        matrixStack.pop();

        player.setYaw(pastYaw);
        player.prevYaw = pastPrevYaw;

        RenderSystem.applyModelViewMatrix();
        DiffuseLighting.enableGuiDepthLighting();
    }

    public void onPlayerDirectionChange(float prevPitch, float prevYaw, float pitch, float yaw) {
        yawOffset += (yaw - prevYaw) / 2;
    }

    @Override
    public boolean tickable() {
        return true;
    }

    @Override
    public void tick() {
        lastYawOffset = yawOffset;
        yawOffset *= .93f;
        lastYOffset = yOffset;
        if (client.player != null && client.player.isInSwimmingPose()) {
            float rawPitch = client.player.isTouchingWater() ? -90.0F - client.player.getPitch() : -90.0F;
            float pitch = MathHelper.lerp(client.player.getLeaningPitch(1), 0.0F, rawPitch);
            float height = client.player.getHeight();
            // sin = opposite / hypotenuse
            float offset = (float) (Math.sin(Math.toRadians(pitch)) * height);
            yOffset = Math.abs(offset) + 35;
        } else if (client.player != null && client.player.isFallFlying()) {
            // Elytra!

            float j = (float) client.player.getRoll() + 1;
            float k = MathHelper.clamp(j * j / 100.0F, 0.0F, 1.0F);

            float pitch = k * (-90.0F - client.player.getPitch()) + 90;
            float height = client.player.getHeight();
            // sin = opposite / hypotenuse
            float offset = (float) (Math.sin(Math.toRadians(pitch)) * height) * 50;
            yOffset = 35 - offset;
            if (pitch < 0) {
                yOffset -= ((1 / (1 + Math.exp(-pitch / 4))) - .5) * 20;
            }
        } else {
            yOffset *= .8;
        }
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
        options.add(dynamicRotation);
        options.add(rotation);
        options.add(background);
        options.add(backgroundColor);
        options.add(outline);
        options.add(outlineColor);
        return options;
    }
}
