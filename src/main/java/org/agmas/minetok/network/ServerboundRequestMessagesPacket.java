package org.agmas.minetok.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.agmas.minetok.porting.MTID;

public record ServerboundRequestMessagesPacket(String UUID, int page) implements CustomPacketPayload {
    public static final Type<ServerboundRequestMessagesPacket> TYPE = new Type<>(MTID.of("requestMessages").id);
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundRequestMessagesPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ServerboundRequestMessagesPacket::UUID,
            ByteBufCodecs.INT, ServerboundRequestMessagesPacket::page, ServerboundRequestMessagesPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
