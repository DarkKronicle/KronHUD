package io.github.darkkronicle.kronhud.mixins;

import io.github.darkkronicle.kronhud.KronHUD;
import io.github.darkkronicle.kronhud.gui.hud.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public class MixinInGameHud {

    @Shadow private int overlayRemaining;

    @Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"), cancellable = true)
    public void renderStatusEffect(MatrixStack matrices, CallbackInfo ci) {
        PotionsHud hud = (PotionsHud) KronHUD.hudManager.get(PotionsHud.ID);
        if (hud != null && hud.isEnabled()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    public void renderCrosshair(MatrixStack matrices, CallbackInfo ci) {
        CrosshairHud hud = (CrosshairHud) KronHUD.hudManager.get(CrosshairHud.ID);
        if (hud != null && hud.isEnabled()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    public void renderScoreboard(MatrixStack matrices, ScoreboardObjective objective, CallbackInfo ci) {
        ScoreboardHud hud = (ScoreboardHud) KronHUD.hudManager.get(ScoreboardHud.ID);
        if (hud != null && hud.isEnabled()) {
            ci.cancel();
        }
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I"))
    public int getActionBar(TextRenderer instance, MatrixStack matrices, Text text, float x, float y, int color){
        ActionBarHud hud = (ActionBarHud) KronHUD.hudManager.get(ActionBarHud.ID);
        if (hud != null && hud.isEnabled()) {
                hud.setActionBar(text, color);// give us selves the correct values
        } else {
            instance.draw(matrices, text, x, y, color);
        }
        return 0;
    }

    @Inject(method = "setOverlayMessage", at = @At("TAIL"))
    public void setDuration(Text message, boolean tinted, CallbackInfo ci){
        ActionBarHud hud = (ActionBarHud) KronHUD.hudManager.get(ActionBarHud.ID);
        if (hud != null && hud.isEnabled()) {
            overlayRemaining = hud.timeShown.getIntegerValue();
        }
    }


}
