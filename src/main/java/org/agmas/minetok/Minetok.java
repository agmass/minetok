package org.agmas.minetok;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.LevelResource;
import org.agmas.minetok.init.ModAttachments;
import org.agmas.minetok.init.ModItems;
import org.agmas.minetok.init.ModPackets;
import org.agmas.minetok.messages.MessagesHandler;
import org.agmas.minetok.network.NetMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.UUID;

public class Minetok implements ModInitializer {
	public static final String MOD_ID = "minetok";

	public static MessagesHandler messagesHandler;
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("phone phone phone sahur");

		ServerTickEvents.END_SERVER_TICK.register((server -> {
			server.getPlayerList().getPlayers().forEach((p)->{
				if (p.getAttached(ModAttachments.RINGING_ATTACHMENT_TYPE) != null) {
					if (server.getPlayerList().getPlayer(p.getAttached(ModAttachments.RINGING_ATTACHMENT_TYPE).callingId) == null) p.removeAttached(ModAttachments.RINGING_ATTACHMENT_TYPE);
				}
			});
		}));

		ServerLifecycleEvents.SERVER_STARTED.register((minecraftServer -> {
			messagesHandler = new MessagesHandler(minecraftServer.getWorldPath(LevelResource.ROOT));
		}));


		ModItems.init();
		ModPackets.init();
		ModAttachments.init();
	}
}
