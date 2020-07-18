package de.neocraftr.scammerlist;

import net.labymod.api.events.MessageSendEvent;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

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
                                    if(uuid.equals(name)) {
                                        sc.getApi().displayMessageInChat(sc.getPrefix() + "§aDer Spieler §e" + name + " (UUID nicht verfügbar) §awurde zu deiner Scammerliste hinzugefügt.");
                                    } else {
                                        sc.getApi().displayMessageInChat(sc.getPrefix() + "§aDer Spieler §e" + name + " (" + uuid + ") §awurde zu deiner Scammerliste hinzugefügt.");
                                    }
                                    sc.saveSettings();
                                } else {
                                    sc.getApi().displayMessageInChat(sc.getPrefix() + "§cDer Spieler §e" + name + " §cbefindet sich bereits auf deiner Scammerliste.");
                                }
                            } else {
                                sc.getApi().displayMessageInChat(sc.getPrefix() + "§cEs gibt keinen Spieler mit diesem Namen.");
                            }
                        }).start();
                    } else {
                        sc.getApi().displayMessageInChat(sc.getPrefix() + "§cVerwendung: " + sc.getCommandPrefix() + "scammer add <Name>");
                    }
                } else if(args[1].equalsIgnoreCase("addclan")) {
                    if (args.length == 3) {
                        sc.setAddClan(true);
                        Minecraft.getMinecraft().thePlayer.sendChatMessage("/clan info "+args[2]);

                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                if(sc.isAddClan()) {
                                    sc.setAddClan(false);
                                    sc.getApi().displayMessageInChat(sc.getPrefix()+"§cBeim hinzufügen des Clans ist ein Fehler aufgetreten.");
                                }
                            }
                        }, 3000);
                    } else {
                        sc.getApi().displayMessageInChat(sc.getPrefix() + "§cVerwendung: " + sc.getCommandPrefix() + "scammer addclan <Name|ClanTag>");
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
                                    if(uuid.equals(name)) {
                                        sc.getApi().displayMessageInChat(sc.getPrefix() + "§aDer Spieler §e" + name + " (UUID nicht verfügbar) §awurde von deiner Scammerliste entfernt.");
                                    } else {
                                        sc.getApi().displayMessageInChat(sc.getPrefix() + "§aDer Spieler §e" + name + " (" + uuid + ") §awurde von deiner Scammerliste entfernt.");
                                    }
                                    sc.saveSettings();
                                } else {
                                    sc.getApi().displayMessageInChat(sc.getPrefix() + "§cDer Spieler §e" + name + " §cbefindet sich nicht auf deiner Scammerliste.");
                                }
                            } else {
                                sc.getApi().displayMessageInChat(sc.getPrefix() + "§cEs gibt keinen Spieler mit diesem Namen.");
                            }
                        }).start();
                    } else {
                        sc.getApi().displayMessageInChat(sc.getPrefix() + "§cVerwendung: " + sc.getCommandPrefix() + "scammer remove <Name>");
                    }
                } else if(args[1].equalsIgnoreCase("removeclan")) {
                    if (args.length == 3) {
                        sc.setRemoveClan(true);
                        Minecraft.getMinecraft().thePlayer.sendChatMessage("/clan info "+args[2]);

                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                if(sc.isRemoveClan()) {
                                    sc.setRemoveClan(false);
                                    sc.getApi().displayMessageInChat(sc.getPrefix()+"§cBeim entfernen des Clans ist ein Fehler aufgetreten.");
                                }
                            }
                        }, 3000);
                    } else {
                        sc.getApi().displayMessageInChat(sc.getPrefix() + "§cVerwendung: " + sc.getCommandPrefix() + "scammer removeclan <Name|ClanTag>");
                    }
                } else if (args[1].equalsIgnoreCase("check")) {
                    if (args.length == 3) {
                        new Thread(() -> {
                            String uuid = sc.getUUIDFromName(args[2]);
                            if (uuid != null) {
                                ArrayList<String> nameHistory = sc.getNamesFromUUID(uuid);
                                if (sc.getScammerListUUID().contains(uuid)) {
                                    if (nameHistory.size() == 1) {
                                        sc.getApi().displayMessageInChat(sc.getPrefix() + "§cDer Spieler §e" + nameHistory.get(0) + " §cbefindet sich auf deiner Scammerliste.");
                                    } else {
                                        sc.getApi().displayMessageInChat(sc.getPrefix() + "§cDer Spieler §e" + nameHistory.get(0) + " [" + nameHistory.get(1) + "] §cbefindet sich auf deiner Scammerliste.");
                                    }
                                } else if(sc.getSettingsManager().isShowOnlineScammer() && sc.getOnlineScammerListUUID().contains(uuid)) {
                                    if (nameHistory.size() == 1) {
                                        sc.getApi().displayMessageInChat(sc.getPrefix() + "§cDer Spieler §e" + nameHistory.get(0) + " §cbefindet sich auf der online Scammerliste.");
                                    } else {
                                        sc.getApi().displayMessageInChat(sc.getPrefix() + "§cDer Spieler §e" + nameHistory.get(0) + " [" + nameHistory.get(1) + "] §cbefindet sich auf der online Scammerliste.");
                                    }
                                } else {
                                    sc.getApi().displayMessageInChat(sc.getPrefix() + "§aDer Spieler §e" + nameHistory.get(0) + " §abefindet sich nicht auf der Scammerliste.");
                                }
                            } else {
                                sc.getApi().displayMessageInChat(sc.getPrefix() + "§cEs gibt keinen Spieler mit diesem Namen.");
                            }
                        }).start();
                    } else {
                        sc.getApi().displayMessageInChat(sc.getPrefix() + "§cVerwendung: " + sc.getCommandPrefix() + "scammer check <Name>");
                    }
                } else if (args[1].equalsIgnoreCase("list")) {
                    if (!sc.getScammerListName().isEmpty()|| (sc.getSettingsManager().isShowOnlineScammer() && !sc.getOnlineScammerListName().isEmpty())) {
                        StringBuilder text = new StringBuilder();
                        int totalScammer = 0;

                        text.append("\n§7-------------------- §eScammerliste §7--------------------");
                        for(int i=0; i<sc.getScammerListName().size(); i++) {
                            text.append("\n§8- §c").append(sc.getScammerListName().get(i)).append(" §8(§ePrivat§8)");
                            totalScammer++;
                        }
                        if(sc.getSettingsManager().isShowOnlineScammer()) {
                            for(int i=0; i<sc.getOnlineScammerListName().size(); i++) {
                                text.append("\n§8- §c").append(sc.getOnlineScammerListName().get(i)).append(" §8(§bOnline§8)");
                                totalScammer++;
                            }
                        }
                        text.append("\n§4Einträge insgesamt: §c"+totalScammer);
                        text.append("\n§7-----------------------------------------------------");
                        sc.getApi().displayMessageInChat(text.toString());
                    } else {
                        sc.getApi().displayMessageInChat(sc.getPrefix() + "§cDeine Scammerliste ist leer.");
                    }
                } else if (args[1].equalsIgnoreCase("update")) {
                    sc.getScammerListName().clear();
                    if (!sc.getScammerListUUID().isEmpty() || sc.getSettingsManager().isShowOnlineScammer()) {
                        sc.getApi().displayMessageInChat(sc.getPrefix() + "§aDie Namen der Scammerlisten werden aktualisiert...");
                        new Thread(() -> {
                            sc.updateLists();
                            sc.getApi().displayMessageInChat(sc.getPrefix() + "§aAktualisieung abgeschlossen.");
                        }).start();
                    } else {
                        sc.getApi().displayMessageInChat(sc.getPrefix() + "§cDeine Scammerliste ist leer.");
                    }
                } else if (args[1].equalsIgnoreCase("clear")) {
                    if(args.length >= 3 && args[2].equalsIgnoreCase("confirm")) {
                        if(sc.isConfirmClear()) {
                            sc.setConfirmClear(false);
                            sc.getScammerListUUID().clear();
                            sc.getScammerListName().clear();
                            sc.saveSettings();
                            sc.getApi().displayMessageInChat(sc.getPrefix()+"§aAlle Einträge deiner Scammerliste wurden gelöscht.");
                        } else {
                            sc.getApi().displayMessageInChat(sc.getPrefix()+"§cBitte gib zuerst §e"+sc.getCommandPrefix()+"scammer clear §cein.");
                        }
                    } else {
                        if(!sc.isConfirmClear()) {
                            sc.setConfirmClear(true);
                            sc.getApi().displayMessageInChat(sc.getPrefix() + "§c§lAchtung: §cUm alle Einträge deiner Scammerliste zu löschen bestätige dies in den nächsten 15 Sekunden mit §e"
                                    +sc.getCommandPrefix()+"scammer clear confirm§c.");
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    sc.setConfirmClear(false);
                                }
                            }, 15000);
                        } else {
                            sc.getApi().displayMessageInChat(sc.getPrefix() + "§cBitte bestätige das Löschen aller Einträge mit §e"
                                    +sc.getCommandPrefix()+"scammer clear confirm§c.");
                        }
                    }
                } else {
                    sc.printHelp();
                }
            } else {
                sc.printHelp();
            }
            return true;
        }
        return false;
    }
}