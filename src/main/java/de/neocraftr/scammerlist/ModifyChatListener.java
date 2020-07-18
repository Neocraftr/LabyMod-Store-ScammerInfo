package de.neocraftr.scammerlist;

import net.labymod.api.events.MessageModifyChatEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.util.regex.Matcher;

public class ModifyChatListener implements MessageModifyChatEvent {

    private ScammerList sc = ScammerList.getScammerList();
    private ChatComponentText privateScammerMessage, onlineScammerMessage;

    public ModifyChatListener() {
        setPrivateScammerMessage(new ChatComponentText("§c§l[§4§l!§c§l] §r"));
        getPrivateScammerMessage().getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("§4§lScammer §8§l(§e§lPrivat§8§l)")));
        setOnlineScammerMessage(new ChatComponentText("§c§l[§4§l!§c§l] §r"));
        getOnlineScammerMessage().getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("§4§lScammer §8§l(§b§lOnline§8§l)")));
    }

    @Override
    public Object onModifyChatMessage(Object o) {
        IChatComponent msg = (IChatComponent) o;

        if(sc.getSettingsManager().isHighlightInChat()) {
            Matcher m = sc.getChatRegex().matcher(msg.getUnformattedText());
            // Chat message
            if(m.find()) {
                if(sc.getScammerListName().contains(m.group(1))) {
                    msg.getSiblings().add(0, getPrivateScammerMessage());
                } else if(sc.getSettingsManager().isShowOnlineScammer() && sc.getOnlineScammerListName().contains(m.group(1))) {
                    msg.getSiblings().add(0, getOnlineScammerMessage());
                }
            } else {
                m = sc.getMsgRegex().matcher(msg.getUnformattedText());
                // Msg receive
                if(m.find()) {
                    if(sc.getScammerListName().contains(m.group(1))) {
                        msg.getSiblings().add(0, getPrivateScammerMessage());
                    } else if(sc.getSettingsManager().isShowOnlineScammer() && sc.getOnlineScammerListName().contains(m.group(1))) {
                        msg.getSiblings().add(0, getOnlineScammerMessage());
                    }
                } else {
                    m = sc.getMsg2Regex().matcher(msg.getUnformattedText());
                    // Msg send
                    if(m.find()) {
                        if(sc.getScammerListName().contains(m.group(1))) {
                            msg.getSiblings().add(0, getPrivateScammerMessage());
                        } else if(sc.getSettingsManager().isShowOnlineScammer() && sc.getOnlineScammerListName().contains(m.group(1))) {
                            msg.getSiblings().add(0, getOnlineScammerMessage());
                        }
                    }
                }
            }
        }

        return o;
    }

    public ChatComponentText getPrivateScammerMessage() {
        return privateScammerMessage;
    }
    public void setPrivateScammerMessage(ChatComponentText privateScammerMessage) {
        this.privateScammerMessage = privateScammerMessage;
    }

    public ChatComponentText getOnlineScammerMessage() {
        return onlineScammerMessage;
    }
    public void setOnlineScammerMessage(ChatComponentText onlineScammerMessage) {
        this.onlineScammerMessage = onlineScammerMessage;
    }
}
