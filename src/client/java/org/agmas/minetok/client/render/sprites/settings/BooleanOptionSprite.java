package org.agmas.minetok.client.render.sprites.settings;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.agmas.minetok.api.PhoneTapInformation;
import org.agmas.minetok.client.MinetokClient;
import org.agmas.minetok.client.api.Sprite;
import org.agmas.minetok.porting.MTIdentifier;

public class BooleanOptionSprite extends Sprite {

    public boolean ticked;
    public Component component;

    public BooleanOptionSprite(Component component, boolean ticked) {
        this.ticked = ticked;
        this.component = component;
    }

    @Override
    public void render(GuiGraphics graphics, PhoneTapInformation mouse, float renderDelta) {
        graphics.pose().pushMatrix();
        if (mouse.y+40 > y-6 && mouse.y+40 < y+54) {
            graphics.fill(-6,-6,54,54,-1);
            if (mouse.justReleased) {
                ticked = !ticked;
                SettingsApplicationSprite.dirty = true;
            }
          }
        graphics.blit(MTIdentifier.of("textures/gui/checkbox" + (ticked ? "_ticked.png" : ".png")).id,0,0,48,48,0,1,0,1);
        graphics.pose().pushMatrix();
        graphics.pose().translate(54,0);
        graphics.pose().scale(6,6);
        graphics.drawString(Minecraft.getInstance().font, component, 0,0,-1);
        graphics.pose().popMatrix();
        graphics.pose().popMatrix();
    }
}
