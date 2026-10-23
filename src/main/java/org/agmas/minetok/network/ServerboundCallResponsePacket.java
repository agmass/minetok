package org.agmas.minetok.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.agmas.minetok.porting.MTIdentifier;

public record ServerboundCallResponsePacket(boolean accept) implements CustomPacketPayload {
    public static final Type<ServerboundCallResponsePacket> TYPE = new Type<>(MTIdentifier.of("call_response").id);
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundCallResponsePacket> CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, ServerboundCallResponsePacket::accept, ServerboundCallResponsePacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
