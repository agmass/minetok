package org.agmas.minetok.client.vlcj;

//? if <26.3 {
//? if >=26.2 {
import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.vulkan.VulkanGpuTexture;
//? }
//? if >1.21.1 {
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
//? if <26.2
//import com.mojang.blaze3d.textures.TextureFormat;
//? } else {
/*import com.mojang.blaze3d.platform.GlStateManager;
*///? }
//? } else {
/*
import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.commands.CommandEncoder;
import com.mojang.renderpearl.backend.opengl.GlStateManager;
import com.mojang.renderpearl.backend.vulkan.VulkanGpuTexture;
import com.mojang.renderpearl.backend.opengl.GlTexture;
*///? }
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import org.agmas.minetok.client.MinetokClient;
import org.agmas.minetok.client.render.Phone;
import org.lwjgl.opengl.*;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.AudioChannel;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CallbackVideoSurface;
import uk.co.caprica.vlcj.player.embedded.videosurface.VideoSurfaceAdapters;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormat;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormatCallback;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.RenderCallback;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.format.RV32BufferFormat;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;

public class VLCJHandler extends AbstractTexture {
    public Identifier textureId;
    public BufferedImage bufferedImage;

    public int sourceWidth;
    public int sourceHeight;

    public EmbeddedMediaPlayer mediaPlayer;
    public boolean textureDirty = false;
    public boolean textureLocked = false;

    public void destroy() {
        close();
        mediaPlayer.release();
    }

    //? if >1.21.1 {
    public void ensureSize() {
        if (sizeDirty) {
            sizeDirty = false;
            if (texture != null) {
                texture.close();
            }
            if (textureView != null) {
                textureView.close();
            }
            texture = RenderSystem.getDevice().createTexture("videoTexture", 13,
                    //? <26.2
                    //TextureFormat.RGBA8
                    //? >=26.2
                    GpuFormat.RGBA8_UNORM
                    , sourceWidth, sourceHeight, 1, 1);
            textureView = RenderSystem.getDevice().createTextureView(texture);
            Minecraft.getInstance().getTextureManager().register(textureId, this);
        }
    }

