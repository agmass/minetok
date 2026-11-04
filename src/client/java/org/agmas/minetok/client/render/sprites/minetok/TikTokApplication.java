package org.agmas.minetok.client.render.sprites.minetok;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.platform.NativeImage;

//? if >1.21.1 {
/*import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
*///? }
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
//? if >=26.2
//import net.minecraft.client.PreferredGraphicsApi;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.agmas.minetok.api.PhoneTapInformation;
import org.agmas.minetok.client.MinetokClient;
import org.agmas.minetok.client.animation.MTGraphics;
import org.agmas.minetok.client.animation.NBTHelper;
import org.agmas.minetok.client.api.NetworkSprite;
import org.agmas.minetok.client.api.Sprite;
import org.agmas.minetok.client.init.MinetokOverlays;
import org.agmas.minetok.client.render.Phone;
import org.agmas.minetok.client.render.sprites.NotificationSprite;
import org.agmas.minetok.client.vlcj.YTDLPDownloader;
import org.agmas.minetok.porting.MTID;
import org.joml.Random;
import uk.co.caprica.vlcj.player.base.State;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class TikTokApplication extends Sprite implements NetworkSprite {

    VideoPlayer player;
    public boolean loading = false;

    public TikTokApplication() {
        try {
            player = new VideoPlayer(MinetokClient.currentlyRendering_phone);
        } catch (Exception e) {
            MinetokClient.currentlyRendering_phone.addNetOverlay(MinetokOverlays.NOTIFICATION, new NotificationSprite(Component.translatable("phone.no_vlc", e.getMessage()), true, MinetokClient.phone));
            throw e;
        }
        if (!YTDLPDownloader.checkYtDLP()) {
            MinetokClient.currentlyRendering_phone.addNetOverlay(MinetokOverlays.NOTIFICATION, new NotificationSprite(Component.translatable("phone.error.no_ytdlp"), true, MinetokClient.phone));
            throw new RuntimeException("No yt-dlp downloaded");
        }
    }

    public boolean warnedAboutVulkan = false;
    public ParsedMetadata metadata = null;
    public boolean commentsOpen = false;
    public boolean commentSpritesDirty = true;
    public ArrayList<CommentSprite> comments = new ArrayList<>();
    public float commentsTransition = 0;
    public float startCommentsScroll = 0;
    public float commentsScroll = 0;

    public void parseMetadata(File file) {
        try {
            JsonElement element = JsonParser.parseReader(new FileReader(file));
            JsonObject object = element.getAsJsonObject();
            ArrayList<ParsedMetadata.Comment> comments = new ArrayList<>();

            if (object.has("comments")) {
                for (JsonElement jsonElement : object.getAsJsonArray("comments")) {
                    JsonObject comment = jsonElement.getAsJsonObject();
                    comments.add(new ParsedMetadata.Comment(
                            comment.has("text") ? comment.getAsJsonPrimitive("text").getAsString() : "Empty Comment",
                            comment.has("author") ? comment.getAsJsonPrimitive("author").getAsString() : "Unknown Author",
                            comment.has("like_count") ? comment.getAsJsonPrimitive("like_count").getAsInt() : 0
                    ));
                }
            }

            commentSpritesDirty = true;

            if (object.has("extractor")) {
                if (object.get("extractor").getAsString().equals("Instagram")) {
                    metadata = new ParsedMetadata(
                            object.has("description") ? object.getAsJsonPrimitive("description").getAsString() : "No Title",
                            object.has("uploader") ? object.getAsJsonPrimitive("uploader").getAsString() : "No Uploader",
                            object.has("like_count") ? object.getAsJsonPrimitive("like_count").getAsInt() : 0,
                            object.has("channel_follower_count") ? object.getAsJsonPrimitive("channel_follower_count").getAsInt() : 0,
                            comments
                    );
                    return;
                }
            }
            metadata = new ParsedMetadata(
                    object.has("title") ? object.getAsJsonPrimitive("title").getAsString() : "No Title",
                    object.has("uploader") ? object.getAsJsonPrimitive("uploader").getAsString() : "No Uploader",
                    object.has("like_count") ? object.getAsJsonPrimitive("like_count").getAsInt() : 0,
                    object.has("channel_follower_count") ? object.getAsJsonPrimitive("channel_follower_count").getAsInt() : 0,
                    comments
            );
        } catch (Exception e) {
            Log.info(LogCategory.GENERAL, "Metadata not found or corrupt (" + e.getCause().toString() + ")");
        }
    }

    @Override
    public void render(GuiGraphics graphics, PhoneTapInformation mouse, float renderDelta) {
        super.render(graphics, mouse, renderDelta);
        //? if >=26.2 {
        /*if (mouse.isClient && Minecraft.getInstance().options.preferredGraphicsBackend().get().equals(PreferredGraphicsApi.VULKAN) && !warnedAboutVulkan) {
            warnedAboutVulkan = true;
            MinetokClient.currentlyRendering_phone.addNetOverlay(MinetokOverlays.NOTIFICATION, new NotificationSprite(Component.translatable("phone.vulkan_warning"), false, MinetokClient.phone));
        }
        *///? }
        YTDLPDownloader downloader = MinetokClient.currentlyRendering_phone.downloader;
        if (mouse.isClient) {
            if (!downloader.hasNextReel() && !YTDLPDownloader.isDownloadingVideo()) {
                try {
                    byte[] tiktokFile = Minecraft.getInstance().getResourceManager().getResourceStack(MTID.of("reels.txt").id).getLast().open().readAllBytes();
                    String s = new String(tiktokFile, StandardCharsets.UTF_8);
                    int totalTiktoks = s.split("\n").length - 1;
                    String reel = s.split("\n")[new Random().nextInt(totalTiktoks)];
                    downloader.nextReel(reel,false);
                    net_nextReel = reel;
                    MinetokClient.phone.networkDirty = true;
                } catch (Exception ignored) {
                }
            }
            if (!downloader.hasPlayableReel() && downloader.hasNextReel()) {
                downloader.cycleReel();
                net_currentReel = net_nextReel;
                likedCurrentReel = false;
                metadata = null; 
            }
        } else {
            if (net_expectedReel) {
                downloader.cycleReel();
                likedCurrentReel = false;
            }
            if (!net_currentReel.equals(net_lastCurrentReel)) {
                downloader.nextReel(net_currentReel,true);
                metadata = null;
                net_lastCurrentReel = net_currentReel;
            }
            if (!net_nextReel.equals(net_lastNextReel)) {
                downloader.nextReel(net_nextReel,true);
                metadata = null;
                net_lastNextReel = net_nextReel;
            }

        }


        player.canTimeOut = downloader.hasPlayableReel();
        if (downloader.hasPlayableReel() && !player.handler.mediaPlayer.status().isPlaying() && !player.handler.mediaPlayer.status().state().equals(State.PAUSED)) {
            if (!loading) {
                player.handler.mediaPlayer.controls().stop();
                player.handler.mediaPlayer.media().reset();
                if (downloader.metadataAndComments().exists()) {
                    parseMetadata(downloader.metadataAndComments());
                }
                player.handler.mediaPlayer.media().play("ytdlp/" + downloader.prefix + "/reel1.mp4");
                player.handler.mediaPlayer.controls().setRepeat(false);
                MinetokClient.phone.networkDirty = true;
            }
            loading = true;
        } else {
            loading = false;
        }

        if (mouse.justReleased && Math.abs(mouse.tappedX-mouse.x) < 12) {
            player.handler.mediaPlayer.controls().pause();
            MinetokClient.phone.networkDirty = true;
        }


        if (mouse.tapping) {
            if (!commentsOpen) {
                scroll = ((mouse.y - mouse.tappedY) / 1000f);
            } else {
                commentsScroll = startCommentsScroll + (mouse.y - mouse.tappedY);
            }
        } else {
            if (scroll < -0.5f) {
                player.handler.mediaPlayer.controls().stop();
                player.handler.mediaPlayer.media().reset();
                downloader.cycleReel();
                net_currentReel = net_nextReel;
            }
            scroll = 0;
            if (commentsScroll > 0) commentsScroll = 0;
            startCommentsScroll = commentsScroll;
        }

        graphics.pose().pushPose();
        MTGraphics.translate(graphics, 0, scroll*1000f);

        graphics.pose().pushPose();
        MTGraphics.scale(graphics, 3,3);
        graphics.drawString(Minecraft.getInstance().font, "its loading ok let it think", 4, 32, Color.WHITE.getRGB());
        graphics.pose().popPose();

        if (downloader.hasNextReel() && downloader.nextThumbnail().exists()) {
            if (!thumbnailState) {
                try {
                    image = NativeImage.read(new FileInputStream(downloader.nextThumbnail()));
                    thumbnailTexture = new DynamicTexture(
                            //? if >1.21.1
                            //()->"thumbnailTexture",
                            image);
                    Minecraft.getInstance().getTextureManager().register(MTID.of(MinetokClient.currentlyRendering_phone.player.getStringUUID() + "-thumbnail").id, thumbnailTexture);
                } catch (Exception e) {
                    e.printStackTrace();
                    image = null;
                }
            }
            thumbnailState = true;
        } else {
            thumbnailState = false;
        }
        graphics.pose().pushPose();
        float scale = Mth.lerp(commentsTransition, 1f, 0.4f);
        MTGraphics.translate(graphics, (Phone.SCREEN_WIDTH/2f) - (Phone.SCREEN_WIDTH*scale)/2f,0);
        MTGraphics.scale(graphics, scale);
        if (downloader.hasPlayableReel()) {
            player.render(graphics, mouse, renderDelta);
            player.handler.mediaPlayer.audio().setVolume(Math.clamp((int)Mth.lerp(
                    MinetokClient.currentlyRendering_phone.player.distanceTo(Minecraft.getInstance().player)*32,
                    100,
                    0
            ),0,100));
        }
        if (metadata != null) {
            graphics.pose().pushPose();
            MTGraphics.translate(graphics, 32,Phone.SCREEN_HEIGHT-260);
            MTGraphics.scale(graphics, 3,3);
            graphics.drawString(Minecraft.getInstance().font, metadata.title(), 0, 0, -1);
            graphics.pose().popPose();

            graphics.pose().pushPose();
            MTGraphics.translate(graphics, 32,Phone.SCREEN_HEIGHT-208);
            MTGraphics.scale(graphics, 2,2);
            graphics.drawString(Minecraft.getInstance().font, Component.literal(metadata.author()).append(Component.translatable("phone.minetok.followers", metadata.followers()).withStyle(ChatFormatting.DARK_GRAY)), 0, 0, -1);
            graphics.pose().popPose();

            graphics.pose().pushPose();
            MTGraphics.translate(graphics, Phone.SCREEN_WIDTH-96,Phone.SCREEN_HEIGHT-612);
            graphics.blit(ResourceLocation.withDefaultNamespace("textures/gui/sprites/hud/heart/container.png"), 0,0,64,64,0,1,0,1);
            if (likedCurrentReel) {
                graphics.blit(ResourceLocation.withDefaultNamespace("textures/gui/sprites/hud/heart/full.png"), 0,0,64,64,0,1,0,1);
            }
            if (mouse.x+40 > Phone.SCREEN_WIDTH-96 && mouse.y+40 > Phone.SCREEN_HEIGHT-612) {
                if (mouse.x+40 < Phone.SCREEN_WIDTH-16 && mouse.y+40 < Phone.SCREEN_HEIGHT-550) {
                    graphics.blit(ResourceLocation.withDefaultNamespace("textures/gui/sprites/hud/heart/frozen_full.png"), 0,0,64,64,0,1,0,1);
                    if (mouse.justReleased && !commentsOpen) {
                        likedCurrentReel = !likedCurrentReel;
                    }
                }
            }
            MTGraphics.translate(graphics, 32 - (Minecraft.getInstance().font.width((metadata.likes() + (likedCurrentReel ? 1 : 0))+"")),68);
            MTGraphics.scale(graphics, 2,2);
            graphics.drawString(Minecraft.getInstance().font, (metadata.likes() + (likedCurrentReel ? 1 : 0))+"", 0,0,-1);
            graphics.pose().popPose();

            graphics.pose().pushPose();
            MTGraphics.translate(graphics, Phone.SCREEN_WIDTH-96,Phone.SCREEN_HEIGHT-508);
            graphics.blit(ResourceLocation.withDefaultNamespace("textures/gui/sprites/hud/heart/container.png"), 0,0,64,64,0,1,0,1);

            if (mouse.x+40 > Phone.SCREEN_WIDTH-96 && mouse.y+40 > Phone.SCREEN_HEIGHT-508) {
                if (mouse.x+40 < Phone.SCREEN_WIDTH-16 && mouse.y+40 < Phone.SCREEN_HEIGHT-444) {
                    if (mouse.justReleased) {
                        commentsOpen = true;
                    }
                }
            }
            MTGraphics.translate(graphics, 32 - (Minecraft.getInstance().font.width(metadata.comments().size()+"")),68);
            MTGraphics.scale(graphics, 2,2);
            graphics.drawString(Minecraft.getInstance().font, metadata.comments().size()+"", 0,0,-1);
            graphics.pose().popPose();

            if (mouse.justReleased && mouse.tappedY < 512 && mouse.y < 512) {
                commentsOpen = false;
            }
            if (commentsOpen) {
                commentsTransition += renderDelta;
            } else {
                commentsTransition -= renderDelta;
            }
            commentsTransition = Math.clamp(commentsTransition,0f,1f);
        } else {
            commentsTransition = 0;
        }
        if (thumbnailTexture != null) {
            graphics.blit(MTID.of(MinetokClient.currentlyRendering_phone.player.getStringUUID() + "-thumbnail").id, 0,Phone.SCREEN_HEIGHT, Phone.SCREEN_WIDTH, Phone.SCREEN_HEIGHT, 0,1,0,1);
        }
        graphics.pose().popPose();

        graphics.pose().popPose();
        if (commentsOpen) {
            if (commentSpritesDirty) {
                comments.clear();
                commentSpritesDirty = false;
                for (ParsedMetadata.Comment comment : metadata.comments()) {
                    comments.add(new CommentSprite(comment,graphics));
                }
            }
            graphics.enableScissor(0,480,Phone.SCREEN_WIDTH,Phone.SCREEN_HEIGHT);

            graphics.pose().pushPose();
            MTGraphics.translate(graphics, 0,Mth.lerp(commentsTransition, Phone.SCREEN_HEIGHT, 512));
            graphics.fill(0, 0, Phone.SCREEN_WIDTH, Phone.SCREEN_HEIGHT, new Color(52, 52, 52,255).getRGB());

            graphics.pose().pushPose();
            MTGraphics.translate(graphics, 16,commentsScroll);
            float y = 0;
            for (CommentSprite comment : comments) {
                comment.y = y+commentsScroll+512;
                comment.render(graphics, mouse, renderDelta);
                MTGraphics.translate(graphics, 0,comment.height()+48);
                y += comment.height()+48;
            }
            graphics.pose().popPose();
            graphics.disableScissor();
            graphics.fill(0, -32, Phone.SCREEN_WIDTH, 0, new Color(33, 33, 33,255).getRGB());
            graphics.pose().popPose();
        }

    }

    DynamicTexture thumbnailTexture;
    NativeImage image = null;
    public boolean thumbnailState = false;
    public boolean likedCurrentReel = false;
    public float scroll = 0;


    @Override
    public void unfocusedRender() {
        super.unfocusedRender();
    }

    @Override
    public void destroy() {
        player.destroy();
    }

    public String net_currentReel = "";
    public String net_nextReel = "";
    public String net_lastCurrentReel = "";
    public String net_lastNextReel = "";
    public float net_time = 0f;
    public boolean net_expectedReel = false;

    @Override
    public CompoundTag createTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString("next_reel", net_nextReel);
        tag.putString("current_reel", net_currentReel);
        tag.putBoolean("likedReel", likedCurrentReel);
        tag.putBoolean("commentsOpen", commentsOpen);
        tag.putFloat("commentsScroll", commentsScroll);
        tag.putFloat("time", player.handler.mediaPlayer.status().position());
        tag.putBoolean("paused", player.handler.mediaPlayer.status().isPlaying());
        return tag;
    }

    @Override
    public void sync(CompoundTag phoneData) {
        likedCurrentReel = NBTHelper.getBooleanOr(phoneData, "likedReel", false);
        net_currentReel = NBTHelper.getStringOr(phoneData, "current_reel", "");
        commentsOpen = NBTHelper.getBooleanOr(phoneData, "commentsOpen", false);
        commentsScroll = NBTHelper.getFloatOr(phoneData, "commentsScroll", 0f);
        net_expectedReel = net_currentReel.equals(net_nextReel);
        net_nextReel = NBTHelper.getStringOr(phoneData, "next_reel", "");
        net_time = NBTHelper.getFloatOr(phoneData, "time", 0f);
    }
}
