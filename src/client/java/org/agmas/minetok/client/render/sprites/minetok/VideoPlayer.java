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
import org.agmas.minetok.porting.MTIdentifier;

public class VideoPlayer extends Sprite {

    VLCJHandler handler = new VLCJHandler(MTIdentifier.of("heyallscotthere").id);
    float timeout = 0;

    public void setVideo(String link) {
    }

    @Override
    public void render(GuiGraphics graphics, PhoneTapInformation mouse, float renderDelta) {
        super.render(graphics, mouse, renderDelta);
        if (!handler.updateTexture()) {
            timeout += renderDelta;
            if (timeout >= 5) {
                if (mouse.isClient) {
                    MinetokClient.phone.addNetOverlay(MinetokOverlays.NOTIFICATION, new NotificationSprite(Component.translatable("phone.media_not_found", "media"), true, MinetokClient.phone));
                    timeout = -999999999;
                }
            }
        } else {
            timeout = 0;
        }
        graphics.blit(handler.textureId, 0,0,Phone.SCREEN_WIDTH,Phone.SCREEN_HEIGHT,0,1,0,1);
    }
}
