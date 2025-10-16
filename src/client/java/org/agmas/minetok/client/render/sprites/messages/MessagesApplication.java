package org.agmas.minetok.client.render.sprites.messages;

import com.google.common.collect.BiMap;
import com.mojang.authlib.GameProfile;
//? if <=1.21.1
//import com.mojang.authlib.yggdrasil.ProfileResult;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.agmas.minetok.Minetok;
import org.agmas.minetok.api.PhoneTapInformation;
import org.agmas.minetok.client.MinetokClient;
import org.agmas.minetok.client.api.MTGraphics;
import org.agmas.minetok.client.animation.NBTHelper;
import org.agmas.minetok.client.api.NetworkSprite;
import org.agmas.minetok.client.init.MinetokOverlays;
import org.agmas.minetok.client.render.Phone;
import org.agmas.minetok.client.render.sprites.ExitButtonSprite;
import org.agmas.minetok.client.render.sprites.NotificationSprite;
import org.agmas.minetok.client.render.sprites.ScrollBarSprite;
import org.agmas.minetok.network.ServerboundRequestMessagesPacket;
import org.agmas.minetok.porting.MTID;

import java.awt.*;
import java.util.*;
import java.util.List;

public class MessagesApplication extends ExitButtonSprite implements NetworkSprite {
    public HashMap<UUID, Component> contactsAndPreview = new HashMap<>();
    HashMap<UUID, PlayerInfo> profiles = new HashMap<>();
    public static boolean warnedAboutSecurity = false;
    public MessagingContainerSprite messagingContainerSprite = null;
    EditBox textField = new EditBox(Minecraft.getInstance().font, 0,0,110,10, Component.empty());
    ScrollBarSprite scrollBarSprite = new ScrollBarSprite(Phone.SCREEN_WIDTH-64, 200);

    public MessagesApplication() {
        ClientPlayNetworking.send(new ServerboundRequestMessagesPacket(Minecraft.getInstance().player.getStringUUID(),-1));
    }

