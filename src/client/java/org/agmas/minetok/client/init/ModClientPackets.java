package org.agmas.minetok.client.init;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import org.agmas.minetok.client.MinetokClient;
import org.agmas.minetok.client.render.sprites.NotificationSprite;
import org.agmas.minetok.client.render.sprites.messages.MessagesApplication;
import org.agmas.minetok.network.ClientboundPhoneNotificationPacket;
import org.agmas.minetok.network.ClientboundRecieveMessageListPacket;

import java.util.UUID;

public class ModClientPackets {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(ClientboundPhoneNotificationPacket.TYPE, (packet, context)->{
            MinetokClient.phone.addNetOverlay(MinetokOverlays.NOTIFICATION, new NotificationSprite(packet.component(), packet.isError(), MinetokClient.phone));
        });

        ClientPlayNetworking.registerGlobalReceiver(ClientboundRecieveMessageListPacket.TYPE, (packet, context)->{
            if (MinetokClient.phone.topSprite.focusedSprite instanceof MessagesApplication app) {
                UUID uuid = UUID.fromString(packet.uuid());
                app.contactsAndPreview.put(uuid, packet.messages().getFirst().message());

                if (app.messagingContainerSprite != null) {
                    app.messagingContainerSprite.messages.addAll(packet.messages());
                }
            }
        });
    }
}
