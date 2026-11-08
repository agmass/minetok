package org.agmas.minetok.client.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import org.agmas.minetok.client.MinetokClient;
import org.agmas.minetok.item.PhoneItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Entity.class)
public class CaptureMouseMixin {


    @WrapMethod(method = "turn")
    public void disableWhenWatchingCams(double dx, double dy, Operation<Void> original) {
        if (((Entity)(Object)this) instanceof LocalPlayer lp) {
            if (lp.isUsingItem() && lp.getActiveItem().getItem() instanceof PhoneItem) {
                MinetokClient.phone.mouse.x += dx;
                MinetokClient.phone.mouse.y += dy;
                return;
            }
        }
        original.call(dx,dy);
    }
}
