package org.agmas.minetok.client.vlcj;

//? if >=26.2
//import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
//? if <26.2
import com.mojang.blaze3d.textures.TextureFormat;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;
import org.agmas.minetok.client.render.PhoneTextureRenderer;
import org.agmas.minetok.porting.MTIdentifier;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL33C;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent;
import uk.co.caprica.vlcj.player.component.MediaPlayerSpecs;
import uk.co.caprica.vlcj.player.component.callback.FilledCallbackImagePainter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CallbackVideoSurface;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormat;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormatCallback;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.RenderCallback;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.RenderCallbackAdapter;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.format.RV32BufferFormat;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public class VLCJHandler extends AbstractTexture {
    public Identifier textureId;
    public static CallbackVideoSurface emptyCallbackSurface;
    public int[] savedBuffer;
    public int textureWidth;
    public int textureHeight;

    private final EmbeddedMediaPlayer mediaPlayer;

    public void updateTexture() {
        if (texture instanceof GlTexture gl) {
            GlStateManager._bindTexture(gl.glId());
            GL11C.glTexSubImage2D(GL33C.GL_TEXTURE_2D, 0, 0, 0, textureWidth, textureHeight, GL33C.GL_RGBA, GL33C.GL_UNSIGNED_BYTE, savedBuffer);
        }
    }

    public VLCJHandler(int textureWidth, int textureHeight, Identifier identifier) {
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        texture = RenderSystem.getDevice().createTexture("videoTexture", 13,
                //? <26.2
                TextureFormat.RGBA8
                //? >=26.2
                //GpuFormat.RGBA8_UNORM
                , textureWidth, textureHeight, 1, 1);
        textureId = identifier;
        textureView = RenderSystem.getDevice().createTextureView(texture);
        Minecraft.getInstance().getTextureManager().register(textureId, this);

        savedBuffer = new int[1280*720];

        mediaPlayer = new MediaPlayerFactory().mediaPlayers().newEmbeddedMediaPlayer();
        mediaPlayer.videoSurface().set(new CallbackVideoSurface(
                new BufferFormatCallback() {
                    @Override
                    public BufferFormat getBufferFormat(int sourceWidth, int sourceHeight) {
                        return new RV32BufferFormat(textureWidth, textureHeight);
                    }

                    @Override
                    public void newFormatSize(int bufferWidth, int bufferHeight, int displayWidth, int displayHeight) {
                    }

                    @Override
                    public void allocatedBuffers(ByteBuffer[] buffers) {
                    }
                },
                new RenderCallbackAdapter(new int[1280*720]) {
                    @Override
                    protected void onDisplay(MediaPlayer mediaPlayer, int[] buffer) {
                        RV32BufferFormat
                        System.arraycopy(buffer, 0, savedBuffer, 0, buffer.length);
                    }
                },
                true,
                null
        ));

        mediaPlayer.media().play("test_video.mp4");
        sampler = RenderSystem.getSamplerCache().getRepeat(FilterMode.LINEAR);
    }

}
