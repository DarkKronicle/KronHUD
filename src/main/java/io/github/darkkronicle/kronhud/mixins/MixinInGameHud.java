package io.github.darkkronicle.kronhud.mixins;

import io.github.darkkronicle.kronhud.KronHUD;
import io.github.darkkronicle.kronhud.gui.hud.CrossHairHud;
import io.github.darkkronicle.kronhud.hooks.KronHudHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public class MixinInGameHud {
    @Inject(method = "render", at = @At("HEAD"))
    public void render(MatrixStack matrices, float delta, CallbackInfo ci) {
        KronHudHooks.HUD_RENDER_PRE.invoker().render(matrices, delta);
    }

    @Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"), cancellable = true)
    public void renderStatusEffect(MatrixStack matrices, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "renderVignetteOverlay", at = @At("HEAD"), cancellable = true)
    public void renderVignette(Entity entity, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    public void renderCrosshair(MatrixStack matrices, CallbackInfo ci) {
        CrossHairHud hud = (CrossHairHud) KronHUD.hudManager.get(CrossHairHud.ID);
        if (hud != null && hud.isEnabled()) {
            ci.cancel();
        }
    }
}
