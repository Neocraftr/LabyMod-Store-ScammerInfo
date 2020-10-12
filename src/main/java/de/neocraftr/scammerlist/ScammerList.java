package de.neocraftr.scammerlist;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import de.neocraftr.scammerlist.listener.*;
import de.neocraftr.scammerlist.utils.Helper;
import de.neocraftr.scammerlist.utils.PlayerList;
import de.neocraftr.scammerlist.utils.SettingsManager;
import de.neocraftr.scammerlist.utils.Updater;
import net.labymod.addon.AddonLoader;
import net.labymod.api.LabyModAddon;
import net.labymod.settings.elements.SettingsElement;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ScammerList extends LabyModAddon {

    public static final String PREFIX = "§8[§4Scammerliste§8] §r",
                               PREFIX_LINE = "§7-------------------- §4Scammerliste §7--------------------",
                               COMMAND_PREFIX = ".",
                               ONLINE_SCAMMER_URL = "https://coolertyp.scammer-radar.de/onlineScammer.json",
                               VERSION = "1.1.0";
    public static final int PLAYERS_PER_LIST_PAGE = 15,
                            UPDATE_INTERVAL = 604800000; // 1 week

    private static ScammerList scammerList;
    private Gson gson;
    private SettingsManager settings;
    private Helper helper;
    private Updater updater;
    private File listDir, onlineListFile, privateListFile;
    private long nextUpdate = 0;
    private PlayerList privateList = new PlayerList();
    private PlayerList onlineList = new PlayerList();
    private List<String> nameChangedPlayers = new ArrayList<>();
    private boolean addClan, removeClan, clanInProcess, updatingList, listsToConvert;
    private Set<ClientCommandEvent> commandListeners = new HashSet<>();

    @Override
    public void onEnable() {
        scammerList = this;
        gson = new GsonBuilder().setPrettyPrinting().create();
        settings = new SettingsManager();
        helper = new Helper();
        updater = new Updater();

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

        if(getConfig().has("nameChangedPlayers")) {
            nameChangedPlayers = gson.fromJson(getConfig().get("nameChangedPlayers"), ArrayList.class);
        }
        if(getConfig().has("nextUpdate")) {
            nextUpdate = getConfig().get("nextUpdate").getAsLong();
        }

        listDir = new File(AddonLoader.getConfigDirectory(), "ScammerList");
        if(!listDir.exists()) {
            listDir.mkdirs();
        }
        privateListFile = new File(listDir, "PrivateList.json");
        if(!privateListFile.exists()) {
            try {
                privateListFile.createNewFile();
                savePrivateList();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        onlineListFile = new File(listDir, "OnlineList.json");
        if(!onlineListFile.exists()) {
            try {
                onlineListFile.createNewFile();
                if(settings.isShowOnlineScammer()) helper.downloadOnlineScammerList();
                saveOnlineList();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            FileReader reader = new FileReader(privateListFile);
            privateList = gson.fromJson(reader, new TypeToken<PlayerList>(){}.getType());
            reader.close();

            reader = new FileReader(onlineListFile);
            onlineList = gson.fromJson(reader, new TypeToken<PlayerList>(){}.getType());
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(settings.isAutoUpdate()) {
            if(nextUpdate < System.currentTimeMillis()) {
                new Thread(() -> {
                    helper.updateLists();
                    System.out.println("[ScammerList] Updated player names.");
                }).start();
            }
        }

        // Convert old list format
        if(getConfig().has("scammerListUUID")) {
            setListsToConvert(true);
        }
    }

    public void savePrivateList() {
        try {
            FileWriter writer = new FileWriter(privateListFile);
            writer.write(gson.toJson(privateList));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveOnlineList() {
        try {
            FileWriter writer = new FileWriter(onlineListFile);
            writer.write(gson.toJson(onlineList));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
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

    public PlayerList getPrivateList() {
        return privateList;
    }
    public void setPrivateList(PlayerList privateList) {
        this.privateList = privateList;
    }

    public PlayerList getOnlineList() {
        return onlineList;
    }
    public void setOnlineList(PlayerList onlineList) {
        this.onlineList = onlineList;
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

    public boolean isListsToConvert() {
        return listsToConvert;
    }
    public void setListsToConvert(boolean listsToConvert) {
        this.listsToConvert = listsToConvert;
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
