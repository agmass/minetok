package org.agmas.minetok.client.render;

//? if >=26.3
 //import net.minecraft.world.item.component.SwingAnimation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
//? if >1.21.1
//import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import org.agmas.minetok.Minetok;
import org.agmas.minetok.client.MinetokClient;
import org.agmas.minetok.api.PhoneTapInformation;
import org.agmas.minetok.client.animation.NBTHelper;
import org.agmas.minetok.client.api.Application;
import org.agmas.minetok.client.api.NetworkSprite;
import org.agmas.minetok.client.api.Sprite;
import org.agmas.minetok.client.init.MinetokOverlays;
import org.agmas.minetok.client.render.sprites.HomeScreenSprite;
import org.agmas.minetok.client.render.sprites.calls.CallingSprite;
import org.agmas.minetok.client.util.MinetokClientRegistries;
import org.agmas.minetok.client.vlcj.YTDLPDownloader;
import org.agmas.minetok.init.ModAttachments;
import org.agmas.minetok.porting.MTID;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class Phone {
    public static int SCREEN_WIDTH = 720;
    public static int SCREEN_HEIGHT = 1280;

    public GuiEventListener selectedElement = null;
    public boolean captured = false;
    public HomeScreenSprite topSprite = new HomeScreenSprite();
    public boolean networkDirty = false;
    public YTDLPDownloader downloader;

    public PhoneTapInformation mouse;
    public Player player;
    @Nullable CallingSprite callingSprite = null;

    private static final ResourceLocation CROSSHAIR_SPRITE = ResourceLocation.withDefaultNamespace("hud/crosshair");

    public Phone(Player player) {
        this.player = player;
        mouse = new PhoneTapInformation(player.isLocalPlayer());
        downloader = new YTDLPDownloader(player.getStringUUID());
    }

    public void addNetOverlay(Application overlayType, Sprite overlay) {
        overlay.overlay = overlayType;
        setDirty();
        topSprite.overlays.add(overlay);
    }

    public void render(GuiGraphics graphics) {
        if (this == MinetokClient.phone)
            handleMouseInput();

        if (player.getAttached(ModAttachments.RINGING_ATTACHMENT_TYPE) != null && callingSprite == null) {
            callingSprite = new CallingSprite(player.getAttached(ModAttachments.RINGING_ATTACHMENT_TYPE).recieving, player.getAttached(ModAttachments.RINGING_ATTACHMENT_TYPE).callingId);
            addNetOverlay(MinetokOverlays.CALLING, callingSprite);
            setDirty();
        }
        if (player.getAttached(ModAttachments.RINGING_ATTACHMENT_TYPE) == null && topSprite.overlays.contains(callingSprite)) {
            topSprite.overlays.remove(callingSprite);
            callingSprite = null;
            setDirty();
        }


        topSprite.render(graphics,mouse, Minecraft.getInstance().getTimer().getRealtimeDeltaTicks());

        graphics.blitSprite(
                //? if >1.21.1
                //RenderPipelines.CROSSHAIR,
                CROSSHAIR_SPRITE, (int)mouse.x, (int)mouse.y, 80, 80);
        
        if (mouse.justTapped)
            setDirty();
    }
    
    public void setDirty() {
        networkDirty = true;
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

    //? if >1.21.1 {
    /*public void renderInHand(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, ItemStackRenderState renderState, int light, float timeUsingPhone) {

        poseStack.pushPose();
        poseStack.translate(-0.1f,0.0f,-13.5f);
        poseStack.scale(20,20,20);
        renderState.submit(poseStack,submitNodeCollector,light,OverlayTexture.NO_OVERLAY,0);
        poseStack.popPose();
    *///? } else {
    public void renderInHand(PoseStack poseStack, MultiBufferSource submitNodeCollector, int light, float timeUsingPhone) {
    //? }

        poseStack.pushPose();
        poseStack.translate(-3.95f,-6.5f,-12.88f);
        poseStack.scale(0.85f,0.85f,0.85f);
        createPlane(submitNodeCollector,poseStack);

        poseStack.popPose();
    }

    int fullbright = 15728880;
    //? if >1.21.1 {
    /*public void createPlane(SubmitNodeCollector submitNodeCollector, PoseStack stack) {
        submitNodeCollector.submitCustomGeometry(stack, MinetokClient.getPhoneRenderType(MTID.of("phone_texture")), ((pose, vertexConsumer) -> {
            vertexConsumer.addVertex(pose, 0,0,0).setUv(0,0).setLight(fullbright).setNormal(1,1,1).setUv2(0,0).setColor(-1);
            vertexConsumer.addVertex(pose, 9,0,0).setUv(1,0).setLight(fullbright).setNormal(1,1,1).setUv2(0,0).setColor(-1);
            vertexConsumer.addVertex(pose, 9,16,0).setUv(1,1).setLight(fullbright).setNormal(1,1,1).setUv2(0,0).setColor(-1);
            vertexConsumer.addVertex(pose, 0,16,0).setUv(0,1).setLight(fullbright).setNormal(1,1,1).setUv2(0,0).setColor(-1);
        }));
    }
    *///? } else {
    public void createPlane(MultiBufferSource submitNodeCollector, PoseStack stack) {
        VertexConsumer consumer = submitNodeCollector.getBuffer(MinetokClient.getPhoneRenderType(MTID.of("phone_texture")));
        consumer.addVertex(stack.last(), 0,0,0).setUv(0,0).setLight(fullbright).setNormal(1,1,1).setUv2(0,0).setColor(-1);
        consumer.addVertex(stack.last(), 9,0,0).setUv(1,0).setLight(fullbright).setNormal(1,1,1).setUv2(0,0).setColor(-1);
        consumer.addVertex(stack.last(), 9,16,0).setUv(1,1).setLight(fullbright).setNormal(1,1,1).setUv2(0,0).setColor(-1);
        consumer.addVertex(stack.last(), 0,16,0).setUv(0,1).setLight(fullbright).setNormal(1,1,1).setUv2(0,0).setColor(-1);
    }
    //? }

    public CompoundTag createOnlyMouseTag() {
        CompoundTag mouseTag = new CompoundTag();
        mouseTag.putFloat("x", mouse.x);
        mouseTag.putFloat("y", mouse.y);
        return mouseTag;
    }
    public CompoundTag createTag() {
        CompoundTag tag = new CompoundTag();

        CompoundTag mouseTag = new CompoundTag();
        mouseTag.putFloat("x", mouse.x);
        mouseTag.putFloat("y", mouse.y);
        tag.put("mouse", mouseTag);

        CompoundTag phoneTag = new CompoundTag();

        if (topSprite.focusedSprite != null) {
            if (topSprite.focusedSprite.application != null) {
                if (MinetokClientRegistries.APPLICATIONS.getKey(topSprite.focusedSprite.application) != null) {
                    phoneTag.putString("application_id", MinetokClientRegistries.APPLICATIONS.getKey(topSprite.focusedSprite.application).toString());

                    CompoundTag applicationTag = new CompoundTag();
                    if (topSprite.focusedSprite instanceof NetworkSprite networkSprite) {
                        applicationTag = networkSprite.createTag();
                    }
                    if (applicationTag == null) {
                        throw new NullPointerException("Application Tag is null");
                    }
                    phoneTag.put("application", applicationTag);
                }
            }
        }

        if (!topSprite.overlays.isEmpty()) {
            if (topSprite.overlays.getLast().overlay != null) {
                if (MinetokClientRegistries.OVERLAYS.getKey(topSprite.overlays.getLast().overlay) != null) {
                    phoneTag.putString("overlay_id", MinetokClientRegistries.OVERLAYS.getKey(topSprite.overlays.getLast().overlay).toString());

                    CompoundTag overlayTag = new CompoundTag();
                    if (topSprite.overlays.getLast() instanceof NetworkSprite networkSprite) {
                        overlayTag = networkSprite.createTag();
                    }
                    if (overlayTag == null) {
                        throw new NullPointerException("Overlay Tag is null");
                    }
                    phoneTag.put("overlay", overlayTag);
                }
            }
        }
        tag.put("phone", phoneTag);
        return tag;
    }

    public void sync(CompoundTag tag) {
        CompoundTag netMouse = NBTHelper.getCompoundOrEmpty(tag, "mouse");
        if (!netMouse.isEmpty()) {
            mouse.prevX = mouse.x;
            mouse.prevY = mouse.y;
            mouse.x = NBTHelper.getFloatOr(netMouse, "x", 0f);
            mouse.y = NBTHelper.getFloatOr(netMouse, "y", 0f);
        }

        CompoundTag netPhone = NBTHelper.getCompoundOrEmpty(tag, "phone");
        if (!netPhone.isEmpty()) {
            if (netPhone.contains("application_id")) {
                //? if >1.21.1 {
                /*Optional<Holder.Reference<Application>> netAppOp = MinetokClientRegistries.APPLICATIONS.get(ResourceLocation.parse(netPhone.getString("application_id").get()));
                if (netAppOp.isPresent()) {
                    Application netApp = netAppOp.get().value();
                *///? } else {
                if (MinetokClientRegistries.APPLICATIONS.containsKey(ResourceLocation.parse(netPhone.getString("application_id")))) {
                    Application netApp = MinetokClientRegistries.APPLICATIONS.get(ResourceLocation.parse(netPhone.getString("application_id")));
                //? }
                    if (topSprite.focusedSprite == null || !topSprite.focusedSprite.application.equals(netApp)) {
                        try {
                            topSprite.focusedSprite = netApp.initializerSprite.getConstructor().newInstance();
                            topSprite.focusedSprite.application = netApp;
                            topSprite.openingAppAnimation = 1;
                            topSprite.closing = false;
                        } catch (Exception ignored) {}
                    }
                }

                CompoundTag netAppData = NBTHelper.getCompoundOrEmpty(netPhone, "application");
                if (topSprite.focusedSprite instanceof NetworkSprite networkSprite) {
                    networkSprite.sync(netAppData);
                }
            } else {
                if (topSprite.focusedSprite != null && topSprite.focusedSprite.application != null && !topSprite.closing) {
                    topSprite.closing = true;
                    topSprite.openingAppAnimation = 1;
                }
            }

            if (netPhone.contains("overlay_id")) {
                //? if >1.21.1 {
                /*Optional<Holder.Reference<Application>> netOverlayOp = MinetokClientRegistries.OVERLAYS.get(ResourceLocation.parse(netPhone.getString("overlay_id").get()));
                if (netOverlayOp.isPresent()) {
                    Application netOverlay = netOverlayOp.get().value();
                *///? } else {
                    if (MinetokClientRegistries.OVERLAYS.containsKey(ResourceLocation.parse(netPhone.getString("overlay_id")))) {
                        Application netOverlay = MinetokClientRegistries.OVERLAYS.get(ResourceLocation.parse(netPhone.getString("overlay_id")));
                //? }
                    if (topSprite.overlays.isEmpty() || topSprite.overlays.getLast().overlay != netOverlay) {
                        topSprite.overlays.clear();
                        try { topSprite.overlays.add(netOverlay.initializerSprite.getConstructor().newInstance()); } catch (Exception ignored) {}
                    }
                }

                CompoundTag netAppData = NBTHelper.getCompoundOrEmpty(netPhone, "overlay");

                if (!topSprite.overlays.isEmpty()) {
                    if (topSprite.overlays.getLast() instanceof NetworkSprite networkSprite) {
                        networkSprite.sync(netAppData);
                    }
                }

            }
        }
    }

    public void destroy() {
        topSprite.destroy();
        for (Sprite overlay : topSprite.overlays) {
            overlay.destroy();
        }
        topSprite.focusedSprite.destroy();
    }

    public static Phone getForPlayer(Player p) {
        if (p != Minecraft.getInstance().player) {
            return MinetokClient.playerPhoneHashMap.containsKey(p.getUUID()) ? MinetokClient.playerPhoneHashMap.get(p.getUUID()).phone : MinetokClient.phone;
        } else {
            return MinetokClient.phone;
        }
    }

    public static MTID getTextureForPlayer(Player p) {
        if (p != Minecraft.getInstance().player) {
            return MinetokClient.playerPhoneHashMap.containsKey(p.getUUID()) ? MinetokClient.playerPhoneHashMap.get(p.getUUID()).textureResourceLocation : MinetokClient.missingId;
        } else {
            return MTID.of("phone_texture");
        }
    }

    /*
    super official epic documentation on how the nbt is structured

    - mouse
      - x (float)
      - y (float)
    - phone
      - application_id (string // resourceLocation)
      - (if ^ exists) application (compound)
      - overlay_id
      - (if ^ exists) overlay
     */
}
