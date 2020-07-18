package de.neocraftr.scammerlist;

import net.labymod.settings.elements.BooleanElement;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.utils.Material;

import java.util.List;

public class SettingsManager {

    private ScammerList sc = ScammerList.getScammerList();

    private boolean showOnlineScammer = true, highlightInChat = true;

    public void loadSettings() {
        if(sc.getConfig().has("showOnlineScammer")) {
            setShowOnlineScammer(sc.getConfig().get("showOnlineScammer").getAsBoolean());
        }
        if(sc.getConfig().has("highlightInChat")) {
            setHighlightInChat(sc.getConfig().get("highlightInChat").getAsBoolean());
        }
    }

    public void fillSettings(final List<SettingsElement> settings) {
        final BooleanElement showOnlineScammerBtn = new BooleanElement("[SCAMMER] Radar Integration", new ControlElement.IconData(Material.COMPASS), value -> {
            setShowOnlineScammer(value);
            sc.saveSettings();
        }, isShowOnlineScammer());
        settings.add(showOnlineScammerBtn);

        final BooleanElement highlightInChatBtn = new BooleanElement("Im Chat Markieren", new ControlElement.IconData(Material.LEVER), value -> {
            setHighlightInChat(value);
            sc.saveSettings();
        }, isHighlightInChat());
        settings.add(highlightInChatBtn);
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
}
