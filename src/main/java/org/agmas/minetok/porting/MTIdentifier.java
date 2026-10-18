package org.agmas.minetok.porting;

//? if >=1.21.11 {

import net.minecraft.resources.Identifier;
//? } else {
/*import net.minecraft.resources.ResourceLocation;
*///? }

import net.minecraft.resources.ResourceKey;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.StringReader;
import org.agmas.minetok.Minetok;

public class MTIdentifier {
    //? if >=1.21.11 {
    public Identifier id;
    //? } else {
    /*public ResourceLocation id;
     *///? }


    //? if >=1.21.11 {
    public MTIdentifier(Identifier id) {
        //? } else {
        /*public MTIdentifier(ResourceLocation id) {
         *///? }
        this.id = id;
    }

    //? if >=1.21.11 {
    public static MTIdentifier of(String path) {
        return new MTIdentifier(Identifier.fromNamespaceAndPath(Minetok.MOD_ID, path));
    }
    public static MTIdentifier of(ResourceKey path) {
        return new MTIdentifier(path.identifier());
    }
    public static MTIdentifier parse(String path) {
        return new MTIdentifier(Identifier.parse(path));
    }
    public static MTIdentifier ofVanilla(String path) {
        return new MTIdentifier(Identifier.fromNamespaceAndPath("minecraft", path));
    }
    public static MTIdentifier read(StringReader stringReader) throws CommandSyntaxException {
        return new MTIdentifier(Identifier.read(stringReader));
    }
    //? } else if >1.20.4 {
    /*public static MTIdentifier of(String path) {
        return new MTIdentifier(ResourceLocation.fromNamespaceAndPath(Minetok.MOD_ID, path));
    }
    public static MTIdentifier of(ResourceKey path) {
        return new MTIdentifier(path.location());
    }
    public static MTIdentifier parse(String path) {
        return new MTIdentifier(ResourceLocation.parse(path));
    }
    public static MTIdentifier ofVanilla(String path) {
        return new MTIdentifier(ResourceLocation.fromNamespaceAndPath("minecraft", path));
    }
    public static MTIdentifier read(StringReader stringReader) throws CommandSyntaxException {
        return new MTIdentifier(ResourceLocation.read(stringReader));
    }
    *///? } else {

    /*public static MTIdentifier of(ResourceKey path) {
        return new MTIdentifier(path.location());
    }
   public static MTIdentifier of(String path) {
        return new MTIdentifier(ResourceLocation.tryParse(Minetok.MOD_ID + ":" + path));
    }
    public static MTIdentifier parse(String path) {
        return new MTIdentifier(ResourceLocation.tryParse(path));
    }
    public static MTIdentifier ofVanilla(String path) {
        return new MTIdentifier(ResourceLocation.tryParse("minecraft:" + path));
    }
    public static MTIdentifier read(StringReader stringReader) throws CommandSyntaxException {
        return new MTIdentifier(ResourceLocation.read(stringReader));
    }
    *///? }
}
