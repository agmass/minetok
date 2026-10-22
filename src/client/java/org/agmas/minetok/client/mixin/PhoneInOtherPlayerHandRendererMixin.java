package org.agmas.minetok.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.agmas.minetok.client.MinetokClient;
import org.agmas.minetok.client.animation.PhonePoser;
import org.agmas.minetok.init.ModItems;
import org.agmas.minetok.porting.MTIdentifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandLayer.class)
public class PhoneInOtherPlayerHandRendererMixin {

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V"), method = "submitArmWithItem")
	private void left(ArmedEntityRenderState armedEntityRenderState, ItemStackRenderState item, ItemStack itemStack, HumanoidArm arm, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, CallbackInfo ci) {
		if (armedEntityRenderState instanceof AvatarRenderState state) {
			if (itemStack.is(ModItems.PHONE) && state.getDataOrDefault(MinetokClient.phoneTextureID, MinetokClient.missingId) != MinetokClient.missingId) {
				poseStack.pushPose();
				MinetokClient.positionPhoneInThirdPersonHand(poseStack);
				int fullbright = 15728880;
				submitNodeCollector.submitCustomGeometry(poseStack, MinetokClient.getPhoneRenderType(state.getData(MinetokClient.phoneTextureID)), ((pose, vertexConsumer) -> {
					vertexConsumer.addVertex(pose, 0,0,0).setUv(0,0).setLight(fullbright).setNormal(1,1,1).setUv2(0,0).setColor(-1);
					vertexConsumer.addVertex(pose, 1,0,0).setUv(1,0).setLight(fullbright).setNormal(1,1,1).setUv2(0,0).setColor(-1);
					vertexConsumer.addVertex(pose, 1,1.777777778f,0).setUv(1,1).setLight(fullbright).setNormal(1,1,1).setUv2(0,0).setColor(-1);
					vertexConsumer.addVertex(pose, 0,1.777777778f,0).setUv(0,1).setLight(fullbright).setNormal(1,1,1).setUv2(0,0).setColor(-1);
				}));
				poseStack.popPose();
			}
		}
	}
}