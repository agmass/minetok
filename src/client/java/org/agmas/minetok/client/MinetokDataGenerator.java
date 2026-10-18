package org.agmas.minetok.client;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import org.agmas.minetok.client.datagen.ItemModelDatagen;
import org.agmas.minetok.client.datagen.LanguageDatagen;

public class MinetokDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(ItemModelDatagen::new);
		pack.addProvider(LanguageDatagen::new);
	}
}
