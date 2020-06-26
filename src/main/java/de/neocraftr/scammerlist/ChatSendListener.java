package de.neocraftr.scammerlist;

import net.labymod.api.events.MessageSendEvent;

import java.util.ArrayList;

public class ChatSendListener implements MessageSendEvent {

    private ScammerList sc = ScammerList.getScammerList();

    @Override
    public boolean onSend(String msg) {
        String[] args = msg.split(" ");
        if (args[0].equalsIgnoreCase(sc.getCommandPrefix() + "scammer") || args[0].equalsIgnoreCase(sc.getCommandPrefix() + "sc")) {
            if (args.length >= 2) {
                if (args[1].equalsIgnoreCase("add")) {
                    if (args.length == 3) {
                        new Thread(() -> {
                            String uuid = sc.getUUIDFromName(args[2]);
                            if (uuid != null) {
                                String name = sc.getNamesFromUUID(uuid).get(0);
                                if (!sc.getScammerListUUID().contains(uuid)) {
                                    sc.getScammerListUUID().add(uuid);
                                    sc.getScammerListName().add(name);
                                    sc.getApi().displayMessageInChat(sc.getPrefix() + "§aDer Spieler §e" + name + " (" + uuid + ") §awurde zur Scammer Liste hinzugefügt.");
                                    sc.saveSettings();
                                } else {
                                    sc.getApi().displayMessageInChat(sc.getPrefix() + "§cDer Spieler §e" + name + " §cbefindet sich bereits auf der Scammer Liste.");
                                }
                            } else {
                                sc.getApi().displayMessageInChat(sc.getPrefix() + "§cEs gibt keinen Spieler mit diesem Namen.");
                            }
                        }).start();
                    } else {
                        sc.getApi().displayMessageInChat(sc.getPrefix() + "§cVerwendung: " + sc.getCommandPrefix() + "scammer add <name>");
                    }
                } else if (args[1].equalsIgnoreCase("remove")) {
                    if (args.length == 3) {
                        new Thread(() -> {
                            String uuid = sc.getUUIDFromName(args[2]);
                            if (uuid != null) {
                                String name = sc.getNamesFromUUID(uuid).get(0);
                                if (sc.getScammerListUUID().contains(uuid)) {
                                    sc.getScammerListUUID().remove(uuid);
                                    sc.getScammerListName().remove(name);
                                    sc.getApi().displayMessageInChat(sc.getPrefix() + "§aDer Spieler §e" + name + " (" + uuid + ") §awurde von der Scammer Liste entfernt.");
                                    sc.saveSettings();
                                } else {
                                    sc.getApi().displayMessageInChat(sc.getPrefix() + "§cDer Spieler §e" + name + " §cbefindet sich nicht auf der Scammer Liste.");
                                }
                            } else {
                                sc.getApi().displayMessageInChat(sc.getPrefix() + "§cEs gibt keinen Spieler mit diesem Namen.");
                            }
                        }).start();
                    } else {
                        sc.getApi().displayMessageInChat(sc.getPrefix() + "§cVerwendung: " + sc.getCommandPrefix() + "scammer remove <name>");
                    }
                } else if (args[1].equalsIgnoreCase("check")) {
                    if (args.length == 3) {
                        new Thread(() -> {
                            String uuid = sc.getUUIDFromName(args[2]);
                            if (uuid != null) {
                                ArrayList<String> nameHistory = sc.getNamesFromUUID(uuid);
                                if (sc.getScammerListUUID().contains(uuid)) {
                                    if (nameHistory.size() == 1) {
                                        sc.getApi().displayMessageInChat(sc.getPrefix() + "§cDer Spieler §e" + nameHistory.get(0) + " §cbefindet sich auf der Scammer Liste.");
                                    } else {
                                        sc.getApi().displayMessageInChat(sc.getPrefix() + "§cDer Spieler §e" + nameHistory.get(0) + " [" + nameHistory.get(1) + "] §cbefindet sich auf der Scammer Liste.");
                                    }
                                } else {
                                    sc.getApi().displayMessageInChat(sc.getPrefix() + "§aDer Spieler §e" + nameHistory.get(0) + " §abefindet sich nicht auf der Scammer Liste.");
                                }
                            } else {
                                sc.getApi().displayMessageInChat(sc.getPrefix() + "§cEs gibt keinen Spieler mit diesem Namen.");
                            }
                        }).start();
                    } else {
                        sc.getApi().displayMessageInChat(sc.getPrefix() + "§cVerwendung: " + sc.getCommandPrefix() + "scammer check <name>");
                    }
                } else if (args[1].equalsIgnoreCase("list")) {
                    if (sc.getScammerListName().size() > 0) {
                        StringBuilder text = new StringBuilder();
                        text.append("\n§7-------------------- §eScammer Liste §7--------------------");
                        for (int i = 0; i < sc.getScammerListName().size(); i++) {
                            text.append("\n§8- §c").append(sc.getScammerListName().get(i));
                        }
                        text.append("\n§4Einträge insgesamt: §c"+sc.getScammerListName().size());
                        text.append("\n§7-----------------------------------------------------");
                        sc.getApi().displayMessageInChat(text.toString());
                    } else {
                        sc.getApi().displayMessageInChat(sc.getPrefix() + "§cDie Scammer Liste ist leer. Falls sich Spieler auf der Liste befinden sollten, aktualisiere sie mit §e" + sc.getCommandPrefix() + "scammer update§c.");
                    }
                } else if (args[1].equalsIgnoreCase("update")) {
                    sc.getScammerListName().clear();
                    if (sc.getScammerListUUID().size() > 0) {
                        sc.getApi().displayMessageInChat(sc.getPrefix() + "§aDie Namen der Scammer Liste werden aktualisiert...");
                        new Thread(() -> {
                            for (int i = 0; i < sc.getScammerListUUID().size(); i++) {
                                String name = sc.getNamesFromUUID(sc.getScammerListUUID().get(i)).get(0);
                                if (name == null) continue;
                                sc.getScammerListName().add(name);
                            }
                            sc.saveSettings();
                            sc.getApi().displayMessageInChat(sc.getPrefix() + "§aAktualisieung abgeschlossen.");
                        }).start();
                    } else {
                        sc.getApi().displayMessageInChat(sc.getPrefix() + "§cDie Scammer Liste ist leer.");
                    }
                } else {
                    sc.getApi().displayMessageInChat(sc.getPrefix() + "§cVerwendung: " + sc.getCommandPrefix() + "scammer <add|remove|list|update> [name]");
                }
            } else {
                sc.getApi().displayMessageInChat(sc.getPrefix() + "§cVerwendung: " + sc.getCommandPrefix() + "scammer <add|remove|check|list|update> [name]");
            }
            return true;
        }
        return false;
    }
}