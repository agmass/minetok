package org.agmas.minetok.porting;

//~ !identify
//? if >=1.21.11 {

/*import net.minecraft.resources.Identifier;
*///? } else {
import net.minecraft.resources.ResourceLocation;
//? }

import net.minecraft.resources.ResourceKey;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.StringReader;
import org.agmas.minetok.Minetok;

public class MTID {
    //? if >=1.21.11 {
    /*public Identifier id;
    *///? } else {
    public ResourceLocation id;
     //? }


    //? if >=1.21.11 {
    /*public MTID(Identifier id) {
        *///? } else {
        public MTID(ResourceLocation id) {
         //? }
        this.id = id;
    }

    //? if >=1.21.11 {
    /*public static MTID of(String path) {
        return new MTID(Identifier.fromNamespaceAndPath(Minetok.MOD_ID, path));
    }
    public static MTID of(ResourceKey path) {
        return new MTID(path.identifier());
    }
    public static MTID parse(String path) {
        return new MTID(Identifier.parse(path));
    }
    public static MTID ofVanilla(String path) {
        return new MTID(Identifier.fromNamespaceAndPath("minecraft", path));
    }
    public static MTID read(StringReader stringReader) throws CommandSyntaxException {
        return new MTID(Identifier.read(stringReader));
    }
    *///? } else if >1.20.4 {
    public static MTID of(String path) {
        return new MTID(ResourceLocation.fromNamespaceAndPath(Minetok.MOD_ID, path));
    }
    public static MTID of(ResourceKey path) {
        return new MTID(path.location());
    }
    public static MTID parse(String path) {
        return new MTID(ResourceLocation.parse(path));
    }
    public static MTID ofVanilla(String path) {
        return new MTID(ResourceLocation.fromNamespaceAndPath("minecraft", path));
    }
    public static MTID read(StringReader stringReader) throws CommandSyntaxException {
        return new MTID(ResourceLocation.read(stringReader));
    }
    //? } else {

    /*public static MTID of(ResourceKey path) {
        return new MTID(path.location());
    }
   public static MTID of(String path) {
        return new MTID(ResourceLocation.tryParse(Minetok.MOD_ID + ":" + path));
    }
    public static MTID parse(String path) {
        return new MTID(ResourceLocation.tryParse(path));
    }
    public static MTID ofVanilla(String path) {
        return new MTID(ResourceLocation.tryParse("minecraft:" + path));
    }
    public static MTID read(StringReader stringReader) throws CommandSyntaxException {
        return new MTID(ResourceLocation.read(stringReader));
    }
    *///? }
}
