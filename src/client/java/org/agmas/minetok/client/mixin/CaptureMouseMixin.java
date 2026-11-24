package org.agmas.minetok.client.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec2;
import org.agmas.minetok.client.MinetokClient;
import org.agmas.minetok.item.PhoneItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MouseHandler.class)
public class CaptureMouseMixin {


    @Shadow
    private double accumulatedDX;

    @Shadow
    private double accumulatedDY;

    @WrapMethod(method = "turnPlayer")
    public void disableWhenWatchingCams(double d, Operation<Void> original) {
        if (Minecraft.getInstance().player instanceof LivingEntity lp) {
            if (lp.isUsingItem() && lp.getActiveItem().getItem() instanceof PhoneItem) {
                MinetokClient.phone.mouse.x += (float) accumulatedDX;
                MinetokClient.phone.mouse.y += (float) accumulatedDY;
                return;
            }
        }
        original.call(d);
    }
}
