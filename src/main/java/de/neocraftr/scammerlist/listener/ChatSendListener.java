package de.neocraftr.scammerlist.listener;

import de.neocraftr.scammerlist.ScammerList;
import net.labymod.api.events.MessageSendEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatSendListener implements MessageSendEvent {

    private ScammerList sc = ScammerList.getScammerList();

    @Override
    public boolean onSend(String msg) {
        List<String> msg_split = new ArrayList<>(Arrays.asList(msg.split(" ")));
        String cmd = msg_split.get(0).replaceFirst(ScammerList.COMMAND_PREFIX, "");
        msg_split.remove(0);
        String[] args = msg_split.toArray(new String[0]);

        boolean handled = false;

        for(ClientCommandEvent listener : sc.getCommandListeners()) {
            if(listener.onCommand(cmd, args)) {
                handled = true;
            }
        }

        return handled;
    }
}