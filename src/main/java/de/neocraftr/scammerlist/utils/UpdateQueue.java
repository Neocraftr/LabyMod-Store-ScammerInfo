package de.neocraftr.scammerlist.utils;

import java.util.ArrayList;
import java.util.List;

public class UpdateQueue {

    private List<Runnable> finishCallbacks = new ArrayList<>();
    private List<PlayerList> pendingLists = new ArrayList<>();
    private PlayerList currentList;

    public void tick() {
        if(currentList == null) {
            if(pendingLists.size() > 0) {
                currentList = pendingLists.get(0);
                currentList.startUpdate();
            }
        } else {
            if(!currentList.isUpdating()) {
                pendingLists.remove(currentList);
                currentList = null;

                if(pendingLists.size() == 0) {
                    while(finishCallbacks.size() > 0) {
                        if(finishCallbacks.get(0) != null) {
                            finishCallbacks.get(0).run();
                        }

                        finishCallbacks.remove(0);
                    }
                }
            }
        }
    }

    public void addList(PlayerList list) {
        if(pendingLists.contains(list)) return;
        pendingLists.add(list);
    }

    public void removeList(PlayerList list) {
        pendingLists.remove(list);
        if(currentList == list) {
            currentList.stopUpdate();
        }
    }

    public void registerFinishCallback(Runnable callback) {
        finishCallbacks.add(callback);
    }

    public boolean isUpdating() {
        return pendingLists.size() > 0;
    }
}
