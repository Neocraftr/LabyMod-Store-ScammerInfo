package de.neocraftr.scammerlist.utils;

import com.google.common.base.CharMatcher;
import com.google.common.io.Resources;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import de.neocraftr.scammerlist.ScammerList;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class Helper {

    private ScammerList sc = ScammerList.getScammerList();

    public List<String> getNamesFromUUID(String uuid) {
        List<String> names = new ArrayList<>();

        if(uuid.startsWith("!")) {
            names.add(uuid);
            return names;
        }

        try {
            uuid = CharMatcher.is('-').removeFrom(uuid);
            try (BufferedReader reader = Resources.asCharSource(new URL(String.format("https://api.mojang.com/user/profiles/%s/names", uuid)), StandardCharsets.UTF_8).openBufferedStream()) {
                JsonReader json = new JsonReader(reader);
                json.beginArray();

                while (json.hasNext()) {
                    json.beginObject();
                    while (json.hasNext()) {
                        String key = json.nextName();
                        switch (key) {
                            case "name":
                                names.add(json.nextString());
                                break;
                            default:
                                json.skipValue();
                                break;
                        }
                    }
                    json.endObject();
                }

                json.endArray();
            }
        } catch(IOException e) {
            System.out.println("[ScammerList] Could not get name from mojang api: "+e.getMessage());
        }
        Collections.reverse(names);
        return names;
    }

    public String getUUIDFromName(String name) {
        if(name.startsWith("!")) {
            return name;
        }

        try {
            BufferedReader reader = Resources.asCharSource(new URL(String.format("https://api.mojang.com/users/profiles/minecraft/%s", name)), StandardCharsets.UTF_8).openBufferedStream();
            JsonObject json = sc.getGson().fromJson(reader, JsonObject.class);
            if(json == null || !json.has("id")) return null;
            String uuid = json.get("id").getAsString();
            return Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})").matcher(uuid).replaceAll("$1-$2-$3-$4-$5");
        } catch(IOException e) {
            System.out.println("[ScammerList] Could not get uuid from mojang api: "+e.getMessage());
        }
        return null;
    }

    public void downloadOnlineScammerList() {
        try {
            List<Scammer> onlineList = new ArrayList<>();
            BufferedReader reader = Resources.asCharSource(new URL(ScammerList.ONLINE_SCAMMER_URL), StandardCharsets.UTF_8).openBufferedStream();
            JsonReader json = new JsonReader(reader);
            json.beginArray();


            while (json.hasNext()) {
                json.beginObject();
                String uuid = null, name = null;
                while (json.hasNext()) {
                    switch (json.nextName()) {
                        case "uuid":
                            uuid = json.nextString();
                            break;
                        case "name":
                            name = json.nextString();
                            break;
                        default:
                            json.nextString();
                    }
                }
                onlineList.add(new Scammer(uuid, name));
                json.endObject();
            }

            json.endArray();

            sc.setOnlineList(onlineList);
        } catch (IOException e) {
            System.out.println("[ScammerList] Could not download online scammer list: "+e.getMessage());
        } catch(IllegalStateException e) {
            System.out.println("[ScammerList] Could not read online scammer list: "+e.getMessage());
        }
    }

    public void updateLists() {
        sc.setUpdatingList(true);
        sc.getNameChangedPlayers().clear();

        // Update online list
        if(sc.getSettings().isShowOnlineScammer()) {
            downloadOnlineScammerList();
            sc.getOnlineList().forEach(scammer -> {
                List<String> names = getNamesFromUUID(scammer.getUUID());
                if(names.size() == 0) return;
                if(!scammer.getName().equals(names.get(0))) {
                    addNameChange(names);
                }
                scammer.setName(names.get(0));
            });
            sc.saveOnlineList();
        }

        // Update private list
        sc.getPrivateList().forEach(scammer -> {
            List<String> names = getNamesFromUUID(scammer.getUUID());
            if(names.size() == 0) return;
            if(!scammer.getName().equals(names.get(0))) {
                addNameChange(names);
            }
            scammer.setName(names.get(0));
        });
        sc.savePrivateList();

        sc.getConfig().add("nameChangedPlayers", sc.getGson().toJsonTree(sc.getNameChangedPlayers()));
        sc.setNextUpdate(System.currentTimeMillis()+ScammerList.UPDATE_INTERVAL);
        sc.getConfig().addProperty("nextUpdate", sc.getNextUpdate());
        sc.saveConfig();
        sc.setUpdatingList(false);
    }

    private void addNameChange(List<String> names) {
        if(names.size() == 1) {
            // normally impossible
            if(!sc.getNameChangedPlayers().contains(names.get(0))) {
                sc.getNameChangedPlayers().add(names.get(0));
            }
        } else {
            if(!sc.getNameChangedPlayers().contains(names.get(1)+" -> "+names.get(0))) {
                sc.getNameChangedPlayers().add(names.get(1)+" -> "+names.get(0));
            }
        }
    }

    public String colorize(String msg) {
        return msg.replace("&", "§");
    }

    public boolean checkUUID(String uuid, List<Scammer> list) {
        for(Scammer scammer : list) {
            if(scammer.getUUID().equals(uuid)) return true;
        }
        return false;
    }

    public boolean checkName(String name, List<Scammer> list) {
        for(Scammer scammer : list) {
            if(scammer.getName().equals(name)) return true;
        }
        return false;
    }

    public Scammer getByUUID(String uuid, List<Scammer> list) {
        for(Scammer scammer : list) {
            if(scammer.getUUID().equals(uuid)) return scammer;
        }
        return null;
    }

    public Scammer getByName(String name, List<Scammer> list) {
        for(Scammer scammer : list) {
            if(scammer.getName().equals(name)) return scammer;
        }
        return null;
    }

    public void removeFromList(String uuid, List<Scammer> list) {
        list.removeIf(scammer -> scammer.getUUID().equals(uuid));
    }
}
