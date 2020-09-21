package de.neocraftr.scammerlist.utils;

public class Scammer {

    private String uuid;
    private String name;

    public Scammer(String uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public Scammer(String uuid) {
        this.uuid = uuid;
    }

    public String getUUID() {
        return uuid;
    }
    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
