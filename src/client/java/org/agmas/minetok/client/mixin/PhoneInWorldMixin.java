package org.agmas.minetok.client.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
//? if >=26.3 {
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
//? }
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.util.EasingType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.agmas.minetok.client.MinetokClient;
import org.agmas.minetok.init.ModItems;
import org.agmas.minetok.item.PhoneItem;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FirstPersonHandsAndItemsRenderer.class)
public abstract class PhoneInWorldMixin {


    //? if <26.3 {
    /*@Shadow
    protected abstract void renderPlayerArm(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int i, float f, float g, HumanoidArm humanoidArm);
    @Shadow
    @Final
    private ItemModelResolver itemModelResolver;
    *///? } else {
    @Shadow
    protected abstract void renderPlayerArm(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, float inverseArmHeight, float attackValue, HumanoidArm arm, PlayerRenderState playerState);
    //? }

    //? if <26.3 {
    /*//? if <26.2
    //@WrapMethod(method = "renderArmWithItem")
    //? if >=26.2
    @WrapMethod(method = "submitArmWithItem")
    public void disableWhenWatchingCams(AbstractClientPlayer abstractClientPlayer, float f, float g, InteractionHand interactionHand, float h, ItemStack itemStack, float i, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int j, Operation<Void> original) {
        if (abstractClientPlayer.isUsingItem() && abstractClientPlayer.getActiveItem().getItem() instanceof PhoneItem && abstractClientPlayer.getUsedItemHand().equals(interactionHand)) {
            poseStack.pushPose();
            poseStack.translate(0.15f,0,0);
            poseStack.pushPose();
            renderPlayerArm(poseStack,submitNodeCollector,j,i,abstractClientPlayer.getMainArm().equals(HumanoidArm.LEFT) ? h : 0,HumanoidArm.LEFT);
            poseStack.popPose();
            poseStack.popPose();

            poseStack.pushPose();
            poseStack.translate(-0.15f,0,0);
            renderPlayerArm(poseStack,submitNodeCollector,j,i,abstractClientPlayer.getMainArm().equals(HumanoidArm.RIGHT) ? h : 0,HumanoidArm.RIGHT);
            poseStack.popPose();

            ItemStackRenderState itemStackRenderState = new ItemStackRenderState();
            itemModelResolver.updateForTopItem(itemStackRenderState, itemStack, ItemDisplayContext.NONE, abstractClientPlayer.level(), abstractClientPlayer, abstractClientPlayer.getId());
            MinetokClient.phone.renderInHand(poseStack, submitNodeCollector, itemStackRenderState,j,abstractClientPlayer.getTicksUsingItem(f));
            return;
        }
        original.call(abstractClientPlayer,f,g,interactionHand,h,itemStack,i,poseStack,submitNodeCollector,j);
    }
     *///? } else if >=26.3 {
    @WrapMethod(method = "submitArmWithItem")
    public void disableWhenWatchingCams(PlayerRenderState playerState, FirstPersonHandsAndItemsRenderState state, float partialTicks, float xRot, InteractionHand hand, float attack, ItemStack itemStack, float inverseArmHeight, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, Operation<Void> original) {
        if (playerState.avatarRenderState.isUsingItem && playerState.avatarRenderState.getUseItemStackForArm(playerState.avatarRenderState.useItemHand.asArm(playerState.avatarRenderState.mainArm)).getItem() instanceof PhoneItem && hand.equals(playerState.avatarRenderState.useItemHand)) {
            poseStack.pushPose();
            poseStack.translate(0.15f,0,0);
            renderPlayerArm(poseStack,submitNodeCollector,lightCoords,inverseArmHeight,0,HumanoidArm.LEFT,playerState);
            poseStack.popPose();
            poseStack.pushPose();
            poseStack.translate(-0.15f,0,0);
            renderPlayerArm(poseStack,submitNodeCollector,lightCoords,inverseArmHeight,attack,HumanoidArm.RIGHT,playerState);
            poseStack.popPose();

            ItemStackRenderState itemStackRenderState = new ItemStackRenderState();
            Minecraft.getInstance().getItemModelResolver().updateForTopItem(itemStackRenderState, itemStack, ItemDisplayContext.NONE,  null,null,0);
            MinetokClient.phone.renderInHand(poseStack, submitNodeCollector, itemStackRenderState,lightCoords, playerState.avatarRenderState.useItemHand == InteractionHand.MAIN_HAND ? state.mainHandUseDuration : state.offHandUseDuration);
            return;
        }
        original.call(playerState, state, partialTicks, xRot, hand, attack, itemStack, inverseArmHeight, poseStack, submitNodeCollector, lightCoords);
    }
    //? }
}
