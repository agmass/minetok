package org.agmas.minetok.client.animation;

import net.minecraft.client.gui.GuiGraphics;

/*
    Helper class cuz versions have weird GuiGraphics changes.
    Maybe this could be used to port to 1.19....... Curious................
 */
public class MTGraphics {
    public static void scale(GuiGraphics graphics, float s) {
        //? if >1.21.1
        //graphics.pose().scale(s);
        //? if <=1.21.1
        graphics.pose().scale(s,s,1);
    }
    public static void scale(GuiGraphics graphics, float x, float y) {
        //? if >1.21.1
        //graphics.pose().scale(x,y);
        //? if <=1.21.1
        graphics.pose().scale(x,y,1);
    }

    public static void translate(GuiGraphics graphics, float x, float y) {
        //? if >1.21.1
        //graphics.pose().translate(x,y);
        //? if <=1.21.1
        graphics.pose().translate(x,y,0);
    }
}
