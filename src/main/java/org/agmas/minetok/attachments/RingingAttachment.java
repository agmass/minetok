package org.agmas.minetok.attachments;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.minecraft.network.Utf8String;
import net.minecraft.network.codec.StreamCodec;

import java.util.UUID;

public class RingingAttachment {


    public static StreamCodec<ByteBuf, RingingAttachment> STREAM_CODEC = new StreamCodec<ByteBuf, RingingAttachment>() {
        public RingingAttachment decode(ByteBuf byteBuf) {
            return RingingAttachment.decode(byteBuf);
        }

        public void encode(ByteBuf byteBuf, RingingAttachment ringingAttachment) {
            ringingAttachment.encode(byteBuf,ringingAttachment);
        }
    };

    public int ringtone = 0;
    public UUID callingId = null;
    public boolean recieving = false;

    public static RingingAttachment decode(ByteBuf byteBuf) {
        RingingAttachment attachment = new RingingAttachment();
        attachment.ringtone = byteBuf.readInt();
        attachment.recieving = byteBuf.readBoolean();
        attachment.callingId = UUID.fromString(Utf8String.read(byteBuf, 32767));

        Log.info(LogCategory.GENERAL, "DECODED ATTACHMENT:\nringtone: " + attachment.ringtone + "\nplayer: " + attachment.callingId + "\nrecieving: " + attachment.recieving);
        return attachment;
    }

    public void encode(ByteBuf byteBuf, RingingAttachment perkyAttachment) {
        byteBuf.writeInt(ringtone);
        byteBuf.writeBoolean(recieving);
        if (callingId != null) {
            Utf8String.write(byteBuf, callingId.toString(), 32767);
        }
    }
}
