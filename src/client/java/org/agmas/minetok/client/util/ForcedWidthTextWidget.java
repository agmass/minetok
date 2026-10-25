package org.agmas.minetok.client.util;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.network.chat.Component;


public class ForcedWidthTextWidget extends MultiLineTextWidget {
    public int forcedWidth = 100;
    public ForcedWidthTextWidget(Component component, Font font, int forcedWidth) {
        super(component, font);
        this.forcedWidth = forcedWidth;
    }

    @Override
    public int getWidth() {
        return forcedWidth;
    }
}
