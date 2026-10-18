package org.agmas.minetok.client.render;

//? if <26.3 {

/*//? if >=26.2
import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.systems.GpuDevice;
*///? } else {
import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.device.GpuDevice;
//? }
import net.minecraft.client.gui.render.GuiRenderer;
import com.mojang.renderpearl.api.textures.*;
import net.minecraft.client.renderer.feature.*;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.AbstractTexture;
import org.agmas.minetok.client.MinetokClient;
//? if <26.1 {
/*import net.minecraft.client.gui.render.state.GuiRenderState;
*///? } else {
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.client.renderer.state.gui.PanoramaRenderState;
//? }
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.joml.Vector4f;
import java.util.List;
import java.util.function.Supplier;

public class PhoneTextureRenderer extends AbstractTexture {

    public static PhoneTextureRenderer INSTANCE;
    //? if <26.2
    //public static final MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(new ByteBufferBuilder(1536));
    public GpuTexture depthTexture;
    public GuiRenderer renderer;
    public GpuTextureView depthTextureView;
    public GuiRenderState renderState = new GuiRenderState();

    public static boolean overrideBuffers = false;

    public FeatureRenderDispatcher featureRenderDispatcher;
    private final ProjectionMatrixBuffer projectionMatrixBuffer = new ProjectionMatrixBuffer("PhoneMatrix"
            //? if <26.1
            //, 0f, 1000.0f, true
    );

    SubmitNodeStorage submitNodeCollector = new SubmitNodeStorage();

    public PhoneTextureRenderer() {
        super();
        createTexture();

        //? if <26.2 {
        /*featureRenderDispatcher = new FeatureRenderDispatcher(submitNodeCollector,
                //? <26.1
                //Minecraft.getInstance().getBlockRenderer(),
                //? >=26.1
                Minecraft.getInstance().getModelManager(),
                bufferSource, Minecraft.getInstance().getAtlasManager(), Minecraft.getInstance().renderBuffers().outlineBufferSource(), Minecraft.getInstance().renderBuffers().crumblingBufferSource(), Minecraft.getInstance().font
                //? >=26.1
                , Minecraft.getInstance().gameRenderer.getGameRenderState()
        );
        *///? } else {
        featureRenderDispatcher = new FeatureRenderDispatcher(Minecraft.getInstance().gameRenderer.renderBuffers(), Minecraft.getInstance().getModelManager(), Minecraft.getInstance().getAtlasManager(), Minecraft.getInstance().font, Minecraft.getInstance().gameRenderer.gameRenderState());
        //? }
        renderer = new GuiRenderer(renderState
                //? if <26.2
                //, bufferSource, submitNodeCollector
                , featureRenderDispatcher, List.of());
    }

    public void render(Camera camera) {
        RenderSystem.backupProjectionMatrix();
        RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(texture,
                //? if <26.2
                //0,
                //? if >=26.2
                new Vector4f(0,0,0,255),
                depthTexture, 1.0);

        //? if <26.3 {
        /*RenderSystem.outputColorTextureOverride = this.textureView;
        RenderSystem.outputDepthTextureOverride = this.depthTextureView;
        *///? }
        overrideBuffers = true;
        PoseStack poseStack = new PoseStack();

        GuiGraphicsExtractor graphics = new GuiGraphicsExtractor(Minecraft.getInstance(), renderState, 0,0);

        poseStack.pushPose();
        poseStack.rotate(camera.rotation());
        MinetokClient.phone.render(graphics);
        //? if >=26.1 {
        Projection projection = new Projection();
        projection.setupOrtho(0,1000f,720,1280,true);
        //? }
        renderer.render(
                //? if <26.2 {
                /*this.projectionMatrixBuffer.getBuffer(
                //? if <26.1
                //720,1280
                //? if >=26.1
                projection
                )
                *///? }
        );
        poseStack.popPose();

        //? if <26.3 {
        /*RenderSystem.outputColorTextureOverride = null;
        RenderSystem.outputDepthTextureOverride = null;
        *///? }
        overrideBuffers = false;
        RenderSystem.restoreProjectionMatrix();
    }

    public void createTexture() {
        GpuDevice gpuDevice = RenderSystem.getDevice();

        texture = gpuDevice.createTexture("phoneTexture", 13,
                //? <26.2
                //TextureFormat.RGBA8
                //? >=26.2
                GpuFormat.RGBA8_UNORM
                , 720, 1280, 1, 1);
        textureView = gpuDevice.createTextureView(texture);

        depthTexture = gpuDevice.createTexture(() -> "phoneDepthTexture", 9,
                //? if <26.2
                //TextureFormat.DEPTH32
                //? if >=26.2
                GpuFormat.D32_FLOAT
                , 720, 1280, 1, 1);
        depthTextureView = gpuDevice.createTextureView(depthTexture);

        sampler = RenderSystem.getSamplerCache().getRepeat(FilterMode.LINEAR);

        gpuDevice.createCommandEncoder().clearColorAndDepthTextures(texture,
                //? if <26.2
                //0,
                //? if >=26.2
                new Vector4f(0,0,0,255),
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
}
