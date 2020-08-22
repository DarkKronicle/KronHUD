package io.github.darkkronicle.kronhud.mixins;

import io.github.darkkronicle.kronhud.KronHUD;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
@Environment(EnvType.CLIENT)
public class MixinMinecraftClient {

    // For some unknown reason mc decided to make fps private... why?
    @Shadow private static int currentFps;

    @Inject(method = "tick", at = @At("HEAD"))
    public void fpsTick(CallbackInfo ci) {
        KronHUD.fps = currentFps;
    }

}
