package org.agmas.minetok.client.init;

import net.minecraft.core.Registry;
import org.agmas.minetok.client.api.Application;
import org.agmas.minetok.client.render.sprites.CallsApplication;
import org.agmas.minetok.client.util.MinetokClientRegistries;
import org.agmas.minetok.porting.MTIdentifier;

public class MinetokApplications {
    public static void init() {
        Registry.register(MinetokClientRegistries.APPLICATIONS, MTIdentifier.of("calls").id, new Application(CallsApplication.class));
    }
}
