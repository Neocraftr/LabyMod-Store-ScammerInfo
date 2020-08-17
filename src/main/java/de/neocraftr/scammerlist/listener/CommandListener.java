package de.neocraftr.scammerlist.listener;

import de.neocraftr.scammerlist.ScammerList;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;

import java.util.ArrayList;
import java.util.StringJoiner;
import java.util.Timer;
import java.util.TimerTask;

public class CommandListener implements ClientCommandEvent {

    private ScammerList sc = ScammerList.getScammerList();
    private boolean confirmClear;

    @Override
    public boolean onCommand(String cmd, String[] args) {
        if(!cmd.equalsIgnoreCase("scammer") && !cmd.equalsIgnoreCase("sc")) return false;
        if(args.length == 0) {
            printHelp();
            return true;
        }

        // Add player
        if (args[0].equalsIgnoreCase("add")) {
            if (args.length == 2) {
                new Thread(() -> {
                    String uuid = sc.getHelper().getUUIDFromName(args[1]);
                    if (uuid != null) {
                        String name = sc.getHelper().getNamesFromUUID(uuid).get(0);
                        if (!sc.getScammerListUUID().contains(uuid)) {
                            sc.getScammerListUUID().add(uuid);
                            sc.getScammerListName().add(name);
                            if(uuid.equals(name)) {
                                sc.getApi().displayMessageInChat(ScammerList.PREFIX + "§aDer Spieler §e" + name + " (UUID nicht verfügbar) §awurde zu deiner Scammerliste hinzugefügt.");
                            } else {
                                sc.getApi().displayMessageInChat(ScammerList.PREFIX + "§aDer Spieler §e" + name + " (" + uuid + ") §awurde zu deiner Scammerliste hinzugefügt.");
                            }
                            sc.saveConfig();
                        } else {
                            sc.getApi().displayMessageInChat(ScammerList.PREFIX + "§cDer Spieler §e" + name + " §cbefindet sich bereits auf deiner Scammerliste.");
                        }
                    } else {
                        sc.getApi().displayMessageInChat(ScammerList.PREFIX + "§cEs gibt keinen Spieler mit diesem Namen.");
                    }
                }).start();
            } else {
                sc.getApi().displayMessageInChat(ScammerList.PREFIX + "§cVerwendung: " + ScammerList.COMMAND_PREFIX + cmd + " add <Name>");
            }
        } else

        // Add clan
        if(args[0].equalsIgnoreCase("addclan")) {
            if (args.length == 2) {
                if(!sc.isClanInProcess()) {
                    sc.getApi().displayMessageInChat(ScammerList.PREFIX+"§aBitte warten...");
                    sc.setAddClan(true);
                    sc.setClanInProcess(true);
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/clan info "+args[1]);

                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if(sc.isAddClan()) {
                                sc.setAddClan(false);
                                sc.setClanInProcess(false);
                                sc.getApi().displayMessageInChat(ScammerList.PREFIX+"§cBeim hinzufügen des Clans ist ein Fehler aufgetreten.");
                            }
                        }
                    }, 3000);
                } else {
                    sc.getApi().displayMessageInChat(ScammerList.PREFIX + "§cEs wird bereits ein Clan hinzugefügt oder entfernt. Bitte warten!");
                }
            } else {
                sc.getApi().displayMessageInChat(ScammerList.PREFIX + "§cVerwendung: " + ScammerList.COMMAND_PREFIX + cmd + " addclan <Name|ClanTag>");
            }
        } else

        // Remove player
        if (args[0].equalsIgnoreCase("remove")) {
            if (args.length == 2) {
                new Thread(() -> {
                    String uuid = sc.getHelper().getUUIDFromName(args[1]);
                    if (uuid != null) {
                        String name = sc.getHelper().getNamesFromUUID(uuid).get(0);
                        if (sc.getScammerListUUID().contains(uuid)) {
                            sc.getScammerListUUID().remove(uuid);
                            sc.getScammerListName().remove(name);
                            if(uuid.equals(name)) {
                                sc.getApi().displayMessageInChat(ScammerList.PREFIX + "§aDer Spieler §e" + name + " (UUID nicht verfügbar) §awurde von deiner Scammerliste entfernt.");
                            } else {
                                sc.getApi().displayMessageInChat(ScammerList.PREFIX + "§aDer Spieler §e" + name + " (" + uuid + ") §awurde von deiner Scammerliste entfernt.");
                            }
                            sc.saveConfig();
                        } else {
                            sc.getApi().displayMessageInChat(ScammerList.PREFIX + "§cDer Spieler §e" + name + " §cbefindet sich nicht auf deiner Scammerliste.");
                        }
                    } else {
                        sc.getApi().displayMessageInChat(ScammerList.PREFIX + "§cEs gibt keinen Spieler mit diesem Namen.");
                    }
                }).start();
            } else {
                sc.getApi().displayMessageInChat(ScammerList.PREFIX + "§cVerwendung: " + ScammerList.COMMAND_PREFIX + cmd + " remove <Name>");
            }
        } else

        // Remove clan
        if(args[0].equalsIgnoreCase("removeclan")) {
            if (args.length == 2) {
                if(!sc.isClanInProcess()) {
                    sc.getApi().displayMessageInChat(ScammerList.PREFIX+"§aBitte warten...");
                    sc.setRemoveClan(true);
                    sc.setClanInProcess(true);
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/clan info "+args[1]);

                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if(sc.isRemoveClan()) {
                                sc.setRemoveClan(false);
                                sc.setClanInProcess(false);
                                sc.getApi().displayMessageInChat(ScammerList.PREFIX+"§cBeim entfernen des Clans ist ein Fehler aufgetreten.");
                            }
                        }
                    }, 3000);
                } else {
                    sc.getApi().displayMessageInChat(ScammerList.PREFIX + "§cEs wird bereits ein Clan hinzugefügt oder entfernt. Bitte warten!");
                }
            } else {
                sc.getApi().displayMessageInChat(ScammerList.PREFIX + "§cVerwendung: " + ScammerList.COMMAND_PREFIX + cmd + " removeclan <Name|ClanTag>");
            }
        } else

        // Check player
        if (args[0].equalsIgnoreCase("check")) {
            if (args.length == 2) {
                new Thread(() -> {
                    String uuid = sc.getHelper().getUUIDFromName(args[1]);
                    if (uuid != null) {
                        ArrayList<String> nameHistory = sc.getHelper().getNamesFromUUID(uuid);
                        if (sc.getScammerListUUID().contains(uuid)) {
                            if (nameHistory.size() == 1) {
                                sc.getApi().displayMessageInChat(ScammerList.PREFIX + "§cDer Spieler §e" + nameHistory.get(0) + " §cbefindet sich auf deiner Scammerliste.");
                            } else {
                                sc.getApi().displayMessageInChat(ScammerList.PREFIX + "§cDer Spieler §e" + nameHistory.get(0) + " [" + nameHistory.get(1) + "] §cbefindet sich auf deiner Scammerliste.");
                            }
                        } else if(sc.getSettingsManager().isShowOnlineScammer() && sc.getOnlineScammerListUUID().contains(uuid)) {
                            if (nameHistory.size() == 1) {
                                sc.getApi().displayMessageInChat(ScammerList.PREFIX + "§cDer Spieler §e" + nameHistory.get(0) + " §cbefindet sich auf der online Scammerliste.");
                            } else {
                                sc.getApi().displayMessageInChat(ScammerList.PREFIX + "§cDer Spieler §e" + nameHistory.get(0) + " [" + nameHistory.get(1) + "] §cbefindet sich auf der online Scammerliste.");
                            }
                        } else {
                            sc.getApi().displayMessageInChat(ScammerList.PREFIX + "§aDer Spieler §e" + nameHistory.get(0) + " §abefindet sich nicht auf der Scammerliste.");
                        }
                    } else {
                        sc.getApi().displayMessageInChat(ScammerList.PREFIX + "§cEs gibt keinen Spieler mit diesem Namen.");
                    }
                }).start();
            } else {
                sc.getApi().displayMessageInChat(ScammerList.PREFIX + "§cVerwendung: " + ScammerList.COMMAND_PREFIX + cmd + " check <Name>");
            }
        } else

        // List scammers
        if (args[0].equalsIgnoreCase("list")) {
            if (!sc.getScammerListName().isEmpty()) {
                try {
                    int page = 0;
                    if(args.length >= 2) page = Integer.parseInt(args[1]) - 1;
                    if(page < 0) throw new NumberFormatException();

                    int numPages = (int) Math.ceil(sc.getScammerListName().size() / (double)ScammerList.PLAYERS_PER_LIST_PAGE);

                    if(page < numPages) {
                        int from = page * ScammerList.PLAYERS_PER_LIST_PAGE;
                        int to = page * ScammerList.PLAYERS_PER_LIST_PAGE + ScammerList.PLAYERS_PER_LIST_PAGE;
                        if(to > sc.getScammerListName().size() - 1)
                            to = (sc.getScammerListName().size() - 1 % ScammerList.PLAYERS_PER_LIST_PAGE) + 1;

                        ChatComponentText text = new ChatComponentText("");
                        text.appendText("\n§7-------------------- §eScammerliste §7--------------------");
                        for(int i=from; i<to; i++) {
                            text.appendText("\n§8- §c"+sc.getScammerListName().get(i));
                        }
                        if(page >= numPages - 1) {
                            text.appendText("\n§4Einträge insgesamt: §c"+sc.getScammerListName().size());
                        }
                        if(page > 0) {
                            ChatComponentText previousPage = new ChatComponentText("\n§a§l§n<<<");
                            previousPage.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ScammerList.COMMAND_PREFIX+cmd+" list "+page));
                            text.appendSibling(previousPage);
                        } else {
                            text.appendText("\n§7§l<<<");
                        }
                        text.appendText(" §8§l[§e§l"+(page + 1)+"§8§l/§e§l"+numPages+"§8§l] ");
                        if(page < numPages - 1) {
                            ChatComponentText nextPage = new ChatComponentText("§a§l§n>>>");
                            nextPage.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,ScammerList.COMMAND_PREFIX+cmd+" list "+(page + 2)));
                            text.appendSibling(nextPage);
                        } else {
                            text.appendText("§7§l>>>");
                        }

                        text.appendText("\n§7-----------------------------------------------------");
                        Minecraft.getMinecraft().thePlayer.addChatComponentMessage(text);
                    } else {
                        sc.getApi().displayMessageInChat(ScammerList.PREFIX + "§cSeite §e"+(page + 1)+" §cexistiert nicht.");
                    }
                } catch(NumberFormatException e) {
                    sc.getApi().displayMessageInChat(ScammerList.PREFIX + "§cVerwendung: " + ScammerList.COMMAND_PREFIX + cmd + " list [Seite]");
                }
            } else {
                sc.getApi().displayMessageInChat(ScammerList.PREFIX + "§cDeine Scammerliste ist leer.");
            }
        } else

        // List name changes
        if (args[0].equalsIgnoreCase("namechanges")) {
            if (!sc.getNameChangedPlayers().isEmpty()) {
                StringJoiner joiner = new StringJoiner("\n");
                joiner.add(ScammerList.PREFIX+"§aLetzte Namensänderungen:");
                sc.getNameChangedPlayers().forEach(name -> {
                    joiner.add("§8- §e"+name);
                });
                sc.getApi().displayMessageInChat(joiner.toString());
            } else {
                sc.getApi().displayMessageInChat(ScammerList.PREFIX + "§cBei der letzten Aktualisierung wurden keine Namensänderungen festgestellt.");
            }
        } else

        // Update lists
        if (args[0].equalsIgnoreCase("update")) {
            if (!sc.getScammerListUUID().isEmpty() || sc.getSettingsManager().isShowOnlineScammer()) {
                if(!sc.isUpdatingList()) {
                    sc.getApi().displayMessageInChat(ScammerList.PREFIX + "§aDie Namen der Scammerlisten werden aktualisiert. Dies kann einige Minuten dauern...");
                    new Thread(() -> {
                        sc.getHelper().updateLists();
                        sc.setNextUpdate(System.currentTimeMillis()+ScammerList.UPDATE_INTERVAL);
                        sc.saveConfig();
                        sc.getApi().displayMessageInChat(ScammerList.PREFIX + "§aAktualisierung abgeschlossen.");
                    }).start();
                } else {
                    sc.getApi().displayMessageInChat(ScammerList.PREFIX + "§cDie Scammerliste wird bereits aktualisiert. Bite warten!");
                }
            } else {
                sc.getApi().displayMessageInChat(ScammerList.PREFIX + "§cDeine Scammerliste ist leer.");
            }
        } else

        // Clear scammer list
        if (args[0].equalsIgnoreCase("clear")) {
            if (args.length >= 2 && args[1].equalsIgnoreCase("confirm")) {
                if (isConfirmClear()) {
                    setConfirmClear(false);
                    sc.getScammerListUUID().clear();
                    sc.getScammerListName().clear();
                    sc.saveConfig();
                    sc.getApi().displayMessageInChat(ScammerList.PREFIX + "§aAlle Einträge deiner Scammerliste wurden gelöscht.");
                } else {
                    sc.getApi().displayMessageInChat(ScammerList.PREFIX + "§cBitte gib zuerst §e" + ScammerList.COMMAND_PREFIX + cmd + " clear §cein.");
                }
            } else {
                if (!isConfirmClear()) {
                    setConfirmClear(true);
                    sc.getApi().displayMessageInChat(ScammerList.PREFIX + "§c§lAchtung: §cUm alle Einträge deiner Scammerliste zu löschen bestätige dies in den nächsten 15 Sekunden mit §e"
                            + ScammerList.COMMAND_PREFIX + cmd + " clear confirm§c.");
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            setConfirmClear(false);
                        }
                    }, 15000);
                } else {
                    sc.getApi().displayMessageInChat(ScammerList.PREFIX + "§cBitte bestätige das Löschen aller Einträge mit §e"
                            + ScammerList.COMMAND_PREFIX + cmd + " clear confirm§c.");
                }
            }
        } else printHelp();

        return true;
    }

    private void printHelp() {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add(ScammerList.PREFIX + "§aVerfügbare Befehle:");
        joiner.add("§e"+ScammerList.COMMAND_PREFIX+"scammer add <Name> §8- §aFügt einen Spieler zur Scammerliste hinzu.");
        joiner.add("§e"+ScammerList.COMMAND_PREFIX+"scammer remove <Name> §8- §aEntfernt einen Spieler von der Scammerliste.");
        joiner.add("§e"+ScammerList.COMMAND_PREFIX+"scammer addclan <Name|ClanTag §8- §aFügt die Spieler eines Clans zur Scammerliste hinzu.");
        joiner.add("§e"+ScammerList.COMMAND_PREFIX+"scammer removeclan <Name|ClanTag> §8- §aEntfernt die Spieler eines Clans von der Scammerliste.");
        joiner.add("§e"+ScammerList.COMMAND_PREFIX+"scammer check <Name> §8- §aÜberprüft ob sich ein Spieler auf der Scammerliste befindet.");
        joiner.add("§e"+ScammerList.COMMAND_PREFIX+"scammer clear §8- §aEntfernt alle Spieler von der Scammerliste.");
        joiner.add("§e"+ScammerList.COMMAND_PREFIX+"scammer list §8- §aZeigt alle Spieler auf der Scammerliste.");
        joiner.add("§e"+ScammerList.COMMAND_PREFIX+"scammer update §8- §aAktualisiert die Namen der Spieler. (Wird automatisch durchgeführt.)");
        joiner.add("§e"+ScammerList.COMMAND_PREFIX+"scammer namechanges §8- §aZeigt die Namensänderungen der letzten Aktualisierung an.)");
        sc.getApi().displayMessageInChat(joiner.toString());
    }

        public boolean isConfirmClear() {
            return confirmClear;
        }
        public void setConfirmClear(boolean confirmClear) {
            this.confirmClear = confirmClear;
        }
}
