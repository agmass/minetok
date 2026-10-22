package org.agmas.minetok.client.init;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import org.agmas.minetok.client.MinetokClient;
import org.agmas.minetok.client.render.sprites.NotificationSprite;
import org.agmas.minetok.network.ClientboundPhoneNotificationPacket;

public class ModClientPackets {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(ClientboundPhoneNotificationPacket.TYPE, (packet, context)->{
            MinetokClient.phone.addNetOverlay(MinetokOverlays.NOTIFICATION, new NotificationSprite(packet.component(), packet.isError(), MinetokClient.phone));
        });
    }
}
