package org.agmas.minetok.client.render.sprites.messages;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.agmas.minetok.api.PhoneTapInformation;
import org.agmas.minetok.client.MinetokClient;
import org.agmas.minetok.client.animation.EasingHelper;
import org.agmas.minetok.client.api.MTGraphics;
import org.agmas.minetok.client.render.Phone;
import org.agmas.minetok.client.render.sprites.ExitButtonSprite;
import org.agmas.minetok.network.NetMessage;
import org.agmas.minetok.network.ServerboundRequestMessagesPacket;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MessagingContainerSprite extends ExitButtonSprite {

    float transition = 0;
    boolean closing = false;
    boolean requesting = false;
    public List<NetMessage> messages = new ArrayList<>();
    public List<MessageContainer> messageSprites = new ArrayList<>();
    float scroll = 0;
    EditBox chatbox = new EditBox(Minecraft.getInstance().font, 0,0,120,10, Component.empty());
    public PlayerInfo other;
    public MessagingContainerSprite(PlayerInfo other) {
        closeBehaviour = CloseBehaviour.HANDLED;
        this.other = other;
    }

    @Override
    public void render(GuiGraphics graphics, PhoneTapInformation mouse, float renderDelta) {
        if (!closing) {
            transition += renderDelta;
        } else {
            transition -= renderDelta;
        }
        transition = Math.clamp(transition,0,1);
        exitBarCapture(mouse);
        graphics.pose().pushMatrix();
        MTGraphics.translate(graphics,(float)((1.0-EasingHelper.outCirc(transition))*1280),(float)y);
        MTGraphics.blitSprite(graphics, Identifier.withDefaultNamespace("textures/block/white_wool.png"), -8,-8,0,0, Phone.SCREEN_WIDTH+16, Phone.SCREEN_HEIGHT+16,128,128);



        if (!requesting && messages.isEmpty()) {
            requesting = true;
            ClientPlayNetworking.send(new ServerboundRequestMessagesPacket(other.getProfile().id().toString(), 0));
        }


        if (messageSprites.size() != messages.size()) {
            messageSprites.clear();
            for (NetMessage message : messages) {
                messageSprites.add(new MessageContainer(message));
            }
        }
        int messageSpacing = 12;
        int y = Phone.SCREEN_HEIGHT - 240;
        for (MessageContainer message : messageSprites) {
            y -= message.getHeight();
            message.x = message.message.receiving() ? 16 : (Phone.SCREEN_WIDTH - message.getWidth()) - 16;
            message.y = y;
            message.render(graphics,mouse,renderDelta);
            y -= messageSpacing;
        }

        if (pressedExit) {
            closing = true;
            pressedExit = false;
        }
        graphics.pose().popMatrix();


        chatbox.setEditable(true);

        graphics.pose().pushMatrix();
        MTGraphics.translate(graphics, 16, Phone.SCREEN_HEIGHT-200);
        MTGraphics.scale(graphics, 5);

        //? if >1.21.11
        //textField.extractRenderState(graphics, (int) mouse.x, (int) mouse.y,renderDelta);
        //? if <=1.21.11
        chatbox.render(graphics, (int) mouse.x, (int) mouse.y, renderDelta);
        if (MTGraphics.hoveringButton(mouse,16,Phone.SCREEN_HEIGHT-200,600,70)) {
            if (mouse.justTapped) {
                MinetokClient.phone.selectedElement = chatbox;
            }
        }
        chatbox.setFocused(MinetokClient.phone.selectedElement == chatbox);
        graphics.pose().popMatrix();
        super.render(graphics, mouse, renderDelta);

        graphics.pose().pushMatrix();
        MTGraphics.translate(graphics,(float)((1.0-EasingHelper.outCirc(transition))*1280),0);

        PlayerFaceRenderer.
                //? if <=1.21.11
                        draw
                //? if >1.21.11
                //extractRenderState
                        (graphics, other.getSkin(), 80,84, 48);
        graphics.pose().pushMatrix();
        MTGraphics.translate(graphics, 140,84+8);
        MTGraphics.scale(graphics, 4);
        graphics.drawString(Minecraft.getInstance().font, other.getProfile().name(), 0, 0, -1);
        graphics.pose().popMatrix();

    }
}
