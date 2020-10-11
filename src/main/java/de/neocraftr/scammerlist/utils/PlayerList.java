package de.neocraftr.scammerlist.utils;

import java.util.ArrayList;

public class PlayerList extends ArrayList<Scammer> {
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
}
