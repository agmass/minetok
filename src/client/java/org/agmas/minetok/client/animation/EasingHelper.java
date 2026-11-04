package org.agmas.minetok.client.animation;

import net.minecraft.util.Mth;

/*
    Yes, This is just a cut down version of EasingType.
    Used because EasingType is a version-dependant class. Thanks mojang
 */
public class EasingHelper {

    public static float outCirc(final float x) {
        return (float)Math.sqrt((double)(1.0F - Mth.square(x - 1.0F)));
    }

    public static float outQuad(final float x) {
        return 1.0F - Mth.square(1.0F - x);
    }

}
