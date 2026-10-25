package org.agmas.minetok.api;

public class PhoneTapInformation {

    public PhoneTapInformation(boolean isClient) {
        this.isClient = isClient;
    }

    public PhoneTapInformation() {}


    public boolean isClient = false;

    public float x = 0;
    public float y = 0;

    public float prevX = 0;
    public float prevY = 0;

    public float tappedX = 0;
    public float tappedY = 0;

    public boolean tapping = false;
    public boolean justTapped = false;
    public boolean justReleased = false;

    public boolean rightClicking = false;
    public boolean justRightClicked = false;

}
