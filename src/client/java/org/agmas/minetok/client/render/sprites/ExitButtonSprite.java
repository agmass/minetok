package org.agmas.minetok.client.render.sprites;

import net.minecraft.client.gui.GuiGraphics;
import org.agmas.minetok.client.MinetokClient;
import org.agmas.minetok.api.PhoneTapInformation;
import org.agmas.minetok.client.api.MTGraphics;
import org.agmas.minetok.client.api.Sprite;
import org.agmas.minetok.client.render.Phone;
import org.agmas.minetok.porting.MTID;

import java.awt.*;

public class ExitButtonSprite extends Sprite {

    @Override
    public void render(GuiGraphics graphics, PhoneTapInformation mouse, float renderDelta) {
        super.render(graphics, mouse, renderDelta);
        renderTopBar(graphics,mouse,renderDelta);
    }

    public boolean pressedExit = false;
    public boolean padded = true;
    public CloseBehaviour closeBehaviour = CloseBehaviour.CLOSE_APP;
    public int width = Phone.SCREEN_WIDTH;

    public void exitBarCapture(PhoneTapInformation mouse) {
        pressedExit = false;
        if (MTGraphics.hoveringButton(mouse,x+16,((padded ? 84 : 2)+y),48,48)) {
            if (mouse.justReleased) {
                pressedExit = true;
                mouse.justTapped = false;
                mouse.justReleased = false;
            }
        }
    }
    public void renderTopBar(GuiGraphics graphics, PhoneTapInformation mouse, float renderDelta) {
        graphics.fill(x,y, x+width, y+(padded ? 138 : 54), new Color(100,100,100,50).getRGB());
        if (MTGraphics.hoveringButton(mouse,x+16,((padded ? 84 : 2)+y),48,48)) {
            graphics.fill(x+12,y+(padded ? 84 : 2)-4,x+(16+48)+4,y+(padded ? 132 : 50)+4,-1);
        }
        MTGraphics.blit(graphics, MTID.of("textures/gui/close.png").id, x+16, y+(padded ? 84 : 2), x+(16+48), y+(padded ? 132 : 50), 0, 1, 0, 1);
        if (pressedExit) {
            if (closeBehaviour.equals(CloseBehaviour.CLOSE_OVERLAY)) {
                pressedExit = false;
                MinetokClient.phone.topSprite.overlays.removeLast();
                mouse.justReleased = false;
            } else if (closeBehaviour.equals(CloseBehaviour.CLOSE_APP)) {
                pressedExit = false;
                MinetokClient.phone.topSprite.closing = true;
                MinetokClient.phone.topSprite.openingAppAnimation = 1;
                mouse.justReleased = false;
                MinetokClient.phone.setDirty();
            }
        }
    }

    public enum CloseBehaviour {
        CLOSE_APP,
        CLOSE_OVERLAY,
        HANDLED
    }
}
