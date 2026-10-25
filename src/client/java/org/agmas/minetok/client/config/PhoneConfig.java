package org.agmas.minetok.client.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import org.agmas.minetok.Minetok;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class PhoneConfig {
    public static PhoneConfig INSTANCE = new PhoneConfig();

    public boolean muteOtherPhones = false;
    public boolean disableRingingIndicator = false;
    public boolean muteRingtones = false;
    public Identifier ringtone = SoundEvents.MUSIC_DISC_FAR.key().identifier();

    public static PhoneConfig parse() {
        PhoneConfig parsed = new PhoneConfig();
        File configFile = new File("config/minetok.json");
        if (configFile.exists()) {
            try {
                JsonElement element = JsonParser.parseReader(new FileReader(configFile));
                parsed.muteRingtones = element.getAsJsonObject().getAsJsonPrimitive("mute_ringtones").getAsBoolean();
                parsed.disableRingingIndicator = element.getAsJsonObject().getAsJsonPrimitive("disable_ringing_indicator").getAsBoolean();
                parsed.muteOtherPhones = element.getAsJsonObject().getAsJsonPrimitive("mute_other_phones").getAsBoolean();
                parsed.ringtone = Identifier.parse(element.getAsJsonObject().getAsJsonPrimitive("ringtone").getAsString());
            } catch (Exception ignored) {}
        }
        return parsed;
    }

    public static void write() {
        File configFile = new File("config/minetok.json");

        try {
            configFile.createNewFile();
            FileWriter writer = new FileWriter("config/minetok.json");
            JsonWriter jsonWriter = new JsonWriter(writer);

            jsonWriter.beginObject();
            jsonWriter.name("mute_ringtones").value(INSTANCE.muteRingtones);
            jsonWriter.name("mute_other_phones").value(INSTANCE.muteOtherPhones);
            jsonWriter.name("disable_ringing_indicator").value(INSTANCE.disableRingingIndicator);
            jsonWriter.name("ringtone").value(INSTANCE.ringtone.toString());
            jsonWriter.endObject();
            jsonWriter.close();

            Minetok.LOGGER.info("Minetok config updated");
        } catch (Exception e) {
            Minetok.LOGGER.info("Failed to update config: " + e.getMessage());
        }
    }
}
