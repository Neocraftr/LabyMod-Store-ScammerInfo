package de.neocraftr.scammerlist.listener;

import de.neocraftr.scammerlist.ScammerList;
import de.neocraftr.scammerlist.utils.Scammer;
import net.labymod.api.events.MessageReceiveEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatReceiveListener implements MessageReceiveEvent {

    private ScammerList sc = ScammerList.getScammerList();
    private Pattern clanMemberRegex = Pattern.compile("^\\u00BB (\\!?\\w{1,16}) \\((online|offline)\\)");
    private List<String> clanMemberList = new ArrayList<>();
    private String clanName;
    private boolean clanMessage;
    private int newPlayers;

    @Override
    public boolean onReceive(String msgRaw, String msg) {
        if(sc.isAddClan() || sc.isRemoveClan()) {
            if(msg.equals("------------[ Clan-Mitglieder ]------------")) {
                if(clanMessage) {
                    boolean addClan = sc.isAddClan(), removeClan = sc.isRemoveClan();
                    new Thread(() -> {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        clanMemberList.forEach(name -> {
                            if(addClan) {
                                String uuid = sc.getHelper().getUUIDFromName(name);
                                if (uuid != null) {
                                    if (!sc.getPrivateList().containsUUID(uuid)) {
                                        sc.getPrivateList().add(new Scammer(uuid, name, "Clan Mitglied - "+clanName));
                                        newPlayers++;
                                    }
                                } else {
                                    sc.displayMessage(ScammerList.PREFIX + "§cDer Spieler §e"+name+" §cwurde nicht gefunden.");
                                }
                            }
                            if(removeClan) {
                                String uuid = sc.getHelper().getUUIDFromName(name);
                                if (uuid != null) {
                                    if (sc.getPrivateList().removeByUUID(uuid)) {
                                        newPlayers++;
                                    }
                                } else {
                                    sc.displayMessage(ScammerList.PREFIX + "§cDer Spieler §e"+name+" §cwurde nicht gefunden.");
                                }
                            }
                        });

                        sc.savePrivateList();
                        if(addClan) sc.displayMessage(ScammerList.PREFIX + "§aEs wurden §e"+newPlayers+" §aSpieler des Clans §e"+clanName+" §azur Scammerliste hinzugefügt.");
                        if(removeClan) sc.displayMessage(ScammerList.PREFIX + "§aEs wurden §e"+newPlayers+" §aSpieler des Clans §e"+clanName+" §avon der Scammerliste entfernt.");

                        clanMessage = false;
                        clanMemberList.clear();
                        clanName = "";
                        newPlayers = 0;
                        sc.setClanInProcess(false);
                    }).start();

                    sc.setAddClan(false);
                    sc.setRemoveClan(false);
                } else {
                    clanMessage = true;
                }
            }

            if(msg.startsWith("[Clan]")) {
                if(sc.isAddClan()) sc.displayMessage(ScammerList.PREFIX+"§cDer Clan konnte nicht hinzugefügt werden!");
                if(sc.isRemoveClan()) sc.displayMessage(ScammerList.PREFIX+"§cDer Clan konnte nicht entfernt werden!");
                sc.setAddClan(false);
                sc.setRemoveClan(false);
                sc.setClanInProcess(false);
            }

            if(clanMessage) {
                if(msg.startsWith("Clan-Name:")) {
                    clanName = msg.split(":")[1].trim();
                }

                Matcher m = clanMemberRegex.matcher(msg);
                if(m.matches()) {
                    clanMemberList.add(m.group(1));
                }
            }
        }
        return false;
    }
}
