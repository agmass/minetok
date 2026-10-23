package org.agmas.minetok.client.render.sprites.calls;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

//? if >=26.1 {
/*import net.minecraft.client.renderer.state.gui.PanoramaRenderState;
*///? }
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.nbt.CompoundTag;
import org.agmas.minetok.client.MinetokClient;
import net.minecraft.client.multiplayer.PlayerInfo;
import org.agmas.minetok.api.PhoneTapInformation;
import org.agmas.minetok.client.api.NetworkSprite;
import org.agmas.minetok.client.api.Sprite;
import org.agmas.minetok.client.render.Phone;
import org.agmas.minetok.network.ServerboundCallResponsePacket;
import org.agmas.minetok.porting.MTIdentifier;

import java.awt.*;
import java.util.UUID;

public class CallingSprite extends Sprite implements NetworkSprite {

    public boolean receiving;
    public UUID otherPlayer;

    final MTIdentifier HANG_UP = MTIdentifier.of("textures/gui/hang_up.png");
    final MTIdentifier PICK_UP = MTIdentifier.of("textures/gui/pick_up.png");

    public  CallingSprite(boolean receiving, UUID otherPlayer) {
        this.receiving = receiving;
        this.otherPlayer = otherPlayer;
    }

    @Override
    public void render(GuiGraphics graphics, PhoneTapInformation mouse, float renderDelta) {
        super.render(graphics, mouse, renderDelta);

        graphics.guiRenderState.reset();
        //? if <26.1 {
        Minecraft.getInstance().gameRenderer.getPanorama().render(graphics,0,0,true);
         //? } else {
        /*MinetokClient.phone.topSprite.spin += 0.1f * Minecraft.getInstance().getDeltaTracker().getRealtimeDeltaTicks();
        MinetokClient.phone.topSprite.panoramaRenderState = new PanoramaRenderState(MinetokClient.phone.topSprite.spin);
        MinetokClient.phone.topSprite.pano.render(10.0F, MinetokClient.phone.topSprite.panoramaRenderState.spin());
        *///? }

        PlayerInfo info = Minecraft.getInstance().getConnection().getPlayerInfo(otherPlayer);
        if (info != null) {
            graphics.pose().pushMatrix();
            graphics.pose().translate((Phone.SCREEN_WIDTH / 2f) , (Phone.SCREEN_HEIGHT / 2f) - 150);
            PlayerFaceRenderer.
                    //? if <=1.21.11
                            draw
                    //? if >1.21.11
                    //extractRenderState
                (graphics, info.getSkin(), -128,-128,256);
            graphics.pose().translate(-(Minecraft.getInstance().font.width(info.getProfile().name())*3) , 0);
            graphics.pose().translate(0,256);
            graphics.pose().scale(6,6);
            graphics.drawString(Minecraft.getInstance().font, info.getProfile().name(), 0,-9,-1);
            graphics.pose().popMatrix();
        }

        int marginX = 80;
        int marginY = 450;
        int size = 200;

        if (receiving) {
            graphics.blit(HANG_UP.id, marginX, Phone.SCREEN_HEIGHT - marginY, (marginX + size), Phone.SCREEN_HEIGHT - (marginY - size), 0, 1, 0, 1);
            if (mouse.x > marginX && mouse.x < (marginX + size)) {
                if (mouse.y > Phone.SCREEN_HEIGHT - marginY && mouse.y < Phone.SCREEN_HEIGHT - (marginY - size)) {
                    graphics.fill(marginX, Phone.SCREEN_HEIGHT - marginY, marginX + size, Phone.SCREEN_HEIGHT - (marginY - size), new Color(100, 100, 100, 100).getRGB());
                    if (mouse.justTapped) {
                        ClientPlayNetworking.send(new ServerboundCallResponsePacket(false));
                    }
                }
            }

            graphics.blit(PICK_UP.id, Phone.SCREEN_WIDTH - (marginX + size), Phone.SCREEN_HEIGHT - marginY, Phone.SCREEN_WIDTH - marginX, Phone.SCREEN_HEIGHT - (marginY - size), 0, 1, 0, 1);
            if (mouse.x > Phone.SCREEN_WIDTH - (marginX + size) && mouse.x < Phone.SCREEN_WIDTH - marginX) {
                if (mouse.y > Phone.SCREEN_HEIGHT - marginY && mouse.y < Phone.SCREEN_HEIGHT - (marginY - size)) {
                    graphics.fill(Phone.SCREEN_WIDTH - (marginX + size), Phone.SCREEN_HEIGHT - marginY, Phone.SCREEN_WIDTH - marginX, Phone.SCREEN_HEIGHT - (marginY - size), new Color(100, 100, 100, 100).getRGB());
                    if (mouse.justTapped) {
                        ClientPlayNetworking.send(new ServerboundCallResponsePacket(true));
                    }
                }
            }
        } else {
            graphics.blit(HANG_UP.id, (Phone.SCREEN_WIDTH/2) - (size/2), Phone.SCREEN_HEIGHT - marginY, (Phone.SCREEN_WIDTH/2) + (size/2), Phone.SCREEN_HEIGHT - (marginY - size), 0, 1, 0, 1);
            if (mouse.x > (Phone.SCREEN_WIDTH/2f) - (size/2f) && mouse.x < (Phone.SCREEN_WIDTH/2f) + (size/2f)) {
                if (mouse.y > Phone.SCREEN_HEIGHT - marginY && mouse.y < Phone.SCREEN_HEIGHT - (marginY - size)) {
                    graphics.fill((Phone.SCREEN_WIDTH/2) - (size/2), Phone.SCREEN_HEIGHT - marginY, (Phone.SCREEN_WIDTH/2) + (size/2), Phone.SCREEN_HEIGHT - (marginY - size), new Color(100, 100, 100, 100).getRGB());
                    if (mouse.justTapped) {
                        ClientPlayNetworking.send(new ServerboundCallResponsePacket(false));
                    }
                }
            }
        }
    }

    @Override
    public CompoundTag createTag() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("receiving", receiving);
        return tag;
    }

    @Override
    public void sync(CompoundTag phoneData) {
        receiving = phoneData.getBooleanOr("receiving", true);
    }
}
