package org.agmas.minetok.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.agmas.minetok.porting.MTIdentifier;

public record ClientboundPhoneNotificationPacket(boolean isError, Component component) implements CustomPacketPayload {
    public static final Type<ClientboundPhoneNotificationPacket> TYPE = new Type<>(MTIdentifier.of("notification").id);
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundPhoneNotificationPacket> CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, ClientboundPhoneNotificationPacket::isError, ComponentSerialization.STREAM_CODEC, ClientboundPhoneNotificationPacket::component, ClientboundPhoneNotificationPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
