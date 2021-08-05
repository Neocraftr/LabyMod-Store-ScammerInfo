package de.neocraftr.scammerlist.chat;

import net.minecraft.util.IChatComponent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrivateMessage extends ChatModule {

    private Pattern msgReceiveRegex = Pattern.compile("\\[.+ \\u2503 (\\!?\\w{1,16}) -> mir\\]");
    private Pattern msgSendRegex = Pattern.compile("\\[mir -> .+ \\u2503 (\\!?\\w{1,16})\\]");

    @Override
    public boolean handleMessage(IChatComponent msg) {
        if(!sc.getSettings().isHighlightInChat()) return false;

        Matcher pmMatcher = msgReceiveRegex.matcher(msg.getUnformattedText());
        if(!pmMatcher.find()) {
            pmMatcher = msgSendRegex.matcher(msg.getUnformattedText());
            if(!pmMatcher.find()) return false;
        }

        if(!sc.getListManager().checkName(pmMatcher.group(1))) return false;

        for(int i=0; i<msg.getSiblings().size(); i++) {
            IChatComponent sibbling = msg.getSiblings().get(i);
            if (sibbling.getFormattedText().contains("§6[§r")) {
                addPrefix(msg, i, pmMatcher.group(1));
                return true;
            }
        }

        // Invalid message, falling back to default
        addPrefix(msg, 0, pmMatcher.group(1));
        return true;
    }
}
