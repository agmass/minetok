package org.agmas.minetok.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;

public record NetMessage(boolean receiving, long timestamp, Component message) {
    public static StreamCodec<RegistryFriendlyByteBuf, NetMessage> STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, NetMessage>() {
        @Override
        public void encode(RegistryFriendlyByteBuf object, NetMessage object2) {
            object.writeBoolean(object2.receiving);
            object.writeLong(object2.timestamp);
            ComponentSerialization.STREAM_CODEC.encode(object, object2.message);
        }

        public NetMessage decode(RegistryFriendlyByteBuf byteBuf) {
            return new NetMessage(byteBuf.readBoolean(), byteBuf.readLong(), ComponentSerialization.STREAM_CODEC.decode(byteBuf));
        }
    };
}
