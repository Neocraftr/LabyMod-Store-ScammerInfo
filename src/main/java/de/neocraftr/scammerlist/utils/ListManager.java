package de.neocraftr.scammerlist.utils;

import com.google.gson.*;
import de.neocraftr.scammerlist.ScammerList;
import net.labymod.addon.AddonLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ListManager {
    private List<PlayerList> lists;
    private PlayerList privateListScammer, privateListTrusted;
    private File listDir;

    private ScammerList sc = ScammerList.getScammerList();

    public void loadLists() {
        lists = new ArrayList<>();

        listDir = new File(AddonLoader.getConfigDirectory(), "ScammerList");
        if(!listDir.exists()) {
            listDir.mkdirs();
        }

        privateListScammer = new PlayerList(true, "Private Liste", null, PlayerType.SCAMMER);
        privateListScammer.getMeta().setId("private");
        privateListScammer.load();

        privateListTrusted = new PlayerList(true, "Private Trusted Liste", null, PlayerType.TRUSTED);
        privateListTrusted.getMeta().setId("private-trusted");
        privateListTrusted.load();

        if(!sc.getConfig().has("lists")) {
            lists.add(new PlayerList(true, "[SCAMMER] Radar", "%scammer-radar%", PlayerType.SCAMMER));
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

        sc.getUpdateQueue().addList(privateListScammer);
        sc.getUpdateQueue().addList(privateListTrusted);
        for(PlayerList list : lists) {
            sc.getUpdateQueue().addList(list);
        }
    }

    public void cancelAllUpdates() {
        sc.getUpdateQueue().removeList(privateListScammer);
        sc.getUpdateQueue().removeList(privateListTrusted);
        for(PlayerList list : lists) {
            sc.getUpdateQueue().removeList(list);
        }
    }

    public boolean checkName(String name, PlayerType type) {
        if(type == PlayerType.SCAMMER && privateListScammer.containsName(name)) return true;
        if(type == PlayerType.TRUSTED && privateListTrusted.containsName(name)) return true;
        for(PlayerList list : lists) {
            if(!list.getMeta().isEnabled()) continue;
            if(list.getMeta().getType() != type) continue;
            if(list.containsName(name)) return true;
        }
        return false;
    }

    public boolean checkUUID(String uuid, PlayerType type) {
        if(type == PlayerType.SCAMMER && privateListScammer.containsUUID(uuid)) return true;
        if(type == PlayerType.TRUSTED && privateListTrusted.containsUUID(uuid)) return true;
        for(PlayerList list : lists) {
            if(!list.getMeta().isEnabled()) continue;
            if(list.getMeta().getType() != type) continue;
            if(list.containsUUID(uuid)) return true;
        }
        return false;
    }

    public List<String> getContainingLists(String uuid, PlayerType type) {
        List<String> containungLists = new ArrayList<>();
        if(privateListScammer.containsUUID(uuid) || privateListTrusted.containsUUID(uuid)) containungLists.add("Privat");
        for(PlayerList list : lists) {
            if(!list.getMeta().isEnabled()) continue;
            if(list.getMeta().getType() != type) continue;
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

    public PlayerList createList(boolean enabled, String name, String url, PlayerType type) {
        PlayerList list = new PlayerList(enabled, name, url, type);
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

    public PlayerList getPrivateListScammer() {
        return privateListScammer;
    }

    public PlayerList getPrivateListTrusted() {
        return privateListTrusted;
    }

    public File getListDir() {
        return listDir;
    }
}
