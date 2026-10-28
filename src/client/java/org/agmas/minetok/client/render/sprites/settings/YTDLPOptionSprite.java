package org.agmas.minetok.client.render.sprites.settings;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import org.agmas.minetok.api.PhoneTapInformation;
import org.agmas.minetok.client.api.Sprite;
import org.agmas.minetok.client.vlcj.YTDLPDownloader;
import org.agmas.minetok.porting.MTIdentifier;

public class YTDLPOptionSprite extends Sprite {

    public Component component;

    public YTDLPOptionSprite(Component component) {
        this.component = component;
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, PhoneTapInformation mouse, float renderDelta) {
        graphics.pose().pushMatrix();
        if (mouse.y+40 > y-6 && mouse.y+40 < y+54) {
            graphics.fill(-6,-6,54,54,-1);
            if (mouse.justReleased) {
                YTDLPDownloader.downloadYTDLP();
                SettingsApplicationSprite.dirty = true;
            }
          }
        graphics.blit(MTIdentifier.of("textures/gui/checkbox" + (YTDLPDownloader.checkYtDLP() ? "_ticked.png" : ".png")).id,0,0,48,48,0,1,0,1);
        graphics.pose().pushMatrix();
        graphics.pose().translate(54,0);
        graphics.pose().scale(5,5);
        graphics.text(Minecraft.getInstance().font, component, 0,0,-1);
        graphics.pose().popMatrix();
        graphics.pose().popMatrix();
    }
}
