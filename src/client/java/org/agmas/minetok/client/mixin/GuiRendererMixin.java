package org.agmas.minetok.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.render.GuiRenderer;
//? if >=26.1
import net.minecraft.client.renderer.Projection;
//? if <26.3
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.minecraft.client.renderer.ProjectionMatrixBuffer;
import org.agmas.minetok.client.render.PhoneTextureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GuiRenderer.class)
public class GuiRendererMixin {

    //? if <26.1 {
    /*@WrapOperation(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ProjectionMatrixBuffer;getBuffer(FF)Lcom/mojang/blaze3d/buffers/GpuBufferSlice;"))
    public GpuBufferSlice override(ProjectionMatrixBuffer instance, float f, float g, Operation<GpuBufferSlice> original) {
        if (PhoneTextureRenderer.overrideBuffers) {
            return original.call(instance,720f,1280f);
        }
        return original.call(instance,f,g);
    }
    *///? } else {
    @WrapOperation(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/Projection;setupOrtho(FFFFZ)V"))
    public void override(Projection instance, float zNear, float zFar, float width, float height, boolean invertY, Operation<Void> original) {
        if (PhoneTextureRenderer.overrideBuffers) {
            original.call(instance,zNear,zFar,720f,1280f,invertY);
            return;
        }
        original.call(instance,zNear,zFar,width,height,invertY);
    }
    //? }

}
