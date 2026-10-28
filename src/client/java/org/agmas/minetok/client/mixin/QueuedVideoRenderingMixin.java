

package org.agmas.minetok.client.mixin;


import net.minecraft.client.Minecraft;
import org.agmas.minetok.Minetok;
import org.agmas.minetok.client.MinetokClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Minecraft.class)
public abstract class QueuedVideoRenderingMixin {

	@Inject(method = "runTick", at = @At("TAIL"))
	public void run(boolean bl, CallbackInfo ci) {
		if (MinetokClient.lastRenderFrame != null) {
			MinetokClient.lastRenderFrame.run();
			MinetokClient.lastRenderFrame = null;
		}
	}
}