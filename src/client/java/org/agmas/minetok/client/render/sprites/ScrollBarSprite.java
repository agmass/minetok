package org.agmas.minetok.client.render.sprites;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import org.agmas.minetok.api.PhoneTapInformation;
import org.agmas.minetok.client.MinetokClient;
import org.agmas.minetok.client.api.NetworkSprite;
import org.agmas.minetok.client.api.Sprite;
import org.agmas.minetok.client.render.Phone;

import java.awt.*;

public class ScrollBarSprite extends Sprite implements NetworkSprite {
    public float prevScroll = 0;
    public float scroll = 0;
    public float timeSinceUpdate = 0;
    public float netScroll = 0;

    public int width = 32;
    public int height = Phone.SCREEN_HEIGHT-400;
    public int scrollBarHeight = 200;

    public ScrollBarSprite(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public ScrollBarSprite(int x, int y, int width, int height, int scrollBarHeight) {
        this(x,y);
        this.width = width;
        this.height = height;
        this.scrollBarHeight = scrollBarHeight;
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, PhoneTapInformation mouse, float renderDelta) {
        int scrollBarY = (int) Mth.lerp(scroll, 0, (height-scrollBarHeight));
        graphics.fill(x,y, x+width,y+height,Color.DARK_GRAY.getRGB());

        graphics.fill(x,y+scrollBarY,x+width,y+(scrollBarY+scrollBarHeight), Color.LIGHT_GRAY.getRGB());

        if (mouse.tapping && mouse.tappedX+40 > x && mouse.tappedX+40 < x+width && mouse.tappedY+40 > y && mouse.tappedY+40 < y+height) {
            scroll = prevScroll - ((mouse.tappedY- mouse.y)/height);
            if (scroll < 0) scroll = 0;
            if (scroll > 1) scroll = 1;
            MinetokClient.phone.setDirty();
        } else {
            if (mouse.isClient) {
                prevScroll = scroll;
            } else {
                timeSinceUpdate += renderDelta;
                scroll = Mth.lerp(Mth.clamp(timeSinceUpdate*20f, 0, 1), prevScroll, netScroll);
            }
        }
        super.render(graphics, mouse, renderDelta);
    }

    @Override
    public CompoundTag createTag() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("scroll", scroll);
        return tag;
    }

    @Override
    public void sync(CompoundTag phoneData) {
        netScroll = phoneData.getFloatOr("scroll", 0);
        timeSinceUpdate = 0;
    }
}
