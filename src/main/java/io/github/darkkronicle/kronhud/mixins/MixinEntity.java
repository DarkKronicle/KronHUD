package io.github.darkkronicle.kronhud.mixins;

import io.github.darkkronicle.kronhud.hooks.KronHudHooks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Entity.class)
public abstract class MixinEntity {

    @Shadow public abstract float getPitch();

    @Shadow public abstract float getYaw();

    @Shadow public abstract List<Entity> getPassengerList();

    @Inject(method = "changeLookDirection", at = @At("HEAD"))
    private void updateLookDirection(double mouseDeltaX, double mouseDeltaY, CallbackInfo ci) {
        if (mouseDeltaX == 0 && mouseDeltaY == 0) {
            return;
        }

        float prevPitch = getPitch();
        float prevYaw = getYaw();
        float pitch = prevPitch + (float) (mouseDeltaY * .15);
        float yaw = prevYaw + (float) (mouseDeltaX * .15);
        pitch = MathHelper.clamp(pitch, -90.0F, 90.0F);
        KronHudHooks.PLAYER_DIRECTION_CHANGE.invoker().onChange(prevPitch, prevYaw, pitch, yaw);
    }

    @Inject(method = "setYaw", at = @At("TAIL"))
    private void updateEntityYaw(float yaw, CallbackInfo ci) {
        if (this.getPassengerList().contains(MinecraftClient.getInstance().player)) {
            KronHudHooks.MOUNT_DIRECTION_CHANGE.invoker().onChange(yaw);
        }
    }

}
