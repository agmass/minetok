package org.agmas.minetok.client.render.sprites.settings;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.agmas.minetok.api.PhoneTapInformation;
import org.agmas.minetok.client.MinetokClient;
import org.agmas.minetok.client.animation.MTGraphics;
import org.agmas.minetok.client.api.Sprite;
import org.agmas.minetok.client.init.MinetokOverlays;
import org.agmas.minetok.client.render.sprites.selectionOverlays.SoundSelectorOverlay;

public class SoundOverlayOptionSprite extends Sprite {

    public Component component;
    public SoundSelectorOverlay soundSelectorOverlay;

    public SoundOverlayOptionSprite(Component component, ResourceLocation soundId) {
        soundSelectorOverlay = new SoundSelectorOverlay();
        soundSelectorOverlay.chosenResourceLocation = soundId;
        this.component = component;
    }


    @Override
    public void render(GuiGraphics graphics, PhoneTapInformation mouse, float renderDelta) {
        graphics.pose().pushPose();
        if (mouse.y+40 > y-6 && mouse.y+40 < y+54) {
            graphics.fill(-6,-6,54,54,-1);
            if (mouse.justReleased) {
                MinetokClient.phone.addNetOverlay(MinetokOverlays.SOUND_SELECTOR, soundSelectorOverlay);
            }
        }
        graphics.blit(ResourceLocation.fromNamespaceAndPath("minecraft", "textures/item/music_disc_stal.png"),0,0,48,48,0,1,0,1);
        graphics.pose().pushPose();
        MTGraphics.translate(graphics, 54,0);
        MTGraphics.scale(graphics, 6,6);
        graphics.drawString(Minecraft.getInstance().font, component, 0,0,-1);
        graphics.pose().popPose();
        graphics.pose().popPose();
    }
}
