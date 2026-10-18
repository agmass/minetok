package org.agmas.minetok.client.api;

import net.minecraft.world.phys.Vec2;

public class PhoneTapInformation {

    public float x = 0;
    public float y = 0;

    public float tappedX = 0;
    public float tappedY = 0;

    public boolean tapping = false;
    public boolean justTapped = false;
    public boolean justReleased = false;

    public boolean rightClicking = false;
    public boolean justRightClicked = false;
}
