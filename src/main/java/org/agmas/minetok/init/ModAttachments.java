package org.agmas.minetok.init;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.impl.attachment.AttachmentTargetImpl;
import net.fabricmc.fabric.impl.attachment.sync.AttachmentChange;
import net.minecraft.world.entity.player.Player;
import org.agmas.minetok.attachments.RingingAttachment;
import org.agmas.minetok.porting.MTIdentifier;

public class ModAttachments {
    public static final AttachmentType<RingingAttachment> RINGING_ATTACHMENT_TYPE = AttachmentRegistry.create(
            MTIdentifier.of("ringing_attachment").id,
            ringingAttachmentBuilder -> ringingAttachmentBuilder
                    .copyOnDeath()
                    .initializer(RingingAttachment::new)
                    .syncWith(RingingAttachment.STREAM_CODEC, AttachmentSyncPredicate.all())
    );

    public static void forceSyncRingingAttachment(Player player, RingingAttachment playerAttachment) {
        AttachmentTargetImpl attachmentTarget = ((AttachmentTargetImpl) player);

        //? if >=26.2 {
        /*attachmentTarget.fabric_syncChange(ModAttachments.RINGING_ATTACHMENT_TYPE, new AttachmentChange(attachmentTarget.fabric_getSyncTargetInfo(), ModAttachments.RINGING_ATTACHMENT_TYPE, playerAttachment));
        *///? } else if =26.1 {
        attachmentTarget.fabric_syncChange(ModAttachments.RINGING_ATTACHMENT_TYPE, AttachmentChange.create(attachmentTarget.fabric_getSyncTargetInfo(), ModAttachments.RINGING_ATTACHMENT_TYPE, playerAttachment, attachmentTarget.fabric_getRegistryAccess()));
         //? } else {
        /*attachmentTarget.fabric_syncChange(ModAttachments.RINGING_ATTACHMENT_TYPE, AttachmentChange.create(attachmentTarget.fabric_getSyncTargetInfo(), ModAttachments.RINGING_ATTACHMENT_TYPE, playerAttachment, attachmentTarget.fabric_getDynamicRegistryManager()));
         *///? }
    }

    public static void init() {}
}
