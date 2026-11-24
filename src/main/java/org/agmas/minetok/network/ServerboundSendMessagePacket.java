package org.agmas.minetok.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.agmas.minetok.porting.MTID;

import java.util.UUID;

public record ServerboundSendMessagePacket(String uuid, String s) implements CustomPacketPayload {
    public static final Type<ServerboundSendMessagePacket> TYPE = new Type<>(MTID.of("send_messages").id);
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundSendMessagePacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ServerboundSendMessagePacket::uuid,
            ByteBufCodecs.STRING_UTF8, ServerboundSendMessagePacket::s, ServerboundSendMessagePacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
