package org.agmas.minetok.client.api;

import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.Nullable;

public class Sprite {
    public int x = 0;
    public int y = 0;
    @Nullable
    public Application application = null;
    @Nullable
    public Application overlay = null;

    public void render(GuiGraphics graphics, org.agmas.minetok.api.PhoneTapInformation mouse, float renderDelta) {}

}
