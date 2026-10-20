package org.agmas.minetok.client.render.sprites;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.agmas.minetok.Minetok;
import org.agmas.minetok.client.MinetokClient;
import org.agmas.minetok.client.api.PhoneTapInformation;
import org.agmas.minetok.client.api.Sprite;
import org.agmas.minetok.client.render.Phone;
import org.agmas.minetok.network.ServerboundCallPacket;
import org.joml.Vector3f;

import java.awt.*;

public class CallsApplication extends ExitButtonSprite {

    EditBox textField = new EditBox(Minecraft.getInstance().font, 0,0,100,10,Component.empty());
    public float prevScroll = 0;
    public float scroll = 0;

    @Override
    public void render(GuiGraphicsExtractor graphics, PhoneTapInformation mouse, float renderDelta) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, Identifier.withDefaultNamespace("textures/block/white_concrete.png"), -8,-8,0,0, Phone.SCREEN_WIDTH+16, Phone.SCREEN_HEIGHT+16,128,128);

        if (FabricLoader.getInstance().isModLoaded("voicechat_api")) {
            graphics.fill(Phone.SCREEN_WIDTH-32,200, Phone.SCREEN_WIDTH-64,Phone.SCREEN_HEIGHT-200,Color.DARK_GRAY.getRGB());

            int area = Phone.SCREEN_HEIGHT-600;
            int scrollBarY = (int) Mth.lerp(scroll, 0, area);
            graphics.fill(Phone.SCREEN_WIDTH-32,200 + (scrollBarY), Phone.SCREEN_WIDTH-64,400 + (scrollBarY),Color.LIGHT_GRAY.getRGB());

            if (mouse.tapping && mouse.tappedX+80 > Phone.SCREEN_WIDTH-42) {
                scroll = prevScroll - ((mouse.tappedY- mouse.y)/area);
                if (scroll < 0) scroll = 0;
                if (scroll > 1) scroll = 1;
            } else {
                prevScroll = scroll;
            }

            int marginX = 40;
            int marginY;
            int i = 0;
            int spacing = 58;
            int scrollableArea = spacing * Minecraft.getInstance().getConnection().getListedOnlinePlayers().size();

            marginY = (int) Mth.lerp(scroll, 180, scrollableArea);

            for (PlayerInfo listedOnlinePlayer : Minecraft.getInstance().getConnection().getListedOnlinePlayers()) {
                if (!textField.getValue().isEmpty() && !listedOnlinePlayer.getProfile().name().toLowerCase().contains(textField.getValue().toLowerCase())) continue;
                boolean selected = false;
                if (mouse.x+40 > marginX && mouse.x+40 < Phone.SCREEN_WIDTH-34) {
                    if (mouse.y+40 > marginY + (spacing*i) && mouse.y+40 < marginY + (spacing*i) + 50) {
                        selected = true;
                        graphics.fill(marginX-6, marginY + (spacing*i) - 6, marginX+56, marginY + (spacing*i) + 56, -1);
                        if (mouse.justReleased && Math.abs(mouse.tappedY-mouse.y) < 8) {
                            ClientPlayNetworking.send(new ServerboundCallPacket(listedOnlinePlayer.getProfile().id().toString()));
                        }
                    }
                }
                PlayerFaceExtractor.
                        //? if <=1.21.11
                        // draw
                        //? if >1.21.11
                        extractRenderState
                                (graphics,listedOnlinePlayer.getSkin(),marginX,marginY + (spacing*i),48);
                graphics.pose().pushMatrix();
                graphics.pose().translate(marginX + 58,marginY+ 8 + (spacing*i));
                graphics.pose().scale(4);
                graphics.text(Minecraft.getInstance().font, listedOnlinePlayer.getProfile().name(), 0,0, selected ? -1 : Color.LIGHT_GRAY.getRGB());
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
            textField.extractRenderState(graphics, (int) mouse.x, (int) mouse.y,renderDelta);
            //? if <=1.21.11
            // textField.render(graphics, (int) mouse.x, (int) mouse.y,renderDelta);
            if (mouse.x+40 > 80 && mouse.y+40 > 70 && mouse.x+40 < 680 && mouse.y+40 < 140) {
                if (mouse.justTapped) {
                    MinetokClient.phone.selectedElement = textField;
                }
            }
            textField.setFocused(MinetokClient.phone.selectedElement == textField);
            graphics.pose().popMatrix();
        }
    }

    public void renderNoService(GuiGraphicsExtractor graphics, PhoneTapInformation mouse, float renderDelta) {
        graphics.pose().pushMatrix();
        graphics.pose().translate(Phone.SCREEN_WIDTH/2f, (Phone.SCREEN_HEIGHT/2f)-90);
        graphics.pose().scale(10);
        graphics.text(Minecraft.getInstance().font, Component.translatable("phone.error.no_service"), -Minecraft.getInstance().font.width(Component.translatable("phone.error.no_service"))/2,-4,Color.RED.getRGB());
        graphics.pose().popMatrix();

        graphics.pose().pushMatrix();
        graphics.pose().translate(Phone.SCREEN_WIDTH/2f, (Phone.SCREEN_HEIGHT/2f)-90);
        graphics.pose().scale(3);
        graphics.text(Minecraft.getInstance().font, Component.translatable("phone.error.missing_mod", "Simple Voicechat"), -Minecraft.getInstance().font.width(Component.translatable("phone.error.missing_mod", "Simple Voicechat"))/2,20,Color.LIGHT_GRAY.getRGB());
        graphics.pose().popMatrix();
    }
}
