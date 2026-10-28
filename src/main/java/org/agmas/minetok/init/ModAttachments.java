package org.agmas.minetok.init;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.impl.attachment.AttachmentTargetImpl;
import net.fabricmc.fabric.impl.attachment.sync.AttachmentChange;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
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

    public static final AttachmentType<Identifier> SAVED_RINGTONE = AttachmentRegistry.create(
            MTIdentifier.of("saved_ringtone").id,
            ringingAttachmentBuilder -> ringingAttachmentBuilder
                    .copyOnDeath()
                    .initializer(()-> SoundEvents.MUSIC_DISC_FAR.key().identifier())
    );

    public static final AttachmentType<CompoundTag> PHONE_STATE = AttachmentRegistry.create(
            MTIdentifier.of("phone_state").id,
            phoneStateBuilder -> phoneStateBuilder
                    .copyOnDeath()
                    .syncWith(ByteBufCodecs.COMPOUND_TAG, AttachmentSyncPredicate.all())
    );


    public static void forceSyncPhoneTagAttachment(Player player, CompoundTag attachment) {
        AttachmentTargetImpl attachmentTarget = ((AttachmentTargetImpl) player);

        //? if >=26.2 {
        attachmentTarget.fabric_syncChange(ModAttachments.PHONE_STATE, new AttachmentChange(attachmentTarget.fabric_getSyncTargetInfo(), ModAttachments.PHONE_STATE, attachment));
         //? } else if =26.1 {
        /*attachmentTarget.fabric_syncChange(ModAttachments.PHONE_STATE, AttachmentChange.create(attachmentTarget.fabric_getSyncTargetInfo(), ModAttachments.PHONE_STATE, attachment, attachmentTarget.fabric_getRegistryAccess()));
        *///? } else {
        /*attachmentTarget.fabric_syncChange(ModAttachments.PHONE_STATE, AttachmentChange.create(attachmentTarget.fabric_getSyncTargetInfo(), ModAttachments.PHONE_STATE, attachment, attachmentTarget.fabric_getDynamicRegistryManager()));
         *///? }
    }

    public static void init() {}
}
