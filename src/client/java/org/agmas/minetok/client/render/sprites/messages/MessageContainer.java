package org.agmas.minetok.client.render.sprites.messages;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import org.agmas.minetok.api.PhoneTapInformation;
import org.agmas.minetok.client.api.MTGraphics;
import org.agmas.minetok.client.api.Sprite;
import org.agmas.minetok.network.NetMessage;

import java.awt.*;

public class MessageContainer extends Sprite {


    MultiLineTextWidget messageWidget;
    NetMessage message;
    int padding = 8;

    public MessageContainer(NetMessage message) {
        this.message = message;
        messageWidget = new MultiLineTextWidget(message.message(), Minecraft.getInstance().font);
        messageWidget.setMaxWidth(100);
    }

    public int getHeight() {
        return (padding*2) + (messageWidget.getHeight()*4);
    }
    public int getWidth() {
        return (padding*2) + (messageWidget.getWidth()*4);
    }

    @Override
    public void render(GuiGraphics graphics, PhoneTapInformation mouse, float renderDelta) {
        messageWidget.visitLines(graphics.textRenderer());

        graphics.pose().pushMatrix();

        MTGraphics.translate(graphics, x,y);
        graphics.fill(0,0,getWidth(),getHeight(), message.receiving() ? Color.LIGHT_GRAY.getRGB() : new Color(200,200,255).getRGB());

        MTGraphics.translate(graphics, padding,padding);
        MTGraphics.scale(graphics,4);
        messageWidget.render(graphics,0,0, renderDelta);
        graphics.pose().popMatrix();
        super.render(graphics, mouse, renderDelta);
    }
}
