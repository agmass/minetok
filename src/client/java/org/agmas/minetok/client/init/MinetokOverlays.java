package org.agmas.minetok.client.init;

import net.minecraft.core.Registry;
import org.agmas.minetok.client.api.Application;
import org.agmas.minetok.client.render.sprites.NotificationSprite;
import org.agmas.minetok.client.render.sprites.calls.CallingSprite;
import org.agmas.minetok.client.render.sprites.selectionOverlays.SoundSelectorOverlay;
import org.agmas.minetok.client.util.MinetokClientRegistries;
import org.agmas.minetok.porting.MTIdentifier;

public class MinetokOverlays {
    public static Application CALLING;
    public static Application NOTIFICATION;
    public static Application SOUND_SELECTOR;

    public static void init() {
        CALLING = Registry.register(MinetokClientRegistries.OVERLAYS, MTIdentifier.of("calling").id, new Application(CallingSprite.class));
        NOTIFICATION = Registry.register(MinetokClientRegistries.OVERLAYS, MTIdentifier.of("notification").id, new Application(NotificationSprite.class));
        SOUND_SELECTOR = Registry.register(MinetokClientRegistries.OVERLAYS, MTIdentifier.of("sound_selector").id, new Application(SoundSelectorOverlay.class));
    }
}
