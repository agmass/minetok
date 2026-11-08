package org.agmas.minetok.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
//? if >1.21.1 {
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
//? } else {
/*import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
*///? }
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.agmas.minetok.client.MinetokClient;
import org.agmas.minetok.client.render.Phone;
import org.agmas.minetok.init.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandLayer.class)
public class PhoneInOtherPlayerHandRendererMixin {


	//? if >1.21.1 {
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
	//? } else {
	/*@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"), method = "renderArmWithItem")
	private void left(LivingEntity livingEntity, ItemStack itemStack, ItemDisplayContext itemDisplayContext, HumanoidArm humanoidArm, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
		if (livingEntity instanceof Player state) {
			if (itemStack.is(ModItems.PHONE) && Phone.getTextureForPlayer(state) != MinetokClient.missingId) {
				poseStack.pushPose();
				MinetokClient.positionPhoneInThirdPersonHand(poseStack);
				int fullbright = 15728880;
				VertexConsumer consumer = multiBufferSource.getBuffer(MinetokClient.getPhoneRenderType(Phone.getTextureForPlayer(state)));
				consumer.addVertex(poseStack.last(), 0,0,0).setUv(0,0).setLight(fullbright).setNormal(1,1,1).setUv2(0,0).setColor(-1);
				consumer.addVertex(poseStack.last(), 1,0,0).setUv(1,0).setLight(fullbright).setNormal(1,1,1).setUv2(0,0).setColor(-1);
				consumer.addVertex(poseStack.last(), 1,1.777777778f,0).setUv(1,1).setLight(fullbright).setNormal(1,1,1).setUv2(0,0).setColor(-1);
				consumer.addVertex(poseStack.last(), 0,1.777777778f,0).setUv(0,1).setLight(fullbright).setNormal(1,1,1).setUv2(0,0).setColor(-1);
				poseStack.popPose();
			}
		}
	}
	*///? }
}