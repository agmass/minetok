package org.agmas.minetok.client.api;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.Nullable;

public class Sprite {
    public int x = 0;
    public int y = 0;
    @Nullable
    public Application application = null;
    @Nullable
    public Application overlay = null;

    public void render(GuiGraphicsExtractor graphics, org.agmas.minetok.api.PhoneTapInformation mouse, float renderDelta) {}

    public void destroy() {}
}
