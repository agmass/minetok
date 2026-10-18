package org.agmas.minetok.client.render.sprites.calls;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

//? if >=26.1 {
import net.minecraft.client.renderer.state.gui.PanoramaRenderState;
//? }
import org.agmas.minetok.client.MinetokClient;
import org.agmas.minetok.client.api.PhoneTapInformation;
import org.agmas.minetok.client.api.Sprite;
import org.agmas.minetok.client.render.Phone;

public class CallingSprite extends Sprite {
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


    }
}
