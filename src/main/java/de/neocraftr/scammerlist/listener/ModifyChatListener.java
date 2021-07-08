package de.neocraftr.scammerlist.listener;

import de.neocraftr.scammerlist.ScammerList;
import net.labymod.api.events.MessageModifyChatEvent;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModifyChatListener implements MessageModifyChatEvent {

    private ScammerList sc = ScammerList.getScammerList();
    private Pattern chatRegex = Pattern.compile("^(?:\\[[^\\]]+\\] )?[A-Za-z\\-]+\\+? \\u2503 (\\!?\\w{1,16}) \\u00BB");
    private Pattern msgReceiveRegex = Pattern.compile("^\\[[A-Za-z\\-]+\\+? \\u2503 (\\!?\\w{1,16}) -> mir\\]");
    private Pattern msgSendRegex = Pattern.compile("^\\[mir -> [A-Za-z\\-]+\\+? \\u2503 (\\!?\\w{1,16})\\]");
    private Pattern clanMemberRegex = Pattern.compile("^\\u00BB (\\!?\\w{1,16}) \\((online|offline)\\)");
    private Pattern startkickTargetRegex = Pattern.compile("^\\[StartKick\\] Soll der Spieler (\\!?\\w{1,16}) rausgeworfen werden\\? \\/ja \\/nein");
    private Pattern startkickCreatorRegex = Pattern.compile("^\\[StartKick\\] Ersteller: (\\!?\\w{1,16})");

    @Override
    public Object onModifyChatMessage(Object o) {
        IChatComponent msg = (IChatComponent) o;

        if(sc.getSettings().isHighlightInChat()) {
            Matcher m = chatRegex.matcher(msg.getUnformattedText());
            // Chat message
            if(m.find()) {
                checkAndModify(msg, 0, m.group(1));
            } else {
                m = msgSendRegex.matcher(msg.getUnformattedText());
                // Msg receive
                if(m.find()) {
                    checkAndModify(msg, 0, m.group(1));
                } else {
                    m = msgReceiveRegex.matcher(msg.getUnformattedText());
                    // Msg send
                    if(m.find()) {
                        checkAndModify(msg, 0, m.group(1));
                    }
                }
            }
        }

        if(sc.getSettings().isHighlightInClanInfo()) {
            Matcher m = clanMemberRegex.matcher(msg.getUnformattedText());
            if(m.find()) {
                checkAndModify(msg, 1, m.group(1));
            }
        }

        if(sc.getSettings().isHighlightInStartkick()) {
            Matcher m = startkickTargetRegex.matcher(msg.getUnformattedText());
            if(m.find()) {
                checkAndModify(msg, 4, m.group(1));
            }

            m = startkickCreatorRegex.matcher(msg.getUnformattedText());
            if(m.find()) {
                // Divide in two sibblings
                msg.getSiblings().remove(3);
                IChatComponent creator1 = new ChatComponentText("Ersteller: ");
                IChatComponent creator2 = new ChatComponentText(m.group(1));
                msg.getSiblings().add(creator1);
                msg.getSiblings().add(creator2);

                checkAndModify(msg, 4, m.group(1));
            }
        }

        return o;
    }

    private void checkAndModify(IChatComponent msg, int after, String playerName) {
        if(!sc.getListManager().checkName(playerName)) return;
        IChatComponent scammerPrefix = new ChatComponentText(sc.getHelper().colorize(sc.getSettings().getScammerPrefix())+" §r");
        scammerPrefix.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("§4*Weitere Informationen*")));
        scammerPrefix.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ".scammer check "+playerName));
        msg.getSiblings().add(after, scammerPrefix);
    }
}
