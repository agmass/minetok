package org.agmas.minetok.client.render.sprites.messages;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.network.chat.Component;
import org.agmas.minetok.api.PhoneTapInformation;
import org.agmas.minetok.client.api.MTGraphics;
import org.agmas.minetok.client.api.Sprite;
import org.agmas.minetok.network.NetMessage;

import java.awt.*;
import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoField;

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
        return (padding*2) + (messageWidget.getHeight()*4) + 8;
    }
    public int getWidth() {
        return (padding*2) + (messageWidget.getWidth()*4);
    }

    @Override
    public void render(GuiGraphics graphics, PhoneTapInformation mouse, float renderDelta) {
        graphics.pose().pushMatrix();

        MTGraphics.translate(graphics, x,y);
        graphics.fill(0,0,getWidth(),getHeight()-8, message.receiving() ? Color.LIGHT_GRAY.getRGB() : new Color(200,200,255).getRGB());

        Instant instant = Instant.ofEpochMilli(message.timestamp());

        Component timecomp = Component.literal(Date.from(instant).getHours() + ":" + Date.from(instant).getMinutes());

        graphics.drawString(Minecraft.getInstance().font, timecomp,(getWidth()-(Minecraft.getInstance().font.width(timecomp)/2))-16,getHeight()-3,Color.LIGHT_GRAY.getRGB());
        MTGraphics.translate(graphics, padding,padding);
        MTGraphics.scale(graphics,4);
        //? if <26.1
        messageWidget.render(graphics,0,0, renderDelta);
        //? if >=26.1
        //messageWidget.extractRenderState(graphics,0,0, renderDelta);

        graphics.pose().popMatrix();
        super.render(graphics, mouse, renderDelta);
    }
}
