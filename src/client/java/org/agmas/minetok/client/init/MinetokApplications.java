package org.agmas.minetok.client.init;

import net.minecraft.core.Registry;
import org.agmas.minetok.client.api.Application;
import org.agmas.minetok.client.render.sprites.calls.CallsApplicationSprite;
import org.agmas.minetok.client.render.sprites.minetok.TikTokApplication;
import org.agmas.minetok.client.render.sprites.settings.SettingsApplicationSprite;
import org.agmas.minetok.client.util.MinetokClientRegistries;
import org.agmas.minetok.porting.MTIdentifier;

public class MinetokApplications {
    public static void init() {
        Registry.register(MinetokClientRegistries.APPLICATIONS, MTIdentifier.of("calls").id, new Application(CallsApplicationSprite.class));
        Registry.register(MinetokClientRegistries.APPLICATIONS, MTIdentifier.of("settings").id, new Application(SettingsApplicationSprite.class));
        Registry.register(MinetokClientRegistries.APPLICATIONS, MTIdentifier.of("minetok").id, new Application(TikTokApplication.class));
    }
}
