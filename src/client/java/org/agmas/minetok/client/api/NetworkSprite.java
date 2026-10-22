package org.agmas.minetok.client.api;

import net.minecraft.nbt.CompoundTag;import net.minecraft.world.level.storage.ValueInput;

public interface NetworkSprite {
    CompoundTag createTag();

    void sync(CompoundTag phoneData);
}
