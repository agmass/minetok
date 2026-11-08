package org.agmas.minetok.voicechat;

import de.maxhenkel.voicechat.api.*;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.agmas.minetok.attachments.RingingAttachment;
import org.agmas.minetok.init.ModAttachments;

import java.util.UUID;

public class MinetokVoicechatPlugin implements VoicechatPlugin {
    @Override
    public String getPluginId() {
        return "minetok";
    }

    public static VoicechatApi api;

    @Override
    public void initialize(VoicechatApi api) {
        VoicechatPlugin.super.initialize(api);
        MinetokVoicechatPlugin.api = api;
    }
    public static void acceptRing(ServerPlayer sender, ServerPlayer reciever) {
        VoicechatServerApi serverApi = (VoicechatServerApi) api;
        VoicechatConnection connection = serverApi.getConnectionOf(api.fromServerPlayer(sender));
        VoicechatConnection connectionReceiver = serverApi.getConnectionOf(api.fromServerPlayer(reciever));
        if (connection.getGroup() == null) return;
        connectionReceiver.setGroup(connection.getGroup());
    }

    public static void ringPlayer(ServerPlayer sender, ServerPlayer reciever) {
        VoicechatServerApi serverApi = (VoicechatServerApi) api;
        VoicechatConnection connection = serverApi.getConnectionOf(api.fromServerPlayer(sender));

        connection.setGroup(null);
        Group group = serverApi.groupBuilder()
                .setHidden(true)
                .setPersistent(false)
                .setName(sender.getScoreboardName() +"'s call")
                .setType(Group.Type.OPEN)
                .build();

        connection.setGroup(group);

        RingingAttachment ringingAttachment = new RingingAttachment();
        ringingAttachment.recieving = true;
        ringingAttachment.callingId = sender.getUUID();
        ringingAttachment.ringtone = reciever.getAttachedOrCreate(ModAttachments.SAVED_RINGTONE);
        reciever.setAttached(ModAttachments.RINGING_ATTACHMENT_TYPE, ringingAttachment);

        RingingAttachment sendingAttachment = new RingingAttachment();
        sendingAttachment.recieving = false;
        sendingAttachment.callingId = reciever.getUUID();
        sendingAttachment.ringtone = sender.getAttachedOrCreate(ModAttachments.SAVED_RINGTONE);
        sender.setAttached(ModAttachments.RINGING_ATTACHMENT_TYPE, sendingAttachment);
    }

}
