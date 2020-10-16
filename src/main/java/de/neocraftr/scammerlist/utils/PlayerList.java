package de.neocraftr.scammerlist.utils;

import java.util.ArrayList;

public class PlayerList extends ArrayList<Scammer> {
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
