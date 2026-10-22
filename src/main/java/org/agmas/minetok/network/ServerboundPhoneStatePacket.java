package org.agmas.minetok.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.agmas.minetok.porting.MTIdentifier;

public record ServerboundPhoneStatePacket(CompoundTag phoneTag) implements CustomPacketPayload {
    public static final Type<ServerboundPhoneStatePacket> TYPE = new Type<>(MTIdentifier.of("phone_network").id);
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundPhoneStatePacket> CODEC = StreamCodec.composite(ByteBufCodecs.COMPOUND_TAG, ServerboundPhoneStatePacket::phoneTag, ServerboundPhoneStatePacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
