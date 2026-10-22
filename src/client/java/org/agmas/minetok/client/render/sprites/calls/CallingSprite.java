package org.agmas.minetok.client.render.sprites.calls;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

//? if >=26.1 {
/*import net.minecraft.client.renderer.state.gui.PanoramaRenderState;
*///? }
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import org.agmas.minetok.client.MinetokClient;
import net.minecraft.client.multiplayer.PlayerInfo;
import org.agmas.minetok.api.PhoneTapInformation;
import org.agmas.minetok.client.api.Sprite;
import org.agmas.minetok.client.render.Phone;
import org.agmas.minetok.porting.MTIdentifier;

import java.util.UUID;

public class CallingSprite extends Sprite {

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

        graphics.blit(HANG_UP.id, marginX, Phone.SCREEN_HEIGHT - marginY, (marginX+size), Phone.SCREEN_HEIGHT - (marginY-size), 0,1,0,1);
        graphics.blit(PICK_UP.id, Phone.SCREEN_WIDTH-(marginX+size), Phone.SCREEN_HEIGHT - marginY, Phone.SCREEN_WIDTH-marginX, Phone.SCREEN_HEIGHT - (marginY-size), 0,1,0,1);

    }
}
