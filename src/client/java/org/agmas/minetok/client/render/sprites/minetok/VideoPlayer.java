package org.agmas.minetok.client.render.sprites.minetok;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.agmas.minetok.Minetok;
import org.agmas.minetok.api.PhoneTapInformation;
import org.agmas.minetok.client.MinetokClient;
import org.agmas.minetok.client.api.Sprite;
import org.agmas.minetok.client.init.MinetokApplications;
import org.agmas.minetok.client.init.MinetokOverlays;
import org.agmas.minetok.client.render.Phone;
import org.agmas.minetok.client.render.sprites.NotificationSprite;
import org.agmas.minetok.client.vlcj.VLCJHandler;
import org.agmas.minetok.client.vlcj.YTDLPDownloader;
import org.agmas.minetok.porting.MTIdentifier;

public class VideoPlayer extends Sprite {

    public VLCJHandler handler;
    float timeout = 0;
    boolean crashRightAfterOverlayClosed = false;
    public boolean canTimeOut = false;

    public VideoPlayer(Phone phone) {
        handler = new VLCJHandler(MTIdentifier.of(phone.player.getStringUUID() + "-videotexture").id);
    }

    @Override
    public void render(GuiGraphics graphics, PhoneTapInformation mouse, float renderDelta) {
        if (MinetokClient.phone.topSprite.overlays.isEmpty() && mouse.isClient && crashRightAfterOverlayClosed && !MinetokClient.phone.topSprite.closing) {
            MinetokClient.phone.topSprite.closing = true;
            MinetokClient.phone.topSprite.openingAppAnimation = 1;
            mouse.justReleased = false;
            MinetokClient.phone.setDirty();
        }
        super.render(graphics, mouse, renderDelta);
        if (!handler.updateTexture()) {
            timeout += renderDelta;
            if (timeout >= 5) {
                if (mouse.isClient) {
                    if (YTDLPDownloader.checkYtDLP()) {
                        MinetokClient.currentlyRendering_phone.addNetOverlay(MinetokOverlays.NOTIFICATION, new NotificationSprite(Component.translatable("phone.media_not_found", "media"), true, MinetokClient.phone));
                    } else {
                        MinetokClient.currentlyRendering_phone.addNetOverlay(MinetokOverlays.NOTIFICATION, new NotificationSprite(Component.translatable("phone.error.no_ytdlp"), true, MinetokClient.phone));
                    }
                    crashRightAfterOverlayClosed = true;
                    timeout = -999999999;
                }
            }
        } else {
            timeout = 0;
        }
        graphics.blit(handler.textureId, 0,0,Phone.SCREEN_WIDTH,Phone.SCREEN_HEIGHT,0,1,0,1);
    }
}
