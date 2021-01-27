package de.neocraftr.scammerlist.utils;

import com.google.gson.*;
import de.neocraftr.scammerlist.ScammerList;
import net.labymod.addon.AddonLoader;

import java.io.File;
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

        privateList = new PlayerList(true, "Private Liste", null);
        privateList.getMeta().setId("private");
        privateList.load();


        if(!sc.getConfig().has("lists")) {
            lists.add(new PlayerList(true, "[SCAMMER] Radar", "https://coolertyp.scammer-radar.de/onlineScammer.json"));
            saveListSettings();
        }

        lists.clear();
        for(JsonElement element : sc.getConfig().get("lists").getAsJsonArray()) {
            JsonObject list = element.getAsJsonObject();
            PlayerList playerList = new PlayerList(sc.getGson().fromJson(list, PlayerList.Meta.class));

            playerList.load();
            lists.add(playerList);
        }

        saveListSettings();
    }

    public void updateLists(Runnable callback) {
        if(callback != null) {
            sc.getUpdateQueue().registerFinishCallback(callback);
        }

        sc.getUpdateQueue().addList(privateList);
        for(PlayerList list : lists) {
            sc.getUpdateQueue().addList(list);
        }
    }

    public void cancelAllUpdates() {
        sc.getUpdateQueue().removeList(privateList);
        for(PlayerList list : lists) {
            sc.getUpdateQueue().removeList(list);
        }
    }

    public boolean checkName(String name) {
        if(privateList.containsName(name)) return true;
        for(PlayerList list : lists) {
            if(!list.getMeta().isEnabled()) continue;
            if(list.containsName(name)) return true;
        }
        return false;
    }

    public boolean checkUUID(String uuid) {
        if(privateList.containsUUID(uuid)) return true;
        for(PlayerList list : lists) {
            if(!list.getMeta().isEnabled()) continue;
            if(list.containsUUID(uuid)) return true;
        }
        return false;
    }

    public List<String> getContainingLists(String uuid) {
        List<String> containungLists = new ArrayList<>();
        if(privateList.containsUUID(uuid)) containungLists.add("Privat");
        for(PlayerList list : lists) {
            if(!list.getMeta().isEnabled()) continue;
            if(list.containsUUID(uuid)) containungLists.add(list.getMeta().getName());
        }
        return containungLists;
    }

    public void saveListSettings() {
        JsonArray savedLists = new JsonArray();
        for(PlayerList list : lists) {
            savedLists.add(sc.getGson().toJsonTree(list.getMeta(), PlayerList.Meta.class));
        }
        sc.getConfig().add("lists", savedLists);
        sc.saveConfig();
    }

    public PlayerList createList(boolean enabled, String name, String url) {
        PlayerList list = new PlayerList(enabled, name, url);
        lists.add(list);
        return list;
    }

    public void deleteList(PlayerList list) {
        list.deleteListFile();
        lists.remove(list);
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
