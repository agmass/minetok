package org.agmas.minetok.client.render.sprites.calls;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

//? if >=26.1 {
/*import net.minecraft.client.renderer.state.gui.PanoramaRenderState;
*///? }
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.client.multiplayer.PlayerInfo;
import org.agmas.minetok.api.PhoneTapInformation;
import org.agmas.minetok.client.animation.MTGraphics;
import org.agmas.minetok.client.animation.NBTHelper;
import org.agmas.minetok.client.api.NetworkSprite;
import org.agmas.minetok.client.api.Sprite;
import org.agmas.minetok.client.render.Phone;
import org.agmas.minetok.client.render.sprites.PanoramaSprite;
import org.agmas.minetok.network.ServerboundCallResponsePacket;
import org.agmas.minetok.porting.MTID;

import java.awt.*;
import java.util.UUID;

public class CallingSprite extends Sprite implements NetworkSprite {

    public boolean receiving;
    public UUID otherPlayer;
    public PanoramaSprite panoramaSprite = new PanoramaSprite();

    final MTID HANG_UP = MTID.of("textures/gui/hang_up.png");
    final MTID PICK_UP = MTID.of("textures/gui/pick_up.png");

    public  CallingSprite(boolean receiving, UUID otherPlayer) {
        this.receiving = receiving;
        this.otherPlayer = otherPlayer;
    }

    @Override
    public void render(GuiGraphics graphics, PhoneTapInformation mouse, float renderDelta) {
        super.render(graphics, mouse, renderDelta);

        //? if >1.21.1
        graphics.guiRenderState.reset();
        panoramaSprite.render(graphics,mouse,renderDelta);

        PlayerInfo info = Minecraft.getInstance().getConnection().getPlayerInfo(otherPlayer);
        if (info != null) {
            graphics.pose().pushMatrix();
            MTGraphics.translate(graphics, (Phone.SCREEN_WIDTH / 2f) , (Phone.SCREEN_HEIGHT / 2f) - 150);
            PlayerFaceRenderer.
                    //? if <=1.21.11
                            draw
                    //? if >1.21.11
                    //extractRenderState
                (graphics, info.getSkin(), -128,-128,256);
            MTGraphics.translate(graphics, -(Minecraft.getInstance().font.width(info.getProfile().name())*3) , 0);
            MTGraphics.translate(graphics, 0,256);
            MTGraphics.scale(graphics, 6,6);
            graphics.drawString(Minecraft.getInstance().font, info.getProfile().name(), 0,-9,-1);
            graphics.pose().popMatrix();
        }

        int marginX = 80;
        int marginY = 450;
        int size = 200;

        if (receiving) {
            MTGraphics.blit(graphics, HANG_UP.id, marginX, Phone.SCREEN_HEIGHT - marginY, (marginX + size), Phone.SCREEN_HEIGHT - (marginY - size), 0, 1, 0, 1);
            if (mouse.x > marginX && mouse.x < (marginX + size)) {
                if (mouse.y > Phone.SCREEN_HEIGHT - marginY && mouse.y < Phone.SCREEN_HEIGHT - (marginY - size)) {
                    graphics.fill(marginX, Phone.SCREEN_HEIGHT - marginY, marginX + size, Phone.SCREEN_HEIGHT - (marginY - size), new Color(100, 100, 100, 100).getRGB());
                    if (mouse.justTapped) {
                        ClientPlayNetworking.send(new ServerboundCallResponsePacket(false));
                    }
                }
            }

            MTGraphics.blit(graphics, PICK_UP.id, Phone.SCREEN_WIDTH - (marginX + size), Phone.SCREEN_HEIGHT - marginY, Phone.SCREEN_WIDTH - marginX, Phone.SCREEN_HEIGHT - (marginY - size), 0, 1, 0, 1);
            if (mouse.x > Phone.SCREEN_WIDTH - (marginX + size) && mouse.x < Phone.SCREEN_WIDTH - marginX) {
                if (mouse.y > Phone.SCREEN_HEIGHT - marginY && mouse.y < Phone.SCREEN_HEIGHT - (marginY - size)) {
                    graphics.fill(Phone.SCREEN_WIDTH - (marginX + size), Phone.SCREEN_HEIGHT - marginY, Phone.SCREEN_WIDTH - marginX, Phone.SCREEN_HEIGHT - (marginY - size), new Color(100, 100, 100, 100).getRGB());
                    if (mouse.justTapped) {
                        ClientPlayNetworking.send(new ServerboundCallResponsePacket(true));
                    }
                }
            }
        } else {
            MTGraphics.blit(graphics, HANG_UP.id, (Phone.SCREEN_WIDTH/2) - (size/2), Phone.SCREEN_HEIGHT - marginY, (Phone.SCREEN_WIDTH/2) + (size/2), Phone.SCREEN_HEIGHT - (marginY - size), 0, 1, 0, 1);
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
        receiving = NBTHelper.getBooleanOr(phoneData, "receiving", true);
    }
}
