package org.agmas.minetok.client.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.renderpearl.api.textures.GpuTexture;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import org.agmas.minetok.client.render.PhoneTextureRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RenderTarget.class)
public class ColorTextureMixin {

    @WrapMethod(method = "getColorTexture")
    public GpuTexture override(Operation<GpuTexture> original) {
        if (PhoneTextureRenderer.overrideBuffers) {
            return PhoneTextureRenderer.INSTANCE.getTexture();
        }
        return original.call();
    }

    @WrapMethod(method = "getColorTextureView")
    public GpuTextureView overrideView(Operation<GpuTextureView> original) {
        if (PhoneTextureRenderer.overrideBuffers) {
            return PhoneTextureRenderer.INSTANCE.getTextureView();
        }
        return original.call();
    }

    @WrapMethod(method = "getDepthTexture")
    public GpuTexture overrideD(Operation<GpuTexture> original) {
        if (PhoneTextureRenderer.overrideBuffers) {
            return PhoneTextureRenderer.INSTANCE.depthTexture;
        }
        return original.call();
    }

    @WrapMethod(method = "getDepthTextureView")
    public GpuTextureView overrideDView(Operation<GpuTextureView> original) {
        if (PhoneTextureRenderer.overrideBuffers) {
            return PhoneTextureRenderer.INSTANCE.depthTextureView;
        }
        return original.call();
    }
}
