package org.agmas.minetok.client.render.sprites;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import org.agmas.minetok.client.MinetokClient;
import org.agmas.minetok.client.animation.EasingHelper;
import org.agmas.minetok.client.animation.MTGraphics;
import org.agmas.minetok.client.api.Application;
import org.agmas.minetok.api.PhoneTapInformation;
import org.agmas.minetok.client.api.Sprite;
import org.agmas.minetok.client.render.Phone;
import org.agmas.minetok.client.util.MinetokClientRegistries;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

public class HomeScreenSprite extends Sprite {

    public Sprite focusedSprite = null;
    public float openingAppAnimation = 0;
    public boolean closing = false;
    public PanoramaSprite panoramaSprite = new PanoramaSprite();

    public ArrayList<ApplicationGridIcon> gridApps = new ArrayList<>();

    public ArrayList<Sprite> overlays = new ArrayList<>();

    public HomeScreenSprite() {
        refreshApps();
    }

    public void refreshApps() {
        gridApps.clear();
        for (Map.Entry<ResourceKey<Application>, Application> entry : MinetokClientRegistries.APPLICATIONS.entrySet()) {
            if (entry.getValue().allowedToBeShown()) {
                gridApps.add(new ApplicationGridIcon(entry.getKey().identifier(), entry.getValue()));
            }
        }
    }

    @Override
    public void render(GuiGraphics graphics, PhoneTapInformation originalMouse, float renderDelta) {
        super.render(graphics, originalMouse, renderDelta);
        PhoneTapInformation mouse = originalMouse;
        if (mouse.justTapped) {
            MinetokClient.phone.selectedElement = null;
        }
        if (!overlays.isEmpty()) {
            mouse = new PhoneTapInformation(originalMouse.isClient);
        }

        if (focusedSprite == null || openingAppAnimation > 0) {
            panoramaSprite.render(graphics,mouse,renderDelta);
            if (mouse.justTapped) {
                refreshApps();
            }

            PhoneTapInformation gridMouseInfo = mouse;
            if (openingAppAnimation > 0 && !closing) gridMouseInfo = new PhoneTapInformation(originalMouse.isClient);

            renderApplicationGrid(graphics,gridMouseInfo,renderDelta);
        }

        MTGraphics.oldUp(graphics);
        if (openingAppAnimation > 0 && focusedSprite != null) {
            openingAppAnimation -= renderDelta*0.5f;

            graphics.pose().pushMatrix();
            if (closing) {
                MTGraphics.translate(graphics, Mth.lerp(EasingHelper.outQuad(openingAppAnimation), Phone.SCREEN_WIDTH / 2f, 0f), Mth.lerp(EasingHelper.outQuad(openingAppAnimation),  Phone.SCREEN_HEIGHT / 2f, 0f));
                MTGraphics.scale(graphics, Mth.lerp(EasingHelper.outQuad(openingAppAnimation), 0f, 1f));
            } else {
                MTGraphics.translate(graphics, Mth.lerp(EasingHelper.outQuad(openingAppAnimation), 0f, Phone.SCREEN_WIDTH / 2f), Mth.lerp(EasingHelper.outQuad(openingAppAnimation), 0f, Phone.SCREEN_HEIGHT / 2f));
                MTGraphics.scale(graphics, Mth.lerp(EasingHelper.outQuad(openingAppAnimation), 1f, 0f));
            }
            graphics.fill(0,0,720,1280, Color.BLACK.getRGB());
            graphics.pose().pushMatrix();
            focusedSprite.render(graphics,mouse,renderDelta);
            graphics.pose().popMatrix();

            graphics.pose().popMatrix();
            if (closing && openingAppAnimation <= 0) {
                focusedSprite = null;
                closing = false;
            }
        } else if (focusedSprite != null) {
            focusedSprite.render(graphics,mouse,renderDelta);
        }

        ArrayList<Sprite> unmodifiedOverlays = (ArrayList<Sprite>) overlays.clone();
        for (Sprite overlay : unmodifiedOverlays) {
            MTGraphics.oldUp(graphics);
            overlay.render(graphics,originalMouse,renderDelta);
        }
    }

    float prePressedCurrentPage = 0;
    float currentPage = 0;

    public void renderApplicationGrid(GuiGraphics graphicsExtractor, PhoneTapInformation mouse, float renderDelta) {
        if (mouse.tapping) {
            currentPage = prePressedCurrentPage - ((mouse.x - mouse.tappedX)/600f);
        } else {
            currentPage = (float) Mth.lerp(0.33f, currentPage,
                    currentPage < 0 ? 0 : Math.round(currentPage)
            );
            prePressedCurrentPage = currentPage;
        }

        int page = 0;

        int paddingX = 60;
        int paddngY = 30;

        int marginX = 50;
        int marginY = 150;

        int currentX = marginX;
        int currentY = marginY;

        for (ApplicationGridIcon applicationGridIcon : gridApps) {
            applicationGridIcon.x = (int) (currentX + (720 * (page - currentPage)));
            applicationGridIcon.y = currentY;
            applicationGridIcon.render(graphicsExtractor, mouse, renderDelta,this);

            currentX += 128 + paddingX;
            if ((currentX+128) > Phone.SCREEN_WIDTH-marginX) {
                currentX = marginX;
                currentY += ApplicationGridIcon.APP_GRID_ICON_HEIGHT + paddngY;
                if ((currentY+128) > Phone.SCREEN_HEIGHT-marginY) {
                    currentY = marginY;
                    page++;
                }
            }
        }

        if (currentPage > page) {
            currentPage = page;
        }
    }

}
