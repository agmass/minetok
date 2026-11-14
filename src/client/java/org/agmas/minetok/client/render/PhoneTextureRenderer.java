package org.agmas.minetok.client.render;

//? if >1.21.1 {
//? if <26.3 {

//? if >=26.2
//import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.systems.GpuDevice;
//? } else {
/*import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.device.GpuDevice;
*///? }
import net.minecraft.client.gui.render.GuiRenderer;
import com.mojang.blaze3d.textures.*;
import net.minecraft.client.renderer.feature.*;
//? if <26.1 {
import net.minecraft.client.gui.render.state.GuiRenderState;
//? } else {
/*import net.minecraft.client.renderer.state.gui.GuiRenderState;
*///? }
//? } else {
/*import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.pipeline.MainTarget;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.minecraft.client.gui.Gui;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import net.minecraft.client.renderer.texture.Dumpable;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL30;

import java.io.IOException;
import java.nio.file.Path;
import static org.lwjgl.opengl.ARBInternalformatQuery2.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11C.GL_READ_BUFFER;
import static org.lwjgl.opengl.GL11C.glCopyTexSubImage2D;
import static org.lwjgl.opengl.GL15C.glBindBuffer;
import static org.lwjgl.opengl.GL30C.GL_READ_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30C.glGenerateMipmap;
*///? }
import net.minecraft.client.gui.GuiGraphics;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.AbstractTexture;
import org.joml.Vector4f;

import java.util.List;


public class PhoneTextureRenderer extends AbstractTexture {

    public static PhoneTextureRenderer INSTANCE;

    //? if >1.21.1 {
    //? if <26.2
    public static final MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(new ByteBufferBuilder(1536));
    public GpuTexture depthTexture;
    public GuiRenderer renderer;
    public GpuTextureView depthTextureView;
    public GuiRenderState renderState = new GuiRenderState();

    public static GpuTexture overrideColorBuffer;
    public static GpuTexture overrideDepthBuffer;
    public static GpuTextureView overrideColorViewBuffer;
    public static GpuTextureView overrideDepthViewBuffer;

    public FeatureRenderDispatcher featureRenderDispatcher;
    private static final CachedOrthoProjectionMatrixBuffer projectionMatrixBuffer = new CachedOrthoProjectionMatrixBuffer("PhoneMatrix"
            //? if <26.1
            , 0f, 1000.0f, true
    );

    SubmitNodeStorage submitNodeCollector = new SubmitNodeStorage();

    public PhoneTextureRenderer() {
        super();
        createTexture();

        //? if <26.2 {
        featureRenderDispatcher = new FeatureRenderDispatcher(submitNodeCollector,
                //? <26.1
                Minecraft.getInstance().getBlockRenderer(),
                //? >=26.1
                //Minecraft.getInstance().getModelManager(),
                bufferSource, Minecraft.getInstance().getAtlasManager(), Minecraft.getInstance().renderBuffers().outlineBufferSource(), Minecraft.getInstance().renderBuffers().crumblingBufferSource(), Minecraft.getInstance().font
                //? >=26.1
                //, Minecraft.getInstance().gameRenderer.getGameRenderState()
        );
        //? } else {
        /*featureRenderDispatcher = new FeatureRenderDispatcher(Minecraft.getInstance().gameRenderer.renderBuffers(), Minecraft.getInstance().getModelManager(), Minecraft.getInstance().getAtlasManager(), Minecraft.getInstance().font, Minecraft.getInstance().gameRenderer.gameRenderState());
        *///? }
        renderer = new GuiRenderer(renderState
                //? if <26.2
                , bufferSource, submitNodeCollector
                , featureRenderDispatcher, List.of());
    }

