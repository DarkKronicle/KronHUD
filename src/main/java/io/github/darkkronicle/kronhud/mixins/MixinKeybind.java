package io.github.darkkronicle.kronhud.mixins;

import io.github.darkkronicle.kronhud.hooks.KronHudHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(KeyBinding.class)
public class MixinKeybind {

    @Inject(method = "setBoundKey", at = @At("RETURN"))
    public void boundKeySet(InputUtil.Key key, CallbackInfo ci) {
        KronHudHooks.KEYBIND_CHANGE.invoker().setBoundKey(key);
    }

    @Inject(method = "setPressed", at = @At("RETURN"))
    public void onPress(boolean pressed, CallbackInfo ci) {
        if (pressed) {
            KronHudHooks.KEYBIND_PRESS.invoker().onPress((KeyBinding)((Object)this));
        }
    }

}
