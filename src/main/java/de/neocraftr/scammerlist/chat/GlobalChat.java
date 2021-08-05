package de.neocraftr.scammerlist.chat;

import net.minecraft.util.IChatComponent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GlobalChat extends ChatModule {

    private Pattern chatRegex = Pattern.compile("[A-Za-z\\-]+\\+? \\u2503 (\\!?\\w{1,16}) \\u00BB");

    @Override
    public boolean handleMessage(IChatComponent msg) {
        if(!sc.getSettings().isHighlightInChat()) return false;

        Matcher chatMatcher = chatRegex.matcher(msg.getUnformattedText());
        if(!chatMatcher.find()) return false;

        if(!sc.getListManager().checkName(chatMatcher.group(1))) return false;

        for(int i=0; i<msg.getSiblings().size(); i++) {
            IChatComponent sibbling = msg.getSiblings().get(i);
            Matcher m = chatRegex.matcher(sibbling.getUnformattedText());
            if (m.find()) {
                int insertIndex = i;
                if (i > 0) {
                    String clanTag = msg.getSiblings().get(i - 1).getFormattedText();
                    if (clanTag.contains("§r§6[") && clanTag.contains("§r§6]"))
                        insertIndex--;
                }
                addPrefix(msg, insertIndex, chatMatcher.group(1));
                return true;
            }
        }

        // Invalid message, falling back to default
        addPrefix(msg, 0, chatMatcher.group(1));
        return true;
    }
}
