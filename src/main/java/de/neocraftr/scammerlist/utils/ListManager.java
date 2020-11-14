package de.neocraftr.scammerlist.utils;

import com.google.gson.*;
import de.neocraftr.scammerlist.ScammerList;
import net.labymod.addon.AddonLoader;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListManager {
    private List<PlayerList> lists;
    private PlayerList privateList;
    private File listDir;

    private ScammerList sc = ScammerList.getScammerList();

    public void loadLists() {
        lists = new ArrayList<>();

        listDir = new File(AddonLoader.getConfigDirectory(), "ScammerList");
        if(!listDir.exists()) {
            listDir.mkdirs();
        }

        convertOldLists();

        privateList = new PlayerList(true, "Privat", null);
        privateList.load();

        if(!sc.getConfig().has("lists")) {
            lists.add(new PlayerList(true, "[SCAMMER] Radar", "https://coolertyp.scammer-radar.de/onlineScammer.json"));
            saveListSettings();
        }

        for(JsonElement element : sc.getConfig().get("lists").getAsJsonArray()) {
            JsonObject list = element.getAsJsonObject();
            if(list.has("enabled") && list.has("name") && list.has("url")) {
                boolean enabled = list.get("enabled").getAsBoolean();
                String name = list.get("name").getAsString();
                String url = list.get("url").getAsString();
                if(name.equalsIgnoreCase("Privat")) continue;

                PlayerList playerList = new PlayerList(enabled, name, url);
                if(!playerList.load()) continue;
                lists.add(playerList);
            }
        }

        cleanListDir();
    }

    public void updateLists() {
        sc.setUpdatingList(true);
        sc.getNameChangedPlayers().clear();

        privateList.download();
        privateList.load();
        privateList.update();

        for(PlayerList list : lists) {
            list.download();
            list.load();
            list.update();
        }

        sc.getConfig().add("nameChangedPlayers", sc.getGson().toJsonTree(sc.getNameChangedPlayers()));
        sc.saveConfig();

        sc.setUpdatingList(false);
    }

    private void cleanListDir() {
        for(File f : listDir.listFiles()) {
            if(f.isFile() && f.getName().endsWith("-list.json")) {
                String listName = f.getName().replace("-list.json", "");
                if(listName.equals("Privat") || listExists(listName)) continue;
                f.delete();
            }
        }
    }

    public void convertOldLists() {
        try {
            File oldPrivateList = new File(listDir, "PrivateList.json");
            if(oldPrivateList.isFile()) {
                File newPrivateList = new File(listDir, "Privat-list.json");
                if(!newPrivateList.isFile()) {
                    FileUtils.copyFile(oldPrivateList, newPrivateList);
                    System.out.println("[ScammerList] Converted list from old storage format.");
                } else {
                    System.out.println("[ScammerList] Could not convert list from old storage format: There is already a new list.");
                }
                FileUtils.moveFile(oldPrivateList, new File(listDir, "PrivateList.json.old"));
            }

            File oldOnlineList = new File(listDir, "OnlineList.json");
            if(oldOnlineList.isFile()) oldOnlineList.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean checkName(String name) {
        if(privateList.containsName(name)) return true;
        for(PlayerList list : lists) {
            if(!list.isEnabled()) continue;
            if(list.containsName(name)) return true;
        }
        return false;
    }

    public boolean checkUUID(String uuid) {
        if(privateList.containsUUID(uuid)) return true;
        for(PlayerList list : lists) {
            if(!list.isEnabled()) continue;
            if(list.containsUUID(uuid)) return true;
        }
        return false;
    }

    public List<String> getContainingLists(String uuid) {
        List<String> containungLists = new ArrayList<>();
        if(privateList.containsUUID(uuid)) containungLists.add("Privat");
        for(PlayerList list : lists) {
            if(!list.isEnabled()) continue;
            if(list.containsUUID(uuid)) containungLists.add(list.getName());
        }
        return containungLists;
    }

    public void saveListSettings() {
        JsonArray savedLists = new JsonArray();
        for(PlayerList list : lists) {
            JsonObject savedList = new JsonObject();
            savedList.addProperty("name", list.getName());
            savedList.addProperty("enabled", list.isEnabled());
            savedList.addProperty("url", list.getUrl());
            savedLists.add(savedList);
        }
        sc.getConfig().add("lists", savedLists);
        sc.saveConfig();
    }

    public boolean listExists(String name) {
        for(PlayerList list : lists) {
            if(list.getName().equals(name)) return true;
        }
        return false;
    }

    public List<PlayerList> getLists() {
        return lists;
    }

    public PlayerList getPrivateList() {
        return privateList;
    }

    public File getListDir() {
        return listDir;
    }
}
