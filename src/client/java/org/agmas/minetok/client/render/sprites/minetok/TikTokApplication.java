package org.agmas.minetok.client.render.sprites.minetok;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
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
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;

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

    @Override
    public void render(GuiGraphicsExtractor graphics, PhoneTapInformation mouse, float renderDelta) {
        super.render(graphics, mouse, renderDelta);
        //? if >=26.2 {
        if (mouse.isClient && Minecraft.getInstance().options.preferredGraphicsBackend().get().equals(PreferredGraphicsApi.VULKAN) && !warnedAboutVulkan) {
            warnedAboutVulkan = true;
            MinetokClient.currentlyRendering_phone.addNetOverlay(MinetokOverlays.NOTIFICATION, new NotificationSprite(Component.translatable("phone.vulkan_warning"), false, MinetokClient.phone));
        }
        //? }
        if (!YTDLPDownloader.hasNextReel() && !YTDLPDownloader.isDownloadingVideo()) {
            try {
                byte[] tiktokFile = Minecraft.getInstance().getResourceManager().getResourceStack(MTIdentifier.of("reels.txt").id).getLast().open().readAllBytes();
                String s = new String(tiktokFile, StandardCharsets.UTF_8);
                int totalTiktoks = s.split("\n").length - 1;
                YTDLPDownloader.nextReel(s.split("\n")[new Random().nextInt(totalTiktoks)]);
            } catch (Exception ignored) {}
        }

        if (!YTDLPDownloader.hasPlayableReel() && YTDLPDownloader.hasNextReel()) {
            YTDLPDownloader.cycleReel();
        }

        player.canTimeOut = YTDLPDownloader.hasPlayableReel();
        if (YTDLPDownloader.hasPlayableReel() && !player.handler.mediaPlayer.status().isPlaying() && !player.handler.mediaPlayer.status().state().equals(State.PAUSED)) {
            if (!loading) {
                player.handler.mediaPlayer.controls().stop();
                player.handler.mediaPlayer.media().reset();
                player.handler.mediaPlayer.media().play("ytdlp/reel1.mp4");
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
                YTDLPDownloader.cycleReel();
            }
            scroll = 0;
        }

        graphics.pose().pushMatrix();
        graphics.pose().translate(0, scroll*1000f);
        if (YTDLPDownloader.hasNextReel() && YTDLPDownloader.nextThumbnail().exists()) {
            if (!thumbnailState) {
                try {
                    image = NativeImage.read(new FileInputStream(YTDLPDownloader.nextThumbnail()));
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
        if (YTDLPDownloader.hasPlayableReel()) {
            player.render(graphics, mouse, renderDelta);
        }
        if (thumbnailTexture != null) {
            graphics.blit(MTIdentifier.of(MinetokClient.currentlyRendering_phone.player.getStringUUID() + "-thumbnail").id, 0,Phone.SCREEN_HEIGHT, Phone.SCREEN_WIDTH, Phone.SCREEN_HEIGHT, 0,1,0,1);
        }
        graphics.pose().popMatrix();

        graphics.pose().pushMatrix();
        graphics.pose().scale(3,3);
        graphics.text(Minecraft.getInstance().font, "its loading ok let it think", 0, 0, Color.WHITE.getRGB());
        graphics.pose().popMatrix();
    }

    DynamicTexture thumbnailTexture;
    NativeImage image = null;
    public boolean thumbnailState = false;
    public float scroll = 0;

    @Override
    public void destroy() {
        player.destroy();
    }

    @Override
    public CompoundTag createTag() {
        return new CompoundTag();
    }

    @Override
    public void sync(CompoundTag phoneData) {

    }
}
