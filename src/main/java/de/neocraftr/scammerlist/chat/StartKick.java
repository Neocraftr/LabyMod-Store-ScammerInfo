package de.neocraftr.scammerlist.chat;

import de.neocraftr.scammerlist.utils.PlayerType;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StartKick extends ChatModule {

    private Pattern startkickTargetRegex = Pattern.compile("\\[StartKick\\] Soll der Spieler (\\!?\\w{1,16}) rausgeworfen werden\\? \\/ja \\/nein");
    private Pattern startkickCreatorRegex = Pattern.compile("\\[StartKick\\] Ersteller: (\\!?\\w{1,16})");

    @Override
    public boolean handleMessage(IChatComponent msg) {
        if(!sc.getSettings().isHighlightInStartkick()) return false;

        boolean creator = false;
        Matcher startkickMatcher = startkickCreatorRegex.matcher(msg.getUnformattedText());
        if(startkickMatcher.find()) {
            creator = true;
        } else {
            startkickMatcher = startkickTargetRegex.matcher(msg.getUnformattedText());
            if(!startkickMatcher.find()) return false;
        }

        PlayerType playerType = checkPlayer(startkickMatcher.group(1));
        if(playerType == null) return false;

        for(int i=0; i<msg.getSiblings().size(); i++) {
            if(creator) {
                String creatorName = msg.getSiblings().get(i).getUnformattedText();
                if(creatorName.startsWith("Ersteller:") && msg.getSiblings().size() >= i+1) {
                    msg.getSiblings().remove(i);
                    msg.getSiblings().add(i, new ChatComponentText("§fErsteller: "));
                    msg.getSiblings().add(i+1, new ChatComponentText(startkickMatcher.group(1)));
                    addPrefix(msg, i+1, startkickMatcher.group(1), playerType);
                    return true;
                }
            } else {
                if (msg.getSiblings().get(i).getUnformattedText().contains("Soll der Spieler") && msg.getSiblings().size() >= i+1) {
                    addPrefix(msg, i+1, startkickMatcher.group(1), playerType);
                    return true;
                }
            }
        }

        addPrefix(msg, 0, startkickMatcher.group(1), playerType);
        return false;
    }
}