    public boolean updateTexture() {
        MinetokClient.lastRenderFrame = this::ensureSize;
        if (textureDirty && !textureLocked && bufferedImage != null && !Minecraft.getInstance().isPaused()) {
            //? if >=26.2 {
            if (texture instanceof VulkanGpuTexture vk) {
                textureDirty = false;
                MinetokClient.lastRenderFrame = () -> {
                    if (textureLocked) return;
                    ensureSize();

                    NativeImage thisMightBeSlowButHeyItWorks = new NativeImage(bufferedImage.getWidth(), bufferedImage.getHeight(),true);

                    textureLocked = true;
                    for (int x = 0; x < bufferedImage.getWidth(); x++) {
                        for (int y = 0; y < bufferedImage.getHeight(); y++) {
                            if (x > thisMightBeSlowButHeyItWorks.getWidth()) break;
                            if (y > thisMightBeSlowButHeyItWorks.getHeight()) continue;
                            thisMightBeSlowButHeyItWorks.setPixel(x, y, bufferedImage.getRGB(x,y));
                        }
                    }
                    textureLocked = false;

                    RenderSystem.getDevice().createCommandEncoder().writeToTexture(vk, thisMightBeSlowButHeyItWorks);
                    thisMightBeSlowButHeyItWorks.close();
                };
            }
            //? }
            if (texture instanceof GlTexture gl) {
                textureDirty = false;
                MinetokClient.lastRenderFrame = () -> {
                    if (textureLocked) return;
                    textureLocked = true;
                    ensureSize();

                    GlStateManager._bindTexture(gl.glId());
                    int[] pixels = bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null, 0, bufferedImage.getWidth());

                    GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 4);
                    GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, 0);
                    GL33.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL33.GL_BGRA, bufferedImage.getWidth(), bufferedImage.getHeight(), 0, GL33.GL_BGRA, GL11.GL_UNSIGNED_BYTE, pixels);
                    textureLocked = false;
                    GlStateManager._bindTexture(0);
                };
                return true;
            }
        }
        return false;
    }
    //? } else {
    /*public boolean updateTexture() {

        if (textureDirty && !textureLocked && bufferedImage != null && !Minecraft.getInstance().isPaused()) {
            textureDirty = false;
            MinetokClient.lastRenderFrame = () -> {
                if (textureLocked) return;
                textureLocked = true;
                // It's genuinely go so much easier on old versions LOL
                if (!GL11.glIsTexture(id) || id == -1) {
                    id = TextureUtil.generateTextureId();
                    Minecraft.getInstance().getTextureManager().register(textureId, this);
                }
                RenderSystem.bindTexture(id);
                TextureUtil.prepareImage(NativeImage.InternalGlFormat.RGBA, id, 4, bufferedImage.getWidth(), bufferedImage.getHeight());
                int[] pixels = bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null, 0, bufferedImage.getWidth());

                GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 4);
                GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, 0);
                GL33.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0,0, bufferedImage.getWidth(), bufferedImage.getHeight(), GL33.GL_BGRA, GL11.GL_UNSIGNED_BYTE, pixels);
                GlStateManager._bindTexture(0);
                textureLocked = false;
            };
        }
        return true;
    }
    *///? }

    public boolean sizeDirty = false;
    public VLCJHandler(Identifier resourceLocation) {
        //? if >1.21.1 {
        texture = RenderSystem.getDevice().createTexture("videoTexture", 13,
                //? <26.2
                //TextureFormat.RGBA8
                //? >=26.2
                GpuFormat.RGBA8_UNORM
                , 720, 1280, 1, 1);
        textureView = RenderSystem.getDevice().createTextureView(texture);
        //? } else {
        /*id = TextureUtil.generateTextureId();
        *///? }
        textureId = resourceLocation;
        Minecraft.getInstance().getTextureManager().register(textureId, this);
        bufferedImage = new BufferedImage(720,1280, Image.SCALE_FAST);

        mediaPlayer = new MediaPlayerFactory().mediaPlayers().newEmbeddedMediaPlayer();
        mediaPlayer.videoSurface().set(new CallbackVideoSurface(
                new BufferFormatCallback() {
                    @Override
                    public BufferFormat getBufferFormat(int sourceWidth_, int sourceHeight_) {
                        sourceWidth = sourceWidth_;
                        sourceHeight = sourceHeight_;
                        bufferedImage = new BufferedImage(sourceWidth,sourceHeight, Image.SCALE_FAST);
                        sizeDirty = true;
                        return new RV32BufferFormat(sourceWidth_,sourceHeight_);
                    }

                    @Override
                    public void newFormatSize(int bufferWidth, int bufferHeight, int displayWidth, int displayHeight) {
                        sizeDirty = true;
                        bufferedImage = new BufferedImage(bufferWidth,bufferHeight, Image.SCALE_FAST);
                    }

                    @Override
                    public void allocatedBuffers(ByteBuffer[] buffers) {
                    }
                },
                new RenderCallback() {
                    @Override
                    public void display(MediaPlayer mediaPlayer, ByteBuffer[] nativeBuffers, BufferFormat bufferFormat, int displayWidth, int displayHeight) {
                        if (!textureDirty && !textureLocked) {
                            textureDirty = true;
                            textureLocked = true;
                            int[] rgbArray = new int[sourceWidth * sourceHeight];
                            nativeBuffers[0].asIntBuffer().get(rgbArray);
                            bufferedImage.setRGB(0, 0, sourceWidth, sourceHeight, rgbArray, 0, sourceWidth);
                            textureLocked = false;
                        }
                    }

                    @Override
                    public void lock(MediaPlayer mediaPlayer) {}

                    @Override
                    public void unlock(MediaPlayer mediaPlayer) {}
                },
                true,
                VideoSurfaceAdapters.getVideoSurfaceAdapter()
        ));
        mediaPlayer.audio().setChannel(AudioChannel.MONO);

        //? if >1.21.1
        sampler = RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR);
    }

    //? if <=1.21.1 {
    /*@Override
    public void load(ResourceManager resourceManager) throws IOException {

    }
    *///? }
}
