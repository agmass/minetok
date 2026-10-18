package org.agmas.minetok.client.api;

import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.ArrayList;

public class Sprite {
    public int x = 0;
    public int y = 0;
    public ArrayList<Sprite> children = new ArrayList<>();

    public void render(GuiGraphicsExtractor graphics, PhoneTapInformation mouse, float renderDelta) {
        for (Sprite child : children) {
            child.render(graphics,mouse,renderDelta);
        }
    }
}
