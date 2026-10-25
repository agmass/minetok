package org.agmas.minetok.client.render.sprites.calls;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.agmas.minetok.client.MinetokClient;
import org.agmas.minetok.api.PhoneTapInformation;
import org.agmas.minetok.client.api.NetworkSprite;
import org.agmas.minetok.client.config.PhoneConfig;
import org.agmas.minetok.client.init.MinetokOverlays;
import org.agmas.minetok.client.render.Phone;
import org.agmas.minetok.client.render.sprites.ExitButtonSprite;
import org.agmas.minetok.client.render.sprites.NotificationSprite;
import org.agmas.minetok.client.render.sprites.ScrollBarSprite;
import org.agmas.minetok.network.ServerboundCallPacket;

import java.awt.*;

public class CallsApplicationSprite extends ExitButtonSprite implements NetworkSprite {

    EditBox textField = new EditBox(Minecraft.getInstance().font, 0,0,100,10,Component.empty());
    ScrollBarSprite scrollBarSprite = new ScrollBarSprite(Phone.SCREEN_WIDTH-64, 200);

    @Override
    public void render(GuiGraphics graphics, PhoneTapInformation mouse, float renderDelta) {
        exitBarCapture(mouse);

        graphics.blit(RenderPipelines.GUI_TEXTURED, Identifier.withDefaultNamespace("textures/block/white_concrete.png"), -8,-8,0,0, Phone.SCREEN_WIDTH+16, Phone.SCREEN_HEIGHT+16,128,128);

        if (FabricLoader.getInstance().isModLoaded("voicechat_api")) {

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
                        if (!listedOnlinePlayer.getProfile().id().equals(Minecraft.getInstance().player.getUUID())) {
                            if (mouse.justReleased && (Math.abs(mouse.tappedY - mouse.y) < 8)) {
                                ClientPlayNetworking.send(new ServerboundCallPacket(listedOnlinePlayer.getProfile().id().toString()));
                            }
                        } else if (mouse.justReleased && (Math.abs(mouse.tappedY - mouse.y) < 8)) {
                            MinetokClient.phone.addNetOverlay(MinetokOverlays.NOTIFICATION, new NotificationSprite(Component.translatable("phone.error.cant_call_self"), true, MinetokClient.phone, true));
                        }
                    }
                }
                PlayerFaceRenderer.
                        //? if <=1.21.11
                         draw
                        //? if >1.21.11
                        //extractRenderState
                                (graphics,listedOnlinePlayer.getSkin(),marginX,marginY + (spacing*i),48);
                graphics.pose().pushMatrix();
                graphics.pose().translate(marginX + 58,marginY+ 8 + (spacing*i));
                graphics.pose().scale(4);
                graphics.drawString(Minecraft.getInstance().font, listedOnlinePlayer.getProfile().name(), 0,0, selected ? -1 : Color.LIGHT_GRAY.getRGB());
                graphics.pose().popMatrix();
                i++;
            }
        } else {
            renderNoService(graphics,mouse,renderDelta);
        }
        super.render(graphics, mouse, renderDelta);

        if (FabricLoader.getInstance().isModLoaded("voicechat_api")) {
            textField.setEditable(true);

            graphics.pose().pushMatrix();
            graphics.pose().translate(100,85);
            graphics.pose().scale(5);

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
    }

    public void renderNoService(GuiGraphics graphics, PhoneTapInformation mouse, float renderDelta) {
        graphics.pose().pushMatrix();
        graphics.pose().translate(Phone.SCREEN_WIDTH/2f, (Phone.SCREEN_HEIGHT/2f)-90);
        graphics.pose().scale(10);
        graphics.drawString(Minecraft.getInstance().font, Component.translatable("phone.error.no_service"), -Minecraft.getInstance().font.width(Component.translatable("phone.error.no_service"))/2,-4,Color.RED.getRGB());
        graphics.pose().popMatrix();

        graphics.pose().pushMatrix();
        graphics.pose().translate(Phone.SCREEN_WIDTH/2f, (Phone.SCREEN_HEIGHT/2f)-90);
        graphics.pose().scale(3);
        graphics.drawString(Minecraft.getInstance().font, Component.translatable("phone.error.missing_mod", "Simple Voicechat"), -Minecraft.getInstance().font.width(Component.translatable("phone.error.missing_mod", "Simple Voicechat"))/2,20,Color.LIGHT_GRAY.getRGB());
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
        textField.setValue(phoneData.getStringOr("search", ""));
        scrollBarSprite.sync(phoneData.getCompoundOrEmpty("scroll"));
    }
}
