package org.agmas.minetok.voicechat;

import de.maxhenkel.voicechat.api.Player;
import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;

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

    public static void ringPlayer(Player sender, Player reciever) {}

}
