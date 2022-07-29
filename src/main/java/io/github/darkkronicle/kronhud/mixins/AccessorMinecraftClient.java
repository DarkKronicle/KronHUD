package io.github.darkkronicle.kronhud.mixins;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftClient.class)
@Environment(EnvType.CLIENT)
public interface AccessorMinecraftClient {

    @Accessor
    static int getCurrentFps() {
        return 0;
    }

}
