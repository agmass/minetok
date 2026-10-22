package org.agmas.minetok.client.mixin;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.InteractionHand;
import org.agmas.minetok.client.animation.PhonePoser;
import org.agmas.minetok.init.ModItems;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public class ThirdPersonPhonePoseMixin {
	@Shadow
	@Final
	public ModelPart rightArm;

	@Shadow
	@Final
	public ModelPart leftArm;

	@Shadow
	@Final
	public ModelPart head;

	@Inject(at = @At("TAIL"), method = "poseRightArm")
	private void right(HumanoidRenderState state, CallbackInfo info) {
		if (state.isUsingItem) {
			if (state.getUseItemStackForArm(state.useItemHand == InteractionHand.MAIN_HAND ? state.mainArm : state.mainArm.getOpposite()).is(ModItems.PHONE)) {
				PhonePoser.poseMainArm(rightArm, state.ticksUsingItem);
			}
		}
	}

	@Inject(at = @At("TAIL"), method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V")
	private void left(HumanoidRenderState state, CallbackInfo info) {
		if (state.isUsingItem) {
			if (state.getUseItemStackForArm(state.useItemHand == InteractionHand.MAIN_HAND ? state.mainArm : state.mainArm.getOpposite()).is(ModItems.PHONE)) {
				PhonePoser.poseHeadDown(head);
			}
		}
	}
}