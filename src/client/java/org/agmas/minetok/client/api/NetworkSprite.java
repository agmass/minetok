package org.agmas.minetok.client.api;

import net.minecraft.nbt.CompoundTag;

public interface NetworkSprite {
    CompoundTag createTag();

    void sync(CompoundTag phoneData);
}
