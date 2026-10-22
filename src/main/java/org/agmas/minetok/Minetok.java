package org.agmas.minetok;

import net.fabricmc.api.ModInitializer;
import org.agmas.minetok.init.ModAttachments;
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

		ModItems.init();
		ModPackets.init();
		ModAttachments.init();
	}
}
