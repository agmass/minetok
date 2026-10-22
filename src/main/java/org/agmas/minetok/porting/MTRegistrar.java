package org.agmas.minetok.porting;

//? if >=26.1 {
/*import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
 *///? } else {
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
//? }
//? if >1.20.4 {
//? }
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;

public class MTRegistrar {

    public static Block registerBlock(String name, Function<BlockBehaviour.Properties, Block> blockFactory, BlockBehaviour.Properties properties, boolean shouldRegisterItem,
                                      //? if >1.19
                                      ResourceKey<CreativeModeTab> tab
                                      //? if <=1.19
                                      //CreativeModeTab tab
    ) {

        Block block = blockFactory.apply(properties
                //? if >=1.21.4 {
                .setId(keyOfBlock(name))
                 //? }
        );

        if (shouldRegisterItem) {
            registerItem(name, (b)->{return new BlockItem(block, b);}, new Item.Properties()
                    //? if >=1.21.4 {
                    .setId(keyOfItem(name)).useBlockDescriptionPrefix()
                     //? }
            , tab);
        }

        return Registry.register(BuiltInRegistries.BLOCK, org.agmas.minetok.porting.MTIdentifier.of(name).id, block);
    }


    public static <T> ResourceKey<Registry<T>> createRegistryKey(String string) {
        return ResourceKey.createRegistryKey(org.agmas.minetok.porting.MTIdentifier.of(string).id);
    }

    public static <T extends Item> T registerItem(String name, Function<Item.Properties, T> itemFactory, Item.Properties settings,
                                                  //? if >1.19
                                                  ResourceKey<CreativeModeTab> tab
                                                  //? if <=1.19
                                                  //CreativeModeTab tab
                                                  ) {
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, org.agmas.minetok.porting.MTIdentifier.of(name).id);

        T item = itemFactory.apply(settings
                //? if >=1.21.4 {
                .setId(itemKey)
                 //? }
        );

        Registry.register(BuiltInRegistries.ITEM, itemKey, item);

        //? if >=26.1 {
        /*CreativeModeTabEvents.modifyOutputEvent(tab)
        *///? } else {
        ItemGroupEvents.modifyEntriesEvent(tab)
        //? }
        .register((creativeTab) -> {
            creativeTab.accept(item);
        });

        return item;
    }


    //? if >=1.21.4 {
    private static ResourceKey<Block> keyOfBlock(String name) {
        return ResourceKey.create(Registries.BLOCK, org.agmas.minetok.porting.MTIdentifier.of(name).id);
    }

    private static ResourceKey<Item> keyOfItem(String name) {
        return ResourceKey.create(Registries.ITEM, org.agmas.minetok.porting.MTIdentifier.of(name).id);
    }
    //? }

}
