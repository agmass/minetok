package org.agmas.minetok.client.render.sprites.minetok;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.agmas.minetok.api.PhoneTapInformation;
import org.agmas.minetok.client.MinetokClient;
import org.agmas.minetok.client.api.MTGraphics;
import org.agmas.minetok.client.api.Sprite;
import org.agmas.minetok.client.init.MinetokOverlays;
import org.agmas.minetok.client.render.Phone;
import org.agmas.minetok.client.render.sprites.NotificationSprite;
import org.agmas.minetok.client.vlcj.VLCJHandler;
import org.agmas.minetok.client.vlcj.YTDLPDownloader;
import org.agmas.minetok.porting.MTID;
import uk.co.caprica.vlcj.binding.lib.LibVlc;
import uk.co.caprica.vlcj.support.version.LibVlcVersion;

public class VideoPlayer extends Sprite {

    public VLCJHandler handler;
    boolean crashRightAfterOverlayClosed = false;
    public boolean canTimeOut = false;



    public VideoPlayer(Phone phone) {
        handler = new VLCJHandler(MTID.of(phone.player.getStringUUID() + "-videotexture").id);
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
            if (mouse.isClient) {
                if (!YTDLPDownloader.checkYtDLP()) {
                    MinetokClient.currentlyRendering_phone.addNetOverlay(MinetokOverlays.NOTIFICATION, new NotificationSprite(Component.translatable("phone.error.no_ytdlp"), true, MinetokClient.phone));
                    crashRightAfterOverlayClosed = true;
                }
            }
        }
        MTGraphics.blit(graphics, handler.textureId, 0,0,Phone.SCREEN_WIDTH,Phone.SCREEN_HEIGHT,0,1,0,1);
    }

    @Override
    public void destroy() {
        handler.destroy();
    }
}
