package org.agmas.minetok.client.render.sprites;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.agmas.minetok.client.MinetokClient;
import org.agmas.minetok.api.PhoneTapInformation;
import org.agmas.minetok.client.api.Sprite;
import org.agmas.minetok.client.render.Phone;
import org.agmas.minetok.porting.MTIdentifier;

import java.awt.*;

public class ExitButtonSprite extends Sprite {
    @Override
    public void render(GuiGraphicsExtractor graphics, PhoneTapInformation mouse, float renderDelta) {
        super.render(graphics, mouse, renderDelta);
        renderTopBar(graphics,mouse,renderDelta);
    }
    public void renderTopBar(GuiGraphicsExtractor graphics, PhoneTapInformation mouse, float renderDelta) {
        graphics.fill(0,0, Phone.SCREEN_WIDTH, 138, new Color(100,100,100,50).getRGB());
        graphics.blit(MTIdentifier.of("textures/gui/close.png").id, 16, 84, 16+48, 132, 0, 1, 0, 1);
        if (mouse.justReleased) {
            if (mouse.x+40 > 16 && mouse.y+40 > 84) {
                if (mouse.x + 40 < 48 && mouse.y + 40 < 132) {
                    MinetokClient.phone.topSprite.closing = true;
                    MinetokClient.phone.topSprite.openingAppAnimation = 1;
                    mouse.justReleased = false;
                }
            }
        }
    }
}
