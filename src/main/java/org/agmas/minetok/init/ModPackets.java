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
import org.agmas.minetok.network.*;
import org.agmas.minetok.voicechat.MinetokVoicechatPlugin;

import java.util.UUID;

public class ModPackets {
    public static void init() {
        //? if >=26.1 {
        PayloadTypeRegistry.serverboundPlay()
        //? } else {
        /*PayloadTypeRegistry.playC2S()
         *///? }
            .register(ServerboundCallPacket.TYPE, ServerboundCallPacket.CODEC);


        //? if >=26.1 {
        PayloadTypeRegistry.clientboundPlay()
                //? } else {
                /*PayloadTypeRegistry.playS2C()
                 *///? }
                .register(ClientboundPhoneNotificationPacket.TYPE, ClientboundPhoneNotificationPacket.CODEC);

        //? if >=26.1 {
        PayloadTypeRegistry.serverboundPlay()
                //? } else {
                /*PayloadTypeRegistry.playC2S()
                 *///? }
                .register(ServerboundPhoneStatePacket.TYPE, ServerboundPhoneStatePacket.CODEC);

        //? if >=26.1 {
        PayloadTypeRegistry.serverboundPlay()
         //? } else {
        /*PayloadTypeRegistry.playC2S()
                *///? }
                .register(ServerboundCallResponsePacket.TYPE, ServerboundCallResponsePacket.CODEC);

        //? if >=26.1 {
        PayloadTypeRegistry.serverboundPlay()
         //? } else {
        /*PayloadTypeRegistry.playC2S()
                *///? }
                .register(ServerboundRingtoneSetPacket.TYPE, ServerboundRingtoneSetPacket.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ServerboundPhoneStatePacket.TYPE, (payload, context)->{
            if (payload.phoneTag().contains("x")) {
                CompoundTag currentTag = context.player().getAttachedOrElse(ModAttachments.PHONE_STATE, new CompoundTag());
                currentTag.put("mouse", payload.phoneTag());
                context.player().setAttached(ModAttachments.PHONE_STATE, currentTag);
                ModAttachments.forceSyncPhoneTagAttachment(context.player(), currentTag);
            } else {
                context.player().setAttached(ModAttachments.PHONE_STATE, payload.phoneTag());
                ModAttachments.forceSyncPhoneTagAttachment(context.player(), payload.phoneTag());
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(ServerboundRingtoneSetPacket.TYPE, (payload, context)->{
            context.player().setAttached(ModAttachments.SAVED_RINGTONE, Identifier.parse(payload.ringtoneSoundId()));
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
