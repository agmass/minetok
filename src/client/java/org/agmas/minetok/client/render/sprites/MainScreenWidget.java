package org.agmas.minetok.client.render.sprites;

import net.minecraft.client.gui.GuiGraphics;
import org.agmas.minetok.api.PhoneTapInformation;
import org.agmas.minetok.client.api.Sprite;

public class MainScreenWidget extends Sprite {
    @Override
    public void render(GuiGraphics graphics, PhoneTapInformation mouse, float renderDelta) {
        super.render(graphics, mouse, renderDelta);
        graphics.pose().pushMatrix();
        graphics.pose().translate(x,y);
        graphics.pose().scale(5);
        graphics.pose().popMatrix();
    }
}
