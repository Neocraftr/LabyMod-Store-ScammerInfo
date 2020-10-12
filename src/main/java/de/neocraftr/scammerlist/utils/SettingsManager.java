package de.neocraftr.scammerlist.utils;

import de.neocraftr.scammerlist.ScammerList;
import net.labymod.settings.elements.BooleanElement;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.settings.elements.StringElement;
import net.labymod.utils.Material;

import java.util.List;

public class SettingsManager {

    private ScammerList sc = ScammerList.getScammerList();

    private boolean showOnlineScammer = true,
                    highlightInChat = true,
                    highlightInClanInfo = true,
                    highlightInTablist = true,
                    highlightInStartkick = true,
                    autoUpdate = true,
                    autoUpdateAddon = true;
    private String scammerPrefix = "&c&l[&4&l!&c&l]";

    public void loadSettings() {
        if(sc.getConfig().has("showOnlineScammer")) {
            setShowOnlineScammer(sc.getConfig().get("showOnlineScammer").getAsBoolean());
        }
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
        if(sc.getConfig().has("autoUpdateAddon")) {
            setAutoUpdate(sc.getConfig().get("autoUpdateAddon").getAsBoolean());
        }
        if(sc.getConfig().has("scammerPrefix")) {
            setScammerPrefix(sc.getConfig().get("scammerPrefix").getAsString());
        }
    }

    public void fillSettings(final List<SettingsElement> settings) {
        final BooleanElement showOnlineScammerBtn = new BooleanElement("[SCAMMER] Radar Integration", new ControlElement.IconData(Material.COMPASS), value -> {
            setShowOnlineScammer(value);
            sc.getConfig().addProperty("showOnlineScammer", value);
            sc.saveConfig();
            if(value && !sc.isUpdatingList()) {
                new Thread(() -> {
                     sc.getHelper().updateLists();
                }).start();
            }
        }, isShowOnlineScammer());
        settings.add(showOnlineScammerBtn);

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

        final BooleanElement autoUpdateBtn = new BooleanElement("Listen automatisch aktualisieren", new ControlElement.IconData("labymod/textures/settings/settings/serverlistliveview.png"), value -> {
            setAutoUpdate(value);
            sc.getConfig().addProperty("autoUpdate", value);
            sc.saveConfig();
        }, isAutoUpdate());
        settings.add(autoUpdateBtn);

        final BooleanElement autoUpdateAddonBtn = new BooleanElement("Addon beim start aktualisieren", new ControlElement.IconData("labymod/textures/settings/settings/serverlistliveview.png"), value -> {
            setAutoUpdateAddon(value);
            sc.getConfig().addProperty("autoUpdateAddon", value);
            sc.saveConfig();
        }, isAutoUpdate());
        settings.add(autoUpdateAddonBtn);

        final StringElement scammerPrefixSetting = new StringElement("Scammer Prefix", new ControlElement.IconData(Material.BOOK_AND_QUILL), getScammerPrefix(), value -> {
            setScammerPrefix(value);
            sc.getConfig().addProperty("scammerPrefix", value);
            sc.saveConfig();
        });
        settings.add(scammerPrefixSetting);
    }

    public boolean isShowOnlineScammer() {
        return showOnlineScammer;
    }
    public void setShowOnlineScammer(boolean showOnlineScammer) {
        this.showOnlineScammer = showOnlineScammer;
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

    public boolean isAutoUpdateAddon() {
        return autoUpdateAddon;
    }
    public void setAutoUpdateAddon(boolean autoUpdateAddon) {
        this.autoUpdateAddon = autoUpdateAddon;
    }

    public String getScammerPrefix() {
        return scammerPrefix;
    }
    public void setScammerPrefix(String scammerPrefix) {
        this.scammerPrefix = scammerPrefix;
    }
}
