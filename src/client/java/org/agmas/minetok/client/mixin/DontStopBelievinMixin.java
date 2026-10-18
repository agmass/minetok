package org.agmas.minetok.client.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import org.agmas.minetok.client.MinetokClient;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MultiPlayerGameMode.class)
public class DontStopBelievinMixin {

    @WrapMethod(method = "releaseUsingItem")
    public void disableWhenWatchingCams(Player player, Operation<Void> original) {
        if (!MinetokClient.phone.captured) {
            original.call(player);
        }
    }
}
