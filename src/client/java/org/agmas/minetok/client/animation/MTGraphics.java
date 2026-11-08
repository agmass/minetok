package org.agmas.minetok.client.animation;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;

/*
    Helper class cuz versions have weird GuiGraphicsExtractor changes.
    Maybe this could be used to port to 1.19....... Curious................
 */
public class MTGraphics {
    public static void scale(GuiGraphicsExtractor graphics, float s) {
        //? if >1.21.1
        graphics.pose().scale(s);
        //? if <=1.21.1
        //graphics.pose().scale(s,s,1);
    }
    public static void scale(GuiGraphicsExtractor graphics, float x, float y) {
        //? if >1.21.1
        graphics.pose().scale(x,y);
        //? if <=1.21.1
        //graphics.pose().scale(x,y,1);
    }

    public static void translate(GuiGraphicsExtractor graphics, float x, float y) {
        //? if >1.21.1
        graphics.pose().translate(x,y);
        //? if <=1.21.1
        //graphics.pose().translate(x,y,0);
    }

    public static void oldUp(GuiGraphicsExtractor graphics) {
        // fixes how text is rendered on older versions
        //? if <=1.21.1
        //graphics.pose().translate(0,0,1f);
    }
    public static void oldDown(GuiGraphicsExtractor graphics) {
        // fixes how text is rendered on older versions
        //? if <=1.21.1
        //graphics.pose().translate(0,0,-1f);
    }


    public static void blit(GuiGraphicsExtractor graphics, Identifier identifier, final int x0, final int y0, final int x1, final int y1, final float u0, final float u1, final float v0, final float v1) {
        //? if >1.21.1 {
        graphics.blit(identifier, x0, y0, x1, y1, u0, u1, v0, v1);
        //? }
        //? if <=1.21.1 {
        /*RenderSystem.setShaderTexture(0, identifier);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix4f = graphics.pose().last().pose();
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.addVertex(matrix4f, x0, y0, 0).setUv(u0, v0);
        bufferBuilder.addVertex(matrix4f, x0, y1, 0).setUv(u0, v1);
        bufferBuilder.addVertex(matrix4f, x1, y1, 0).setUv(u1, v1);
        bufferBuilder.addVertex(matrix4f, x1, y0, 0).setUv(u1, v0);
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
        *///? }
    }
}
