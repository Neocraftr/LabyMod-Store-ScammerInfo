package de.neocraftr.scammerlist.settings;

import de.neocraftr.scammerlist.ScammerList;
import net.labymod.settings.elements.*;
import net.labymod.utils.Material;

import java.util.List;

public class SettingsManager {

    private ScammerList sc = ScammerList.getScammerList();

    private boolean highlightInChat = true,
                    highlightInClanInfo = true,
                    highlightInTablist = true,
                    highlightInStartkick = true,
                    autoUpdate = true;
    private String scammerPrefix = "&c&l[&4&l!&c&l]";

    private ButtonElement updateListsBtn;
    private TextElement listUpdateStatus;

    public SettingsManager() {
        updateListsBtn = new ButtonElement("Jetzt aktualisieren", "Start", new ControlElement.IconData("labymod/textures/settings/settings/serverlistliveview.png"), null);
        listUpdateStatus = new TextElement("");
    }

    public void loadSettings() {
        if(sc.getConfig().has("highlightInChat")) {
            setHighlightInChat(sc.getConfig().get("highlightInChat").getAsBoolean());
        }
        if(sc.getConfig().has("highlightInClanInfo")) {
            setHighlightInClanInfo(sc.getConfig().get("highlightInClanInfo").getAsBoolean());
        }
        if(sc.getConfig().has("highlightInTablist")) {
            setHighlightInTablist(sc.getConfig().get("highlightInTablist").getAsBoolean());
        }
        if(sc.getConfig().has("highlightInStartkick")) {
            setHighlightInStartkick(sc.getConfig().get("highlightInStartkick").getAsBoolean());
        }
        if(sc.getConfig().has("autoUpdate")) {
            setAutoUpdate(sc.getConfig().get("autoUpdate").getAsBoolean());
        }
        if(sc.getConfig().has("scammerPrefix")) {
            setScammerPrefix(sc.getConfig().get("scammerPrefix").getAsString());
        }
    }

    public void fillSettings(final List<SettingsElement> settings) {
        settings.add(new HeaderElement("Markierungen"));

        final BooleanElement highlightInChatBtn = new BooleanElement("Im Chat markieren", new ControlElement.IconData("labymod/textures/settings/settings/advanced_chat_settings.png"), value -> {
            setHighlightInChat(value);
            sc.getConfig().addProperty("highlightInChat", value);
            sc.saveConfig();
        }, isHighlightInChat());
        settings.add(highlightInChatBtn);

        final BooleanElement highlightInClanInfoBtn = new BooleanElement("In Clan Info markieren", new ControlElement.IconData("labymod/textures/settings/settings/advanced_chat_settings.png"), value -> {
            setHighlightInClanInfo(value);
            sc.getConfig().addProperty("highlightInClanInfo", value);
            sc.saveConfig();
        }, isHighlightInClanInfo());
        settings.add(highlightInClanInfoBtn);

        final BooleanElement highlightInStartkickBtn = new BooleanElement("Bei Startkicks markieren", new ControlElement.IconData("labymod/textures/settings/settings/advanced_chat_settings.png"), value -> {
            setHighlightInStartkick(value);
            sc.getConfig().addProperty("highlightInStartkick", value);
            sc.saveConfig();
        }, isHighlightInStartkick());
        settings.add(highlightInStartkickBtn);

        final BooleanElement highlightInTablistBtn = new BooleanElement("In Tabliste markieren", new ControlElement.IconData("labymod/textures/settings/settings/oldtablist.png"), value -> {
            setHighlightInTablist(value);
            sc.getConfig().addProperty("highlightInTablist", value);
            sc.saveConfig();
        }, isHighlightInTablist());
        settings.add(highlightInTablistBtn);

        final StringElement scammerPrefixSetting = new StringElement("Scammer Prefix", new ControlElement.IconData(Material.BOOK_AND_QUILL), getScammerPrefix(), value -> {
            setScammerPrefix(value);
            sc.getConfig().addProperty("scammerPrefix", value);
            sc.saveConfig();
        });
        settings.add(scammerPrefixSetting);

        settings.add(new HeaderElement("Listen"));

        final ArraySettingsElement messagesSetting = new ArraySettingsElement("Listen verwalten",
                new ControlElement.IconData(Material.BOOK_AND_QUILL));
        messagesSetting.setDescriptionText("Scammerlisten hinzufügen oder entfernen");
        settings.add(messagesSetting);

        final BooleanElement autoUpdateBtn = new BooleanElement("Automatisch aktualisieren", new ControlElement.IconData("labymod/textures/settings/settings/serverlistliveview.png"), value -> {
            setAutoUpdate(value);
            sc.getConfig().addProperty("autoUpdate", value);
            sc.saveConfig();
        }, isAutoUpdate());
        autoUpdateBtn.setDescriptionText("Listen 1mal wöchentlich automatisch aktualisieren");
        settings.add(autoUpdateBtn);

        updateListsBtn.setClickCallback(() -> {
            if(sc.getUpdateQueue().isUpdating()) {
                sc.getListManager().cancelAllUpdates();
                listUpdateStatus.setText("§cLaufende Aktualisierungen abgebrochen");
            } else {
                sc.getListManager().updateLists(null);
            }
        });
        settings.add(updateListsBtn);

        settings.add(listUpdateStatus);

        settings.add(new TextElement("§7Übersicht aller ingame Befehle: §e.scammer help\n\n§7Installierte Version: §b"+ScammerList.VERSION+"-laby"));
    }

    public boolean isHighlightInChat() {
        return highlightInChat;
    }
    public void setHighlightInChat(boolean highlightInChat) {
        this.highlightInChat = highlightInChat;
    }

    public boolean isHighlightInClanInfo() {
        return highlightInClanInfo;
    }
    public void setHighlightInClanInfo(boolean highlightInClanInfo) {
        this.highlightInClanInfo = highlightInClanInfo;
    }

    public boolean isHighlightInTablist() {
        return highlightInTablist;
    }
    public void setHighlightInTablist(boolean highlightInTablist) {
        this.highlightInTablist = highlightInTablist;
    }


    public boolean isHighlightInStartkick() {
        return highlightInStartkick;
    }
    public void setHighlightInStartkick(boolean highlightInStartkick) {
        this.highlightInStartkick = highlightInStartkick;
    }

    public boolean isAutoUpdate() {
        return autoUpdate;
    }
    public void setAutoUpdate(boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
    }

    public String getScammerPrefix() {
        return scammerPrefix;
    }
    public void setScammerPrefix(String scammerPrefix) {
        this.scammerPrefix = scammerPrefix;
    }

    public TextElement getListUpdateStatus() {
        return listUpdateStatus;
    }

    public ButtonElement getUpdateListsBtn() {
        return updateListsBtn;
    }
}
