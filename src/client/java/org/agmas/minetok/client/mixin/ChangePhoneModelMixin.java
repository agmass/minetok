package org.agmas.minetok.client.mixin;

import net.minecraft.client.Minecraft;
import org.agmas.minetok.init.ModItems;
import org.agmas.minetok.porting.MTID;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
//? if <1.21.11 {
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemRenderer.class)
public abstract class ChangePhoneModelMixin {
    @Shadow
    public abstract ItemModelShaper getItemModelShaper();

    @Shadow
    @Final
    private ItemModelShaper itemModelShaper;

    @ModifyVariable(method = "render", at = @At("HEAD"), argsOnly = true)
    public BakedModel a(BakedModel bakedModel, @Local(argsOnly = true) ItemStack stack, @Local(argsOnly = true)ItemDisplayContext displayContext) {
        if (displayContext.equals(ItemDisplayContext.NONE) && stack.is(ModItems.PHONE)) {
            return getItemModelShaper().getModelManager().getModel(ModelResourceLocation.inventory(MTID.of("phone_empty").id));
        }
        return bakedModel;
    }

    @ModifyVariable(method = "getModel", at = @At("STORE"), ordinal = 1)
    public BakedModel b(BakedModel bakedModel, @Local(argsOnly = true)ItemStack stack) {
        if (stack.getItem() == ModItems.PHONE) {
            return this.itemModelShaper.getModelManager().getModel(ModelResourceLocation.inventory(MTID.of("phone").id));
        }
        return bakedModel;
    }
}
//? } else {
/*@Mixin(Minecraft.class)
public class ChangePhoneModelMixin {
}
*///? }
