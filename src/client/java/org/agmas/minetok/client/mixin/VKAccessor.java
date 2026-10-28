package org.agmas.minetok.client.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;

//? if >=26.2 {

//? if <26.3
import com.mojang.blaze3d
//? if >=26.3
//import com.mojang.renderpearl.backend
        .vulkan.VulkanCommandEncoder;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(VulkanCommandEncoder.class)
public interface VKAccessor {
    @Invoker("commandBuffer")
    public VkCommandBuffer minetok$commandBuffer();

    @Invoker("memoryBarrier")
    void minetok$memoryBarrier(MemoryStack stack);
}
//? } else {
/*@Mixin(Minecraft.class)
public interface VKAccessor {
}
*///? }
