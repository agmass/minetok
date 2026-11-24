package org.agmas.minetok.fanfic;

import java.io.File;
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

    public File getFile(UUID creator, int ficId) {
        return new File(dir()+"/" + creator.toString() + "-" + ficId + ".txt");
    }

    public List<UUID> getFics() {
        ArrayList<UUID> uuids = new ArrayList<>();
        for (String s : new File(dir()).list()) {
        }
        return uuids;
    }
}
