package org.agmas.minetok.client.render.sprites.messages;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.agmas.minetok.Minetok;
import org.agmas.minetok.api.PhoneTapInformation;
import org.agmas.minetok.client.MinetokClient;
import org.agmas.minetok.client.animation.EasingHelper;
import org.agmas.minetok.client.api.MTGraphics;
import org.agmas.minetok.client.api.NetworkSprite;
import org.agmas.minetok.client.render.Phone;
import org.agmas.minetok.client.render.sprites.ExitButtonSprite;
import org.agmas.minetok.network.NetMessage;
import org.agmas.minetok.network.ServerboundRequestMessagesPacket;
import org.agmas.minetok.network.ServerboundSendMessagePacket;
import org.agmas.minetok.porting.MTID;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MessagingContainerSprite extends ExitButtonSprite implements NetworkSprite {

    float transition = 0;
    boolean closing = false;
    public boolean requesting = false;
    public List<NetMessage> messages = new ArrayList<>();
    public List<MessageContainer> messageSprites = new ArrayList<>();
    float scroll = 0;
    float lastScroll = 0;
    public boolean nextMessageMoves = false;
    float forceScroll = 0;
    int page = 0;
    float forceScrollTime = 0;
    EditBox chatbox = new EditBox(Minecraft.getInstance().font, 0,0,120,10, Component.empty());
    public PlayerInfo other;
    public MessagingContainerSprite(PlayerInfo other) {
        closeBehaviour = CloseBehaviour.HANDLED;
        chatbox.setMaxLength(6400);
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

        if (mouse.tapping) {
            scroll = lastScroll - ((mouse.tappedY-mouse.y)*0.8f);
        } else {
            lastScroll = scroll;
        }
        if (scroll < 0) {
            scroll = Mth.lerp(0.3333f, scroll, 0);
        }

        graphics.pose().pushMatrix();
        MTGraphics.translate(graphics,(float)((1.0-EasingHelper.outCirc(transition))*1280),(float)y);
        MTGraphics.blitSprite(graphics, Identifier.withDefaultNamespace("textures/block/white_wool.png"), -8,-8,0,0, Phone.SCREEN_WIDTH+16, Phone.SCREEN_HEIGHT+16,128,128);



        if (!requesting && messages.isEmpty()) {
            requesting = true;
            ClientPlayNetworking.send(new ServerboundRequestMessagesPacket(other.getProfile().id().toString(), 0));
        }


        if (messageSprites.size() != messages.size()) {
            messageSprites.clear();
            for (NetMessage message : messages.reversed()) {
                messageSprites.add(new MessageContainer(message));
            }
            if (nextMessageMoves) {
                nextMessageMoves = false;
                forceScroll = messageSprites.getFirst().getHeight();
                forceScrollTime = 1;
            }
        }
        int messageSpacing = 12;
        int y = Phone.SCREEN_HEIGHT - 240;

        graphics.pose().pushMatrix();
        forceScrollTime -= renderDelta;
        forceScrollTime = Mth.clamp(forceScrollTime,0,1);
        MTGraphics.translate(graphics, 0, scroll + Mth.lerp(EasingHelper.outCirc(forceScrollTime), 0, forceScroll));
        for (MessageContainer message : messageSprites) {
            y -= message.getHeight();
            message.x = message.message.receiving() ? 16 : (Phone.SCREEN_WIDTH - message.getWidth()) - 16;
            message.y = y;
            message.render(graphics,mouse,renderDelta);
            y -= messageSpacing;
        }
        if ((scroll + 500) > Math.abs(y) && !requesting) {
            page++;
            requesting = true;
            ClientPlayNetworking.send(new ServerboundRequestMessagesPacket(other.getProfile().id().toString(), page));
        }
        graphics.pose().popMatrix();

        if (pressedExit) {
            closing = true;
            pressedExit = false;
            ClientPlayNetworking.send(new ServerboundRequestMessagesPacket(Minecraft.getInstance().player.getStringUUID(),0));
        }
        graphics.pose().popMatrix();


        chatbox.setEditable(true);

        graphics.pose().pushMatrix();
        MTGraphics.translate(graphics, 16, Phone.SCREEN_HEIGHT-200);
        MTGraphics.scale(graphics, 5);

        //? if >1.21.11
        //chatbox.extractRenderState(graphics, (int) mouse.x, (int) mouse.y,renderDelta);
        //? if <=1.21.11
        chatbox.render(graphics, (int) mouse.x, (int) mouse.y, renderDelta);
        if (MTGraphics.hoveringButton(mouse,16,Phone.SCREEN_HEIGHT-200,600,70)) {
            if (mouse.justTapped) {
                MinetokClient.phone.selectedElement = chatbox;
            }
        }
        chatbox.setFocused(MinetokClient.phone.selectedElement == chatbox);
        graphics.pose().popMatrix();

        int xfors = chatbox.getX()+(chatbox.getWidth()*5) + 32;
        if (MTGraphics.hoveringButton(mouse,xfors,Phone.SCREEN_HEIGHT-200,48,48)) {
            graphics.fill(xfors-4,Phone.SCREEN_HEIGHT-205, xfors+52, (Phone.SCREEN_HEIGHT-200)+52,-1);
            if (mouse.justReleased) {
                send();
            }
        }
        MTGraphics.blit(graphics, MTID.of("textures/gui/send.png").id,xfors,Phone.SCREEN_HEIGHT-200, xfors+48, (Phone.SCREEN_HEIGHT-200)+48,0,1,0,1);

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

    public void send() {
        String s = chatbox.getValue();
        if (!s.isEmpty()) {
            ClientPlayNetworking.send(new ServerboundSendMessagePacket(other.getProfile().id().toString(), s));
        }
        chatbox.setValue("");
    }

    @Override
    public void charTypedWhileFocused(long l) {
        super.charTypedWhileFocused(l);
        Minetok.LOGGER.info(l+"");
        //? if <26.3
        if (l == 257) {
        //? if >=26.3
        //if (l == 40) {
            send();
        }
    }

    @Override
    public CompoundTag createTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString("other", other.getProfile().id().toString());
        return tag;
    }

    @Override
    public void sync(CompoundTag phoneData) {

    }
}
