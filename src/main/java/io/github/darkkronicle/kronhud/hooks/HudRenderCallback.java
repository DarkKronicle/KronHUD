package io.github.darkkronicle.kronhud.hooks;

import net.minecraft.client.util.math.MatrixStack;

public final class HudRenderCallback {

    private HudRenderCallback() {}

    public interface Pre {
        void render(MatrixStack matrices, float delta);
    }
}
