package io.github.darkkronicle.kronhud.hooks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/*
Brand new to hooks! Based some of this off the unliscensed ClothAPI by shedaniel (what a legend)
https://github.com/shedaniel/cloth-api/blob/1.16/cloth-client-events-v0/src/main/java/me/shedaniel/cloth/api/client/events/v0/ClothClientHooks.java
 */

public class KronHudHooks {
    public static final Event<HudRenderCallback.Pre> HUD_RENDER_PRE = EventFactory.createArrayBacked(HudRenderCallback.Pre.class, listeners -> ((matrices, delta) -> {
        for (HudRenderCallback.Pre listener : listeners) {
            listener.render(matrices, delta);
        }
    }));

    public static final Event<MouseInputCallback> MOUSE_INPUT = EventFactory.createArrayBacked(MouseInputCallback.class, listeners -> ((window, button, action, mods) -> {
        for (MouseInputCallback listener : listeners) {
            listener.onMouseButton(window, button, action, mods);
        }
    }));
}
