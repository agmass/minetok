package org.agmas.minetok.init;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.agmas.minetok.Minetok;
import org.agmas.minetok.messages.MessagesHandler;
import org.agmas.minetok.network.*;
import org.agmas.minetok.voicechat.MinetokVoicechatPlugin;

import java.util.List;
import java.util.UUID;

public class ModPackets {
    public static void init() {
        PayloadTypeRegistry.playS2C()
                .register(ClientboundPhoneNotificationPacket.TYPE, ClientboundPhoneNotificationPacket.CODEC);

        PayloadTypeRegistry.playS2C()
                .register(ClientboundRecieveMessageListPacket.TYPE, ClientboundRecieveMessageListPacket.CODEC);

        PayloadTypeRegistry.playC2S()
                .register(ServerboundCallPacket.TYPE, ServerboundCallPacket.CODEC);

        PayloadTypeRegistry.playC2S()
                .register(ServerboundPhoneStatePacket.TYPE, ServerboundPhoneStatePacket.CODEC);

        PayloadTypeRegistry.playC2S()
                .register(ServerboundRequestMessagesPacket.TYPE, ServerboundRequestMessagesPacket.CODEC);

        PayloadTypeRegistry.playC2S()
                .register(ServerboundCallResponsePacket.TYPE, ServerboundCallResponsePacket.CODEC);

        PayloadTypeRegistry.playC2S()
                .register(ServerboundRingtoneSetPacket.TYPE, ServerboundRingtoneSetPacket.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ServerboundPhoneStatePacket.TYPE, (payload, context)->{
            if (payload.phoneTag().contains("x")) {
                CompoundTag currentTag = context.player().getAttachedOrElse(ModAttachments.PHONE_STATE, new CompoundTag());
                currentTag.remove("mouse");
                currentTag.put("mouse", payload.phoneTag());
                context.player().setAttached(ModAttachments.PHONE_STATE, currentTag);
            } else {
                Log.info(LogCategory.GENERAL, payload.phoneTag().toString());
                context.player().setAttached(ModAttachments.PHONE_STATE, payload.phoneTag());
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(ServerboundRingtoneSetPacket.TYPE, (payload, context)->{
            context.player().setAttached(ModAttachments.SAVED_RINGTONE, Identifier.parse(payload.ringtoneSoundId()));
        });

        ServerPlayNetworking.registerGlobalReceiver(ServerboundRequestMessagesPacket.TYPE, (payload, context)->{
            if (payload.UUID().equals(context.player().getStringUUID())) {
                for (UUID contact : Minetok.messagesHandler.getContacts(context.player().getUUID())) {
                    List<NetMessage> messages = Minetok.messagesHandler.readMessages(context.player(), contact, 0);
                    if (!messages.isEmpty())
                        context.responseSender().sendPacket(new ClientboundRecieveMessageListPacket(contact.toString(), messages.subList(0,1)));
                }
            } else {
                List<NetMessage> messages = Minetok.messagesHandler.readMessages(context.player(), UUID.fromString(payload.UUID()), payload.page());

                if (Minetok.messagesHandler.isRightHand(context.player().getUUID(), UUID.fromString(payload.UUID()))) {
                    List<NetMessage> transformedMessages = List.of();
                    for (NetMessage message : messages) {
                        transformedMessages.add(new NetMessage(!message.receiving(), message.timestamp(), message.message()));
                    }
                    messages = transformedMessages;
                }

                context.responseSender().sendPacket(new ClientboundRecieveMessageListPacket(payload.UUID(), messages));
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(ServerboundCallResponsePacket.TYPE, (payload, context)->{
            if (context.player().getAttached(ModAttachments.RINGING_ATTACHMENT_TYPE) == null) return;

            ServerPlayer otherPlayer = context.server().getPlayerList().getPlayer(context.player().getAttached(ModAttachments.RINGING_ATTACHMENT_TYPE).callingId);

            if (otherPlayer != null) {
                if (payload.accept() && context.player().getAttached(ModAttachments.RINGING_ATTACHMENT_TYPE).recieving) {
                    if (FabricLoader.getInstance().isModLoaded("voicechat_api")) {
                        MinetokVoicechatPlugin.acceptRing(otherPlayer, context.player());
                    }
                }
                otherPlayer.removeAttached(ModAttachments.RINGING_ATTACHMENT_TYPE);
            }
            context.player().removeAttached(ModAttachments.RINGING_ATTACHMENT_TYPE);
        });

        ServerPlayNetworking.registerGlobalReceiver(ServerboundCallPacket.TYPE, (payload, context)->{
            handleRingRequest(context.player(), UUID.fromString(payload.playerUUID()), context.server());
        });
    }

    public static void handleRingRequest(ServerPlayer sender, UUID reciever,  MinecraftServer server) {
        if (FabricLoader.getInstance().isModLoaded("voicechat_api")) {
            ServerPlayer receivingPlayer = server.getPlayerList().getPlayer(reciever);
            if (receivingPlayer == null) {
                ServerPlayNetworking.send(sender, new ClientboundPhoneNotificationPacket(true, Component.translatable("phone.error.receiver_offline")));
                return;
            }
            if (receivingPlayer.getAttached(ModAttachments.RINGING_ATTACHMENT_TYPE) != null) {
                ServerPlayNetworking.send(sender, new ClientboundPhoneNotificationPacket(false, Component.translatable("phone.error.receiver_busy")));
                return;
            }
            MinetokVoicechatPlugin.ringPlayer(sender,receivingPlayer);
        } else {
            ServerPlayNetworking.send(sender, new ClientboundPhoneNotificationPacket(true, Component.translatable("phone.error.server_missing_mod", "Simple Voicechat")));
        }
    }
}
