package org.agmas.minetok.client.api;

import net.minecraft.resources.Identifier;

public class Application {
    public Class<? extends Sprite> initializerSprite;

    public Application(Class<? extends Sprite> initializerSprite) {
        this.initializerSprite = initializerSprite;
    }

    public boolean allowedToBeShown() {
        return true;
    }
}
