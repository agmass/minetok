package org.agmas.minetok.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.agmas.minetok.client.render.Phone;
import org.agmas.minetok.client.render.PhoneTextureRenderer;
import org.agmas.minetok.init.ModAttachments;
import org.agmas.minetok.porting.MTIdentifier;

import java.util.UUID;

public class OtherPlayerPhone {
    public Phone phone;
    public PhoneTextureRenderer renderer;
    public MTIdentifier textureIdentifier;

    public OtherPlayerPhone(Player player) {
        phone = new Phone(player);
        if (player.hasAttached(ModAttachments.PHONE_STATE)) {
            phone.sync(player.getAttachedOrElse(ModAttachments.PHONE_STATE, new CompoundTag()));
        }
        renderer = new PhoneTextureRenderer();
        textureIdentifier = MTIdentifier.of("other_phone_texture_" + player.getStringUUID());
    }

    public void uploadPlayerTexture() {
        Minecraft.getInstance().getTextureManager().register(textureIdentifier.id, renderer);
    }

    public void removePlayerTexture() {
        Minecraft.getInstance().getTextureManager().release(textureIdentifier.id);
    }
}
