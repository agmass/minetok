package org.agmas.minetok.init;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.impl.attachment.AttachmentTargetImpl;
import net.fabricmc.fabric.impl.attachment.sync.AttachmentChange;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import org.agmas.minetok.attachments.RingingAttachment;
import org.agmas.minetok.porting.MTID;

public class ModAttachments {
    public static final AttachmentType<RingingAttachment> RINGING_ATTACHMENT_TYPE = AttachmentRegistry.create(
            MTID.of("ringing_attachment").id,
            ringingAttachmentBuilder -> ringingAttachmentBuilder
                    .copyOnDeath()
                    .initializer(RingingAttachment::new)
                    .syncWith(RingingAttachment.STREAM_CODEC, AttachmentSyncPredicate.all())
    );

    public static final AttachmentType<ResourceLocation> SAVED_RINGTONE = AttachmentRegistry.create(
            MTID.of("saved_ringtone").id,
            ringingAttachmentBuilder -> ringingAttachmentBuilder
                    .copyOnDeath()
                    .initializer(()-> SoundEvents.MUSIC_DISC_FAR.key().location())
    );

    public static final AttachmentType<CompoundTag> PHONE_STATE = AttachmentRegistry.create(
            MTID.of("phone_state").id,
            phoneStateBuilder -> phoneStateBuilder
                    .copyOnDeath()
                    .syncWith(ByteBufCodecs.COMPOUND_TAG, AttachmentSyncPredicate.all())
    );


    public static void forceSyncPhoneTagAttachment(Player player, CompoundTag attachment) {
        AttachmentTargetImpl attachmentTarget = ((AttachmentTargetImpl) player);

        //? if >=26.2 {
        /*attachmentTarget.fabric_syncChange(ModAttachments.PHONE_STATE, new AttachmentChange(attachmentTarget.fabric_getSyncTargetInfo(), ModAttachments.PHONE_STATE, attachment));
         *///? } else if =26.1 {
        /*attachmentTarget.fabric_syncChange(ModAttachments.PHONE_STATE, AttachmentChange.create(attachmentTarget.fabric_getSyncTargetInfo(), ModAttachments.PHONE_STATE, attachment, attachmentTarget.fabric_getRegistryAccess()));
        *///? } else >1.21.1 {
        /*attachmentTarget.fabric_syncChange(ModAttachments.PHONE_STATE, AttachmentChange.create(attachmentTarget.fabric_getSyncTargetInfo(), ModAttachments.PHONE_STATE, attachment, attachmentTarget.fabric_getDynamicRegistryManager()));
        *///? }
    }

    public static void init() {}
}
