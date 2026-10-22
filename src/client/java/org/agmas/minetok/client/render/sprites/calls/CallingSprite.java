package org.agmas.minetok.client.render.sprites.calls;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

//? if >=26.1 {
import net.minecraft.client.renderer.state.gui.PanoramaRenderState;
//? }
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import org.agmas.minetok.client.MinetokClient;
import net.minecraft.client.multiplayer.PlayerInfo;
import org.agmas.minetok.api.PhoneTapInformation;
import org.agmas.minetok.client.api.Sprite;
import org.agmas.minetok.client.render.Phone;

import java.util.UUID;

public class CallingSprite extends Sprite {

    public boolean receiving;
    public UUID otherPlayer;

    public  CallingSprite(boolean receiving, UUID otherPlayer) {
        this.receiving = receiving;
        this.otherPlayer = otherPlayer;
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, PhoneTapInformation mouse, float renderDelta) {
        super.render(graphics, mouse, renderDelta);

        //? if <26.1 {
        /*Minecraft.getInstance().gameRenderer.getPanorama().render(graphics,0,0,true);
         *///? } else {
        MinetokClient.phone.topSprite.spin += 0.1f * Minecraft.getInstance().getDeltaTracker().getRealtimeDeltaTicks();
        MinetokClient.phone.topSprite.panoramaRenderState = new PanoramaRenderState(MinetokClient.phone.topSprite.spin);
        MinetokClient.phone.topSprite.pano.render(10.0F, MinetokClient.phone.topSprite.panoramaRenderState.spin());
        //? }

        PlayerInfo info = Minecraft.getInstance().getConnection().getPlayerInfo(otherPlayer);
        if (info != null) {
            graphics.pose().pushMatrix();
            graphics.pose().translate((Phone.SCREEN_WIDTH / 2f) , (Phone.SCREEN_HEIGHT / 2f));
            PlayerFaceExtractor.extractRenderState(graphics, info.getSkin(), -128,-128,256);
            graphics.pose().translate(-(Minecraft.getInstance().font.width(info.getProfile().name())*3) , -24);
            graphics.pose().translate(0,256);
            graphics.pose().scale(6,6);
            graphics.text(Minecraft.getInstance().font, info.getProfile().name(), -4,-4,-1);
            graphics.pose().popMatrix();
        }

    }
}
