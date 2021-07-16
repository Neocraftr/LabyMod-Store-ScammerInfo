package de.neocraftr.scammerlist.listener;

import de.neocraftr.scammerlist.chat.*;
import net.labymod.api.events.MessageModifyChatEvent;
import net.minecraft.util.IChatComponent;

import java.util.ArrayList;
import java.util.List;

public class ModifyChatListener implements MessageModifyChatEvent {

    private List<ChatModule> chatModules = new ArrayList<>();

    public ModifyChatListener() {
        chatModules.add(new ClanInfo());
        chatModules.add(new GlobalChat());
        chatModules.add(new PrivateMessage());
        chatModules.add(new StartKick());
    }

    @Override
    public Object onModifyChatMessage(Object o) {
        IChatComponent msg = (IChatComponent) o;

        for(ChatModule module : chatModules) {
            if(module.handleMessage(msg)) break;
        }

        return o;
    }
}
