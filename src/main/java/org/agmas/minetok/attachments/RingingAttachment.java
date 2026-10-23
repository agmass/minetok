package org.agmas.minetok.attachments;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.Utf8String;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

import java.util.UUID;

public class RingingAttachment {


    public static StreamCodec<ByteBuf, RingingAttachment> STREAM_CODEC = new StreamCodec<ByteBuf, RingingAttachment>() {
        public RingingAttachment decode(ByteBuf byteBuf) {
            return RingingAttachment.decode(byteBuf);
        }

        public void encode(ByteBuf byteBuf, RingingAttachment ringingAttachment) {
            ringingAttachment.encode(byteBuf);
        }
    };

    public Identifier ringtone = SoundEvents.MUSIC_DISC_FAR.key().identifier();
    public UUID callingId = null;
    public boolean recieving = false;
    public boolean isPlayingSound = false;

    public static RingingAttachment decode(ByteBuf byteBuf) {
        RingingAttachment attachment = new RingingAttachment();
        attachment.recieving = byteBuf.readBoolean();
        attachment.ringtone = Identifier.parse(Utf8String.read(byteBuf, 32767));
        attachment.callingId = UUID.fromString(Utf8String.read(byteBuf, 32767));
        return attachment;
    }

    public void encode(ByteBuf byteBuf) {
        byteBuf.writeBoolean(recieving);
        Utf8String.write(byteBuf, callingId.toString(), 32767);
        Utf8String.write(byteBuf, callingId.toString(), 32767);
    }
}
