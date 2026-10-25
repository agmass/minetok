package org.agmas.minetok.client.render.sprites.settings;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import org.agmas.minetok.Minetok;
import org.agmas.minetok.api.PhoneTapInformation;
import org.agmas.minetok.client.MinetokClient;
import org.agmas.minetok.client.api.NetworkSprite;
import org.agmas.minetok.client.api.Sprite;
import org.agmas.minetok.client.config.PhoneConfig;
import org.agmas.minetok.client.render.Phone;
import org.agmas.minetok.client.render.sprites.ExitButtonSprite;

import java.awt.*;
import java.util.ArrayList;

public class SettingsApplicationSprite extends ExitButtonSprite implements NetworkSprite {

    public static boolean dirty = false;
    BooleanOptionSprite muteRingtones = new BooleanOptionSprite(Component.translatable("config.minetok.muteRingtones"),PhoneConfig.INSTANCE.muteRingtones);
    BooleanOptionSprite muteOtherPhones = new BooleanOptionSprite(Component.translatable("config.minetok.muteOtherPhones"), PhoneConfig.INSTANCE.muteOtherPhones);
    BooleanOptionSprite disableRingingIndicator = new BooleanOptionSprite(Component.translatable("config.minetok.disableRingingIndicator"), PhoneConfig.INSTANCE.disableRingingIndicator);
    SoundOverlayOptionSprite ringtone = new SoundOverlayOptionSprite(Component.translatable("config.minetok.ringtone"), PhoneConfig.INSTANCE.ringtone);

    ArrayList<Sprite> options  = new ArrayList<>();

    public SettingsApplicationSprite() {
        options.add(muteOtherPhones);
        options.add(muteRingtones);
        options.add(disableRingingIndicator);
        options.add(ringtone);
    }

    @Override
    public void render(GuiGraphics graphics, PhoneTapInformation mouse, float renderDelta) {
        exitBarCapture(mouse);


        graphics.blit(RenderPipelines.GUI_TEXTURED, Identifier.withDefaultNamespace("textures/block/white_concrete_powder.png"), -8, -8, 0, 0, Phone.SCREEN_WIDTH + 16, Phone.SCREEN_HEIGHT + 16, 128, 128);

        graphics.pose().pushMatrix();
        int cy = 160;
        graphics.pose().translate(40,160);
        for (Sprite option : options) {
            option.x = 40;
            option.y = cy;
            option.render(graphics,mouse,renderDelta);
            graphics.fill(-40,60,Phone.SCREEN_WIDTH,64,new Color(100,100,100).getRGB());
            graphics.pose().translate(0,70);
            cy += 70;
        }
        graphics.pose().popMatrix();


        if (mouse.isClient) {
            PhoneConfig.INSTANCE.muteRingtones = muteRingtones.ticked;
            PhoneConfig.INSTANCE.muteOtherPhones = muteOtherPhones.ticked;
            PhoneConfig.INSTANCE.disableRingingIndicator = disableRingingIndicator.ticked;
            PhoneConfig.INSTANCE.ringtone = ringtone.soundSelectorOverlay.chosenIdentifier;
            if (dirty) {
                dirty = false;
                PhoneConfig.write();
            }
        }
        super.render(graphics,mouse,renderDelta);
    }


    @Override
    public CompoundTag createTag() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("mute_ringtones", muteRingtones.ticked);
        tag.putBoolean("mute_other_phones", muteOtherPhones.ticked);
        tag.putBoolean("disable_ringing_indicator", disableRingingIndicator.ticked);
        return tag;
    }

    @Override
    public void sync(CompoundTag phoneData) {
        muteRingtones.ticked = phoneData.getBooleanOr("mute_ringtones", false);
        muteOtherPhones.ticked = phoneData.getBooleanOr("mute_other_phones", false);
        disableRingingIndicator.ticked = phoneData.getBooleanOr("mute_other_phones", false);
    }
}
