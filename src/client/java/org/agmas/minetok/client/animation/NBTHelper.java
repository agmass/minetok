package org.agmas.minetok.client.animation;

import net.minecraft.nbt.CompoundTag;

public class NBTHelper {
    public static boolean getBooleanOr(CompoundTag tag, String name, boolean or) {
        //? if >1.21.1
        return tag.getBooleanOr(name, or);
        //?if <=1.21.1
        //return tag.contains(name) ? tag.getBoolean(name) : or;
    }

    public static String getStringOr(CompoundTag tag, String name, String or) {
        //? if >1.21.1
        return tag.getStringOr(name, or);
        //?if <=1.21.1
        //return tag.contains(name) ? tag.getString(name) : or;
    }

    public static float getFloatOr(CompoundTag tag, String name, float or) {
        //? if >1.21.1
        return tag.getFloatOr(name, or);
        //?if <=1.21.1
        //return tag.contains(name) ? tag.getFloat(name) : or;
    }

    public static CompoundTag getCompoundOrEmpty(CompoundTag tag, String name) {
        //? if >1.21.1
        return tag.getCompoundOrEmpty(name);
        //?if <=1.21.1
        //return tag.contains(name) ? tag.getCompound(name) : new CompoundTag();
    }

    public static CompoundTag getCompound(CompoundTag tag, String name) {
        //? if >1.21.1
        return tag.getCompound(name).get();
        //?if <=1.21.1
        //return tag.getCompound(name);
    }
}
