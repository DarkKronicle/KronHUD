package io.github.darkkronicle.kronhud.mixins;

import io.github.darkkronicle.kronhud.gui.HudManager;
import io.github.darkkronicle.kronhud.gui.hud.ElytraHud;
import io.github.darkkronicle.kronhud.gui.hud.ToggleSprintHud;
import net.minecraft.block.AirBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity {
    @Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z"))
    private void alwaysPressed(CallbackInfo info) {
        ToggleSprintHud hud = (ToggleSprintHud) HudManager.getInstance().get(ToggleSprintHud.ID);
        MinecraftClient mc = MinecraftClient.getInstance();
        mc.player.setSprinting(hud.getSprintToggled().getValue() || mc.options.sprintKey.isPressed());
    }

    @Inject(method = "tickMovement", at = @At(value = "HEAD"))
    private void speedGetter(CallbackInfo ci) {
        ElytraHud elytraHud = (ElytraHud) HudManager.getInstance().get(ElytraHud.ID);
        MinecraftClient mc = MinecraftClient.getInstance();
        assert mc.player != null;
        assert mc.world != null;
        Vec3d playerVel = mc.player.getVelocity();
        Vec3d speed = new Vec3d(playerVel.x, mc.player.isOnGround() && playerVel.y<0 ? 0 : playerVel.y, playerVel.z);
        BlockPos playerBlockPos = mc.player.getBlockPos();


        for (int i = playerBlockPos.getY(); i>mc.world.getBottomY(); i--) {
            if (!(mc.world.getBlockState(new BlockPos(playerBlockPos.getX(), i, playerBlockPos.getZ())).getBlock() instanceof AirBlock)) {
                elytraHud.updateSpeed(speed, mc.player.isFallFlying(), mc.player.getPos().getY() - i);
                return;
            }
        }
        elytraHud.updateSpeed(speed, mc.player.isFallFlying(), mc.player.getPos().getY() - mc.world.getBottomY());
    }
}
