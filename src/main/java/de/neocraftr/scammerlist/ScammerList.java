package de.neocraftr.scammerlist;

import com.google.gson.Gson;
import de.neocraftr.scammerlist.listener.*;
import de.neocraftr.scammerlist.utils.Helper;
import de.neocraftr.scammerlist.utils.SettingsManager;
import net.labymod.api.LabyModAddon;
import net.labymod.settings.elements.SettingsElement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ScammerList extends LabyModAddon {

    public static final String PREFIX = "§8[§4Scammerliste§8] §r",
                               COMMAND_PREFIX = ".",
                               ONLINE_SCAMMER_URL = "https://coolertyp.scammer-radar.de/onlineScammer.json";
    public static final int PLAYERS_PER_LIST_PAGE = 15,
                            UPDATE_INTERVAL = 604800000; // 1 week

    private static ScammerList scammerList;
    private Gson gson;
    private SettingsManager settingsManager;
    private Helper helper;
    private long nextUpdate = 0;
    private ArrayList<String> scammerListName = new ArrayList<>();
    private ArrayList<String> scammerListUUID = new ArrayList<>();
    private ArrayList<String> onlineScammerListName = new ArrayList<>();
    private ArrayList<String> onlineScammerListUUID = new ArrayList<>();
    private ArrayList<String> nameChangedPlayers = new ArrayList<>();
    private boolean addClan, removeClan, clanInProcess, updatingList;
    private Set<ClientCommandEvent> commandListeners = new HashSet<>();

    @Override
    public void onEnable() {
        setScammerList(this);
        setGson(new Gson());
        setSettingsManager(new SettingsManager());
        setHelper(new Helper());

        getApi().getEventManager().register(new ChatSendListener());
        getApi().getEventManager().register(new ChatReceiveListener());
        getApi().getEventManager().register(new ModifyChatListener());
        getApi().registerForgeListener(new PreRenderListener());
        registerEvent(new CommandListener());
    }

    @Override
    public void loadConfig() {
        getSettingsManager().loadSettings();

        if(getConfig().has("scammerListName")) {
            setScammerListName(getGson().fromJson(getConfig().get("scammerListName"), ArrayList.class));
        }
        if(getConfig().has("scammerListUUID")) {
            setScammerListUUID(getGson().fromJson(getConfig().get("scammerListUUID"), ArrayList.class));
        }
        if(getConfig().has("onlineScammerListName")) {
            setOnlineScammerListName(getGson().fromJson(getConfig().get("onlineScammerListName"), ArrayList.class));
        }
        if(getConfig().has("onlineScammerListUUID")) {
            setOnlineScammerListUUID(getGson().fromJson(getConfig().get("onlineScammerListUUID"), ArrayList.class));
        }
        if(getConfig().has("nameChangedPlayers")) {
            setNameChangedPlayers(getGson().fromJson(getConfig().get("nameChangedPlayers"), ArrayList.class));
        }
        if(getConfig().has("nextUpdate")) {
            setNextUpdate(getConfig().get("nextUpdate").getAsLong());
        }

        if(getSettingsManager().isAutoUpdate()) {
            if(getNextUpdate() < System.currentTimeMillis()) {
                setNextUpdate(System.currentTimeMillis()+UPDATE_INTERVAL);

                new Thread(() -> {
                    getHelper().updateLists();
                    System.out.println("[ScammerList] Updated player names.");
                }).start();
            }
        }
    }

    @Override
    protected void fillSettings(List<SettingsElement> settings) {
        getSettingsManager().fillSettings(settings);
    }

    public void saveConfig() {
        getConfig().add("scammerListName", getGson().toJsonTree(getScammerListName()));
        getConfig().add("scammerListUUID", getGson().toJsonTree(getScammerListUUID()));
        getConfig().add("onlineScammerListName", getGson().toJsonTree(getOnlineScammerListName()));
        getConfig().add("onlineScammerListUUID", getGson().toJsonTree(getOnlineScammerListUUID()));
        getConfig().add("nameChangedPlayers", getGson().toJsonTree(getNameChangedPlayers()));
        getConfig().addProperty("nextUpdate", getNextUpdate());
        getConfig().addProperty("showOnlineScammer", getSettingsManager().isShowOnlineScammer());
        getConfig().addProperty("highlightInChat", getSettingsManager().isHighlightInChat());
        getConfig().addProperty("highlightInTablist", getSettingsManager().isHighlightInTablist());
        getConfig().addProperty("autoUpdate", getSettingsManager().isAutoUpdate());
        super.saveConfig();
    }

    public void registerEvent(ClientCommandEvent listener) {
        getCommandListeners().add(listener);
    }

    public static void setScammerList(ScammerList scammerList) {
        ScammerList.scammerList = scammerList;
    }
    public static ScammerList getScammerList() {
        return scammerList;
    }

    public Gson getGson() {
        return gson;
    }
    public void setGson(Gson gson) {
        this.gson = gson;
    }

    public void setSettingsManager(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }
    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    public void setHelper(Helper helper) {
        this.helper = helper;
    }
    public Helper getHelper() {
        return helper;
    }

    public ArrayList<String> getScammerListName() {
        return scammerListName;
    }
    public void setScammerListName(ArrayList<String> scammerListName) {
        this.scammerListName = scammerListName;
    }

    public ArrayList<String> getScammerListUUID() {
        return scammerListUUID;
    }
    public void setScammerListUUID(ArrayList<String> scammerListUUID) {
        this.scammerListUUID = scammerListUUID;
    }

    public ArrayList<String> getOnlineScammerListName() {
        return onlineScammerListName;
    }
    public void setOnlineScammerListName(ArrayList<String> onlineScammerListName) {
        this.onlineScammerListName = onlineScammerListName;
    }

    public ArrayList<String> getOnlineScammerListUUID() {
        return onlineScammerListUUID;
    }
    public void setOnlineScammerListUUID(ArrayList<String> onlineScammerListUUID) {
        this.onlineScammerListUUID = onlineScammerListUUID;
    }

    public ArrayList<String> getNameChangedPlayers() {
        return nameChangedPlayers;
    }
    public void setNameChangedPlayers(ArrayList<String> nameChangedPlayers) {
        this.nameChangedPlayers = nameChangedPlayers;
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

    public void setCommandListeners(Set<ClientCommandEvent> commandListeners) {
        this.commandListeners = commandListeners;
    }
    public Set<ClientCommandEvent> getCommandListeners() {
        return commandListeners;
    }
}

