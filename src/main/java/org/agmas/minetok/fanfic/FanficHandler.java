package org.agmas.minetok.fanfic;

import io.netty.buffer.Unpooled;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import org.agmas.minetok.network.NetMessage;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FanficHandler {

    public String prefix;

    public FanficHandler(Path prefix) {
        this.prefix = prefix.toString().substring(0, prefix.toString().length() - 1);
    }

    public String dir() {
        new File(prefix + "fanfic").mkdir();
        return prefix + "fanfic";
    }

    public File getFile(Fanfic fic) {
        return new File(dir()+"/" + fic.author().toString() + "-" + fic.id());
    }


    public void writeFic(Fanfic fanfic, Component body, Player player) {
        try {
            File file = getFile(fanfic);
            FileOutputStream writer = new FileOutputStream(file);

            DataOutputStream outputStream = new DataOutputStream(writer);

            RegistryFriendlyByteBuf byteBuf = new RegistryFriendlyByteBuf(Unpooled.buffer(), player.registryAccess());

            ByteBufCodecs.STRING_UTF8.encode(byteBuf, fanfic.name());
            byteBuf.writeIdentifier(fanfic.itemResourceKey().identifier());
            ComponentSerialization.STREAM_CODEC.encode(byteBuf, body);

            outputStream.write(byteBuf.array());
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Fanfic parseFic(String fanfic, RegistryAccess access) {
        try {
            File file = new File(dir() + "/" + fanfic);
            if (file.exists()) {
                FileInputStream reader = new FileInputStream(file);
                byte[] previousBytes = reader.readAllBytes();
                reader.close();

                RegistryFriendlyByteBuf byteBuf2 = new RegistryFriendlyByteBuf(Unpooled.buffer(), access);
                byteBuf2.writeBytes(previousBytes);
                Fanfic fic = new Fanfic(ByteBufCodecs.STRING_UTF8.decode(byteBuf2), ResourceKey.create(Registries.ITEM, byteBuf2.readIdentifier()), UUID.fromString(fanfic.split("-")[0]), Integer.parseInt(fanfic.split("-")[1]),ComponentSerialization.STREAM_CODEC.decode(byteBuf2));
                reader.close();
                return fic;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Fanfic> getFics(RegistryAccess access) {
        ArrayList<Fanfic> fics = new ArrayList<>();
        new File(dir()).mkdir();
        for (String s : new File(dir()).list()) {
            fics.add(parseFic(s,access));
        }
        return fics;
    }
}
