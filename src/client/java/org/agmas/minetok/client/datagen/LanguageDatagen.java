package org.agmas.minetok.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
//? if >=26.1 {
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
//? } else {
/*import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
*///? }

import java.util.concurrent.CompletableFuture;

public class LanguageDatagen extends FabricLanguageProvider {

    //? if >=26.1 {
    public LanguageDatagen(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        //? } else {
        /*public LanguageDatagen(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
         *///? }
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider provider, TranslationBuilder translationBuilder) {

        translationBuilder.add("phone.app.minetok.calls", "Calls");

        translationBuilder.add("phone.error.missing_mod", "Please Install %s");
        translationBuilder.add("phone.error.no_service", "No Service");
    }

}