    @Override
    public void render(GuiGraphics graphics, PhoneTapInformation mouse, float renderDelta) {
        if (messagingContainerSprite == null || messagingContainerSprite.transition < 1) {
            if (messagingContainerSprite != null) {
                if (mouse.isClient) {
                    if (messagingContainerSprite.closing && messagingContainerSprite.transition <= 0) {
                        messagingContainerSprite = null;
                    }
                }
            }
            exitBarCapture(mouse);
            if (mouse.isClient && !warnedAboutSecurity) {
                warnedAboutSecurity = true;
                MinetokClient.currentlyRendering_phone.addNetOverlay(MinetokOverlays.NOTIFICATION, new NotificationSprite(Component.translatable("phone.messages.privacy"), false, MinetokClient.phone));
            }

            MTGraphics.blitSprite(graphics, Identifier.withDefaultNamespace("textures/block/lime_concrete.png"), -8,-8,0,0, Phone.SCREEN_WIDTH+16, Phone.SCREEN_HEIGHT+16,128,128);
            scrollBarSprite.render(graphics, mouse, renderDelta);

            if (profiles.size() < 5) {
                graphics.pose().pushMatrix();
                MTGraphics.translate(graphics, Phone.SCREEN_WIDTH / 2f, 140);
                MTGraphics.scale(graphics, 2, 2);
                graphics.drawString(Minecraft.getInstance().font, Component.translatable("phone.messages.new_chat"), -Minecraft.getInstance().font.width(Component.translatable("phone.messages.new_chat"))/2, 0, -1);
                graphics.pose().popMatrix();
            }

            int marginX = 40;
            int marginY;
            int i = 0;
            int spacing = 58;
            if (!contactsAndPreview.isEmpty()) {
                for (UUID contact : contactsAndPreview.keySet()) {
                    if (profiles.containsKey(contact)) continue;
                    if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
                        profiles.put(contact,new PlayerInfo(new GameProfile(contact, "Player" + contact.hashCode()), false));
                        continue;
                    }
                    //? if >1.21.1
                    Optional<GameProfile> profile = Minecraft.getInstance().services().profileResolver().fetchById(contact);
                    //? if <=1.21.1 {
                    /*Optional<GameProfile> profile;
                    ProfileResult result = Minecraft.getInstance().getMinecraftSessionService().fetchProfile(contact,false);
                    if (result != null) {
                        profile = Optional.of(result.profile());
                    } else {
                        profile = Optional.empty();
                    }
                    *///? }
                    if (profile.isPresent()) {
                        profiles.put(contact,new PlayerInfo(profile.get(), false));
                        Minetok.LOGGER.info("Added profile " + contact);
                    } else {
                        Minetok.LOGGER.info("Could not load contact: " + contact);
                    }
                }

                int scrollableArea = spacing * profiles.size();

                marginY = (int) Mth.lerp(scrollBarSprite.scroll, 180, -scrollableArea);

                for (PlayerInfo profile : profiles.values()) {
                    if (!textField.getValue().isEmpty() && !profile.getProfile().name().toLowerCase().contains(textField.getValue().toLowerCase()))
                        continue;
                    boolean selected = false;
                    if (mouse.x + 40 > marginX && mouse.x + 40 < Phone.SCREEN_WIDTH - 34) {
                        if (mouse.y + 40 > marginY + (spacing * i) && mouse.y + 40 < marginY + (spacing * i) + 50) {
                            selected = true;
                            graphics.fill(marginX - 6, marginY + (spacing * i) - 6, marginX + 56, marginY + (spacing * i) + 56, -1);
                            if (mouse.justReleased) {
                                messagingContainerSprite = new MessagingContainerSprite(profile);
                            }
                        }
                    }
                    PlayerFaceRenderer.
                            //? if <=1.21.11
                                    draw
                            //? if >1.21.11
                            //extractRenderState
                                    (graphics, profile.getSkin(), marginX, marginY + (spacing * i), 48);
                    graphics.pose().pushMatrix();
                    MTGraphics.translate(graphics, marginX + 58, marginY + (spacing * i));
                    graphics.pose().pushMatrix();
                    MTGraphics.translate(graphics, 0, 32);
                    MTGraphics.scale(graphics, 2);
                    if (contactsAndPreview.containsKey(profile.getProfile().id())) {
                        String s = contactsAndPreview.get(profile.getProfile().id()).tryCollapseToString();
                        if (s != null)
                            graphics.drawString(Minecraft.getInstance().font, s.length() <= 36 ? s : (s.substring(0,36) + "..."), 0, 0, selected ? Color.LIGHT_GRAY.getRGB() : Color.DARK_GRAY.getRGB(), false);
                    }
                    graphics.pose().popMatrix();
                    MTGraphics.translate(graphics,0,-2);
                    MTGraphics.scale(graphics, 4);
                    graphics.drawString(Minecraft.getInstance().font, profile.getProfile().name(), 0, 0, selected ? -1 : Color.LIGHT_GRAY.getRGB());
                    graphics.pose().popMatrix();
                    i++;
                }
            }


            super.render(graphics, mouse, renderDelta);

            textField.setEditable(true);

            graphics.pose().pushMatrix();
            MTGraphics.translate(graphics, 100, 85);
            MTGraphics.scale(graphics, 5);

            //? if >1.21.11
            //textField.extractRenderState(graphics, (int) mouse.x, (int) mouse.y,renderDelta);
            //? if <=1.21.11
            textField.render(graphics, (int) mouse.x, (int) mouse.y, renderDelta);
            if (MTGraphics.hoveringButton(mouse,80,70,600,70)) {
                if (mouse.justTapped) {
                    MinetokClient.phone.selectedElement = textField;
                }
            }
            textField.setFocused(MinetokClient.phone.selectedElement == textField);
            graphics.pose().popMatrix();

            if (MTGraphics.hoveringButton(mouse,(Phone.SCREEN_WIDTH-64),85,48,48)) {
                graphics.fill((Phone.SCREEN_WIDTH-68), 81, (Phone.SCREEN_WIDTH-12), 137, -1);
                if (mouse.justTapped) {
                    if (textField.getValue().isEmpty()) {
                        MinetokClient.currentlyRendering_phone.addNetOverlay(MinetokOverlays.NOTIFICATION, new NotificationSprite(Component.translatable("phone.messages.empty_contact"), false, MinetokClient.phone));
                        return;
                    }

                    if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
                        UUID uuid = UUID.randomUUID();
                        GameProfile profile = new GameProfile(uuid, "Player" + uuid.hashCode());
                        messagingContainerSprite = new MessagingContainerSprite(new PlayerInfo(profile,false));
                    } else {
                        //? if >1.21.1
                        Optional<GameProfile> profile = Minecraft.getInstance().services().profileResolver().fetchByName(textField.getValue());
                        //? if <=1.21.1 {
                        /*Optional<GameProfile> profile;
                        ProfileResult result = Minecraft.getInstance().getMinecraftSessionService().fetchProfile(Minecraft.getInstance().getConnection().getPlayerInfo(textField.getValue()).getProfile().id(), false);
                        if (result != null) {
                            profile = Optional.of(result.profile());
                        } else {
                            profile = Optional.empty();
                        }
                        *///? }
                        if (profile.isPresent()) {
                            if (!profile.get()
                                    //? if >1.21.1
                                    .id()
                                    //? if <=1.21.1
                                    //.getId()
                                    .equals(Minecraft.getInstance().player.getUUID())) {
                                Minetok.LOGGER.info("Added profile " + textField.getValue());
                                messagingContainerSprite = new MessagingContainerSprite(new PlayerInfo(profile.get(), false));
                            } else {
                                Minetok.LOGGER.info("Could not load contact: " + textField.getValue());
                                MinetokClient.phone.addNetOverlay(MinetokOverlays.NOTIFICATION, new NotificationSprite(Component.translatable("phone.error.cant_message_self"), true, MinetokClient.phone, true));
                            }
                        } else {
                            Minetok.LOGGER.info("Could not load contact: " + textField.getValue());
                        }
                    }
                }
            }
            MTGraphics.blit(graphics, MTID.of("textures/gui/add_contact.png").id, (Phone.SCREEN_WIDTH-64),85,(Phone.SCREEN_WIDTH-16),133,0,1,0,1);

        }
        if (messagingContainerSprite != null)
            messagingContainerSprite.render(graphics,mouse,renderDelta);
    }

    @Override
    public CompoundTag createTag() {
        CompoundTag tag = new CompoundTag();
        if (messagingContainerSprite != null) {
            tag.put("msgscreen", messagingContainerSprite.createTag());
        }
        tag.putString("search", textField.getValue());
        tag.put("scroll", scrollBarSprite.createTag());
        CompoundTag contacts = new CompoundTag();
        for (Map.Entry<UUID, Component> uuidComponentEntry : contactsAndPreview.entrySet()) {
            contacts.putString(uuidComponentEntry.getKey().toString(), uuidComponentEntry.getValue().tryCollapseToString());
        }
        tag.put("contacts", contacts);
        return tag;
    }

    @Override
    public void sync(CompoundTag phoneData) {
        if (messagingContainerSprite != null) {
            if (!phoneData.contains("msgscreen")) {
                messagingContainerSprite = null;
            } else {
                messagingContainerSprite.sync(NBTHelper.getCompoundOrEmpty(phoneData, "msgscreen"));
            }
        } else {
            if (phoneData.contains("msgscreen")) {
                UUID uuid = UUID.fromString(NBTHelper.getStringOr(NBTHelper.getCompound(phoneData, "msgscreen"), "other", Minecraft.getInstance().player.getStringUUID()));
                if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
                    GameProfile profile = new GameProfile(uuid, "Player" + uuid.hashCode());
                    messagingContainerSprite = new MessagingContainerSprite(new PlayerInfo(profile,false));
                } else {
                    //? if >1.21.1
                    Optional<GameProfile> profile = Minecraft.getInstance().services().profileResolver().fetchById(uuid);
                    //? if <=1.21.1 {
                    /*Optional<GameProfile> profile;
                    ProfileResult result = Minecraft.getInstance().getMinecraftSessionService().fetchProfile(uuid,false);
                    if (result != null) {
                        profile = Optional.of(result.profile());
                    } else {
                        profile = Optional.empty();
                    }
                    *///? }
                    if (profile.isPresent())
                        messagingContainerSprite = new MessagingContainerSprite(new PlayerInfo(profile.get(),false));
                }
            }
            textField.setValue(NBTHelper.getStringOr(phoneData, "search", ""));
            scrollBarSprite.sync(NBTHelper.getCompoundOrEmpty(phoneData, "scroll"));
            contactsAndPreview.clear();
            for (String s : NBTHelper.getCompoundOrEmpty(phoneData, "contacts").keySet()) {
                contactsAndPreview.put(UUID.fromString(s), Component.literal(NBTHelper.getStringOr(NBTHelper.getCompound(phoneData, "contacts"), s, "test")));
            }
        }
    }

    @Override
    public void charTypedWhileFocused(long l) {
        super.charTypedWhileFocused(l);
        if (messagingContainerSprite != null) {
            messagingContainerSprite.charTypedWhileFocused(l);
        }
    }
}
