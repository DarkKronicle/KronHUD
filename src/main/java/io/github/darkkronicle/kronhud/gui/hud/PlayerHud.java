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

    private final KronBoolean boatFacing = new KronBoolean("boatfacing", ID.getPath(), false);

    private float lastYawOffset = 0;
    private float yawOffset = 0;
    private float lastYOffset = 0;
    private float yOffset = 0;

    // this is for smoothing on boats, since boats don't have a yaw delta
    private float currentYaw = 0;
    private float lastYaw = 0;


    public PlayerHud() {
        super(62, 94, true);
        KronHudHooks.PLAYER_DIRECTION_CHANGE.register(this::onPlayerDirectionChange);
        KronHudHooks.MOUNT_DIRECTION_CHANGE.register(this::onMountDirectionChange);
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
        Entity player  = client.player;
        Entity baseVehicle = getBaseVehicle(player);

        float scaleFraction = 1 / Math.max(getRenderWidth(baseVehicle), getRenderHeight(baseVehicle) / 2.5f);
        float scale = getScale() * 40;
        float scaleSub = scale * (1 - scaleFraction);
        y -= 86 * scaleSub / 80.0;
        scale -= scaleSub;

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
        if (isInBoat(player) && boatFacing.getValue()) { // the camera faces the boat if the boat is the focus
            float deltaYaw = player.getVehicle().getYaw(delta);
            if (dynamicRotation.getValue()) {
                deltaYaw -= (lastYawOffset + ((yawOffset - lastYawOffset) * delta));
            }
            nextStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(deltaYaw - 180 + rotation.getValue().floatValue()));
        } else { // regular player-facing camera
            float deltaYaw = client.player.getYaw(delta);
            if (dynamicRotation.getValue()) {
                deltaYaw -= (lastYawOffset + ((yawOffset - lastYawOffset) * delta));
            }
            nextStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(deltaYaw - 180 + rotation.getValue().floatValue()));
        }
        // Save these to set them back later
        float pastYaw = client.player.getYaw();
        float pastPrevYaw = client.player.prevYaw;

        DiffuseLighting.method_34742();
        EntityRenderDispatcher renderer = client.getEntityRenderDispatcher();
        renderer.setRotation(quaternion);
        renderer.setRenderShadows(false);

        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        renderMounts(
                baseVehicle,
                baseVehicle.getX() - player.getX(),
                baseVehicle.getY() - player.getY(),
                baseVehicle.getZ() - player.getZ(),
                delta,
                renderer,
                nextStack,
                immediate
        );

        immediate.draw();
        renderer.setRenderShadows(true);
        matrixStack.pop();

        player.setYaw(pastYaw);
        player.prevYaw = pastPrevYaw;

        RenderSystem.applyModelViewMatrix();
        DiffuseLighting.enableGuiDepthLighting();
    }

    private Entity getBaseVehicle(Entity entity) {
        if (entity.hasVehicle()) {
            return getBaseVehicle(entity.getVehicle());
        } else {
            return entity;
        }
    }

    private boolean isInBoat(Entity entity) {
        if (entity.hasVehicle()) {
            Entity vehicle = entity.getVehicle();
            return vehicle.getType() == EntityType.BOAT || vehicle.getType() == EntityType.CHEST_BOAT;
        } else {
            return false;
        }
    }

    /**
     * This returns the stack height (in blocks) of the mounted entity stack this entity is the base of
     * (or just the height of the entity if they aren't part of any stack)
     */
    private float getRenderHeight(Entity entity) {
        float renderHeight;
        if (entity == client.player && !client.player.hasVehicle()) {
            renderHeight = 2.5f;
        } else {
            renderHeight = (float) entity.getVisibilityBoundingBox().getYLength();
        }
        if (entity.hasPassengers()) {
            float maxPassengerHeight = Float.NEGATIVE_INFINITY;
            for (Entity other : entity.getPassengerList()) {
                double relativeY = other.getY() - entity.getY();
                maxPassengerHeight = Math.max(getRenderHeight(other) + (float) relativeY, maxPassengerHeight);
            }
            renderHeight += maxPassengerHeight;
        }
        return renderHeight;
    }

    /**
     * Same as getRenderHeight, but for width
     */
    private float getRenderWidth(Entity entity) {
        float renderWidth = (float)Math.max(
                entity.getVisibilityBoundingBox().getXLength(),
                entity.getVisibilityBoundingBox().getZLength()
        );
        for (Entity other : entity.getPassengerList()) {
            renderWidth = Math.max(getRenderWidth(other),renderWidth);
        }
        return renderWidth;
    }

    /**
     * Recursively render everything in the entity stack rooted at this entity
     */
    private void renderMounts(Entity entity, double xOffset, double yOffset, double zOffset, float delta, EntityRenderDispatcher renderer, MatrixStack nextStack, VertexConsumerProvider.Immediate immediate) {
        boolean isBoat = (entity.getType() == EntityType.BOAT || entity.getType() == EntityType.CHEST_BOAT);
        renderer.render(
                entity,
                xOffset,
                yOffset,
                zOffset,
                isBoat ? entity.getYaw(delta) : 0,
                delta,
                nextStack,
                immediate,
                15728880
        );
        for (Entity other : entity.getPassengerList()) {
            renderMounts(
                    other,
                    xOffset + (other.getX() - entity.getX()),
                    yOffset + (other.getY() - entity.getY()),
                    zOffset + (other.getZ() - entity.getZ()),
                    delta,
                    renderer,
                    nextStack,
                    immediate
            );
        }
    }

    public void onPlayerDirectionChange(float prevPitch, float prevYaw, float pitch, float yaw) {
        if (!(boatFacing.getValue() && isInBoat(client.player))) {
            yawOffset += (yaw - prevYaw) / 2;
        }
    }

    private float mod (float a, float b) {
        return a - (float)Math.floor(a / b) * b;
    }

    public void onMountDirectionChange(float yaw) {
        if (boatFacing.getValue() && isInBoat(client.player)) {
            lastYaw = currentYaw;
            currentYaw = yaw;
            float difference = mod((currentYaw - lastYaw + 180), 360) - 180;
            yawOffset += difference / 2;
        }
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
        options.add(boatFacing);
        options.add(rotation);
        options.add(background);
        options.add(backgroundColor);
        options.add(outline);
        options.add(outlineColor);
        return options;
    }
}
