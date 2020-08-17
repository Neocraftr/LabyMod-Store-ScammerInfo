package de.neocraftr.scammerlist.utils;

import net.labymod.settings.elements.BooleanElement;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.utils.Material;

import java.util.List;

public class SettingsManager {

    private ScammerList sc = ScammerList.getScammerList();

    private boolean showOnlineScammer = true,
                    highlightInChat = true,
                    highlightInTablist = true,
                    autoUpdate = true;

    public void loadSettings() {
        if(sc.getConfig().has("showOnlineScammer")) {
            setShowOnlineScammer(sc.getConfig().get("showOnlineScammer").getAsBoolean());
        }
        if(sc.getConfig().has("highlightInChat")) {
            setHighlightInChat(sc.getConfig().get("highlightInChat").getAsBoolean());
        }
        if(sc.getConfig().has("highlightInTablist")) {
            setHighlightInTablist(sc.getConfig().get("highlightInTablist").getAsBoolean());
        }
        if(sc.getConfig().has("autoUpdate")) {
            setAutoUpdate(sc.getConfig().get("autoUpdate").getAsBoolean());
        }
    }

    public void fillSettings(final List<SettingsElement> settings) {
        final BooleanElement showOnlineScammerBtn = new BooleanElement("[SCAMMER] Radar Integration", new ControlElement.IconData(Material.COMPASS), value -> {
            setShowOnlineScammer(value);
            sc.saveConfig();
        }, isShowOnlineScammer());
        settings.add(showOnlineScammerBtn);

        final BooleanElement highlightInChatBtn = new BooleanElement("Im Chat markieren", new ControlElement.IconData("labymod/textures/settings/settings/advanced_chat_settings.png"), value -> {
            setHighlightInChat(value);
            sc.saveConfig();
        }, isHighlightInChat());
        settings.add(highlightInChatBtn);

        final BooleanElement highlightInTablistBtn = new BooleanElement("In Tabliste markieren", new ControlElement.IconData("labymod/textures/settings/settings/oldtablist.png"), value -> {
            setHighlightInTablist(value);
            sc.saveConfig();
        }, isHighlightInTablist());
        settings.add(highlightInTablistBtn);

        final BooleanElement autoUpdateBtn = new BooleanElement("Automatisch aktualisieren", new ControlElement.IconData("labymod/textures/settings/settings/serverlistliveview.png"), value -> {
            setAutoUpdate(value);
            sc.saveConfig();
        }, isAutoUpdate());
        settings.add(autoUpdateBtn);
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

    public boolean isHighlightInTablist() {
        return highlightInTablist;
    }
    public void setHighlightInTablist(boolean highlightInTablist) {
        this.highlightInTablist = highlightInTablist;
    }

    public boolean isAutoUpdate() {
        return autoUpdate;
    }
    public void setAutoUpdate(boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
    }
}
