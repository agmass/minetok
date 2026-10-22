package org.agmas.minetok.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import org.agmas.minetok.client.init.MinetokApplications;
import org.agmas.minetok.client.init.MinetokOverlays;
import org.agmas.minetok.client.init.ModClientPackets;
import org.agmas.minetok.client.render.Phone;
import org.agmas.minetok.client.render.PhoneTextureRenderer;
import org.agmas.minetok.client.util.MinetokClientRegistries;
import org.agmas.minetok.client.util.OtherPlayerPhone;
import org.agmas.minetok.init.ModAttachments;
import org.agmas.minetok.init.ModItems;
import org.agmas.minetok.network.ServerboundPhoneStatePacket;
import org.agmas.minetok.porting.MTIdentifier;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Function;

public class MinetokClient implements ClientModInitializer {

	public static Phone phone;
	public static HashMap<UUID, OtherPlayerPhone> playerPhoneHashMap = new HashMap<>();
	public static RenderStateDataKey<MTIdentifier> phoneTextureID = RenderStateDataKey.create(()->"phoneTextureID");
	public static MTIdentifier missingId = MTIdentifier.of("missing");

	private static final Function<Identifier, RenderType> PHONE_RENDER_TYPE = Util.memoize((texture) -> RenderType.create("phone_render_type", RenderSetup.builder(RenderPipelines.SOLID_BLOCK).useLightmap().withTexture("Sampler0", texture).affectsCrumbling().setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE).createRenderSetup()));

	public static final RenderType getPhoneRenderType(MTIdentifier identifier) {
		return PHONE_RENDER_TYPE.apply(identifier.id);
	}

	@Override
	public void onInitializeClient() {

		MinetokClientRegistries.init();
		MinetokApplications.init();
		MinetokOverlays.init();
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
			playerPhoneHashMap.clear();
		});

		ClientTickEvents.END_LEVEL_TICK.register((l)->{
			if (phone != null) {
				if (phone.networkDirty) {
					ClientPlayNetworking.send(new ServerboundPhoneStatePacket(phone.createTag()));
					phone.networkDirty = false;
				} else {
					ClientPlayNetworking.send(new ServerboundPhoneStatePacket(phone.createOnlyMouseTag()));
				}

				for (AbstractClientPlayer player : Minecraft.getInstance().level.players()) {
					if (player.hasAttached(ModAttachments.PHONE_STATE) && playerPhoneHashMap.containsKey(player.getUUID())) {
						OtherPlayerPhone otherPhone = playerPhoneHashMap.get(player.getUUID());
						otherPhone.phone.sync(player.getAttachedOrElse(ModAttachments.PHONE_STATE, new CompoundTag()));
					}
				}
			}

		});

        LevelRenderEvents.END_MAIN.register((wrc)->{
			if (Minecraft.getInstance().player != null) {
				if (!Minecraft.getInstance().player.isUsingItem()) {
					phone.selectedElement = null;
				}

				playerPhoneHashMap.keySet().removeIf(uuid -> {
					if (!Minecraft.getInstance().getConnection().getOnlinePlayerIds().contains(uuid)) {
						playerPhoneHashMap.get(uuid).removePlayerTexture();
						return true;
					}
					return false;
				});

				for (AbstractClientPlayer player : Minecraft.getInstance().level.players()) {
					if (player.hasAttached(ModAttachments.PHONE_STATE) && !playerPhoneHashMap.containsKey(player.getUUID())) {
						playerPhoneHashMap.put(player.getUUID(), new OtherPlayerPhone(player));
						OtherPlayerPhone otherPhone = playerPhoneHashMap.get(player.getUUID());
						otherPhone.uploadPlayerTexture();
					}
				}
			}
			//? if <=26.1
			PhoneTextureRenderer.INSTANCE.render(wrc.gameRenderer().getMainCamera(),phone);
			//? if >26.1
			//PhoneTextureRenderer.INSTANCE.render(wrc.gameRenderer().mainCamera(),phone);

			for (OtherPlayerPhone value : playerPhoneHashMap.values()) {
				//? if <=26.1
				value.renderer.render(wrc.gameRenderer().getMainCamera(), value.phone);
				//? if >26.1
				//value.renderer.render(wrc.gameRenderer().mainCamera(), value.phone);
			}
        });
	}

}