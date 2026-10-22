package org.agmas.minetok.client.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
//? if >=26.2
//import net.minecraft.client.gui.Hud;
import net.minecraft.world.entity.player.Player;
import org.agmas.minetok.init.ModItems;
import org.agmas.minetok.item.PhoneItem;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

//? if >=26.2
//@Mixin(Hud.class)
//? if <26.2
@Mixin(Gui.class)
public abstract class PhoneNoCrosshairMixin {

    @Shadow
    protected abstract @Nullable Player getCameraPlayer();

    //? if <26.1
    @WrapMethod(method = "renderCrosshair")
    //? if >=26.1
    //@WrapMethod(method = "extractCrosshair")
    public void disableWhenWatchingCams(GuiGraphics guiGraphics, DeltaTracker deltaTracker, Operation<Void> original) {
        if (getCameraPlayer() != null) {
            if (getCameraPlayer().isUsingItem() && getCameraPlayer().getActiveItem().getItem() instanceof PhoneItem) {
                return;
            }
        }
        original.call(guiGraphics,deltaTracker);
    }
}
