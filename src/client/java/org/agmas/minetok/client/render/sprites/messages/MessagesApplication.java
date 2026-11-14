package org.agmas.minetok.client.render.sprites.messages;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.agmas.minetok.api.PhoneTapInformation;
import org.agmas.minetok.client.MinetokClient;
import org.agmas.minetok.client.animation.MTGraphics;
import org.agmas.minetok.client.animation.NBTHelper;
import org.agmas.minetok.client.api.NetworkSprite;
import org.agmas.minetok.client.init.MinetokOverlays;
import org.agmas.minetok.client.render.Phone;
import org.agmas.minetok.client.render.sprites.ExitButtonSprite;
import org.agmas.minetok.client.render.sprites.NotificationSprite;
import org.agmas.minetok.client.render.sprites.ScrollBarSprite;
import org.agmas.minetok.network.ServerboundCallPacket;
import org.agmas.minetok.network.ServerboundRequestMessagesPacket;

import java.awt.*;

public class MessagesApplication extends ExitButtonSprite implements NetworkSprite {

    public static boolean warnedAboutSecurity = false;
    EditBox textField = new EditBox(Minecraft.getInstance().font, 0,0,80,10, Component.empty());
    ScrollBarSprite scrollBarSprite = new ScrollBarSprite(Phone.SCREEN_WIDTH-64, 200);

    public MessagesApplication() {
        ClientPlayNetworking.send(new ServerboundRequestMessagesPacket(Minecraft.getInstance().player.getStringUUID(),0));
    }

    @Override
    public void render(GuiGraphics graphics, PhoneTapInformation mouse, float renderDelta) {
        exitBarCapture(mouse);
        if (mouse.isClient && !warnedAboutSecurity) {
            warnedAboutSecurity = true;
            MinetokClient.currentlyRendering_phone.addNetOverlay(MinetokOverlays.NOTIFICATION, new NotificationSprite(Component.translatable("phone.messages.privacy"), false, MinetokClient.phone));
        }

        MTGraphics.blit(graphics, Identifier.withDefaultNamespace("textures/block/green_concrete.png"),0,0, Phone.SCREEN_WIDTH+16, Phone.SCREEN_HEIGHT+16,0f,0f,6f,6f);

        scrollBarSprite.render(graphics, mouse, renderDelta);

        int marginX = 40;
        int marginY;
        int i = 0;
        int spacing = 58;
        int scrollableArea = spacing * Minecraft.getInstance().getConnection().getListedOnlinePlayers().size();

        marginY = (int) Mth.lerp(scrollBarSprite.scroll, 180, scrollableArea);

        for (PlayerInfo listedOnlinePlayer : Minecraft.getInstance().getConnection().getListedOnlinePlayers()) {
            if (!textField.getValue().isEmpty() && !listedOnlinePlayer.getProfile().name().toLowerCase().contains(textField.getValue().toLowerCase())) continue;
            boolean selected = false;
            if (mouse.x+40 > marginX && mouse.x+40 < Phone.SCREEN_WIDTH-34) {
                if (mouse.y+40 > marginY + (spacing*i) && mouse.y+40 < marginY + (spacing*i) + 50) {
                    selected = true;
                    graphics.fill(marginX-6, marginY + (spacing*i) - 6, marginX+56, marginY + (spacing*i) + 56, -1);

                }
            }
            PlayerFaceRenderer.
                    //? if <=1.21.11
                            draw
                    //? if >1.21.11
                    //extractRenderState
                            (graphics,listedOnlinePlayer.getSkin(),marginX,marginY + (spacing*i),48);
            graphics.pose().pushMatrix();
            MTGraphics.translate(graphics, marginX + 58,marginY+ 8 + (spacing*i));
            MTGraphics.scale(graphics, 4);
            graphics.drawString(Minecraft.getInstance().font, listedOnlinePlayer.getProfile().name(), 0,0, selected ? -1 : Color.LIGHT_GRAY.getRGB());
            graphics.pose().popMatrix();
            i++;
        }

        super.render(graphics, mouse, renderDelta);

        textField.setEditable(true);

        graphics.pose().pushMatrix();
        MTGraphics.translate(graphics, 100,85);
        MTGraphics.scale(graphics, 5);

        //? if >1.21.11
        //textField.extractRenderState(graphics, (int) mouse.x, (int) mouse.y,renderDelta);
        //? if <=1.21.11
        textField.render(graphics, (int) mouse.x, (int) mouse.y,renderDelta);
        if (mouse.x+40 > 80 && mouse.y+40 > 70 && mouse.x+40 < 680 && mouse.y+40 < 140) {
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
        tag.putString("search", textField.getValue());
        tag.put("scroll", scrollBarSprite.createTag());
        return tag;
    }

    @Override
    public void sync(CompoundTag phoneData) {
        textField.setValue(NBTHelper.getStringOr(phoneData, "search", ""));
        scrollBarSprite.sync(NBTHelper.getCompoundOrEmpty(phoneData, "scroll"));
    }
}
