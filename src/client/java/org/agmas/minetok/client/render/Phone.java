package org.agmas.minetok.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.agmas.minetok.client.MinetokClient;
import org.agmas.minetok.client.api.PhoneTapInformation;
import org.agmas.minetok.client.render.sprites.HomeScreenSprite;
import org.agmas.minetok.client.render.sprites.calls.CallingSprite;
import org.agmas.minetok.init.ModAttachments;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class Phone {

    public static int SCREEN_WIDTH = 720;
    public static int SCREEN_HEIGHT = 1280;

    public GuiEventListener selectedElement = null;
    public boolean captured = false;
    public HomeScreenSprite topSprite = new HomeScreenSprite();

    public PhoneTapInformation mouse = new PhoneTapInformation();
    public Player player;
    @Nullable CallingSprite callingSprite = null;

    private static final Identifier CROSSHAIR_SPRITE = Identifier.withDefaultNamespace("hud/crosshair");

    public Phone(Player player) {
        this.player = player;
    }

    public void render(GuiGraphicsExtractor graphics) {
        handleMouseInput();

        if (player.hasAttached(ModAttachments.RINGING_ATTACHMENT_TYPE)) {

        }

        topSprite.render(graphics,mouse, Minecraft.getInstance().getDeltaTracker().getRealtimeDeltaTicks());

        graphics.blitSprite(RenderPipelines.CROSSHAIR, CROSSHAIR_SPRITE, (int)mouse.x, (int)mouse.y, 80, 80);
    }

    public void handleMouseInput() {
        if (Minecraft.getInstance().options.keyUse.isDown()) {
            mouse.justRightClicked = !mouse.rightClicking;
            if (mouse.justRightClicked)
                captured = false;
            mouse.rightClicking = true;
        } else {
            mouse.justRightClicked = false;
            mouse.rightClicking = false;
        }

        if (Minecraft.getInstance().mouseHandler.isLeftPressed()) {
            mouse.justTapped = !mouse.tapping;
            captured = true;
            if (!mouse.tapping) {
                player.swing(InteractionHand.MAIN_HAND
                    //? if >=26.3
                    //, SwingAnimation.DEFAULT, true
                );
                mouse.tappedX = mouse.x;
                mouse.tappedY = mouse.y;
            }
            mouse.tapping = true;

            mouse.justReleased = false;
        } else {
            mouse.justReleased = mouse.tapping;
            mouse.tapping = false;

            mouse.justTapped = false;
        }

        if (mouse.x < 0) {
            mouse.x = 0;
        }
        if (mouse.y < 0) {
            mouse.y = 0;
        }
        if (mouse.x > 680) {
            mouse.x = 680;
        }
        if (mouse.y > 1200) {
            mouse.y = 1200;
        }
    }

    public void renderInHand(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, ItemStackRenderState renderState, int light, float timeUsingPhone) {
        poseStack.pushPose();
        poseStack.translate(-0.1f,0.0f,-13.5f);
        poseStack.scale(20,20,20);
        renderState.submit(poseStack,submitNodeCollector,light,OverlayTexture.NO_OVERLAY,0);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(-3.95f,-6.5f,-12.88f);
        poseStack.scale(0.85f,0.85f,0.85f);
        createPlane(submitNodeCollector,poseStack);

        poseStack.popPose();
    }

    public void createPlane(SubmitNodeCollector submitNodeCollector, PoseStack stack) {
        int fullbright = 15728880;
        submitNodeCollector.submitCustomGeometry(stack, MinetokClient.PHONE_RENDER_TYPE, ((pose, vertexConsumer) -> {
            vertexConsumer.addVertex(pose, 0,0,0).setUv(0,0).setLight(fullbright).setNormal(1,1,1).setUv2(0,0).setColor(-1);
            vertexConsumer.addVertex(pose, 9,0,0).setUv(1,0).setLight(fullbright).setNormal(1,1,1).setUv2(0,0).setColor(-1);
            vertexConsumer.addVertex(pose, 9,16,0).setUv(1,1).setLight(fullbright).setNormal(1,1,1).setUv2(0,0).setColor(-1);
            vertexConsumer.addVertex(pose, 0,16,0).setUv(0,1).setLight(fullbright).setNormal(1,1,1).setUv2(0,0).setColor(-1);
        }));
    }
}
