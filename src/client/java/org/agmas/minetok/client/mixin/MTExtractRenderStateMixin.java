

package org.agmas.minetok.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.client.Minecraft;
//? if >1.21.1 {
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.player.Player;
import org.agmas.minetok.client.MinetokClient;
import org.agmas.minetok.client.render.Phone;
import org.agmas.minetok.porting.MTID;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AvatarRenderer.class)
public abstract class MTExtractRenderStateMixin {

	@Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V", at = @At("TAIL"))
	public void minetokAnimations(Avatar entity, AvatarRenderState state, float partialTicks, CallbackInfo ci) {
		if (entity instanceof Player p) {
			state.setData(MinetokClient.phoneTextureID, Phone.getTextureForPlayer(p));
			state.setData(MinetokClient.headMouseInfoID, Phone.getForPlayer(p).mouse);
		}
	}
}
//? } else {
/*@Mixin(value = Minecraft.class)
public abstract class MTExtractRenderStateMixin {
}
*///? }