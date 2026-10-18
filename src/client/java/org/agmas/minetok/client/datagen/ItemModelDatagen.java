package org.agmas.minetok.client.datagen;

//? if >=26.1 {
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
//? } else {
/*import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
 *///? }

//? if >=1.21.4 {
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.*;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.minecraft.client.renderer.item.properties.select.DisplayContext;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import org.agmas.minetok.init.ModItems;

import java.util.Arrays;
//? } else {
/*import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.*;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
*///? }

public class ItemModelDatagen extends FabricModelProvider {

    //? if >=26.1 {
    public ItemModelDatagen(FabricPackOutput output) {
        super(output);
    }
    //? } else {
    /*public ItemModelDatagen(FabricDataOutput output) {
        super(output);
    }
    *///? }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {

    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        createPhone(itemModelGenerator, ModItems.PHONE, ModelTemplates.FLAT_ITEM);
    }

    public void createPhone(ItemModelGenerators itemModelGenerator, Item item, ModelTemplate template) {
        Identifier world_id = itemModelGenerator.createFlatItemModel(item, template);
        Identifier gui_id = template.create(
                world_id.withPath(world_id.getPath()+"_transparent"),
                TextureMapping.layer0(TextureMapping.getItemTexture(item, "_transparent")),
                itemModelGenerator.modelOutput
        );

        itemModelGenerator.itemModelOutput.accept(item,
                ItemModelUtils.select(
                        new DisplayContext(),
                        ItemModelUtils.plainModel(world_id),
                        ItemModelUtils.when(
                                Arrays.asList(ItemDisplayContext.NONE),
                                ItemModelUtils.plainModel(gui_id)
                        )
                )
        );
    }

    @Override
    public String getName() {
        return "ItemModelDatagen";
    }
}