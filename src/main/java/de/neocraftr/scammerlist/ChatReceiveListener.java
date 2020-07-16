package de.neocraftr.scammerlist;

import net.labymod.api.events.MessageReceiveEvent;

import java.util.ArrayList;
import java.util.regex.Matcher;

public class ChatReceiveListener implements MessageReceiveEvent {

    private ScammerList sc = ScammerList.getScammerList();

    @Override
    public boolean onReceive(String msgRaw, String msg) {
        if(sc.isAddClan() || sc.isRemoveClan()) {
            if(msg.equals("----------- Clan-Mitglieder -----------")) {
                if(sc.isClanMessage()) {
                    final boolean addClan = sc.isAddClan(), removeClan = sc.isRemoveClan();
                    final ArrayList<String> clanMember = new ArrayList<>(sc.getClanMemberList());
                    final String clanName = sc.getClanName();
                    new Thread(() -> {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        sc.getApi().displayMessageInChat(sc.getPrefix() + "§aBitte warten...");
                        clanMember.forEach(name -> {
                            if(addClan) {
                                String uuid = sc.getUUIDFromName(name);
                                if (uuid != null) {
                                    if (!sc.getScammerListUUID().contains(uuid)) {
                                        sc.getScammerListUUID().add(uuid);
                                        sc.getScammerListName().add(name);
                                    }
                                } else {
                                    sc.getApi().displayMessageInChat(sc.getPrefix() + "§cDer Spieler §e"+name+" §cwurde nicht gefunden.");
                                }
                            }
                            if(removeClan) {
                                String uuid = sc.getUUIDFromName(name);
                                if (uuid != null) {
                                    if (sc.getScammerListUUID().contains(uuid)) {
                                        sc.getScammerListUUID().remove(uuid);
                                        sc.getScammerListName().remove(name);
                                    }
                                } else {
                                    sc.getApi().displayMessageInChat(sc.getPrefix() + "§cDer Spieler §e"+name+" §cwurde nicht gefunden.");
                                }
                            }
                        });

                        sc.saveSettings();
                        if(addClan) sc.getApi().displayMessageInChat(sc.getPrefix() + "§aDie Spieler des Clans §e"+clanName+" §awurden zur Scammerliste hinzugefügt.");
                        if(removeClan) sc.getApi().displayMessageInChat(sc.getPrefix() + "§aDie Spieler des Clans §e"+clanName+" §awurden von der Scammerliste entfernt.");
                    }).start();

                    sc.setClanMessage(false);
                    sc.getClanMemberList().clear();
                    sc.setClanName("");
                    sc.setAddClan(false);
                    sc.setRemoveClan(false);
                } else {
                    sc.setClanMessage(true);
                }
            }

            if(msg.startsWith("[Clans]")) {
                sc.setAddClan(false);
                sc.setRemoveClan(false);
                if(sc.isAddClan()) sc.getApi().displayMessageInChat(sc.getPrefix()+"§cBeim hinzufügen des Clans ist ein Fehler aufgetreten.");
                if(sc.isRemoveClan()) sc.getApi().displayMessageInChat(sc.getPrefix()+"§cBeim entfernen des Clans ist ein Fehler aufgetreten.");
            }

            if(sc.isClanMessage()) {
                if(msg.startsWith("Clan-Name:")) {
                    sc.setClanName(msg.split(":")[1].trim());
                }

                Matcher m = sc.getClanMemberRegex().matcher(msg);
                if(m.matches()) {
                    sc.getClanMemberList().add(m.group(1));
                }
            }
        }
        return false;
    }


}
