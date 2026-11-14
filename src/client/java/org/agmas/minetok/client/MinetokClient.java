package org.agmas.minetok.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
//? if >=1.21.11
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
//? >1.21.1
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
//? <=1.21.1
//import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.player.AbstractClientPlayer;
//? if >=1.21.11 {
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.util.Util;
//? } else {
/*import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.Util;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
*///? }
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import org.agmas.minetok.api.PhoneTapInformation;
import org.agmas.minetok.client.config.PhoneConfig;
import org.agmas.minetok.client.init.MinetokApplications;
import org.agmas.minetok.client.init.MinetokOverlays;
import org.agmas.minetok.client.init.ModClientPackets;
import org.agmas.minetok.client.render.Phone;
import org.agmas.minetok.client.render.PhoneTextureRenderer;
import org.agmas.minetok.client.sound.RingtoneSoundInstance;
import org.agmas.minetok.client.util.MinetokClientRegistries;
import org.agmas.minetok.client.util.OtherPlayerPhone;
import org.agmas.minetok.client.vlcj.YTDLPDownloader;
import org.agmas.minetok.init.ModAttachments;
import org.agmas.minetok.item.PhoneItem;
import org.agmas.minetok.network.ServerboundPhoneStatePacket;
import org.agmas.minetok.network.ServerboundRingtoneSetPacket;
import org.agmas.minetok.porting.MTID;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class MinetokClient implements ClientModInitializer {

	public static Phone phone;
	public static Phone currentlyRendering_phone;
	public static HashMap<UUID, OtherPlayerPhone> playerPhoneHashMap = new HashMap<>();
	//? if >=1.21.11 {
	public static RenderStateDataKey<MTID> phoneTextureID = RenderStateDataKey.create(()->"phoneTextureID");
	public static RenderStateDataKey<PhoneTapInformation> headMouseInfoID = RenderStateDataKey.create(()->"headMouseInfoID");
	private static final Function<Identifier, RenderType> PHONE_RENDER_TYPE = Util.memoize((texture) -> RenderType.create("phone_render_type", RenderSetup.builder(RenderPipelines.SOLID_BLOCK).useLightmap().withTexture("Sampler0", texture).affectsCrumbling().setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE).createRenderSetup()));

	public static final RenderType getPhoneRenderType(MTID resourceLocation) {
		return PHONE_RENDER_TYPE.apply(resourceLocation.id);
	}
	//? } else {
	/*private static final Function<Identifier, RenderType> PHONE_RENDER_TYPE = Util.memoize((texture) -> {
        RenderType.CompositeState compositeState = RenderType.CompositeState.builder().setShaderState(RenderType.RENDERTYPE_SOLID_SHADER).setTextureState(new RenderStateShard.TextureStateShard(texture,false,false)).setTransparencyState(RenderStateShard.NO_TRANSPARENCY).setLightmapState(RenderStateShard.LIGHTMAP).setOverlayState(RenderStateShard.OVERLAY).createCompositeState(true);
		return RenderType.create("phone_render_type", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 1536,compositeState);
	});

	public static final RenderType getPhoneRenderType(MTID resourceLocation) {
		return PHONE_RENDER_TYPE.apply(resourceLocation.id);
	}
	*///? }

	public static MTID missingId = MTID.of("missing");

	public static Runnable lastRenderFrame = null;

	@Override
	public void onInitializeClient() {

		YTDLPDownloader.clearYTDLPFolder();

		MinetokClientRegistries.init();
		MinetokApplications.init();
		MinetokOverlays.init();
		ModClientPackets.init();
		PhoneConfig.parse();

		ClientLifecycleEvents.CLIENT_STARTED.register((m)->{
			PhoneTextureRenderer.INSTANCE = new PhoneTextureRenderer();
			Minecraft.getInstance().getTextureManager().register(MTID.of("phone_texture").id, PhoneTextureRenderer.INSTANCE);
		});

		ClientPlayConnectionEvents.JOIN.register((cpl,ps,m)->{
			phone = new Phone(m.player);
			ps.sendPacket(new ServerboundRingtoneSetPacket(PhoneConfig.INSTANCE.ringtone.toString()));
		});

		ClientPlayConnectionEvents.DISCONNECT.register((cpl,m)->{
			playerPhoneHashMap.clear();
		});

		ClientTickEvents.END_WORLD_TICK.register((l)->{
			if (phone != null) {
				phone.mouse.prevX = phone.mouse.x;
				phone.mouse.prevY = phone.mouse.y;
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
					if (!PhoneConfig.INSTANCE.muteRingtones && (!PhoneConfig.INSTANCE.muteOtherPhones || player.equals(Minecraft.getInstance().player))) {
						if (player.getAttached(ModAttachments.RINGING_ATTACHMENT_TYPE) != null && !player.getAttached(ModAttachments.RINGING_ATTACHMENT_TYPE).isPlayingSound) {
							Identifier ringSound = player.getAttached(ModAttachments.RINGING_ATTACHMENT_TYPE).ringtone;
							player.getAttached(ModAttachments.RINGING_ATTACHMENT_TYPE).isPlayingSound = true;
							if (BuiltInRegistries.SOUND_EVENT.containsKey(ringSound)) {
								//? if >1.21.1  {
								Optional<Holder.Reference<SoundEvent>> potentialSound = BuiltInRegistries.SOUND_EVENT.get(ringSound);
								if (potentialSound.isPresent()) {
									SoundEvent s = potentialSound.get().value();
								//? } else {
								/*SoundEvent s = BuiltInRegistries.SOUND_EVENT.get(ringSound);
								if (s != null) {
								*///? }
									if (!PhoneConfig.INSTANCE.disableRingingIndicator)

										Minecraft.getInstance().gui
										//?if >=26.2
										//.hud
												.setOverlayMessage(Component.translatable("phone.ringtone", player.getScoreboardName()), true);
									Minecraft.getInstance().getSoundManager().play(new RingtoneSoundInstance(player, s, SoundSource.PLAYERS, player.getRandom()));
								}
							}
						}
					}
				}
			}
		});

		//? if >1.21.1
        WorldRenderEvents.END_MAIN.register((wrc)->{
		//? if <=1.21.1
		//WorldRenderEvents.END.register((wrc)->{
			YTDLPDownloader.logYtDLP();
			if (Minecraft.getInstance().player != null) {
				if (!Minecraft.getInstance().player.isUsingItem() || !(Minecraft.getInstance().player.getActiveItem().getItem() instanceof PhoneItem)) {
					phone.selectedElement = null;
					if (phone.topSprite.focusedSprite != null) {
						phone.topSprite.focusedSprite.unfocusedRender();
					}
				} else {
					currentlyRendering_phone = phone;
					//? if <=1.21.1
					//PhoneTextureRenderer.INSTANCE.render(phone);
					//? if >1.21.1 && <26.1
					PhoneTextureRenderer.INSTANCE.render(wrc.gameRenderer().getMainCamera(),phone);
					//? if >26.1
					//PhoneTextureRenderer.INSTANCE.render(wrc.gameRenderer().mainCamera(),phone);
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


			for (OtherPlayerPhone value : playerPhoneHashMap.values()) {
				if (value.phone.player.getUUID().equals(MinetokClient.phone.player.getUUID())) return;
				currentlyRendering_phone = value.phone;

				//? if <=1.21.1
				//value.renderer.render(value.phone);
				//? if >1.21.1 && <26.1
				value.renderer.render(wrc.gameRenderer().getMainCamera(), value.phone);
				//? if >26.1
				//value.renderer.render(wrc.gameRenderer().mainCamera(), value.phone);
			}
			currentlyRendering_phone = null;
        });
	}

	public static void positionPhoneInThirdPersonHand(PoseStack poseStack) { //made this just so i can hotswap lol
		poseStack.scale(0.25f,0.25f,0.15f);
		poseStack.translate(-0.5f,-0.15f,0.55f);
	}

}