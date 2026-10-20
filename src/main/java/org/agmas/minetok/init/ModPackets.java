package org.agmas.minetok.init;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.agmas.minetok.network.ClientboundPhoneNotificationPacket;
import org.agmas.minetok.network.ServerboundCallPacket;
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

        ServerPlayNetworking.registerGlobalReceiver(ServerboundCallPacket.TYPE, (payload, context)->{
            handleRingRequest(context.player(), UUID.fromString(payload.playerUUID()), context.server());
        });
    }

    public static void handleRingRequest(ServerPlayer sender, UUID reciever, MinecraftServer server) {
        if (FabricLoader.getInstance().isModLoaded("voicechat_api")) {
            ServerPlayer receivingPlayer = server.getPlayerList().getPlayer(reciever);
            if (receivingPlayer == null) {
                ServerPlayNetworking.send(sender, new ClientboundPhoneNotificationPacket(true, Component.translatable("phone.error.receiver_offline")));
                return;
            }
            if (receivingPlayer.hasAttached(ModAttachments.RINGING_ATTACHMENT_TYPE)) {
                ServerPlayNetworking.send(sender, new ClientboundPhoneNotificationPacket(false, Component.translatable("phone.error.receiver_busy")));
                return;
            }
            MinetokVoicechatPlugin.ringPlayer(sender,receivingPlayer);
        } else {
            ServerPlayNetworking.send(sender, new ClientboundPhoneNotificationPacket(true, Component.translatable("phone.error.server_missing_mod", "Simple Voicechat")));
        }
    }
}
