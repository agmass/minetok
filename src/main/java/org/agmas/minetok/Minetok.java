package org.agmas.minetok;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;import org.agmas.minetok.init.ModAttachments;
import org.agmas.minetok.init.ModItems;
import org.agmas.minetok.init.ModPackets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Minetok implements ModInitializer {
	public static final String MOD_ID = "minetok";

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

		ModItems.init();
		ModPackets.init();
		ModAttachments.init();
	}
}
