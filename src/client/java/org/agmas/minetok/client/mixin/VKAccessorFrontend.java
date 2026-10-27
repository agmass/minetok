package org.agmas.minetok.client.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;

//? if =26.2 {

/*import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.CommandEncoderBackend;
@Mixin(CommandEncoder.class)
public interface VKAccessorFrontend {
    @Accessor("backend")
    public CommandEncoderBackend minetok$backend();
}
*///? } else {
@Mixin(Minecraft.class)
public interface VKAccessorFrontend {
}
//? }
