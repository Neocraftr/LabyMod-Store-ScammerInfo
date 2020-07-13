package de.neocraftr.scammerlist;

import net.labymod.api.events.MessageModifyChatEvent;
import net.minecraft.util.IChatComponent;

import java.util.regex.Matcher;

public class ModifyChatListener implements MessageModifyChatEvent {

    private ScammerList sc = ScammerList.getScammerList();

    @Override
    public Object onModifyChatMessage(Object o) {
        IChatComponent msg = (IChatComponent) o;

        Matcher m = sc.getChatRegex().matcher(msg.getUnformattedText());
        // Chat message
        if(m.find()) {
            if(sc.getScammerListName().contains(m.group(1))) {
                msg.getSiblings().add(0, sc.getScammerMessage());
            }
        } else {
            m = sc.getMsgRegex().matcher(msg.getUnformattedText());
            // Msg receive
            if(m.find()) {
                if(sc.getScammerListName().contains(m.group(1))) {
                    msg.getSiblings().add(0, sc.getScammerMessage());
                }
            } else {
                m = sc.getMsg2Regex().matcher(msg.getUnformattedText());
                // Msg send
                if(m.find()) {
                    if(sc.getScammerListName().contains(m.group(1))) {
                        msg.getSiblings().add(0, sc.getScammerMessage());
                    }
                }
            }
        }

        return o;
    }
}
