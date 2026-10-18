package org.agmas.minetok.messages;

import com.mojang.datafixers.util.Pair;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.NullOps;
import net.minecraft.world.entity.player.Player;
import org.agmas.minetok.Minetok;
import org.agmas.minetok.network.NetMessage;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MessagesHandler {

    public String prefix;

    public MessagesHandler(Path prefix) {
        this.prefix = prefix.toString().substring(0,prefix.toString().length()-1);
    }

    public Pair<UUID, UUID> getUUIDPair(String filename) {
        if (filename.split("\\.").length >= 2) {
            return new Pair<>(UUID.fromString(filename.split("\\.")[0]), UUID.fromString(filename.split("\\.")[1]));
        } else {
            return null;
        }
    }

    public UUID getOpposite(Pair<UUID, UUID> pair, UUID primary) {
        if (pair.getFirst().equals(primary)) {
            return pair.getSecond();
        }
        if (pair.getSecond().equals(primary)) {
            return pair.getFirst();
        }
        return null;
    }

    public String dir() {
        new File(prefix + "messages").mkdir();
        return prefix + "messages";
    }

    public List<NetMessage> readMessages(Player sender, UUID recipient, int page) {
        try {
            File file = getFile(sender.getUUID(), recipient);
            if (file.exists()) {
                FileInputStream reader = new FileInputStream(file);
                byte[] previousBytes = reader.readAllBytes();
                reader.close();

                RegistryFriendlyByteBuf byteBuf2 = new RegistryFriendlyByteBuf(Unpooled.buffer(), sender.registryAccess());
                byteBuf2.writeBytes(previousBytes);
                List<NetMessage> msgArray = ByteBufCodecs.<RegistryFriendlyByteBuf, NetMessage>list().apply(NetMessage.STREAM_CODEC).decode(byteBuf2).reversed();
                if (msgArray.size() <= 10*page) {
                    return List.of();
                }
                return msgArray.subList(10*page, Math.min(10*(page+1), msgArray.size()));
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return List.of();
    }

    public void writeMessage(Player sender, UUID recipient, NetMessage message) {
        try {
            ArrayList<NetMessage> netMessagesList = new ArrayList<>();
            File file = getFile(sender.getUUID(), recipient);
            if (file.exists()) {
                FileInputStream reader = new FileInputStream(file);
                byte[] previousBytes = reader.readAllBytes();
                reader.close();

                RegistryFriendlyByteBuf byteBuf2 = new RegistryFriendlyByteBuf(Unpooled.buffer(), sender.registryAccess());
                byteBuf2.writeBytes(previousBytes);
                netMessagesList.addAll(ByteBufCodecs.<RegistryFriendlyByteBuf, NetMessage>list().apply(NetMessage.STREAM_CODEC).decode(byteBuf2));
            }
            netMessagesList.add(message);
            FileOutputStream writer = new FileOutputStream(file);

            DataOutputStream outputStream = new DataOutputStream(writer);

            RegistryFriendlyByteBuf byteBuf = new RegistryFriendlyByteBuf(Unpooled.buffer(), sender.registryAccess());

            ByteBufCodecs.<RegistryFriendlyByteBuf, NetMessage>list().apply(NetMessage.STREAM_CODEC).encode(byteBuf, netMessagesList);

            outputStream.write(byteBuf.array());
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File getFile(UUID sender, UUID recipient) {
        if (new File(dir()+"/" + recipient.toString() + "." + sender.toString()).exists()) {
            return new File(dir()+"/" + recipient.toString() + "." + sender.toString());
        }
        return new File(dir()+"/" + sender.toString() + "." + recipient.toString());
    }


    public boolean isRightHand(UUID sender, UUID recipient) {
        return new File(dir() + "/" + recipient.toString() + "." + sender.toString()).exists();
    }

    public List<UUID> getContacts(UUID player) {
        ArrayList<UUID> uuids = new ArrayList<>();
        for (String s : new File(dir()).list()) {
            Pair<UUID, UUID> uuidPair = getUUIDPair(s);
            if (uuidPair != null) {
                UUID opposite = getOpposite(uuidPair, player);
                if (opposite != null)
                    uuids.add(opposite);
            } else {
                Minetok.LOGGER.error("Could not parse messages file: " + s);
            }
        }
        return uuids;
    }
}
