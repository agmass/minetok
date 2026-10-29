package org.agmas.minetok.client.vlcj;

import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.minecraft.network.chat.Component;
import org.agmas.minetok.Minetok;
import org.agmas.minetok.client.MinetokClient;
import org.agmas.minetok.client.init.MinetokOverlays;
import org.agmas.minetok.client.render.sprites.NotificationSprite;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

public class YTDLPDownloader {
    public static boolean isYTDLPDownloaded = false;
    public String prefix = "";

    // https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp

    public YTDLPDownloader(String prefix) {
        this.prefix = prefix;
    }

    public static String getReleaseName() {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            return "yt-dlp.exe";
        }
        else if (System.getProperty("os.name").toLowerCase().contains("mac") || System.getProperty("os.name").contains("darwin")) {
            return "yt-dlp_macos";
        }
        return "yt-dlp_linux";
    }

    static ArrayList<Process> installingProcess = new ArrayList<>();

    public static void logYtDLP() {
        new Thread(()->{
            for (Process process : installingProcess) {
                    try {
                        String s = new String(process.getInputStream().readAllBytes());
                        if (!s.isEmpty())
                            Log.info(LogCategory.GENERAL, "[YT-DLP] " + s);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
            }
        }).start();
    }

    public static boolean checkYtDLP() {
        File ytDlpFile = new File("ytdlp/yt-dlp");
        isYTDLPDownloaded = ytDlpFile.exists();
        return isYTDLPDownloaded;
    }

    public Process downloadVideo(String url, String filename, String args) {
        try {
            return new ProcessBuilder()
                    .command(System.getProperty("os.name").toLowerCase().contains("win") ? "" :  "./" + // windows is  so weird and quirky and not like the other kids haha
                            "yt-dlp " + url + " -o " + prefix + "/" + filename + " " + args)
                    .directory(new File("ytdlp"))
                    .start();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isDownloadingVideo() {
        installingProcess.removeIf((p)->!p.isAlive());
        return !installingProcess.isEmpty();
    }

    public File metadataAndComments() {
        return new File("ytdlp/" + prefix + "/reel1.mp4.info.json");
    }

    public boolean hasNextReel() {
        File reel1 = new File("ytdlp/" + prefix + "/nextReel.mp4");
        return reel1.exists();
    }

    public boolean hasPlayableReel() {
        File reel1 = new File("ytdlp/" + prefix + "/reel1.mp4");
        return reel1.exists();
    }

    public File currentThumbnail() {
        return new File("ytdlp/" + prefix + "/reel1.mp4.png");
    }

    public File nextThumbnail() {
        return new File("ytdlp/" + prefix + "/nextReel.mp4.png");
    }
    public void cycleReel() {
        File reel1 = new File("ytdlp/" + prefix + "/reel1.mp4");
        File reel1Thumbnail = new File("ytdlp/"  + prefix + "/reel1.mp4.png");
        File reel1Info = new File("ytdlp/"  + prefix + "/reel1.mp4.info.json");
        File nextReel = new File("ytdlp/" + prefix + "/nextReel.mp4");
        File nextReelThumbnail = new File("ytdlp/" + prefix + "/nextReel.mp4.png");
        File nextReelInfo = new File("ytdlp/" + prefix + "/nextReel.mp4.info.json");
        if (reel1.exists()) {
            reel1.delete();
            reel1Thumbnail.delete();
            reel1Info.delete();
        }

        if (nextReel.exists()) {
            nextReel.renameTo(reel1);
            nextReelThumbnail.renameTo(reel1Thumbnail);
            nextReelInfo.renameTo(reel1Info);
        }
    }
    public boolean nextReel(String url, boolean isCurrent) {
        Process p = downloadVideo(url, isCurrent ? "reel1.mp4" : "nextReel.mp4"," --remux-video mp4 --write-thumbnail --convert-thumbnails png --write-comments"); // Force MP4, png thumbnail, write comments/info
        if (p != null) installingProcess.add(p);
        return p != null;
    }

    public static NotificationSprite installingNotification;
    public static void clearYTDLPFolder() {
        File ytDLPFolder = new File("ytdlp");
        if (ytDLPFolder.exists() && ytDLPFolder.isDirectory()) {
            for (String s : ytDLPFolder.list()) {
                if (!s.equals("yt-dlp")  && !s.equals("README.txt")) {
                    new File("./ytdlp/" +s).delete();
                }
            }
        }
    }

    public static void downloadYTDLP() {
        File README = new File("ytdlp/README.txt");
        File ytDlpFile = new File("ytdlp/yt-dlp");
        new File("ytdlp").mkdir();
        try {
            README.createNewFile();
            ytDlpFile.createNewFile();

            FileWriter writer = new FileWriter("ytdlp/README.txt");
            writer.write("WARNING!!! ANY FILES PLACED IN THIS FOLDER *WILL* BE DELETED ON STARTUP. PLACE FILES HERE WITH CAUTION" +
                    "\n\nVideo files used by Minetok will be stored here." +
                    "\nyt-dlp is downloaded through their github releases link: https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp" +
                    "\nIt's github, so, It should always be trustworthy. But you know what they say... You can never be too sure." +
                    "\n\n\n\nPS: to any devs reading from the YTDLPDownloader class, this class was mostly made for minetok; but you can use it for your own purposes too :) Just be careful not to download a 200 gigabyte video");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        installingNotification = new NotificationSprite(Component.translatable("phone.installing"), false, MinetokClient.phone);
        MinetokClient.phone.addNetOverlay(MinetokOverlays.NOTIFICATION, installingNotification);

        try (BufferedInputStream in = new BufferedInputStream(new URL("https://github.com/yt-dlp/yt-dlp/releases/latest/download/" + getReleaseName()).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(ytDlpFile)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
            MinetokClient.phone.addNetOverlay(MinetokOverlays.NOTIFICATION, new NotificationSprite(Component.translatable("phone.install_success"), false, MinetokClient.phone));
        } catch (IOException e) {
            MinetokClient.phone.addNetOverlay(MinetokOverlays.NOTIFICATION, new NotificationSprite(Component.translatable("phone.install_fail"), false, MinetokClient.phone));
            e.printStackTrace();
        }
        MinetokClient.phone.topSprite.overlays.remove(installingNotification);
        ytDlpFile.setExecutable(true);
    }
}
