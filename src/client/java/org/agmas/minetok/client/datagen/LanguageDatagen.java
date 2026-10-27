package org.agmas.minetok.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
//? if >=26.1 {
/*import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
*///? } else {
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
//? }

import java.util.concurrent.CompletableFuture;

public class LanguageDatagen extends FabricLanguageProvider {

    //? if >=26.1 {
    /*public LanguageDatagen(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        *///? } else {
        public LanguageDatagen(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
         //? }
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider provider, TranslationBuilder translationBuilder) {

        translationBuilder.add("phone.app.minetok.calls", "Calls");
        translationBuilder.add("phone.app.minetok.settings", "Settings");

        translationBuilder.add("config.minetok.muteOtherPhones", "Mute Other Phones");
        translationBuilder.add("config.minetok.muteRingtones", "Mute All Ringtones");
        translationBuilder.add("config.minetok.disableRingingIndicator", "Disable Ringing Text");
        translationBuilder.add("config.minetok.ringtone", "Ringtone Selector");
        translationBuilder.add("config.minetok.install_ytdlp", "Install/Update YT-DLP");



        translationBuilder.add("phone.error", "Error");
        translationBuilder.add("phone.notification", "Notification");
        translationBuilder.add("phone.tap_continue", "Tap Anywhere to Continue");

        translationBuilder.add("phone.error.missing_mod", "Please Install %s");
        translationBuilder.add("phone.error.no_vlc", "Minetok failed to load. You probbably don't have VLC Media Player installed. Go install that please\n\n%s");
        translationBuilder.add("phone.error.no_ytdlp", "Minetok requires yt-dlp. You can go install it (or update when needed) in the Settings app.");
        translationBuilder.add("phone.error.no_service", "No Service");
        translationBuilder.add("phone.install_fail", "Failed to install yt-dlp. Check logs for errors.");
        translationBuilder.add("phone.installing", "Installing..");
        translationBuilder.add("phone.install_success", "Installed yt-dlp!");
        translationBuilder.add("phone.error.server_missing_mod", "Server Missing Mod: %s");
        translationBuilder.add("phone.media_not_found", "%s not found");
        translationBuilder.add("phone.youtube_error", "Youtube links don't work. Sorry. ;_C");

        translationBuilder.add("phone.ringtone", "%s's phone is ringing");
        translationBuilder.add("phone.error.receiver_offline", "Receiver not online");
        translationBuilder.add("phone.error.receiver_busy", "Receiver Busy");
        translationBuilder.add("phone.error.cant_call_self", "You can't call yourself!");
        translationBuilder.add("phone.notification.waiting_for_server", "Waiting for Server...");
    }

}
