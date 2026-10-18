package org.agmas.minetok.init;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import org.agmas.minetok.network.ServerboundCallPacket;

public class ModPackets {
    public static void init() {
        //? if >=26.1 {
        PayloadTypeRegistry.clientboundPlay()
        //? } else {
        /*PayloadTypeRegistry.playS2C()
         *///? }
            .register(ServerboundCallPacket.TYPE, ServerboundCallPacket.CODEC);


    }
}
