package org.agmas.minetok.client.render.sprites.minetok;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.NativeImage;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.texture.DynamicTexture;
//? if >1.21.1
//import net.minecraft.world.entity.player.PlayerSkin;
//? if <=1.21.1
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.agmas.minetok.api.PhoneTapInformation;
import org.agmas.minetok.client.animation.MTGraphics;
import org.agmas.minetok.client.api.Sprite;
import org.agmas.minetok.client.render.Phone;
import org.agmas.minetok.client.util.ForcedWidthTextWidget;

import javax.imageio.ImageIO;
import java.awt.*;
import java.net.URL;
import java.util.UUID;

public class CommentSprite extends Sprite {

    public ParsedMetadata.Comment comment;
    public PlayerSkin profilePicture;
    public ForcedWidthTextWidget body;
    public boolean liked = false;
    public float y = 0;

    public CommentSprite(ParsedMetadata.Comment comment, GuiGraphics graphics) {
        this.comment = comment;
        profilePicture = new PlayerInfo(new GameProfile(UUID.randomUUID(), comment.author()), false).getSkin();
        body = new ForcedWidthTextWidget(Component.literal(comment.body()), Minecraft.getInstance().font,
                (Phone.SCREEN_WIDTH - (64 + 12 + 64 + 64))/4); // face sprite + indent + padding + like button
        body.setMaxWidth((Phone.SCREEN_WIDTH - (64 + 12 + 64 + 64))/4);
    }

    @Override
    public void render(GuiGraphics graphics, PhoneTapInformation mouse, float renderDelta) {
        PlayerFaceRenderer
                //? if <26.1
                .draw
                //? if >=26.1
                //.extractRenderState
                        (graphics,profilePicture,0,0,64);
        graphics.pose().pushPose();
        MTGraphics.translate(graphics, 76, 0);
        MTGraphics.scale(graphics, 3);
        graphics.drawString(Minecraft.getInstance().font, comment.author(), 0, 0, new Color(200,200,200).getRGB());
        graphics.pose().popPose();

        graphics.pose().pushPose();
        MTGraphics.translate(graphics, 76, 24);
        MTGraphics.scale(graphics, 4);
        //? if >1.21.1
        //body.visitLines(graphics.textRenderer());
        body
                //? if <26.1
                .render
                //? if >=26.1
                //.extractRenderState
                        (graphics,0,0,renderDelta);
        graphics.pose().popPose();

        graphics.pose().pushPose();
        MTGraphics.translate(graphics, Phone.SCREEN_WIDTH-96,0);
        graphics.blit(ResourceLocation.withDefaultNamespace("textures/gui/sprites/hud/heart/container.png"), 0,0,64,64,0,1,0,1);
        if (liked) {
            graphics.blit(ResourceLocation.withDefaultNamespace("textures/gui/sprites/hud/heart/full.png"), 0,0,64,64,0,1,0,1);
        }
        if (mouse.x+40 > Phone.SCREEN_WIDTH-96 && mouse.y+40 > y) {
            if (mouse.x+40 < Phone.SCREEN_WIDTH-16 && mouse.y+40 < y+64) {
                graphics.blit(ResourceLocation.withDefaultNamespace("textures/gui/sprites/hud/heart/frozen_full.png"), 0,0,64,64,0,1,0,1);
                if (mouse.justReleased) {
                    liked = !liked;
                }
            }
        }
        MTGraphics.translate(graphics, 32 - (Minecraft.getInstance().font.width((comment.likes()  + (liked ? 1 : 0))+"")),68);
        MTGraphics.scale(graphics, 2,2);
        graphics.drawString(Minecraft.getInstance().font, (comment.likes()  + (liked ? 1 : 0))+"", 0,0,-1);
        graphics.pose().popPose();

        super.render(graphics, mouse, renderDelta);
    }

    public int height() {
        return Math.max(64, body.getHeight()*4);
    }

}
