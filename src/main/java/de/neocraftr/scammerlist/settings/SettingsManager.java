package de.neocraftr.scammerlist.settings;

import de.neocraftr.scammerlist.ScammerList;
import net.labymod.settings.elements.*;
import net.labymod.utils.Material;
import net.minecraft.client.Minecraft;

import java.util.List;

public class SettingsManager {

    private ScammerList sc = ScammerList.getScammerList();

    private boolean highlightInChat = true,
                    highlightInClanInfo = true,
                    highlightInTablist = true,
                    highlightInStartkick = true,
                    autoUpdate = true,
                    autoUpdateAddon = true;
    private String scammerPrefix = "&c&l[&4&l!&c&l]";

    private ButtonElement updateListsBtn;
    private TextElement listUpdateStatus;
    private TextElement infoText;

    public SettingsManager() {
        updateListsBtn = new ButtonElement("Jetzt aktualisieren", "Start", new ControlElement.IconData("labymod/textures/settings/settings/serverlistliveview.png"), null);
        listUpdateStatus = new TextElement("");
    }

    public void loadSettings() {
        if(sc.getConfig().has("highlightInChat")) {
            highlightInChat = sc.getConfig().get("highlightInChat").getAsBoolean();
        }
        if(sc.getConfig().has("highlightInClanInfo")) {
            highlightInClanInfo = sc.getConfig().get("highlightInClanInfo").getAsBoolean();
        }
        if(sc.getConfig().has("highlightInTablist")) {
            highlightInTablist = sc.getConfig().get("highlightInTablist").getAsBoolean();
        }
        if(sc.getConfig().has("highlightInStartkick")) {
            highlightInStartkick = sc.getConfig().get("highlightInStartkick").getAsBoolean();
        }
        if(sc.getConfig().has("autoUpdate")) {
            autoUpdate = sc.getConfig().get("autoUpdate").getAsBoolean();
        }
        if(sc.getConfig().has("autoUpdateAddon")) {
            autoUpdateAddon = sc.getConfig().get("autoUpdateAddon").getAsBoolean();
        }
        if(sc.getConfig().has("scammerPrefix")) {
            scammerPrefix = sc.getConfig().get("scammerPrefix").getAsString();
        }
    }

    public void fillSettings(final List<SettingsElement> settings) {
        settings.add(new HeaderElement("Allgemein"));

        final BooleanElement autoUpdateAddonBtn = new BooleanElement("Addon aktualisieren", new ControlElement.IconData("labymod/textures/settings/settings/serverlistliveview.png"), value -> {
            autoUpdateAddon = value;
            updateInfo();
            sc.getConfig().addProperty("autoUpdateAddon", value);
            sc.saveConfig();
        }, autoUpdateAddon);
        autoUpdateAddonBtn.setDescriptionText("Addon beim beenden automatisch aktualisieren");
        settings.add(autoUpdateAddonBtn);

        settings.add(new HeaderElement("Markierungen"));

        final BooleanElement highlightInChatBtn = new BooleanElement("Im Chat markieren", new ControlElement.IconData("labymod/textures/settings/settings/advanced_chat_settings.png"), value -> {
            highlightInChat = value;
            sc.getConfig().addProperty("highlightInChat", value);
            sc.saveConfig();
        }, highlightInChat);
        settings.add(highlightInChatBtn);

        final BooleanElement highlightInClanInfoBtn = new BooleanElement("In Clan Info markieren", new ControlElement.IconData("labymod/textures/settings/settings/advanced_chat_settings.png"), value -> {
            highlightInClanInfo = value;
            sc.getConfig().addProperty("highlightInClanInfo", value);
            sc.saveConfig();
        }, highlightInClanInfo);
        settings.add(highlightInClanInfoBtn);

        final BooleanElement highlightInStartkickBtn = new BooleanElement("Bei Startkicks markieren", new ControlElement.IconData("labymod/textures/settings/settings/advanced_chat_settings.png"), value -> {
            highlightInStartkick = value;
            sc.getConfig().addProperty("highlightInStartkick", value);
            sc.saveConfig();
        }, highlightInStartkick);
        settings.add(highlightInStartkickBtn);

        final BooleanElement highlightInTablistBtn = new BooleanElement("In Tabliste markieren", new ControlElement.IconData("labymod/textures/settings/settings/oldtablist.png"), value -> {
            highlightInTablist = value;
            sc.getConfig().addProperty("highlightInTablist", value);
            sc.saveConfig();
        }, highlightInTablist);
        settings.add(highlightInTablistBtn);

        final StringElement scammerPrefixSetting = new StringElement("Scammer Prefix", new ControlElement.IconData(Material.BOOK_AND_QUILL), scammerPrefix, value -> {
            scammerPrefix = value;
            sc.getConfig().addProperty("scammerPrefix", value);
            sc.saveConfig();
        });
        settings.add(scammerPrefixSetting);

        settings.add(new HeaderElement("Listen"));

        final ButtonElement listManagerBtn = new ButtonElement("Listenmanager", "Öffnen", new ControlElement.IconData(Material.BOOK_AND_QUILL), new Runnable() {
            @Override
            public void run() {
                Minecraft.getMinecraft().displayGuiScreen(new ListManagerGui(Minecraft.getMinecraft().currentScreen));
            }
        });
        listManagerBtn.setDescriptionText("Scammerlisten hinzufügen oder entfernen");
        settings.add(listManagerBtn);

        final BooleanElement autoUpdateBtn = new BooleanElement("Automatisch aktualisieren", new ControlElement.IconData("labymod/textures/settings/settings/serverlistliveview.png"), value -> {
            autoUpdate = value;
            sc.getConfig().addProperty("autoUpdate", value);
            sc.saveConfig();
        }, autoUpdate);
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

        infoText = new TextElement("");
        updateInfo();
        settings.add(infoText);
    }

    private void updateInfo() {
        String text = "§7Version: §a"+ScammerList.VERSION;
        if(sc.getUpdater().isUpdateAvailable()) {
            text += " §c(";
            if(autoUpdateAddon) {
                if(sc.getUpdater().canDoUpdate()) {
                    text += "Wird beim Beenden aktualisiert";
                } else {
                    text += "Automatisches Update nicht möglich";
                }
            } else {
                text += "Neue Version verfügbar";
            }
            text += ")";
        }

        text += "\n§7Ingame Befehle: §a.scammer help";
        text += "\n§7GitHub: §ahttps://github.com/Neocraftr/LabyMod-Scammerliste";
        infoText.setText(text);
    }

    public boolean isHighlightInChat() {
        return highlightInChat;
    }

    public boolean isHighlightInClanInfo() {
        return highlightInClanInfo;
    }

    public boolean isHighlightInTablist() {
        return highlightInTablist;
    }


    public boolean isHighlightInStartkick() {
        return highlightInStartkick;
    }

    public boolean isAutoUpdate() {
        return autoUpdate;
    }

    public boolean isAutoUpdateAddon() {
        return autoUpdateAddon;
    }

    public String getScammerPrefix() {
        return scammerPrefix;
    }

    public TextElement getListUpdateStatus() {
        return listUpdateStatus;
    }

    public ButtonElement getUpdateListsBtn() {
        return updateListsBtn;
    }
}
