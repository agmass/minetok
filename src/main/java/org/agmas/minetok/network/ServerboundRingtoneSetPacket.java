package org.agmas.minetok.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.agmas.minetok.porting.MTIdentifier;

public record ServerboundRingtoneSetPacket(String ringtoneSoundId) implements CustomPacketPayload {
    public static final Type<ServerboundRingtoneSetPacket> TYPE = new Type<>(MTIdentifier.of("ringtone_set").id);
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundRingtoneSetPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ServerboundRingtoneSetPacket::ringtoneSoundId, ServerboundRingtoneSetPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
