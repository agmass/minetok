package org.agmas.minetok.client.render.sprites;

//? if >=26.1 {
import net.minecraft.client.renderer.state.gui.PanoramaRenderState;
 //? }
//? if <=1.21.1 {
/*import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PanoramaRenderer;
*///? }
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.CubeMap;
//? if <=1.21.1
//import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.resources.Identifier;
import org.agmas.minetok.api.PhoneTapInformation;
import org.agmas.minetok.client.api.Sprite;

public class PanoramaSprite extends Sprite {

    //? if >=26.1 {
    public PanoramaRenderState panoramaRenderState = new PanoramaRenderState(0);
    public float spin = 0;
    //? }
    //? if <=1.21.1 {
    /*public static PanoramaRenderer ROTATING_PANORAMA;
    *///? }
    public CubeMap pano = new CubeMap(Identifier.withDefaultNamespace("textures/gui/title/background/panorama"));

    @Override
    public void render(GuiGraphicsExtractor graphics, PhoneTapInformation mouse, float renderDelta) {
        //? if <1.21.11 {
        /*if (ROTATING_PANORAMA == null) ROTATING_PANORAMA = new PanoramaRenderer(pano);
        ROTATING_PANORAMA.render(graphics, 1280,720,1,renderDelta);
        *///? } else if <26.1 {
        /*Minecraft.getInstance().gameRenderer.getPanorama().render(graphics,0,0,true);
        *///? } else {
        spin += 0.1f * renderDelta;
        panoramaRenderState = new PanoramaRenderState(spin);
        pano.render(10.0F, panoramaRenderState.spin());
        //? }
        super.render(graphics, mouse, renderDelta);
    }
}
