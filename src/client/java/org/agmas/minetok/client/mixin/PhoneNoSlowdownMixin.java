package org.agmas.minetok.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.player.LocalPlayer;
import org.agmas.minetok.init.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LocalPlayer.class)
public class PhoneNoSlowdownMixin {
    //? if <=1.21.1 {

    @WrapOperation(
            method = {"aiStep"},
            at = {@At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z"
            )}
    )
    public boolean noPhoneSlowdown(LocalPlayer instance, Operation<Boolean> original) {
        boolean f = original.call(instance);
        if (f && ((LocalPlayer)(Object)this).getUseItem().is(ModItems.PHONE)) {
            return false;
        }
        return f;
    }
    //? }
}
