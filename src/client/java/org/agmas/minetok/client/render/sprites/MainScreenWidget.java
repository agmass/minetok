package org.agmas.minetok.client.render.sprites;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.agmas.minetok.api.PhoneTapInformation;
import org.agmas.minetok.client.animation.MTGraphics;
import org.agmas.minetok.client.api.Sprite;

public class MainScreenWidget extends Sprite {
    @Override
    public void render(GuiGraphicsExtractor graphics, PhoneTapInformation mouse, float renderDelta) {
        super.render(graphics, mouse, renderDelta);
        graphics.pose().pushMatrix();
        MTGraphics.translate(graphics, x,y);
        MTGraphics.scale(graphics, 5);
        graphics.pose().popMatrix();
    }
}
