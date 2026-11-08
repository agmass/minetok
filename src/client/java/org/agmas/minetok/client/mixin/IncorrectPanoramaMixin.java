package org.agmas.minetok.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
//? if <=1.21.1 {
/*import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.renderer.CubeMap;
*///? }
import net.minecraft.client.renderer.CubeMap;
import org.agmas.minetok.client.MinetokClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CubeMap.class)
public class IncorrectPanoramaMixin {

    //? if <=1.21.1 {
    /*@WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/platform/Window;getHeight()I"
            )
    )
    public int changeWidth(Window instance, Operation<Integer> original) {
        if (MinetokClient.currentlyRendering_phone != null) {
            return 1280;
        }
        return original.call(instance);
    }
    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/platform/Window;getWidth()I"
            )
    )
    public int changeHeight(Window instance, Operation<Integer> original) {
        if (MinetokClient.currentlyRendering_phone != null) {
            return 720;
        }
        return original.call(instance);
    }
    *///? }
}
