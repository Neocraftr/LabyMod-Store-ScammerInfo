package de.neocraftr.scammerlist.chat;

import de.neocraftr.scammerlist.ScammerList;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public abstract class ChatModule {

    protected ScammerList sc = ScammerList.getScammerList();

    public abstract boolean handleMessage(IChatComponent msg);

    protected void addPrefix(IChatComponent msg, int insertIndex, String playerName) {
        IChatComponent scammerPrefix = new ChatComponentText(sc.getHelper().colorize(sc.getSettings().getScammerPrefix())+" §r");
        scammerPrefix.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("§4*Weitere Informationen*")));
        scammerPrefix.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ".scammer check "+playerName));
        msg.getSiblings().add(insertIndex, scammerPrefix);
    }
}
