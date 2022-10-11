package io.github.darkkronicle.kronhud.mixins;

import io.github.darkkronicle.kronhud.gui.HudManager;
import io.github.darkkronicle.kronhud.gui.hud.ToggleSprintHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(KeyboardInput.class)
public abstract class MixinKeyboardInput {

    /**
     * @param instance The sneak key
     * @return boolean whether the player should be sneaking or not
     * @author moehreag
     */
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z", ordinal = 5))
    public boolean toggleSneak(KeyBinding instance) {
        ToggleSprintHud hud = (ToggleSprintHud) HudManager.getInstance().get(ToggleSprintHud.ID);
        return hud.isEnabled() && hud.getSneakToggled().getValue() && MinecraftClient.getInstance().currentScreen == null || instance.isPressed();
    }
}
