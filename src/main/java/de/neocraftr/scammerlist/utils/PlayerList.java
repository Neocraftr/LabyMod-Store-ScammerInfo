package de.neocraftr.scammerlist.utils;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import de.neocraftr.scammerlist.ScammerList;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PlayerList extends ArrayList<Scammer> {

    private static ScammerList sc = ScammerList.getScammerList();

    private boolean enabled;
    private String name;
    private String url;

    public PlayerList(boolean enabled, String name, String url) {
        this.enabled = enabled;
        this.name = name;
        this.url = url;
    }

    public boolean containsUUID(String uuid) {
        for(Scammer scammer : this) {
            if(scammer.getUUID().equals(uuid)) return true;
        }
        return false;
    }

    public boolean containsName(String name) {
        for(Scammer scammer : this) {
            if(scammer.getName().equals(name)) return true;
        }
        return false;
    }

    public Scammer getByUUID(String uuid) {
        for(Scammer scammer : this) {
            if(scammer.getUUID().equals(uuid)) return scammer;
        }
        return null;
    }

    public Scammer getByName(String name) {
        for(Scammer scammer : this) {
            if(scammer.getName().equals(name)) return scammer;
        }
        return null;
    }

    public boolean removeByUUID(String uuid) {
        return removeIf(scammer -> scammer.getUUID().equals(uuid));
    }

    public boolean removeByName(String name) {
        return removeIf(scammer -> scammer.getName().equals(name));
    }

    public boolean download() {
        if(!enabled) return true;
        if(url == null) return false;
        try {
            FileUtils.copyURLToFile(new URL(url), new File(sc.getListManager().getListDir(), name+"-list.json"));
            return true;
        } catch (IOException e) {
            System.err.println("[ScammerList] Error while downloading list "+name+": "+e.getMessage());
        }
        return false;
    }

    public void update() {
        if(!enabled) return;
        for(Scammer scammer : this) {
            List<String> names = sc.getHelper().getNamesFromUUID(scammer.getUUID());
            if(names.size() == 0) return;
            if(!scammer.getName().equals(names.get(0))) {
                sc.getHelper().addNameChange(names);
            }
            scammer.setName(names.get(0));
        }
        save();
    }

    public void save() {
        try {
            FileWriter writer = new FileWriter(new File(sc.getListManager().getListDir(), name+"-list.json"));
            writer.write(sc.getGson().toJson(this, new TypeToken<List<Scammer>>(){}.getType()));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean load() {
        try {
            File listFile = new File(sc.getListManager().getListDir(), name+"-list.json");
            if(!listFile.isFile()) {
                if(name.equals("Privat")) {
                    save();
                } else if(!download()) {
                    return false;
                }
            }
            FileReader reader = new FileReader(listFile);
            List<Scammer> list = sc.getGson().fromJson(reader, new TypeToken<List<Scammer>>(){}.getType());
            reader.close();

            this.clear();
            this.addAll(list);
            return true;
        } catch (IOException | JsonSyntaxException e) {
            System.err.println("[ScammerList] Error while loading list "+name+": "+e.getMessage());
        }
        return false;
    }

    @Override
    public String toString() {
        return "PlayerList{" +
                "enabled=" + enabled +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
}
