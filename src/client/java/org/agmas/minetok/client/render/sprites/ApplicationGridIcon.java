package org.agmas.minetok.client.render.sprites;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.agmas.minetok.client.MinetokClient;
import org.agmas.minetok.client.api.Application;
import org.agmas.minetok.api.PhoneTapInformation;
import org.agmas.minetok.client.api.ApplicationSprite;
import org.agmas.minetok.client.api.Sprite;

import java.awt.*;

public class ApplicationGridIcon extends Sprite {

    public static int APP_GRID_ICON_HEIGHT = 128+32+6;
    Identifier appId;

    public ApplicationGridIcon(Identifier appId, Application application) {
        this.appId = appId;
        this.application = application;
    }

    public void render(GuiGraphicsExtractor graphics, PhoneTapInformation mouse, float renderDelta, HomeScreenSprite parent) {
        super.render(graphics, mouse, renderDelta);

        boolean selected = false;

        if (mouse.x+40 > x && mouse.y+40 > y) {
            if (mouse.x+40 < x+128 && mouse.y+40 < y+128) {
                selected = true;
            }
        }

        graphics.pose().pushMatrix();
        graphics.pose().translate(x,y);

        graphics.blit(appId.withPrefix("textures/apps/"), 0,0,128,128,0,1,0,1);
        if (selected) {
            graphics.fill(0,0,128,128,new Color(100,100,100,50).getRGB());
        }
        graphics.pose().popMatrix();

        graphics.pose().pushMatrix();
        Component text = Component.translatable("phone.app." + appId.getNamespace() + "." + appId.getPath());
        graphics.pose().translate(x,y);
        graphics.pose().translate(64,0);

        // "erm, agmas, isn't this meant to be divided by 2!!!" yeah but we multiply it by 4 right after. quiet down little chudlet in my brain that i just imagined of.
        graphics.pose().translate(-(Minecraft.getInstance().font.width(text)*2),128+6);
        graphics.pose().scale(4);

        graphics.text(Minecraft.getInstance().font, text, 0, 0, selected ? Color.WHITE.getRGB() : Color.LIGHT_GRAY.getRGB());
        graphics.pose().popMatrix();

        if (selected && mouse.justReleased && application != null) {
            if (Math.abs(mouse.tappedX-mouse.x) < 9) {
                try {
                    parent.focusedSprite = application.initializerSprite.getConstructor().newInstance();
                    parent.focusedSprite.application = application;
                    parent.openingAppAnimation = 1;
                    parent.closing = false;
                    MinetokClient.phone.setDirty();
                } catch (Exception ignored) {}
            }
        }
    }
}
