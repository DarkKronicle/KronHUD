package io.github.darkkronicle.kronhud.mixins;

import io.github.darkkronicle.kronhud.gui.hud.HudManager;
import io.github.darkkronicle.kronhud.gui.hud.ToggleSprintHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity {

    /**
     * @param sprintKey the sprint key that the user has bound
     * @return whether the user should try to sprint
     * @author DragonEggBedrockBreaking
     * @license MPL-2.0
     */
    @Redirect(
            method = "tickMovement",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z"
            )
    )
    private boolean alwaysPressed(KeyBinding sprintKey) {
        ToggleSprintHud hud = (ToggleSprintHud) HudManager.getInstance().get(ToggleSprintHud.ID);
        return hud.getSprintToggled().getValue() || sprintKey.isPressed();
    }
}
