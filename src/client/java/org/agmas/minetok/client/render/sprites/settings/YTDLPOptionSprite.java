package org.agmas.minetok.client.render.sprites.settings;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.agmas.minetok.api.PhoneTapInformation;
import org.agmas.minetok.client.animation.MTGraphics;
import org.agmas.minetok.client.api.Sprite;
import org.agmas.minetok.client.vlcj.YTDLPDownloader;
import org.agmas.minetok.porting.MTID;

public class YTDLPOptionSprite extends Sprite {

    public Component component;

    public YTDLPOptionSprite(Component component) {
        this.component = component;
    }

    @Override
    public void render(GuiGraphics graphics, PhoneTapInformation mouse, float renderDelta) {
        graphics.pose().pushPose();
        if (mouse.y+40 > y-6 && mouse.y+40 < y+54) {
            graphics.fill(-6,-6,54,54,-1);
            if (mouse.justReleased) {
                YTDLPDownloader.downloadYTDLP();
                SettingsApplicationSprite.dirty = true;
            }
          }
        graphics.blit(MTID.of("textures/gui/checkbox" + (YTDLPDownloader.checkYtDLP() ? "_ticked.png" : ".png")).id,0,0,48,48,0,1,0,1);
        graphics.pose().pushPose();
        MTGraphics.translate(graphics, 54,0);
        MTGraphics.scale(graphics, 5,5);
        graphics.drawString(Minecraft.getInstance().font, component, 0,0,-1);
        graphics.pose().popPose();
        graphics.pose().popPose();
    }
}
