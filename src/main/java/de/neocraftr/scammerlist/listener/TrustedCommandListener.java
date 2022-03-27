package de.neocraftr.scammerlist.listener;

import de.neocraftr.scammerlist.ScammerList;
import de.neocraftr.scammerlist.utils.PlayerList;
import de.neocraftr.scammerlist.utils.PlayerType;
import de.neocraftr.scammerlist.utils.Scammer;
import net.labymod.core.LabyModCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;

import java.text.SimpleDateFormat;
import java.util.*;

public class TrustedCommandListener implements ClientCommandEvent {

    private ScammerList sc = ScammerList.getScammerList();
    private boolean confirmClear;

    @Override
    public boolean onCommand(String cmd, String[] args) {
        if(!cmd.equalsIgnoreCase("trusted") && !cmd.equalsIgnoreCase("tr")) return false;

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
                        String name = sc.getHelper().getNameFromUUID(uuid);
                        if (!sc.getListManager().getPrivateListTrusted().containsUUID(uuid)) {
                            String description = null;
                            if(args.length >= 3) {
                                StringJoiner joiner = new StringJoiner(" ");
                                for(int i=2; i<args.length; i++) {
                                    joiner.add(args[i]);
                                }
                                description = joiner.toString();
                            }
                            sc.getListManager().getPrivateListTrusted().add(new Scammer(uuid, name, description));
                            if(description != null) {
                                sc.displayMessage(ScammerList.PREFIX + "§7Der Spieler §e" + name + " §7wurde wegen §e"+description+" §azu deiner §aTrustedliste §7hinzugefügt.");
                            } else {
                                sc.displayMessage(ScammerList.PREFIX + "§7Der Spieler §e" + name + " §7wurde zu deiner §aTrustedliste §7hinzugefügt.");
                            }
                            sc.getListManager().getPrivateListTrusted().save();
                        } else {
                            sc.displayMessage(ScammerList.PREFIX + "§7Der Spieler §e" + name + " §7befindet sich bereits auf deiner §aTrustedliste§7.");
                        }
                    } else {
                        sc.displayMessage(ScammerList.PREFIX + "§cEs gibt keinen Spieler mit diesem Namen.");
                    }
                }).start();
            } else {
                sc.displayMessage(ScammerList.PREFIX + "§7Verwendung: §e" + ScammerList.COMMAND_PREFIX + cmd + " add <Name> [Grund]");
            }
        } else

        // Add clan
        if(args[0].equalsIgnoreCase("addclan")) {
            if (args.length == 2) {
                if(!sc.isClanInProcess()) {
                    sc.displayMessage(ScammerList.PREFIX+"§7Bitte warten, dies kann etwas dauern...");
                    sc.setClanPlayerType(PlayerType.TRUSTED);
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
                    sc.displayMessage(ScammerList.PREFIX + "§7Es wird bereits ein Clan hinzugefügt oder entfernt. Bitte warten!");
                }
            } else {
                sc.displayMessage(ScammerList.PREFIX + "§7Verwendung: §e" + ScammerList.COMMAND_PREFIX + cmd + " addclan <Name|ClanTag>");
            }
        } else

        // Remove player
        if (args[0].equalsIgnoreCase("remove")) {
            if (args.length == 2) {
                new Thread(() -> {
                    String uuid = sc.getHelper().getUUIDFromName(args[1]);
                    if (uuid != null) {
                        String name = sc.getHelper().getNameFromUUID(uuid);
                        if (sc.getListManager().getPrivateListTrusted().removeByUUID(uuid)) {
                            sc.displayMessage(ScammerList.PREFIX + "§7Der Spieler §e" + name + " §7wurde von deiner §aTrustedliste §7entfernt.");
                            sc.getListManager().getPrivateListTrusted().save();
                        } else {
                            sc.displayMessage(ScammerList.PREFIX + "§7Der Spieler §e" + name + " §7befindet sich nicht auf deiner §aTrustedliste§7.");
                        }
                    } else {
                        sc.displayMessage(ScammerList.PREFIX + "§cEs gibt keinen Spieler mit diesem Namen.");
                    }
                }).start();
            } else {
                sc.displayMessage(ScammerList.PREFIX + "§7Verwendung: §e" + ScammerList.COMMAND_PREFIX + cmd + " remove <Name>");
            }
        } else

        // Remove clan
        if(args[0].equalsIgnoreCase("removeclan")) {
            if (args.length == 2) {
                if(!sc.isClanInProcess()) {
                    sc.displayMessage(ScammerList.PREFIX+"§7Bitte warten, dies kann etwas dauern...");
                    sc.setClanPlayerType(PlayerType.TRUSTED);
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
                    sc.displayMessage(ScammerList.PREFIX + "§7Es wird bereits ein Clan hinzugefügt oder entfernt. Bitte warten!");
                }
            } else {
                sc.displayMessage(ScammerList.PREFIX + "§7Verwendung: §e" + ScammerList.COMMAND_PREFIX + cmd + " removeclan <Name|ClanTag>");
            }
        } else

        // Check player
        if (args[0].equalsIgnoreCase("check")) {
            if (args.length == 2) {
                new Thread(() -> {
                    String uuid = sc.getHelper().getUUIDFromName(args[1]);
                    if (uuid != null) {
                        List<String> nameHistory = sc.getHelper().getNameHistoryFromUUID(uuid);
                        if(sc.getListManager().checkUUID(uuid, PlayerType.TRUSTED)) {
                            StringJoiner joiner = new StringJoiner("\n");

                            joiner.add(ScammerList.PREFIX_LINE);
                            joiner.add("§aDer Spieler befindet sich auf der Trustedliste.");
                            if(nameHistory.size() ==  1) {
                                joiner.add("§7Name: §e"+nameHistory.get(0));
                            } else {
                                joiner.add("§7Name: §e"+nameHistory.get(0)+" ["+nameHistory.get(1)+"]");
                            }
                            joiner.add("§7UUID: §e"+(uuid.equals(nameHistory.get(0)) ? "Nicht verfügbar" : uuid));

                            List<PlayerList> containingLists = sc.getListManager().getContainingLists(uuid, PlayerType.TRUSTED);
                            for(PlayerList list : containingLists) {
                                joiner.add("§7Liste: §e"+list.getMeta().getName());
                                Scammer s = list.getByUUID(uuid);
                                if(s.getDate() != 0) joiner.add("  §7Hinzugefügt am: §e"+new SimpleDateFormat("dd:MM:yyyy HH:mm").format(new Date(s.getDate())));
                                if(s.getOriginalName() != null) joiner.add("  §7Ursprünglicher Name: §e"+s.getOriginalName());
                                if(s.getDescription() != null) joiner.add("  §7Beschreibung: §e"+s.getDescription());
                            }

                            joiner.add(ScammerList.PREFIX_LINE);
                            sc.displayMessage(joiner.toString());
                        } else {
                            boolean scammer = sc.getListManager().checkUUID(uuid, PlayerType.SCAMMER);
                            sc.displayMessage(ScammerList.PREFIX + "§7Der Spieler §e" + nameHistory.get(0) + " §7befindet sich nicht auf der §aTrustedliste"+(scammer ? " §7und wurde als §cScammer §7hinterlegt." : "§7."));
                        }
                    } else {
                        sc.displayMessage(ScammerList.PREFIX + "§cEs gibt keinen Spieler mit diesem Namen.");
                    }
                }).start();
            } else {
                sc.displayMessage(ScammerList.PREFIX + "§7Verwendung: §e" + ScammerList.COMMAND_PREFIX + cmd + " check <Name>");
            }
        } else

       // Check all
       if(args[0].equalsIgnoreCase("checkall")) {
           NetHandlerPlayClient handler = LabyModCore.getMinecraft().getPlayer().sendQueue;
           Collection<NetworkPlayerInfo> players = handler.getPlayerInfoMap();
           List<String> scammers = new ArrayList<>();

           for(NetworkPlayerInfo player : players) {
               String uuid = player.getGameProfile().getId().toString();
               if(player.getGameProfile().getName().startsWith("!"))
                   uuid = player.getGameProfile().getName();

               if(sc.getListManager().checkUUID(uuid, PlayerType.TRUSTED)) {
                   scammers.add(player.getGameProfile().getName());
               }
           }

           if(scammers.size() == 0) {
               sc.displayMessage(ScammerList.PREFIX + "§7Keine Trusted Spieler auf diesem CityBuild...");
           } else {
               sc.displayMessage(ScammerList.PREFIX + "§7Trusted Spieler auf diesem CityBuild: §a" + String.join("§7, §a", scammers));
           }
       } else

        // List scammers
        if (args[0].equalsIgnoreCase("list")) {
            int listSize = sc.getListManager().getPrivateListTrusted().size();
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
                        text.appendText("\n§7Private Trustedliste:");
                        for(int i=from; i<to; i++) {
                            text.appendText("\n§8- §e"+sc.getListManager().getPrivateListTrusted().get(i).getName());
                        }
                        if(page >= numPages - 1) {
                            text.appendText("\n§7Einträge insgesamt: §e"+listSize);
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
                    sc.displayMessage(ScammerList.PREFIX + "§7Verwendung: §e" + ScammerList.COMMAND_PREFIX + cmd + " list [Seite]");
                }
            } else {
                sc.displayMessage(ScammerList.PREFIX + "§7Deine Trustedliste ist leer.");
            }
        } else

        // Update lists
        if (args[0].equalsIgnoreCase("update")) {
            if(!sc.getUpdateQueue().isUpdating()) {
                sc.displayMessage(ScammerList.PREFIX + "§7Die Namen aller §aTrusted- §7und §cScammerlisten §7werden aktualisiert. Dies kann einige Minuten dauern...");
                sc.getListManager().updateLists(() -> {
                    sc.setLastUpdateTime(System.currentTimeMillis());
                    sc.getConfig().addProperty("lastUpdateTime", sc.getLastUpdateTime());
                    sc.saveConfig();
                    sc.displayMessage(ScammerList.PREFIX + "§7Aktualisierung abgeschlossen.");
                });
            } else {
                sc.displayMessage(ScammerList.PREFIX + "§7Es werden bereits Listen aktualisiert. Bite warten!");
            }
        } else

        // Clear trusted list
        if (args[0].equalsIgnoreCase("clear")) {
            if (args.length >= 2 && args[1].equalsIgnoreCase("confirm")) {
                if (confirmClear) {
                    confirmClear = false;
                    sc.getListManager().getPrivateListTrusted().clear();
                    sc.getListManager().getPrivateListTrusted().save();
                    sc.displayMessage(ScammerList.PREFIX + "§7Alle Einträge deiner Trustedliste wurden gelöscht.");
                } else {
                    sc.displayMessage(ScammerList.PREFIX + "§7Bitte gib zuerst §e" + ScammerList.COMMAND_PREFIX + cmd + " clear §7ein.");
                }
            } else {
                if (!confirmClear) {
                    confirmClear = true;
                    sc.displayMessage(ScammerList.PREFIX + "§c§lAchtung: §cUm alle Einträge deiner Trustedliste zu löschen bestätige dies in den nächsten 15 Sekunden mit §e"
                            + ScammerList.COMMAND_PREFIX + cmd + " clear confirm§c.");
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            confirmClear = false;
                        }
                    }, 15000);
                } else {
                    sc.displayMessage(ScammerList.PREFIX + "§7Bitte bestätige das Löschen aller Einträge mit §e"
                            + ScammerList.COMMAND_PREFIX + cmd + " clear confirm§7.");
                }
            }
        } else printHelp();

        return true;
    }

    private void printHelp() {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add(ScammerList.PREFIX_LINE);
        joiner.add("§7Verfügbare Befehle:");
        joiner.add("§e"+ScammerList.COMMAND_PREFIX+"trusted add <Name> [Grund] §8- §7Fügt einen Spieler zur Trustedliste hinzu.");
        joiner.add("§e"+ScammerList.COMMAND_PREFIX+"trusted remove <Name> §8- §7Entfernt einen Spieler von der Trustedliste.");
        joiner.add("§e"+ScammerList.COMMAND_PREFIX+"trusted addclan <Name|ClanTag §8- §7Fügt die Spieler eines Clans zur Trustedliste hinzu.");
        joiner.add("§e"+ScammerList.COMMAND_PREFIX+"trusted removeclan <Name|ClanTag> §8- §7Entfernt die Spieler eines Clans von der Trustedliste.");
        joiner.add("§e"+ScammerList.COMMAND_PREFIX+"trusted check <Name> §8- §7Überprüft ob sich ein Spieler auf der Trustedliste befindet.");
        joiner.add("§e"+ScammerList.COMMAND_PREFIX+"trusted checkall §8- §7Zeigt alle Scammer auf dem CityBuild an.");
        joiner.add("§e"+ScammerList.COMMAND_PREFIX+"trusted clear §8- §7Entfernt alle Spieler von der Trustedliste.");
        joiner.add("§e"+ScammerList.COMMAND_PREFIX+"trusted list §8- §7Zeigt alle Spieler auf der Trustedliste.");
        joiner.add("§e"+ScammerList.COMMAND_PREFIX+"trusted update §8- §7Aktualisiert die Namen der Spieler. (Wird automatisch durchgeführt)");
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
