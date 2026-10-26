package org.agmas.minetok.client.render.sprites.minetok;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import org.agmas.minetok.api.PhoneTapInformation;
import org.agmas.minetok.client.api.NetworkSprite;
import org.agmas.minetok.client.api.Sprite;

public class TikTokApplication extends Sprite implements NetworkSprite {

    VideoPlayer player = new VideoPlayer();

    @Override
    public void render(GuiGraphics graphics, PhoneTapInformation mouse, float renderDelta) {
        super.render(graphics, mouse, renderDelta);
        player.render(graphics,mouse,renderDelta);
    }

    @Override
    public CompoundTag createTag() {
        return new CompoundTag();
    }

    @Override
    public void sync(CompoundTag phoneData) {

    }
}
