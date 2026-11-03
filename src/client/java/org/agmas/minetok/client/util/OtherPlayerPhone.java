package org.agmas.minetok.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.agmas.minetok.client.render.Phone;
import org.agmas.minetok.client.render.PhoneTextureRenderer;
import org.agmas.minetok.init.ModAttachments;
import org.agmas.minetok.porting.MTID;

public class OtherPlayerPhone {
    public Phone phone;
    public PhoneTextureRenderer renderer;
    public MTID textureResourceLocation;

    public OtherPlayerPhone(Player player) {
        phone = new Phone(player);
        if (player.hasAttached(ModAttachments.PHONE_STATE)) {
            phone.sync(player.getAttachedOrElse(ModAttachments.PHONE_STATE, new CompoundTag()));
        }
        renderer = new PhoneTextureRenderer();
        textureResourceLocation = MTID.of("other_phone_texture_" + player.getStringUUID());
    }

    public void uploadPlayerTexture() {
        Minecraft.getInstance().getTextureManager().register(textureResourceLocation.id, renderer);
    }

    public void removePlayerTexture() {
        Minecraft.getInstance().getTextureManager().release(textureResourceLocation.id);
    }
}
