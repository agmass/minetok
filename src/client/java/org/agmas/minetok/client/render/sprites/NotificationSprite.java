package org.agmas.minetok.client.render.sprites;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.util.EasingType;
import net.minecraft.util.Mth;
import org.agmas.minetok.client.api.PhoneTapInformation;
import org.agmas.minetok.client.api.Sprite;
import org.agmas.minetok.client.render.Phone;

import java.awt.*;

public class NotificationSprite extends Sprite {

    static Color topBarColor = new Color(133, 133, 133,255);
    static Color bgColor = new Color(200,200,200,255);
    static Component error = Component.translatable("phone.error");
    static Component notification = Component.translatable("phone.notification");
    static Component tap_continue = Component.translatable("phone.tap_continue");

    public Component notificationText;
    public boolean isError;

    MultiLineTextWidget messageWidget;
    float time = 0;
    boolean accepted = false;
    Phone parent;

    public NotificationSprite(Component text, boolean isError, Phone parent) {
        this.isError = isError;
        this.notificationText = text;
        messageWidget = new MultiLineTextWidget(text, Minecraft.getInstance().font);
        messageWidget.setMaxWidth(400);
        this.parent = parent;
        parent.topSprite.overlays.add(this);
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, PhoneTapInformation mouse, float renderDelta) {
        super.render(graphics, mouse, renderDelta);
        time += (accepted ? -renderDelta : renderDelta)/20f;
        if (time > 1) time = 1;
        if (time <= 0) {
            time = 0;
            if (accepted) {
                parent.topSprite.overlays.remove(this);
                return;
            }
        }
        if (mouse.justTapped) accepted = true;
        graphics.fill(-100,-100, Phone.SCREEN_WIDTH+100, Phone.SCREEN_HEIGHT+100, new Color(0,0,0, EasingType.OUT_CIRC.apply(time)*0.5f).getRGB());

        int padding = 32;
        int topBarHeight = 48;

        int height = topBarHeight + padding + (messageWidget.getHeight()*4);
        int width = padding + 400;

        graphics.fill((Phone.SCREEN_WIDTH - width)/2, Phone.SCREEN_HEIGHT/2 - height/2, (Phone.SCREEN_WIDTH + width)/2, (Phone.SCREEN_HEIGHT/2 + height) - topBarHeight, bgColor.getRGB());
        graphics.fill((Phone.SCREEN_WIDTH - width)/2, Phone.SCREEN_HEIGHT/2 - height/2, (Phone.SCREEN_WIDTH + width)/2, (Phone.SCREEN_HEIGHT/2 - height/2) + topBarHeight, topBarColor.getRGB());

        Component topBarText = isError ? error : notification;
        int errorWidth = Minecraft.getInstance().font.width(topBarText)*4;

        graphics.pose().pushMatrix();
        graphics.pose().translate((Phone.SCREEN_WIDTH - width)/2f, Phone.SCREEN_HEIGHT/2f - height/2f);
        graphics.pose().translate(width/2f - errorWidth/2f,0);
        graphics.pose().scale(4);
        graphics.text(Minecraft.getInstance().font, topBarText, 0,2,Color.LIGHT_GRAY.getRGB());
        graphics.pose().popMatrix();

        graphics.pose().pushMatrix();
        graphics.pose().translate(Phone.SCREEN_WIDTH/2f - width/2f, Phone.SCREEN_HEIGHT/2f - height/2f);
        graphics.pose().translate(padding/2f,height/2f);
        graphics.pose().scale(4);
        messageWidget.setCentered(true);
        messageWidget.setMaxWidth(100);
        messageWidget.setWidth(100);
        messageWidget.visitLines(graphics.textRenderer());
        messageWidget.extractRenderState(graphics, (int) mouse.x, (int) mouse.y,renderDelta);
        graphics.pose().popMatrix();


        graphics.pose().pushMatrix();
        graphics.pose().translate(Phone.SCREEN_WIDTH/2f, Phone.SCREEN_HEIGHT/2f + 200);
        graphics.pose().translate(-(Minecraft.getInstance().font.width(tap_continue)*3)/2f,0f);
        graphics.pose().scale(3);
        graphics.text(Minecraft.getInstance().font,tap_continue,0,0,-1);
        graphics.pose().popMatrix();
    }
}
