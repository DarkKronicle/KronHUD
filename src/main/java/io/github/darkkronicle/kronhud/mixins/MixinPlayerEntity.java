package io.github.darkkronicle.kronhud.mixins;

import io.github.darkkronicle.kronhud.gui.HudManager;
import io.github.darkkronicle.kronhud.gui.hud.simple.ComboHud;
import io.github.darkkronicle.kronhud.gui.hud.simple.ReachHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends Entity {

    private MixinPlayerEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    // Source moehreag
    // https://github.com/AxolotlClient/AxolotlClient-mod/blob/4ae2678bfe9e0908be1a7a34e61e689c8005ae0a/src/main/java/io/github/axolotlclient/mixin/PlayerEntityMixin.java#L27-L35
    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getAttributeValue(Lnet/minecraft/entity/attribute/EntityAttribute;)D"))
    private void getReach(Entity entity, CallbackInfo ci){
        // This is only ever called when the client attacks. Without more work not possible to get when someone else attacks.
        MinecraftClient mc = MinecraftClient.getInstance();
        assert mc != null;
        assert mc.player != null;

        if (getId() == mc.player.getId() && entity != null){
            if (mc.crosshairTarget != null) {
                ReachHud reachDisplayHud = (ReachHud) HudManager.getInstance().get(ReachHud.ID);
                reachDisplayHud.updateDistance(this.getCameraPosVec(1).distanceTo(mc.crosshairTarget.getPos()));
            }
            ComboHud comboHud = (ComboHud) HudManager.getInstance().get(ComboHud.ID);
            comboHud.onEntityAttack(entity);
        }
    }

}
