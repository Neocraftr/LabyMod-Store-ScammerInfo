package de.neocraftr.scammerlist;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.neocraftr.scammerlist.listener.*;
import de.neocraftr.scammerlist.utils.*;
import net.labymod.addon.AddonLoader;
import net.labymod.api.LabyModAddon;
import net.labymod.settings.elements.SettingsElement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ScammerList extends LabyModAddon {

    public static final String PREFIX = "§8[§4Scammerliste§8] §r",
                               PREFIX_LINE = "§7-------------------- §4Scammerliste §7--------------------",
                               COMMAND_PREFIX = ".",
                               ONLINE_SCAMMER_URL = "https://coolertyp.scammer-radar.de/onlineScammer.json",
                               VERSION = "1.2.0";
    public static final int PLAYERS_PER_LIST_PAGE = 15,
                            UPDATE_INTERVAL = 604800000; // 1 week

    private static ScammerList scammerList;
    private Gson gson;
    private SettingsManager settings;
    private Helper helper;
    private Updater updater;
    private ListManager listManager;
    private long nextUpdate = 0;
    private List<String> nameChangedPlayers = new ArrayList<>();
    private boolean addClan, removeClan, clanInProcess, updatingList;
    private Set<ClientCommandEvent> commandListeners = new HashSet<>();

    @Override
    public void onEnable() {
        scammerList = this;
        gson = new GsonBuilder().setPrettyPrinting().create();
        settings = new SettingsManager();
        helper = new Helper();
        updater = new Updater();
        listManager = new ListManager();

        getApi().getEventManager().register(new ChatSendListener());
        getApi().getEventManager().register(new ChatReceiveListener());
        getApi().getEventManager().register(new ModifyChatListener());
        getApi().registerForgeListener(new PreRenderListener());
        registerEvent(new CommandListener());
    }

    @Override
    public void loadConfig() {
        updater.setAddonJar(AddonLoader.getFiles().get(about.uuid));

        settings.loadSettings();
        listManager.loadLists();

        if(getConfig().has("nameChangedPlayers")) {
            nameChangedPlayers = gson.fromJson(getConfig().get("nameChangedPlayers"), ArrayList.class);
        }
        if(getConfig().has("nextUpdate")) {
            nextUpdate = getConfig().get("nextUpdate").getAsLong();
        }

        if(settings.isAutoUpdate()) {
            if(nextUpdate < System.currentTimeMillis()) {
                new Thread(() -> {
                    listManager.updateLists();
                    System.out.println("[ScammerList] Updated player names.");
                }).start();
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

    public Updater getUpdater() {
        return updater;
    }

    public ListManager getListManager() {
        return listManager;
    }

    public List<String> getNameChangedPlayers() {
        return nameChangedPlayers;
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

    public boolean isUpdatingList() {
        return updatingList;
    }
    public void setUpdatingList(boolean updatingList) {
        this.updatingList = updatingList;
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
