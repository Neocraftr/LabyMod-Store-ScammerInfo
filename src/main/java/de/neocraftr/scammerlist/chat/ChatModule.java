package de.neocraftr.scammerlist.chat;

import de.neocraftr.scammerlist.ScammerList;
import de.neocraftr.scammerlist.utils.PlayerType;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public abstract class ChatModule {

    protected ScammerList sc = ScammerList.getScammerList();

    public abstract boolean handleMessage(IChatComponent msg);

    protected void addPrefix(IChatComponent msg, int insertIndex, String playerName, PlayerType type) {
        switch(type) {
            case SCAMMER:
                IChatComponent scammerPrefix = new ChatComponentText(sc.getHelper().colorize(sc.getSettings().getScammerPrefix())+" §r");
                scammerPrefix.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("§4*Weitere Informationen*")));
                scammerPrefix.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ".scammer check "+playerName));
                msg.getSiblings().add(insertIndex, scammerPrefix);
                break;

            case TRUSTED:
                IChatComponent trustedPrefix = new ChatComponentText(sc.getHelper().colorize(sc.getSettings().getTrustedPrefix())+" §r");
                trustedPrefix.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("§2*Weitere Informationen*")));
                trustedPrefix.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ".trusted check "+playerName));
                msg.getSiblings().add(insertIndex, trustedPrefix);
                break;
        }
    }

    protected PlayerType checkPlayer(String name) {
        if(sc.getListManager().checkName(name, PlayerType.SCAMMER)) return PlayerType.SCAMMER;
        if(sc.getListManager().checkName(name, PlayerType.TRUSTED)) return PlayerType.TRUSTED;
        return null;
    }
}
