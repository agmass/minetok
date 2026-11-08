package org.agmas.minetok.client.mixin;

import net.minecraft.client.Minecraft;
import org.agmas.minetok.init.ModItems;
import org.agmas.minetok.porting.MTID;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

//? if <1.21.11 {
/*import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelIdentifier;
@Mixin(ModelBakery.class)
public abstract class ChangePhoneModelLoaderMixin {
    @Shadow
    protected abstract void loadSpecialItemModelAndDependencies(ModelIdentifier par1);

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/ModelBakery;loadSpecialItemModelAndDependencies(Lnet/minecraft/client/resources/model/ModelIdentifier;)V", ordinal = 0))
    public void b(BlockColors blockColors, ProfilerFiller profilerFiller, Map map, Map map2, CallbackInfo ci) {
        loadSpecialItemModelAndDependencies(ModelIdentifier.inventory(MTID.of("phone_empty").id));
    }
}
*///? } else {
@Mixin(Minecraft.class)
public class ChangePhoneModelLoaderMixin {
}
//? }
