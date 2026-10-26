package org.agmas.minetok.client.render.sprites.minetok;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
import org.agmas.minetok.Minetok;
import org.agmas.minetok.api.PhoneTapInformation;
import org.agmas.minetok.client.MinetokClient;
import org.agmas.minetok.client.api.Sprite;
import org.agmas.minetok.client.render.Phone;
import org.agmas.minetok.client.vlcj.VLCJHandler;
import org.agmas.minetok.porting.MTIdentifier;

public class VideoPlayer extends Sprite {

    VLCJHandler handler = new VLCJHandler(720,1280, MTIdentifier.of("heyallscotthere").id);

    public void setVideo(String link) {
    }

    @Override
    public void render(GuiGraphics graphics, PhoneTapInformation mouse, float renderDelta) {
        super.render(graphics, mouse, renderDelta);
        handler.updateTexture();
        graphics.blit(handler.textureId, 0,0,Phone.SCREEN_WIDTH,Phone.SCREEN_HEIGHT,0,1,0,1);
    }
}