    public void render(Camera camera, Phone phone) {
        if (phone == null) return;
        RenderSystem.backupProjectionMatrix();
        RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(texture,
                //? if <26.2
                0,
                //? if >=26.2
                //new Vector4f(0,0,0,255),
                depthTexture, 1.0);

        //? if <26.3 {
        RenderSystem.outputColorTextureOverride = this.textureView;
        RenderSystem.outputDepthTextureOverride = this.depthTextureView;
        //? }

        overrideColorBuffer = texture;
        overrideColorViewBuffer = textureView;
        overrideDepthBuffer = depthTexture;
        overrideDepthViewBuffer = depthTextureView;

        PoseStack poseStack = new PoseStack();

        GuiGraphics graphics = new GuiGraphics(Minecraft.getInstance(), renderState, 0,0);

        poseStack.pushPose();
        poseStack.mulPose(camera.rotation());
        phone.render(graphics);
        //? if >=26.1 {
        /*Projection projection = new Projection();
        projection.setupOrtho(0,1000f,720,1280,true);
        *///? }
        renderer.render(
                //? if <26.2 {
                this.projectionMatrixBuffer.getBuffer(
                //? if <26.1
                720,1280
                //? if >=26.1
                //projection
                )
                //? }
        );
        poseStack.popPose();

        //? if <26.3 {
        RenderSystem.outputColorTextureOverride = null;
        RenderSystem.outputDepthTextureOverride = null;
        //? }
        overrideColorBuffer = null;
        overrideDepthBuffer = null;
        overrideColorViewBuffer = null;
        overrideDepthViewBuffer = null;
        RenderSystem.restoreProjectionMatrix();
    }

    public void createTexture() {
        GpuDevice gpuDevice = RenderSystem.getDevice();

        texture = gpuDevice.createTexture("phoneTexture", 13,
                //? <26.2
                TextureFormat.RGBA8
                //? >=26.2
                //GpuFormat.RGBA8_UNORM
                , 720, 1280, 1, 1);
        textureView = gpuDevice.createTextureView(texture);

        depthTexture = gpuDevice.createTexture(() -> "phoneDepthTexture", 9,
                //? if <26.2
                TextureFormat.DEPTH32
                //? if >=26.2
                //GpuFormat.D32_FLOAT
                , 720, 1280, 1, 1);
        depthTextureView = gpuDevice.createTextureView(depthTexture);

        sampler = RenderSystem.getSamplerCache().getRepeat(FilterMode.LINEAR);

        gpuDevice.createCommandEncoder().clearColorAndDepthTextures(texture,
                //? if <26.2
                0,
                //? if >=26.2
                //new Vector4f(0,0,0,255),
                depthTexture, 1.0);
    }

    @Override
    public GpuTextureView getTextureView() {
        return super.getTextureView();
    }

    @Override
    public GpuTexture getTexture() {
        return super.getTexture();
    }

    @Override
    public void close() {
        super.close();
        depthTexture.close();
        depthTextureView.close();
    }
    //? } else {

    /*public static boolean overriding = false;
    public RenderTarget phoneTarget;

    public PhoneTextureRenderer() {
        super();
        createTexture();
        phoneTarget = new MainTarget(Phone.SCREEN_WIDTH, Phone.SCREEN_HEIGHT);
        phoneTarget.setClearColor(0f,0f,0f,1f);
        phoneTarget.createBuffers(Phone.SCREEN_WIDTH, Phone.SCREEN_HEIGHT,false);
    }

    @Override
    public void load(ResourceManager resourceManager) throws IOException {

    }

    public void render(Phone phone) {
        if (phone == null) return;

        phoneTarget.clear(true);
        phoneTarget.bindWrite(true);
        RenderSystem.clear(256, Minecraft.ON_OSX);
        Matrix4f matrix4f = (new Matrix4f()).setOrtho(0.0F, Phone.SCREEN_WIDTH, Phone.SCREEN_HEIGHT, 0.0F, 1000.0F, 21000.0F);
        RenderSystem.setProjectionMatrix(matrix4f, VertexSorting.ORTHOGRAPHIC_Z);
        Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
        matrix4fStack.pushMatrix();
        matrix4fStack.translation(0.0F, 0.0F, -11000.0F);
        RenderSystem.applyModelViewMatrix();

        GuiGraphics graphics = new GuiGraphics(Minecraft.getInstance(), Minecraft.getInstance().renderBuffers().bufferSource());
        phone.render(graphics);
        graphics.flush();
        phoneTarget.unbindWrite();
        RenderSystem.bindTexture(id);

        TextureUtil.prepareImage(NativeImage.InternalGlFormat.RGBA, id, 4, Phone.SCREEN_WIDTH, Phone.SCREEN_HEIGHT);
        GlStateManager._glBindFramebuffer(GL_READ_FRAMEBUFFER, phoneTarget.frameBufferId);
        glCopyTexSubImage2D(GL30.GL_TEXTURE_2D,0,0,0,0,0,Phone.SCREEN_WIDTH,Phone.SCREEN_HEIGHT);
        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
        matrix4fStack.popMatrix();
    }

    public void createTexture() {
        id = TextureUtil.generateTextureId();
    }
    *///? }

}
