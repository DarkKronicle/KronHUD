package io.github.darkkronicle.kronhud.mixins;

import io.github.darkkronicle.kronhud.KronHUD;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossBarHud.class)
public class MixinBossBarHud {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(MatrixStack matrices, CallbackInfo ci) {
        io.github.darkkronicle.kronhud.gui.hud.BossBarHud hud = (io.github.darkkronicle.kronhud.gui.hud.BossBarHud) KronHUD.hudManager.get(io.github.darkkronicle.kronhud.gui.hud.BossBarHud.ID);
        if (hud != null && hud.isEnabled()) {
            ci.cancel();
        }
    }
}
