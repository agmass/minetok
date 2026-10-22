package org.agmas.minetok.client.util;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.agmas.minetok.client.api.Application;
import org.agmas.minetok.porting.MTIdentifier;

public class MinetokClientRegistries {
    private static final ResourceKey<Registry<Application>> APPLICATIONS_KEY = createRegistryKey("applications");
    public static final Registry<Application> APPLICATIONS =
        //? if <26.1 {
        FabricRegistryBuilder.createSimple(APPLICATIONS_KEY)
        //? } else {
        /*FabricRegistryBuilder.create(APPLICATIONS_KEY)
         *///? }
        .buildAndRegister();


    private static final ResourceKey<Registry<Application>> OVERLAYS_KEY = createRegistryKey("overlays");
    public static final Registry<Application> OVERLAYS =
            //? if <26.1 {
            FabricRegistryBuilder.createSimple(OVERLAYS_KEY)
             //? } else {
            /*FabricRegistryBuilder.create(OVERLAYS_KEY)
                    *///? }
                    .buildAndRegister();

    public static void init()
    {}

    public static <T> ResourceKey<Registry<T>> createRegistryKey(String string) {
        return ResourceKey.createRegistryKey(MTIdentifier.of(string).id);
    }
}
