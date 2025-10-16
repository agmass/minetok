package org.agmas.minetok.fanfic;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.UUID;

public record Fanfic(String name, ResourceKey<Item> itemResourceKey, UUID author, int id, Component body) {

}
