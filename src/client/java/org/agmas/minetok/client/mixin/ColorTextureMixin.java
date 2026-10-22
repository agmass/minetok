package org.agmas.minetok.client.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import org.agmas.minetok.client.render.PhoneTextureRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RenderTarget.class)
public class ColorTextureMixin {

    @WrapMethod(method = "getColorTexture")
    public GpuTexture override(Operation<GpuTexture> original) {
        if (PhoneTextureRenderer.overrideColorBuffer != null) {
            return PhoneTextureRenderer.overrideColorBuffer;
        }
        return original.call();
    }

    @WrapMethod(method = "getColorTextureView")
    public GpuTextureView overrideView(Operation<GpuTextureView> original) {
        if (PhoneTextureRenderer.overrideColorViewBuffer != null) {
            return PhoneTextureRenderer.overrideColorViewBuffer;
        }
        return original.call();
    }

    @WrapMethod(method = "getDepthTexture")
    public GpuTexture overrideD(Operation<GpuTexture> original) {
        if (PhoneTextureRenderer.overrideDepthBuffer != null) {
            return PhoneTextureRenderer.overrideDepthBuffer;
        }
        return original.call();
    }

    @WrapMethod(method = "getDepthTextureView")
    public GpuTextureView overrideDView(Operation<GpuTextureView> original) {
        if (PhoneTextureRenderer.overrideDepthViewBuffer != null) {
            return PhoneTextureRenderer.overrideDepthViewBuffer;
        }
        return original.call();
    }
}
