package io.github.darkkronicle.kronhud.hooks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/*
Brand new to hooks! Based some of this off the unliscensed ClothAPI by shedaniel (what a legend)
https://github.com/shedaniel/cloth-api/blob/1.16/cloth-client-events-v0/src/main/java/me/shedaniel/cloth/api/client/events/v0/ClothClientHooks.java
 */

public class KronHudHooks {

    public static final Event<MouseInputCallback> MOUSE_INPUT = EventFactory.createArrayBacked(MouseInputCallback.class, listeners -> ((window, button, action, mods) -> {
        for (MouseInputCallback listener : listeners) {
            listener.onMouseButton(window, button, action, mods);
        }
    }));

    public static final Event<KeyBindingCallback.ChangeBind> KEYBIND_CHANGE = EventFactory.createArrayBacked(KeyBindingCallback.ChangeBind.class, listeners -> ((key) -> {
        for (KeyBindingCallback.ChangeBind listener : listeners) {
            listener.setBoundKey(key);
        }
    }));

    public static final Event<KeyBindingCallback.OnPress> KEYBIND_PRESS = EventFactory.createArrayBacked(KeyBindingCallback.OnPress.class, listeners -> ((key) -> {
        for (KeyBindingCallback.OnPress listener : listeners) {
            listener.onPress(key);
        }
    }));
}
