package io.github.darkkronicle.kronhud.hooks;

import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;

public final class KeyBindingCallback {

    public interface ChangeBind {
        void setBoundKey(InputUtil.Key boundKey);
    }

    public interface OnPress {
        void onPress(KeyBinding binding);
    }
}
