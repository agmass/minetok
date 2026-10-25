package org.agmas.minetok.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.ExtraCodecs;
import org.agmas.minetok.porting.MTIdentifier;

import java.util.UUID;

public record ServerboundCallPacket(String playerUUID) implements CustomPacketPayload {
    public static final Type<ServerboundCallPacket> TYPE = new Type<>(MTIdentifier.of("call").id);
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundCallPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ServerboundCallPacket::playerUUID, ServerboundCallPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
