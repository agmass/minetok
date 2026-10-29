package org.agmas.minetok.client.render.sprites.minetok;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
//? if >=26.2
import net.minecraft.client.PreferredGraphicsApi;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.agmas.minetok.api.PhoneTapInformation;
import org.agmas.minetok.client.MinetokClient;
import org.agmas.minetok.client.api.NetworkSprite;
import org.agmas.minetok.client.api.Sprite;
import org.agmas.minetok.client.init.MinetokOverlays;
import org.agmas.minetok.client.render.Phone;
import org.agmas.minetok.client.render.sprites.NotificationSprite;
import org.agmas.minetok.client.vlcj.YTDLPDownloader;
import org.agmas.minetok.porting.MTIdentifier;
import org.joml.Random;
import uk.co.caprica.vlcj.player.base.State;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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
    public float commentsTransition = 0;

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
                            comment.has("author_thumbnail") ? comment.getAsJsonPrimitive("author_thumbnail").getAsString() : "",
                            comment.has("like_count") ? comment.getAsJsonPrimitive("like_count").getAsInt() : 0
                    ));
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
    public void render(GuiGraphicsExtractor graphics, PhoneTapInformation mouse, float renderDelta) {
        super.render(graphics, mouse, renderDelta);
        //? if >=26.2 {
        if (mouse.isClient && Minecraft.getInstance().options.preferredGraphicsBackend().get().equals(PreferredGraphicsApi.VULKAN) && !warnedAboutVulkan) {
            warnedAboutVulkan = true;
            MinetokClient.currentlyRendering_phone.addNetOverlay(MinetokOverlays.NOTIFICATION, new NotificationSprite(Component.translatable("phone.vulkan_warning"), false, MinetokClient.phone));
        }
        //? }
        YTDLPDownloader downloader = MinetokClient.currentlyRendering_phone.downloader;
        if (mouse.isClient) {
            if (!downloader.hasNextReel() && !YTDLPDownloader.isDownloadingVideo()) {
                try {
                    byte[] tiktokFile = Minecraft.getInstance().getResourceManager().getResourceStack(MTIdentifier.of("reels.txt").id).getLast().open().readAllBytes();
                    String s = new String(tiktokFile, StandardCharsets.UTF_8);
                    int totalTiktoks = s.split("\n").length - 1;
                    downloader.nextReel(s.split("\n")[new Random().nextInt(totalTiktoks)],false);
                    net_nextReel = s;
                } catch (Exception ignored) {
                }
            }
            if (!downloader.hasPlayableReel() && downloader.hasNextReel()) {
                downloader.cycleReel();
                net_currentReel = net_nextReel;
                likedCurrentReel = false;
            }
        } else {
            if (net_expectedReel) {
                downloader.cycleReel();
                likedCurrentReel = false;
            }
            if (!net_currentReel.equals(net_lastCurrentReel)) {
                downloader.nextReel(net_currentReel,true);
                net_lastCurrentReel = net_currentReel;
            }
            if (!net_nextReel.equals(net_lastNextReel)) {
                downloader.nextReel(net_nextReel,true);
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
            }
            loading = true;
        } else {
            loading = false;
        }

        if (mouse.justReleased && Math.abs(mouse.tappedX-mouse.x) < 12) {
            player.handler.mediaPlayer.controls().pause();
        }

        if (mouse.tapping) {
            scroll = ((mouse.y - mouse.tappedY)/1000f);
        } else {
            if (scroll < -0.5f) {
                player.handler.mediaPlayer.controls().stop();
                player.handler.mediaPlayer.media().reset();
                downloader.cycleReel();
                net_currentReel = net_nextReel;
            }
            scroll = 0;
        }

        graphics.pose().pushMatrix();
        graphics.pose().translate(0, scroll*1000f);

        graphics.pose().pushMatrix();
        graphics.pose().scale(3,3);
        graphics.text(Minecraft.getInstance().font, "its loading ok let it think", 4, 32, Color.WHITE.getRGB());
        graphics.pose().popMatrix();

        if (downloader.hasNextReel() && downloader.nextThumbnail().exists()) {
            if (!thumbnailState) {
                try {
                    image = NativeImage.read(new FileInputStream(downloader.nextThumbnail()));
                    thumbnailTexture = new DynamicTexture(()->"thumbnailTexture", image);
                    Minecraft.getInstance().getTextureManager().register(MTIdentifier.of(MinetokClient.currentlyRendering_phone.player.getStringUUID() + "-thumbnail").id, thumbnailTexture);
                } catch (Exception e) {
                    e.printStackTrace();
                    image = null;
                }
            }
            thumbnailState = true;
        } else {
            thumbnailState = false;
        }
        if (downloader.hasPlayableReel()) {
            player.render(graphics, mouse, renderDelta);
            player.handler.mediaPlayer.audio().setVolume(Math.clamp((int)Mth.lerp(
                    MinetokClient.currentlyRendering_phone.player.distanceTo(Minecraft.getInstance().player)*32,
                    100,
                    0
            ),0,100));
        }
        if (metadata != null) {
            graphics.pose().pushMatrix();
            graphics.pose().translate(32,Phone.SCREEN_HEIGHT-260);
            graphics.pose().scale(3,3);
            graphics.text(Minecraft.getInstance().font, metadata.title(), 0, 0, -1);
            graphics.pose().popMatrix();

            graphics.pose().pushMatrix();
            graphics.pose().translate(32,Phone.SCREEN_HEIGHT-208);
            graphics.pose().scale(2,2);
            graphics.text(Minecraft.getInstance().font, Component.literal(metadata.author()).append(Component.translatable("phone.minetok.followers", metadata.followers()).withStyle(ChatFormatting.DARK_GRAY)), 0, 0, -1);
            graphics.pose().popMatrix();

            graphics.pose().pushMatrix();
            graphics.pose().translate(Phone.SCREEN_WIDTH-96,Phone.SCREEN_HEIGHT-612);
            graphics.blit(Identifier.withDefaultNamespace("textures/gui/sprites/hud/heart/container.png"), 0,0,64,64,0,1,0,1);
            if (likedCurrentReel) {
                graphics.blit(Identifier.withDefaultNamespace("textures/gui/sprites/hud/heart/full.png"), 0,0,64,64,0,1,0,1);
            }
            if (mouse.x+40 > Phone.SCREEN_WIDTH-96 && mouse.y+40 > Phone.SCREEN_HEIGHT-612) {
                if (mouse.x+40 < Phone.SCREEN_WIDTH-16 && mouse.y+40 < Phone.SCREEN_HEIGHT-550) {
                    graphics.blit(Identifier.withDefaultNamespace("textures/gui/sprites/hud/heart/frozen_full.png"), 0,0,64,64,0,1,0,1);
                    if (mouse.justReleased) {
                        likedCurrentReel = !likedCurrentReel;
                    }
                }
            }
            graphics.pose().translate(32 - (Minecraft.getInstance().font.width(metadata.likes()+"")),68);
            graphics.pose().scale(2,2);
            graphics.text(Minecraft.getInstance().font, metadata.likes()+"", 0,0,-1);
            graphics.pose().popMatrix();

            graphics.pose().pushMatrix();
            graphics.pose().translate(Phone.SCREEN_WIDTH-96,Phone.SCREEN_HEIGHT-412);
            graphics.blit(Identifier.withDefaultNamespace("textures/gui/sprites/hud/heart/container.png"), 0,0,64,64,0,1,0,1);

            if (mouse.x+40 > Phone.SCREEN_WIDTH-96 && mouse.y+40 > Phone.SCREEN_HEIGHT-412) {
                if (mouse.x+40 < Phone.SCREEN_WIDTH-16 && mouse.y+40 < Phone.SCREEN_HEIGHT-350) {
                    if (mouse.justTapped) {
                        commentsOpen = !commentsOpen;
                    }
                }
            }
            graphics.pose().translate(32 - (Minecraft.getInstance().font.width(metadata.likes()+"")),68);
            graphics.pose().scale(2,2);
            graphics.text(Minecraft.getInstance().font, metadata.likes()+"", 0,0,-1);
            graphics.pose().popMatrix();
        }
        if (thumbnailTexture != null) {
            graphics.blit(MTIdentifier.of(MinetokClient.currentlyRendering_phone.player.getStringUUID() + "-thumbnail").id, 0,Phone.SCREEN_HEIGHT, Phone.SCREEN_WIDTH, Phone.SCREEN_HEIGHT, 0,1,0,1);
        }
        graphics.pose().popMatrix();

    }

    DynamicTexture thumbnailTexture;
    NativeImage image = null;
    public boolean thumbnailState = false;
    public boolean likedCurrentReel = false;
    public float scroll = 0;

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
        tag.putFloat("time", player.handler.mediaPlayer.status().position());
        return tag;
    }

    @Override
    public void sync(CompoundTag phoneData) {
        likedCurrentReel = phoneData.getBooleanOr("likedReel", false);
        net_currentReel = phoneData.getStringOr("current_reel", "");
        net_expectedReel = net_currentReel.equals(net_nextReel);
        net_nextReel = phoneData.getStringOr("next_reel", "");
        net_time = phoneData.getFloatOr("time", 0f);
    }
}
