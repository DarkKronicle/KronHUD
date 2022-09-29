package io.github.darkkronicle.kronhud.mixins;


import com.mojang.datafixers.util.Pair;
import io.github.darkkronicle.kronhud.gui.ShaderHandler;
import net.minecraft.client.gl.Program;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Shader;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.resource.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Inject(method = "loadShaders", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;clearShaders()V"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void registerShaders(
            ResourceManager resourceManager, CallbackInfo ci, List<Program> programList, List<Pair<Shader, Consumer<Shader>>> shaderList
    ) {
        // https://github.com/BillyGalbreath/FabricTest/blob/master/src/main/java/test/fabrictest/mixin/GameRendererMixin.java
        List<Pair<Shader, Consumer<Shader>>> extraShaderList = new ArrayList<>();
        try {
            extraShaderList.add(Pair.of(new Shader(resourceManager, "rendertype_chroma", VertexFormats.POSITION_COLOR),
                    shader -> ShaderHandler.getInstance().setChromaColor(shader)
            ));
        } catch (IOException e) {
//            extraShaderList.forEach(pair -> pair.getFirst().close());
            throw new RuntimeException("could not reload shaders", e);
        }
        shaderList.addAll(extraShaderList);
    }

}
