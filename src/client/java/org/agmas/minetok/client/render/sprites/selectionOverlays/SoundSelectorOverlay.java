package org.agmas.minetok.client.render.sprites.selectionOverlays;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.EasingType;
import net.minecraft.util.Mth;
import org.agmas.minetok.api.PhoneTapInformation;
import org.agmas.minetok.client.MinetokClient;
import org.agmas.minetok.client.api.NetworkSprite;
import org.agmas.minetok.client.api.Sprite;
import org.agmas.minetok.client.config.PhoneConfig;
import org.agmas.minetok.client.render.Phone;
import org.agmas.minetok.client.render.sprites.ExitButtonSprite;
import org.agmas.minetok.client.render.sprites.ScrollBarSprite;
import org.agmas.minetok.network.ServerboundCallPacket;
import org.agmas.minetok.network.ServerboundRingtoneSetPacket;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SoundSelectorOverlay extends Sprite implements NetworkSprite {
    public final int WIDTH = 660;
    public final int HEIGHT = 720;
    public final int BG_COLOR = new Color(226, 238, 255).getRGB();
    public float time = 0;
    ScrollBarSprite scrollBarSprite = new ScrollBarSprite(0, 0, 32, HEIGHT-62, 100);
    ExitButtonSprite exitBar = new ExitButtonSprite();
    EditBox textField = new EditBox(Minecraft.getInstance().font, 0,0,76,10, Component.literal("ringtone"));
    public Identifier chosenIdentifier = null;

    public SoundSelectorOverlay() {
        x = (int) (Phone.SCREEN_WIDTH/2f - WIDTH/2f);
        y = (int) (Phone.SCREEN_HEIGHT/2f - HEIGHT/2f);
        exitBar.padded = false;
        exitBar.width = WIDTH;
        exitBar.closesOverlay = true;

        scrollBarSprite.y = y+56;
        scrollBarSprite.x = (x+WIDTH)-48;
        exitBar.y = y;
        exitBar.x = x;
    }

    public float prevScroll = 0;
    String oldSearch = "";
    List<Map.Entry<ResourceKey<SoundEvent>, SoundEvent>> soundArray = null;
    SimpleSoundInstance exampleSound = SimpleSoundInstance.forUI(SoundEvents.MUSIC_DISC_FAR, 1f);

    @Override
    public void render(GuiGraphics graphics, PhoneTapInformation mouse, float renderDelta) {
        time = Math.clamp(time+renderDelta,0f,1f);
        exitBar.exitBarCapture(mouse);
        graphics.fill(-100,-100, Phone.SCREEN_WIDTH+100, Phone.SCREEN_HEIGHT+100, new Color(0,0,0, EasingType.OUT_CIRC.apply(time)*0.5f).getRGB());
        graphics.fill(x,y,x+WIDTH,y+HEIGHT,BG_COLOR);
        scrollBarSprite.render(graphics, mouse, renderDelta);

        int marginX = 40;
        int i = 0;
        int spacing = 58;

        if (!oldSearch.equals(textField.getValue()) || soundArray == null || prevScroll != scrollBarSprite.scroll) {
        soundArray = BuiltInRegistries.SOUND_EVENT.entrySet().stream().toList();
           if (!textField.getValue().isBlank()) {
               soundArray = soundArray.stream().filter((b) -> {
                   return b.getKey().identifier().getNamespace().contains(textField.getValue()) || b.getKey().identifier().getPath().contains(textField.getValue());
               }).toList();

           }
           oldSearch = textField.getValue();
        }
        prevScroll = scrollBarSprite.scroll;

        int canFitOnOneScreen = (HEIGHT/58)-1;
        int startingPoint = Mth.lerpInt(scrollBarSprite.scroll, 0, soundArray.size()-canFitOnOneScreen);

        if (startingPoint >= 0 && !soundArray.isEmpty()) {
            for (int j = startingPoint; j < startingPoint + canFitOnOneScreen; j++) {
                if (j >= soundArray.size()) break;
                WeighedSoundEvents weighedSoundEvents = new SimpleSoundInstance(soundArray.get(j).getValue(), SoundSource.PLAYERS, 1f, 1f, Minecraft.getInstance().player.getRandom(), 0, 0, 0)
                        //? if <26.3
                        .resolve
                        //? if >=26.3
                        //.getOrResolve
                                (Minecraft.getInstance().getSoundManager());
                Component name =weighedSoundEvents == null ? Component.literal(soundArray.get(j).getKey().identifier().toString()) : weighedSoundEvents.getSubtitle() == null ? Component.literal(soundArray.get(j).getKey().identifier().getPath()) : weighedSoundEvents.getSubtitle();

                boolean selected = false;
                if (soundArray.get(j).getKey().identifier() == chosenIdentifier) {
                    graphics.fill(x+marginX-6,y+64+ (spacing * i),(x+marginX+52)+(Minecraft.getInstance().font.width(name)*6), y+64+ (spacing * (i+1)), new Color(160,160,255).getRGB());
                }
                if (mouse.x+40 > x && mouse.y+40 > y + 64 + (spacing * i)) {
                    if (mouse.x+40 < x+(WIDTH-48) && mouse.y+40 < y + 64 + (spacing * (i+1))) {
                        graphics.fill(x+marginX-6,y+64+ (spacing * i),(x+marginX+52)+(Minecraft.getInstance().font.width(name)*6), y+64+ (spacing * (i+1)), -1);
                        selected = true;
                        if (mouse.justTapped) {
                            chosenIdentifier = soundArray.get(j).getKey().identifier();
                            Minecraft.getInstance().getSoundManager().stop(exampleSound);
                            exampleSound = SimpleSoundInstance.forUI(soundArray.get(j).getValue(), 1f);
                            Minecraft.getInstance().getSoundManager().play(exampleSound);
                            PhoneConfig.INSTANCE.ringtone = soundArray.get(j).getKey().identifier();
                            PhoneConfig.write();
                            ClientPlayNetworking.send(new ServerboundRingtoneSetPacket(PhoneConfig.INSTANCE.ringtone.toString()));
                        }
                    }
                }

                Identifier texture = selected ? Identifier.withDefaultNamespace("textures/item/music_disc_cat.png") :  Identifier.fromNamespaceAndPath(soundArray.get(j).getKey().identifier().getNamespace(), "icon.png");
                if (soundArray.get(j).getValue().location().getNamespace().equals("minecraft")) {
                    texture = selected ? Identifier.withDefaultNamespace("textures/item/music_disc_cat.png") :  Identifier.withDefaultNamespace("textures/item/music_disc_stal.png");
                }


                graphics.pose().pushMatrix();
                graphics.pose().translate(x + marginX + 52, y + 64 + (spacing * i));
                graphics.pose().scale(6, 6);
                graphics.drawString(Minecraft.getInstance().font,
                        name.getString().substring(0, Math.min(name.getString().length(), selected ? 32 : 16)), 0, 0, -1);
                graphics.pose().popMatrix();
                graphics.blit(texture, x + marginX, y + 64 + (spacing * i), x + marginX + 48, y + 64 + (spacing * i) + 48, 0, 1, 0, 1);

                i++;
            }
        }
        if (exitBar.pressedExit) {
            Minecraft.getInstance().getSoundManager().stop(exampleSound);
        }
        exitBar.render(graphics,mouse,renderDelta);

        textField.setEditable(true);

        graphics.pose().pushMatrix();
        graphics.pose().translate(x+59,y);
        graphics.pose().scale(5);

        //? if >1.21.11
        //textField.extractRenderState(graphics, (int) mouse.x, (int) mouse.y,renderDelta);
        //? if <=1.21.11
        textField.render(graphics, (int) mouse.x, (int) mouse.y,renderDelta);
        if (mouse.x+40 > x+52 && mouse.y+40 > y && mouse.x+40 < x+452 && mouse.y+40 < y+140) {
            if (mouse.justTapped) {
                MinetokClient.phone.selectedElement = textField;
            }
        }
        textField.setFocused(MinetokClient.phone.selectedElement == textField);
        graphics.pose().popMatrix();
    }

    @Override
    public CompoundTag createTag() {
        CompoundTag tag = new CompoundTag();
        tag.put("scroll", scrollBarSprite.createTag());
        return tag;
    }

    @Override
    public void sync(CompoundTag phoneData) {
        scrollBarSprite.sync(phoneData.getCompoundOrEmpty("scroll"));
    }
}
