package org.agmas.minetok.init;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import org.agmas.minetok.attachments.RingingAttachment;
import org.agmas.minetok.porting.MTIdentifier;

public class ModAttachments {
    public static final AttachmentType<RingingAttachment> RINGING_ATTACHMENT_TYPE = AttachmentRegistry.create(
            MTIdentifier.of("ringing_attachment").id,
            ringingAttachmentBuilder -> ringingAttachmentBuilder
                    .copyOnDeath()
                    .syncWith(RingingAttachment.STREAM_CODEC, AttachmentSyncPredicate.all())
    );


    public static void init() {}
}
