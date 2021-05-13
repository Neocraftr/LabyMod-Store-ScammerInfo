package de.neocraftr.scammerlist;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.neocraftr.scammerlist.listener.*;
import de.neocraftr.scammerlist.settings.SettingsManager;
import de.neocraftr.scammerlist.utils.*;
import net.labymod.api.LabyModAddon;
import net.labymod.settings.elements.SettingsElement;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ScammerList extends LabyModAddon {

    public static final String PREFIX = "§8[§4Scammerliste§8] §r",
                               PREFIX_LINE = "§7-------------------- §4Scammerliste §7--------------------",
                               COMMAND_PREFIX = ".",
                               VERSION = "1.5.3";
    public static final int PLAYERS_PER_LIST_PAGE = 15,
                            UPDATE_INTERVAL = 604800000; // 1 week

    private static ScammerList scammerList;
    private Gson gson;
    private SettingsManager settings;
    private Helper helper;
    private ListManager listManager;
    private UpdateQueue updateQueue;
    private long nextUpdate = 0;
    private boolean addClan, removeClan, clanInProcess;
    private Set<ClientCommandEvent> commandListeners = new HashSet<>();

    @Override
    public void onEnable() {
        scammerList = this;
        gson = new GsonBuilder().setPrettyPrinting().create();
        settings = new SettingsManager();
        helper = new Helper();
        listManager = new ListManager();
        updateQueue = new UpdateQueue();

        getApi().getEventManager().register(new ChatSendListener());
        getApi().getEventManager().register(new ChatReceiveListener());
        getApi().getEventManager().register(new ModifyChatListener());
        getApi().registerForgeListener(new PreRenderListener());
        getApi().registerForgeListener(new TickListener());
        registerEvent(new CommandListener());
    }

    @Override
    public void loadConfig() {
        settings.loadSettings();
        listManager.loadLists();

        if(getConfig().has("nextUpdate")) {
            nextUpdate = getConfig().get("nextUpdate").getAsLong();
        }

        if(settings.isAutoUpdate()) {
            if(nextUpdate < System.currentTimeMillis()) {
                listManager.updateLists(() -> {
                    nextUpdate = System.currentTimeMillis()+ScammerList.UPDATE_INTERVAL;
                    getConfig().addProperty("nextUpdate", nextUpdate);
                    saveConfig();
                    System.out.println("[ScammerList] Updated player names.");
                });
            }
        }
    }

    @Override
    protected void fillSettings(List<SettingsElement> settings) {
        this.settings.fillSettings(settings);
    }

    public void displayMessage(String msg) {
        getApi().displayMessageInChat(msg);
    }

    public void registerEvent(ClientCommandEvent listener) {
        getCommandListeners().add(listener);
    }

    public static ScammerList getScammerList() {
        return scammerList;
    }

    public Gson getGson() {
        return gson;
    }

    public SettingsManager getSettings() {
        return settings;
    }

    public Helper getHelper() {
        return helper;
    }

    public ListManager getListManager() {
        return listManager;
    }

    public UpdateQueue getUpdateQueue() {
        return updateQueue;
    }

    public boolean isAddClan() {
        return addClan;
    }
    public void setAddClan(boolean addClan) {
        this.addClan = addClan;
    }

    public boolean isRemoveClan() {
        return removeClan;
    }
    public void setRemoveClan(boolean removeClan) {
        this.removeClan = removeClan;
    }

    public boolean isClanInProcess() {
        return clanInProcess;
    }
    public void setClanInProcess(boolean clanInProcess) {
        this.clanInProcess = clanInProcess;
    }

    public long getNextUpdate() {
        return nextUpdate;
    }
    public void setNextUpdate(long nextUpdate) {
        this.nextUpdate = nextUpdate;
    }

    public Set<ClientCommandEvent> getCommandListeners() {
        return commandListeners;
    }
}
