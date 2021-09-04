package de.neocraftr.scammerlist.utils;

public enum PlayerType {
    SCAMMER("§cScammerliste"),
    TRUSTED("§aTrustedliste");

    private String displayName;

    PlayerType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
