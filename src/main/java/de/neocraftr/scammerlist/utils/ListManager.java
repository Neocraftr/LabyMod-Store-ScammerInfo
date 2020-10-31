package de.neocraftr.scammerlist.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import de.neocraftr.scammerlist.ScammerList;
import net.labymod.addon.AddonLoader;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ListManager {
    private List<PlayerList> lists;
    private PlayerList privateList;
    private File listDir;

    private Gson gson = new Gson();
    private ScammerList sc = ScammerList.getScammerList();

    public void loadLists() {
        lists = new ArrayList<>();

        listDir = new File(AddonLoader.getConfigDirectory(), "ScammerList");
        if(!listDir.exists()) {
            listDir.mkdirs();
        }

        privateList = new PlayerList(true, "Privat", null);
        readList(privateList);

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
                if(!readList(playerList)) continue;
                lists.add(playerList);
            }
        }

        cleanListDir();
    }

    public void updateLists() {
        sc.setUpdatingList(true);
        sc.getNameChangedPlayers().clear();

        downloadList(privateList);
        readList(privateList);
        updateList(privateList);

        for(PlayerList list : lists) {
            downloadList(list);
            readList(list);
            updateList(list);
        }

        sc.setUpdatingList(false);
    }

    public void updateList(PlayerList playerList) {
        if(!playerList.isEnabled()) return;
        for(Scammer scammer : playerList) {
            List<String> names = sc.getHelper().getNamesFromUUID(scammer.getUUID());
            if(names.size() == 0) return;
            if(!scammer.getName().equals(names.get(0))) {
                sc.getHelper().addNameChange(names);
            }
            scammer.setName(names.get(0));
        }
        saveList(playerList);
    }

    public boolean downloadList(PlayerList playerList) {
        if(!playerList.isEnabled()) return true;
        if(playerList.getUrl() == null) return false;
        try {
            FileUtils.copyURLToFile(new URL(playerList.getUrl()), new File(listDir, playerList.getName()+"-list.json"));
            return true;
        } catch (IOException e) {
            System.err.println("[ScammerList] Error while downloading list "+playerList.getName()+": "+e.getMessage());
        }
        return false;
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

    public boolean readList(PlayerList playerList) {
        try {
            File listFile = new File(listDir, playerList.getName()+"-list.json");
            if(!listFile.isFile()) {
                if(playerList.getName().equals("Privat")) {
                    saveList(playerList);
                } else if(!downloadList(playerList)) {
                    return false;
                }
            }
            FileReader reader = new FileReader(listFile);
            List<Scammer> list = gson.fromJson(reader, new TypeToken<List<Scammer>>(){}.getType());
            reader.close();

            playerList.clear();
            playerList.addAll(list);
            return true;
        } catch (IOException | JsonSyntaxException e) {
            System.err.println("[ScammerList] Error while loading list "+playerList.getName()+": "+e.getMessage());
        }
        return false;
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

    public void savePrivateList() {
        saveList(privateList);
    }

    private void saveList(PlayerList playerList) {
        try {
            FileWriter writer = new FileWriter(new File(listDir, playerList.getName()+"-list.json"));
            writer.write(gson.toJson(playerList));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
}
