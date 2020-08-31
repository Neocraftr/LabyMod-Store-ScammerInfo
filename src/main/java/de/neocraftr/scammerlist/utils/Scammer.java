package de.neocraftr.scammerlist.utils;

public class Scammer {

    private String uuid;
    private String name;
    private String previousName;

    public Scammer(String uuid, String name, String previousName) {
        this.uuid = uuid;
        this.name = name;
        this.previousName = previousName;
    }

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

    // not used at the moment
    public String getPreviousName() {
        return previousName;
    }
    public void setPreviousName(String previousName) {
        this.previousName = previousName;
    }
}
