package de.neocraftr.scammerlist.chat;

import net.minecraft.util.IChatComponent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClanInfo extends ChatModule {

    private Pattern clanMemberRegex = Pattern.compile("\\u00BB (\\!?\\w{1,16}) \\((online|offline)\\)");

    @Override
    public boolean handleMessage(IChatComponent msg) {
        if(!sc.getSettings().isHighlightInClanInfo()) return false;

        Matcher clanMatcher = clanMemberRegex.matcher(msg.getUnformattedText());
        if(!clanMatcher.find()) return false;

        if(!sc.getListManager().checkName(clanMatcher.group(1))) return false;

        for(int i=0; i<msg.getSiblings().size(); i++) {
            IChatComponent sibbling = msg.getSiblings().get(i);
            if (sibbling.getFormattedText().contains("§8» §r") && msg.getSiblings().size() >= i+1) {
                addPrefix(msg, i+1, clanMatcher.group(1));
                return true;
            }
        }

        // Invalid message, falling back to default
        addPrefix(msg, 0, clanMatcher.group(1));
        return false;
    }
}
