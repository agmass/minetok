package org.agmas.minetok.client.render.sprites.minetok;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.agmas.minetok.api.PhoneTapInformation;
import org.agmas.minetok.client.MinetokClient;
import org.agmas.minetok.client.api.NetworkSprite;
import org.agmas.minetok.client.api.Sprite;
import org.agmas.minetok.client.init.MinetokOverlays;
import org.agmas.minetok.client.render.sprites.NotificationSprite;
import org.agmas.minetok.client.vlcj.YTDLPDownloader;
import org.agmas.minetok.porting.MTIdentifier;
import org.joml.Random;

import java.nio.charset.StandardCharsets;

public class TikTokApplication extends Sprite implements NetworkSprite {

    VideoPlayer player;

    public TikTokApplication() {
        try {
            player = new VideoPlayer(MinetokClient.currentlyRendering_phone);
        } catch (Exception e) {
            MinetokClient.currentlyRendering_phone.addNetOverlay(MinetokOverlays.NOTIFICATION, new NotificationSprite(Component.translatable("phone.no_vlc", e.getMessage()), true, MinetokClient.phone));
            throw e;
        }
    }

    @Override
    public void render(GuiGraphics graphics, PhoneTapInformation mouse, float renderDelta) {
        super.render(graphics, mouse, renderDelta);
        player.render(graphics,mouse,renderDelta);
        if (!YTDLPDownloader.hasPlayableReel() && !YTDLPDownloader.isDownloadingVideo()) {
            try {
                byte[] tiktokFile = Minecraft.getInstance().getResourceManager().getResourceStack(MTIdentifier.of("reels.txt").id).getLast().open().readAllBytes();
                String s = new String(tiktokFile, StandardCharsets.UTF_8);
                int totalTiktoks = s.split("\n").length - 1;
                YTDLPDownloader.nextReel(s.split("\n")[new Random().nextInt(totalTiktoks)], true);
            } catch (Exception ignored) {}
        }


        player.canTimeOut = YTDLPDownloader.hasPlayableReel();
        if (YTDLPDownloader.hasPlayableReel() && !player.handler.mediaPlayer.status().isPlaying()) {
            player.handler.mediaPlayer.media().start("ytdlp/reel1.mp4");
        }
    }

    @Override
    public CompoundTag createTag() {
        return new CompoundTag();
    }

    @Override
    public void sync(CompoundTag phoneData) {

    }
}
