package org.agmas.minetok.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.Minecraft;
import org.agmas.minetok.client.init.MinetokApplications;
import org.agmas.minetok.client.init.ModClientPackets;
import org.agmas.minetok.client.render.Phone;
import org.agmas.minetok.client.render.PhoneTextureRenderer;
import org.agmas.minetok.client.util.MinetokClientRegistries;
import org.agmas.minetok.porting.MTIdentifier;

public class MinetokClient implements ClientModInitializer {

	public static Phone phone;

	public static final RenderType PHONE_RENDER_TYPE = RenderType.create("phone_render_type", RenderSetup.builder(RenderPipelines.SOLID_BLOCK).useLightmap().withTexture("Sampler0", MTIdentifier.of("phone_texture").id).affectsCrumbling().setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE).createRenderSetup());
	@Override
	public void onInitializeClient() {

		MinetokClientRegistries.init();
		MinetokApplications.init();
		ModClientPackets.init();

		ClientLifecycleEvents.CLIENT_STARTED.register((m)->{
			PhoneTextureRenderer.INSTANCE = new PhoneTextureRenderer();
			Minecraft.getInstance().getTextureManager().register(MTIdentifier.of("phone_texture").id, PhoneTextureRenderer.INSTANCE);
		});

		ClientPlayConnectionEvents.JOIN.register((cpl,ps,m)->{
			phone = new Phone(m.player);
		});

		ClientPlayConnectionEvents.DISCONNECT.register((cpl,m)->{
			phone = null;
		});

        LevelRenderEvents.END_MAIN.register((wrc)->{
			if (Minecraft.getInstance().player != null) {
				if (!Minecraft.getInstance().player.isUsingItem()) {
					phone.selectedElement = null;
				}
			}
			PhoneTextureRenderer.overrideBuffers = true;
			//? if <=26.1
			PhoneTextureRenderer.INSTANCE.render(wrc.gameRenderer().getMainCamera());
			//? if >26.1
			//PhoneTextureRenderer.INSTANCE.render(wrc.gameRenderer().mainCamera());
			PhoneTextureRenderer.overrideBuffers = false;
        });
	}

}