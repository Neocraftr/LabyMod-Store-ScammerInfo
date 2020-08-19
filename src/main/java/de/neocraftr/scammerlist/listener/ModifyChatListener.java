package de.neocraftr.scammerlist.listener;

import de.neocraftr.scammerlist.ScammerList;
import net.labymod.api.events.MessageModifyChatEvent;
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
    private Pattern clanMemberRegex = Pattern.compile("^>> (\\!?\\w{1,16}) \\((Online|Offline)\\)");
    private ChatComponentText privateScammerMessage, onlineScammerMessage;

    public ModifyChatListener() {
        privateScammerMessage = new ChatComponentText("§c§l[§4§l!§c§l] §r");
        privateScammerMessage.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("§4§lScammer §8§l(§e§lPrivat§8§l)")));
        onlineScammerMessage = new ChatComponentText("§c§l[§4§l!§c§l] §r");
        onlineScammerMessage.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("§4§lScammer §8§l(§b§lOnline§8§l)")));
    }

    @Override
    public Object onModifyChatMessage(Object o) {
        IChatComponent msg = (IChatComponent) o;

        if(sc.getSettingsManager().isHighlightInChat()) {
            Matcher m = chatRegex.matcher(msg.getUnformattedText());
            // Chat message
            if(m.find()) {
                modifyChatMessage(msg, m.group(1));
            } else {
                m = msgSendRegex.matcher(msg.getUnformattedText());
                // Msg receive
                if(m.find()) {
                    modifyChatMessage(msg, m.group(1));
                } else {
                    m = msgReceiveRegex.matcher(msg.getUnformattedText());
                    // Msg send
                    if(m.find()) {
                        modifyChatMessage(msg, m.group(1));
                    }
                }
            }
        }

        if(sc.getSettingsManager().isHighlightInClanInfo()) {
            Matcher m = clanMemberRegex.matcher(msg.getUnformattedText());
            if(m.find()) {
                if(sc.getScammerListName().contains(m.group(1))) {
                    msg.getSiblings().add(1, privateScammerMessage);
                } else if(sc.getSettingsManager().isShowOnlineScammer() && sc.getOnlineScammerListName().contains(m.group(1))) {
                    msg.getSiblings().add(1, onlineScammerMessage);
                }
            }
        }

        return o;
    }

    private void modifyChatMessage(IChatComponent msg, String playerName) {
        if(sc.getScammerListName().contains(playerName)) {
            msg.getSiblings().add(0, privateScammerMessage);
        } else if(sc.getSettingsManager().isShowOnlineScammer() && sc.getOnlineScammerListName().contains(playerName)) {
            msg.getSiblings().add(0, onlineScammerMessage);
        }
    }
}
