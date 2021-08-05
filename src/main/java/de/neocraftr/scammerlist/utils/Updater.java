package de.neocraftr.scammerlist.utils;

import com.google.common.io.Resources;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import de.neocraftr.scammerlist.ScammerList;
import net.labymod.utils.Consumer;
import net.labymod.utils.ServerData;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Updater {

    private static final String UPDATE_URL = "https://api.github.com/repos/Neocraftr/LabyMod-ScammerInfo/releases/latest";

    private ScammerList sc = ScammerList.getScammerList();
    private boolean updateAvailable = false;
    private String downloadUrl = null;
    private String latestVersion = null;
    private File addonJar = null;

    public Updater() {
        checkForUpdates();

        sc.getApi().getEventManager().registerOnJoin(new Consumer<ServerData>() {
            @Override
            public void accept(ServerData serverData) {
                if(updateAvailable) {
                    if(sc.getSettings().isAutoUpdateAddon()) {
                        if(!canDoUpdate()) {
                            sc.displayMessage(ScammerList.PREFIX+"§3Das Addon konnte nicht auf Version §ev"+latestVersion
                                    +" §3aktualisiert werden. Manueller Download: §e"+downloadUrl);
                        }
                    } else {
                        sc.displayMessage(ScammerList.PREFIX+"§3Update auf Version §ev"+latestVersion+" §3verfügbar. Download: §e"+downloadUrl);
                    }
                }
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if(updateAvailable && sc.getSettings().isAutoUpdateAddon()) {
                update();
            }
        }));
    }

    private void checkForUpdates() {
        try {
            BufferedReader reader = Resources.asCharSource(new URL(UPDATE_URL), StandardCharsets.UTF_8).openBufferedStream();
            JsonObject json = new JsonParser().parse(reader).getAsJsonObject();
            if(json.has("tag_name") && json.has("assets")) {
                latestVersion = json.get("tag_name").getAsString().replace("v", "");
                if(!ScammerList.VERSION.equals(latestVersion)) {
                    JsonArray assets = json.get("assets").getAsJsonArray();
                    if(assets.size() > 0)  {
                        JsonObject scammerListAsset = assets.get(0).getAsJsonObject();
                        if(scammerListAsset.has("browser_download_url")) {
                            downloadUrl = scammerListAsset.get("browser_download_url").getAsString();
                            updateAvailable = true;
                        }
                    }
                }
            } else {
                System.out.println("[ScammerList] Could not check for updates: Invalid response.");
            }
        } catch (IOException | IllegalStateException | JsonSyntaxException e) {
            System.out.println("[ScammerList] Could not check for updates: "+e);
        }
    }

    private void update() {
        if(!canDoUpdate()) return;
        addonJar.delete();
        try {
            FileUtils.copyURLToFile(new URL(downloadUrl), addonJar);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean canDoUpdate() {
        return addonJar != null && addonJar.isFile();
    }

    public boolean isUpdateAvailable() {
        return updateAvailable;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public void setAddonJar(File addonJar) {
        this.addonJar = addonJar;
    }
}
