package org.agmas.minetok.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.agmas.minetok.porting.MTID;

import java.util.List;

public record ClientboundRecieveMessageListPacket(String uuid, List<NetMessage> messages) implements CustomPacketPayload {
    public static final Type<ClientboundRecieveMessageListPacket> TYPE = new Type<>(MTID.of("message_list").id);
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundRecieveMessageListPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ClientboundRecieveMessageListPacket::uuid,
            ByteBufCodecs.<RegistryFriendlyByteBuf, NetMessage>list().apply(NetMessage.STREAM_CODEC), ClientboundRecieveMessageListPacket::messages,
            ClientboundRecieveMessageListPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
