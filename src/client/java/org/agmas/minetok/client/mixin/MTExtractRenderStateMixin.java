

package org.agmas.minetok.client.mixin;


import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.agmas.minetok.client.MinetokClient;
import org.agmas.minetok.init.ModAttachments;
import org.agmas.minetok.porting.MTIdentifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AvatarRenderer.class)
public abstract class MTExtractRenderStateMixin {

	@Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V", at = @At("TAIL"))
	public void minetokAnimations(Avatar entity, AvatarRenderState state, float partialTicks, CallbackInfo ci) {
		if (entity instanceof Player p) {
			if (p.isLocalPlayer()) {

				state.setData(MinetokClient.phoneTextureID, MTIdentifier.of("phone_texture"));
				state.setData(MinetokClient.headMouseInfoID, MinetokClient.phone.mouse);
			} else if (MinetokClient.playerPhoneHashMap.containsKey(p.getUUID())) {
				state.setData(MinetokClient.phoneTextureID, MinetokClient.playerPhoneHashMap.get(p.getUUID()).textureIdentifier);
				state.setData(MinetokClient.headMouseInfoID, MinetokClient.playerPhoneHashMap.get(p.getUUID()).phone.mouse);
			}
		}
	}
}