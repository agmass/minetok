package org.agmas.minetok.client.render.sprites.settings;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.agmas.minetok.Minetok;
import org.agmas.minetok.api.PhoneTapInformation;
import org.agmas.minetok.client.MinetokClient;
import org.agmas.minetok.client.api.NetworkSprite;
import org.agmas.minetok.client.api.Sprite;
import org.agmas.minetok.client.init.MinetokOverlays;
import org.agmas.minetok.client.render.sprites.selectionOverlays.SoundSelectorOverlay;
import org.agmas.minetok.porting.MTIdentifier;

public class SoundOverlayOptionSprite extends Sprite {

    public Component component;
    public SoundSelectorOverlay soundSelectorOverlay;

    public SoundOverlayOptionSprite(Component component, Identifier soundId) {
        soundSelectorOverlay = new SoundSelectorOverlay();
        soundSelectorOverlay.chosenIdentifier = soundId;
        this.component = component;
    }


    @Override
    public void render(GuiGraphicsExtractor graphics, PhoneTapInformation mouse, float renderDelta) {
        graphics.pose().pushMatrix();
        if (mouse.y+40 > y-6 && mouse.y+40 < y+54) {
            graphics.fill(-6,-6,54,54,-1);
            if (mouse.justReleased) {
                MinetokClient.phone.addNetOverlay(MinetokOverlays.SOUND_SELECTOR, soundSelectorOverlay);
            }
        }
        graphics.blit(Identifier.fromNamespaceAndPath("minecraft", "textures/item/music_disc_stal.png"),0,0,48,48,0,1,0,1);
        graphics.pose().pushMatrix();
        graphics.pose().translate(54,0);
        graphics.pose().scale(6,6);
        graphics.text(Minecraft.getInstance().font, component, 0,0,-1);
        graphics.pose().popMatrix();
        graphics.pose().popMatrix();
    }
}
