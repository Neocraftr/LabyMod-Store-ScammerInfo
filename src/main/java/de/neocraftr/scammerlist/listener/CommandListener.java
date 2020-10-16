package de.neocraftr.scammerlist.listener;

import de.neocraftr.scammerlist.ScammerList;
import de.neocraftr.scammerlist.utils.Scammer;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;

import java.text.SimpleDateFormat;
import java.util.*;

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
            if (args.length >= 2) {
                new Thread(() -> {
                    String uuid = sc.getHelper().getUUIDFromName(args[1]);
                    if (uuid != null) {
                        List<String> names = sc.getHelper().getNamesFromUUID(uuid);
                        if (!sc.getListManager().getPrivateList().containsUUID(uuid)) {
                            String description = null;
                            if(args.length >= 3) {
                                StringJoiner joiner = new StringJoiner(" ");
                                for(int i=2; i<args.length; i++) {
                                    joiner.add(args[i]);
                                }
                                description = joiner.toString();
                            }
                            sc.getListManager().getPrivateList().add(new Scammer(uuid, names.get(0), description));
                            // TODO: Notify if the player is already on an online list
                            if(description != null) {
                                sc.displayMessage(ScammerList.PREFIX + "§aDer Spieler §e" + names.get(0) + " §awurde wegen §e"+description+" §azu deiner Scammerliste hinzugefügt.");
                            } else {
                                sc.displayMessage(ScammerList.PREFIX + "§aDer Spieler §e" + names.get(0) + " §awurde zu deiner Scammerliste hinzugefügt.");
                            }
                            sc.getListManager().savePrivateList();
                        } else {
                            sc.displayMessage(ScammerList.PREFIX + "§cDer Spieler §e" + names.get(0) + " §cbefindet sich bereits auf deiner Scammerliste.");
                        }
                    } else {
                        sc.displayMessage(ScammerList.PREFIX + "§cEs gibt keinen Spieler mit diesem Namen.");
                    }
                }).start();
            } else {
                sc.displayMessage(ScammerList.PREFIX + "§cVerwendung: " + ScammerList.COMMAND_PREFIX + cmd + " add <Name> [Grund]");
            }
        } else

        // Add clan
        if(args[0].equalsIgnoreCase("addclan")) {
            if (args.length == 2) {
                if(!sc.isClanInProcess()) {
                    sc.displayMessage(ScammerList.PREFIX+"§aBitte warten...");
                    sc.setAddClan(true);
                    sc.setClanInProcess(true);
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/clan info "+args[1]);

                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if(sc.isAddClan()) {
                                sc.setAddClan(false);
                                sc.setClanInProcess(false);
                                sc.displayMessage(ScammerList.PREFIX+"§cDer Clan konnte nicht hinzugefügt werden!");
                            }
                        }
                    }, 3000);
                } else {
                    sc.displayMessage(ScammerList.PREFIX + "§cEs wird bereits ein Clan hinzugefügt oder entfernt. Bitte warten!");
                }
            } else {
                sc.displayMessage(ScammerList.PREFIX + "§cVerwendung: " + ScammerList.COMMAND_PREFIX + cmd + " addclan <Name|ClanTag>");
            }
        } else

        // Remove player
        if (args[0].equalsIgnoreCase("remove")) {
            if (args.length == 2) {
                new Thread(() -> {
                    String uuid = sc.getHelper().getUUIDFromName(args[1]);
                    if (uuid != null) {
                        String name = sc.getHelper().getNamesFromUUID(uuid).get(0);
                        if (sc.getListManager().getPrivateList().removeByUUID(uuid)) {
                            sc.displayMessage(ScammerList.PREFIX + "§aDer Spieler §e" + name + " §awurde von deiner Scammerliste entfernt.");
                            sc.getListManager().savePrivateList();
                        } else {
                            sc.displayMessage(ScammerList.PREFIX + "§cDer Spieler §e" + name + " §cbefindet sich nicht auf deiner Scammerliste.");
                        }
                    } else {
                        sc.displayMessage(ScammerList.PREFIX + "§cEs gibt keinen Spieler mit diesem Namen.");
                    }
                }).start();
            } else {
                sc.displayMessage(ScammerList.PREFIX + "§cVerwendung: " + ScammerList.COMMAND_PREFIX + cmd + " remove <Name>");
            }
        } else

        // Remove clan
        if(args[0].equalsIgnoreCase("removeclan")) {
            if (args.length == 2) {
                if(!sc.isClanInProcess()) {
                    sc.displayMessage(ScammerList.PREFIX+"§aBitte warten...");
                    sc.setRemoveClan(true);
                    sc.setClanInProcess(true);
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/clan info "+args[1]);

                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if(sc.isRemoveClan()) {
                                sc.setRemoveClan(false);
                                sc.setClanInProcess(false);
                                sc.displayMessage(ScammerList.PREFIX+"§cDer Clan konnte nicht entfernt werden!");
                            }
                        }
                    }, 3000);
                } else {
                    sc.displayMessage(ScammerList.PREFIX + "§cEs wird bereits ein Clan hinzugefügt oder entfernt. Bitte warten!");
                }
            } else {
                sc.displayMessage(ScammerList.PREFIX + "§cVerwendung: " + ScammerList.COMMAND_PREFIX + cmd + " removeclan <Name|ClanTag>");
            }
        } else

        // Check player
        if (args[0].equalsIgnoreCase("check")) {
            if (args.length == 2) {
                new Thread(() -> {
                    String uuid = sc.getHelper().getUUIDFromName(args[1]);
                    if (uuid != null) {
                        List<String> nameHistory = sc.getHelper().getNamesFromUUID(uuid);
                        if(sc.getListManager().checkUUID(uuid)) {
                            StringJoiner joiner = new StringJoiner("\n");
                            joiner.add(ScammerList.PREFIX_LINE);
                            joiner.add("§cDer Spieler befindet sich auf der Scammerliste.");
                            if(nameHistory.size() ==  1) {
                                joiner.add("§cName: §e"+nameHistory.get(0));
                            } else {
                                joiner.add("§cName: §e"+nameHistory.get(0)+" ["+nameHistory.get(1)+"]");
                            }
                            joiner.add("§cUUID: §e"+(uuid.equals(nameHistory.get(0)) ? "Nicht verfügbar" : uuid));
                            List<String> containingLists = sc.getListManager().getContainungLists(uuid);
                            joiner.add("§cListe: §e"+formatList(containingLists));
                            if(containingLists.contains("Privat")) {
                                Scammer s = sc.getListManager().getPrivateList().getByUUID(uuid);
                                joiner.add("§cHinzugefügt am: §e"
                                        +(s.getDate() == 0 ? "Nicht verfügbar" : new SimpleDateFormat("dd:MM:yyyy HH:mm").format(new Date(s.getDate()))));
                                if(s.getDescription() != null) joiner.add("§cBeschreibung: §e"+s.getDescription());
                            }
                            joiner.add(ScammerList.PREFIX_LINE);
                            sc.displayMessage(joiner.toString());
                        } else {
                            sc.displayMessage(ScammerList.PREFIX + "§aDer Spieler §e" + nameHistory.get(0) + " §abefindet sich nicht auf der Scammerliste.");
                        }
                    } else {
                        sc.displayMessage(ScammerList.PREFIX + "§cEs gibt keinen Spieler mit diesem Namen.");
                    }
                }).start();
            } else {
                sc.displayMessage(ScammerList.PREFIX + "§cVerwendung: " + ScammerList.COMMAND_PREFIX + cmd + " check <Name>");
            }
        } else

        // List scammers
        if (args[0].equalsIgnoreCase("list")) {
            int listSize = sc.getListManager().getPrivateList().size();
            if (listSize != 0) {
                try {
                    int page = 0;
                    if(args.length >= 2) page = Integer.parseInt(args[1]) - 1;
                    if(page < 0) throw new NumberFormatException();

                    int numPages = (int) Math.ceil(listSize / (double)ScammerList.PLAYERS_PER_LIST_PAGE);

                    if(page < numPages) {
                        int from = page * ScammerList.PLAYERS_PER_LIST_PAGE;
                        int to = page * ScammerList.PLAYERS_PER_LIST_PAGE + ScammerList.PLAYERS_PER_LIST_PAGE;
                        if(to > listSize - 1)
                            to = (listSize - 1 % ScammerList.PLAYERS_PER_LIST_PAGE) + 1;

                        ChatComponentText text = new ChatComponentText(ScammerList.PREFIX_LINE);
                        text.appendText("\n§aPrivate Scammerliste:");
                        for(int i=from; i<to; i++) {
                            text.appendText("\n§8- §e"+sc.getListManager().getPrivateList().get(i).getName());
                        }
                        if(page >= numPages - 1) {
                            text.appendText("\n§aEinträge insgesamt: §e"+listSize);
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

                        text.appendText("\n"+ScammerList.PREFIX_LINE);
                        Minecraft.getMinecraft().thePlayer.addChatComponentMessage(text);
                    } else {
                        sc.displayMessage(ScammerList.PREFIX + "§cSeite §e"+(page + 1)+" §cexistiert nicht.");
                    }
                } catch(NumberFormatException e) {
                    sc.displayMessage(ScammerList.PREFIX + "§cVerwendung: " + ScammerList.COMMAND_PREFIX + cmd + " list [Seite]");
                }
            } else {
                sc.displayMessage(ScammerList.PREFIX + "§cDeine Scammerliste ist leer.");
            }
        } else

        // List name changes
        if (args[0].equalsIgnoreCase("namechanges")) {
            if (!sc.getNameChangedPlayers().isEmpty()) {
                StringJoiner joiner = new StringJoiner("\n");
                joiner.add(ScammerList.PREFIX_LINE);
                joiner.add("§aLetzte Namensänderungen:");
                sc.getNameChangedPlayers().forEach(name -> {
                    joiner.add("§8- §e"+name);
                });
                joiner.add(ScammerList.PREFIX_LINE);
                sc.displayMessage(joiner.toString());
            } else {
                sc.displayMessage(ScammerList.PREFIX + "§cBei der letzten Aktualisierung wurden keine Namensänderungen festgestellt.");
            }
        } else

        // Update lists
        if (args[0].equalsIgnoreCase("update")) {
            if(!sc.isUpdatingList()) {
                sc.displayMessage(ScammerList.PREFIX + "§aDie Namen der Scammerlisten werden aktualisiert. Dies kann einige Minuten dauern...");
                new Thread(() -> {
                    sc.getListManager().updateLists();
                    sc.setNextUpdate(System.currentTimeMillis()+ScammerList.UPDATE_INTERVAL);
                    sc.getConfig().addProperty("nextUpdate", sc.getNextUpdate());
                    sc.saveConfig();
                    sc.displayMessage(ScammerList.PREFIX + "§aAktualisierung abgeschlossen.");
                }).start();
            } else {
                sc.displayMessage(ScammerList.PREFIX + "§cDie Scammerliste wird bereits aktualisiert. Bite warten!");
            }
        } else

        // Clear scammer list
        if (args[0].equalsIgnoreCase("clear")) {
            if (args.length >= 2 && args[1].equalsIgnoreCase("confirm")) {
                if (confirmClear) {
                    confirmClear = false;
                    sc.getListManager().getPrivateList().clear();
                    sc.getListManager().savePrivateList();
                    sc.displayMessage(ScammerList.PREFIX + "§aAlle Einträge deiner Scammerliste wurden gelöscht.");
                } else {
                    sc.displayMessage(ScammerList.PREFIX + "§cBitte gib zuerst §e" + ScammerList.COMMAND_PREFIX + cmd + " clear §cein.");
                }
            } else {
                if (!confirmClear) {
                    confirmClear = true;
                    sc.displayMessage(ScammerList.PREFIX + "§c§lAchtung: §cUm alle Einträge deiner Scammerliste zu löschen bestätige dies in den nächsten 15 Sekunden mit §e"
                            + ScammerList.COMMAND_PREFIX + cmd + " clear confirm§c.");
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            confirmClear = false;
                        }
                    }, 15000);
                } else {
                    sc.displayMessage(ScammerList.PREFIX + "§cBitte bestätige das Löschen aller Einträge mit §e"
                            + ScammerList.COMMAND_PREFIX + cmd + " clear confirm§c.");
                }
            }
        } else

        // Version
        if(args[0].equalsIgnoreCase("version")) {
            sc.displayMessage(ScammerList.PREFIX+"§aInstallierte Version: §ev"+ScammerList.VERSION);

            String latestVersion = sc.getUpdater().getLatestVersion();
            if(latestVersion != null) {
                sc.displayMessage(ScammerList.PREFIX+"§aNeuste Version: §ev"+latestVersion);
            }
        } else printHelp();

        return true;
    }

    private void printHelp() {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add(ScammerList.PREFIX_LINE);
        joiner.add("§aVerfügbare Befehle:");
        joiner.add("§e"+ScammerList.COMMAND_PREFIX+"scammer add <Name> [Grund] §8- §aFügt einen Spieler zur Scammerliste hinzu.");
        joiner.add("§e"+ScammerList.COMMAND_PREFIX+"scammer remove <Name> §8- §aEntfernt einen Spieler von der Scammerliste.");
        joiner.add("§e"+ScammerList.COMMAND_PREFIX+"scammer addclan <Name|ClanTag §8- §aFügt die Spieler eines Clans zur Scammerliste hinzu.");
        joiner.add("§e"+ScammerList.COMMAND_PREFIX+"scammer removeclan <Name|ClanTag> §8- §aEntfernt die Spieler eines Clans von der Scammerliste.");
        joiner.add("§e"+ScammerList.COMMAND_PREFIX+"scammer check <Name> §8- §aÜberprüft ob sich ein Spieler auf der Scammerliste befindet.");
        joiner.add("§e"+ScammerList.COMMAND_PREFIX+"scammer clear §8- §aEntfernt alle Spieler von der Scammerliste.");
        joiner.add("§e"+ScammerList.COMMAND_PREFIX+"scammer list §8- §aZeigt alle Spieler auf der Scammerliste.");
        joiner.add("§e"+ScammerList.COMMAND_PREFIX+"scammer update §8- §aAktualisiert die Namen der Spieler. (Wird automatisch durchgeführt)");
        joiner.add("§e"+ScammerList.COMMAND_PREFIX+"scammer namechanges §8- §aZeigt die Namensänderungen der letzten Aktualisierung an.");
        joiner.add("§e"+ScammerList.COMMAND_PREFIX+"scammer version §8- §aZeigt die Version des Addons an.");
        joiner.add(ScammerList.PREFIX_LINE);
        sc.displayMessage(joiner.toString());
    }

    private <T>String formatList(List<T> list) {
        if(list.size() == 1) return list.get(0).toString();
        StringBuilder builder = new StringBuilder();
        for(int i=0; i<list.size(); i++) {
            if(i == 0) {
                builder.append(list.get(i).toString());
            } else if(i == list.size()-1) {
                builder.append(" und "+list.get(i).toString());
            } else {
                builder.append(", "+list.get(i).toString());
            }
        }
        return builder.toString();
    }
}
