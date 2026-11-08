package org.agmas.minetok.client.mixin;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
//? if >1.21.1
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.agmas.minetok.Minetok;
import org.agmas.minetok.api.PhoneTapInformation;
import org.agmas.minetok.client.MinetokClient;
import org.agmas.minetok.client.animation.PhonePoser;
import org.agmas.minetok.client.render.Phone;
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


	//~ !identify
	//? if >1.21.1 {
	@Inject(at = @At("TAIL"), method = "poseRightArm")
	private void right(HumanoidRenderState state, CallbackInfo info) {
		if (state.isUsingItem) {
			if (state.getUseItemStackForArm(HumanoidArm.RIGHT).is(ModItems.PHONE)) {
				PhonePoser.poseMainArm(rightArm, state.ticksUsingItem);
			}
		}
	}
	@Inject(at = @At("TAIL"), method = "poseLeftArm")
	private void left(HumanoidRenderState state, CallbackInfo info) {
		if (state.isUsingItem) {
			if (state.getUseItemStackForArm(HumanoidArm.LEFT).is(ModItems.PHONE)) {
				PhonePoser.poseMainArm(leftArm, state.ticksUsingItem);
			}
		}
	}

	@Inject(at = @At("TAIL"), method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V")
	private void head(HumanoidRenderState state, CallbackInfo info) {
		if (state.isUsingItem) {
			if (state.getUseItemStackForArm(state.useItemHand == InteractionHand.MAIN_HAND ? state.mainArm : state.mainArm.getOpposite()).is(ModItems.PHONE)) {
				PhonePoser.poseHeadDown(head, state.getDataOrDefault(MinetokClient.headMouseInfoID, new PhoneTapInformation()), state.ageInTicks % 1, state.getUseItemStackForArm(HumanoidArm.RIGHT).is(ModItems.PHONE));
			}
		}
	}
	//? } else {
	/*//~ identify
	@Inject(at = @At("TAIL"), method = "poseRightArm")
	private void right(LivingEntity state, CallbackInfo ci) {
		if (state.isUsingItem()) {
			if (state.getActiveItem().is(ModItems.PHONE)) {
				PhonePoser.poseMainArm(rightArm, 0);
			}
		}
	}
	@Inject(at = @At("TAIL"), method = "poseLeftArm")
	private void left(LivingEntity state, CallbackInfo ci) {
		if (state.isUsingItem()) {
			if (state.getActiveItem().is(ModItems.PHONE)) {
				PhonePoser.poseMainArm(leftArm, 0);
			}
		}
	}

	@Inject(at = @At("TAIL"), method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V")
	private void head(LivingEntity state, float f, float g, float h, float i, float j, CallbackInfo ci) {
		if (state instanceof Player p) {
			if (state.isUsingItem()) {
				if (state.getActiveItem().is(ModItems.PHONE)) {
					PhonePoser.poseHeadDown(head, Phone.getForPlayer(p).mouse, f, state.getActiveItem().is(ModItems.PHONE));
				}
			}
		}
	}
	*///? }
}