package de.neocraftr.scammerlist;

import com.google.common.base.CharMatcher;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import net.labymod.api.LabyModAddon;
import net.labymod.settings.elements.SettingsElement;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class ScammerList extends LabyModAddon {

    private static final String PREFIX = "§8[§4Scammerliste§8] §r",
                                COMMAND_PREFIX = ".",
                                ONLINE_SCAMMER_URL = "https://coolertyp.scammer-radar.de/onlineScammer.json";
    private final int PLAYERS_PER_LIST_PAGE = 15;
    private static ScammerList scammerList;
    private Gson gson;
    private SettingsManager settingsManager;
    private long nextUpdate = 0;
    private ArrayList<String> scammerListName = new ArrayList<>();
    private ArrayList<String> scammerListUUID = new ArrayList<>();
    private ArrayList<String> onlineScammerListName = new ArrayList<>();
    private ArrayList<String> onlineScammerListUUID = new ArrayList<>();
    private ArrayList<String> clanMemberList = new ArrayList<>();
    private boolean addClan, removeClan, clanMessage, confirmClear;
    private String clanName;

    @Override
    public void onEnable() {
        setScammerList(this);
        setGson(new Gson());
        setSettingsManager(new SettingsManager());

        getApi().getEventManager().register(new ChatSendListener());
        getApi().getEventManager().register(new ChatReceiveListener());
        getApi().getEventManager().register(new ModifyChatListener());
        getApi().registerForgeListener(new PreRenderListener());
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
        if(getConfig().has("nextUpdate")) {
            setNextUpdate(getConfig().get("nextUpdate").getAsLong());
        }

        if(getNextUpdate() < System.currentTimeMillis()) {
            setNextUpdate(System.currentTimeMillis()+604800000); // 1 week

            new Thread(() -> {
                updateLists();
                System.out.println("[ScammerList] Updated playernames.");
            }).start();
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
        getConfig().addProperty("nextUpdate", getNextUpdate());
        getConfig().addProperty("showOnlineScammer", getSettingsManager().isShowOnlineScammer());
        getConfig().addProperty("highlightInChat", getSettingsManager().isHighlightInChat());
        getConfig().addProperty("highlightInTablist", getSettingsManager().isHighlightInTablist());
        super.saveConfig();
    }

    public ArrayList<String> getNamesFromUUID(String uuid) {
        ArrayList<String> names = new ArrayList<>();

        if(uuid.startsWith("!")) {
            names.add(uuid);
            return names;
        }

        try {
            uuid = CharMatcher.is('-').removeFrom(uuid);
            try (BufferedReader reader = Resources.asCharSource(new URL(String.format("https://api.mojang.com/user/profiles/%s/names", uuid)), StandardCharsets.UTF_8).openBufferedStream()) {
                JsonReader json = new JsonReader(reader);
                json.beginArray();

                while (json.hasNext()) {
                    json.beginObject();
                    while (json.hasNext()) {
                        String key = json.nextName();
                        switch (key) {
                            case "name":
                                names.add(json.nextString());
                                break;
                            default:
                                json.skipValue();
                                break;
                        }
                    }
                    json.endObject();
                }

                json.endArray();
            }
        } catch(IOException e) {
            System.out.println("[ScammerList] Could not get name from mojang api: "+e.getLocalizedMessage());
        }
        Collections.reverse(names);
        return names;
    }

    public String getUUIDFromName(String name) {
        if(name.startsWith("!")) {
            return name;
        }

        try {
            try (BufferedReader reader = Resources.asCharSource(new URL(String.format("https://api.mojang.com/users/profiles/minecraft/%s", name)), StandardCharsets.UTF_8).openBufferedStream()) {
                JsonObject json = getGson().fromJson(reader, JsonObject.class);
                if(json == null || !json.has("id")) return null;
                String uuid = json.get("id").getAsString();
                return Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})").matcher(uuid).replaceAll("$1-$2-$3-$4-$5");
            }
        } catch(IOException e) {
            System.out.println("[ScammerList] Could not get uuid from mojang api: "+e.getLocalizedMessage());
        }
        return null;
    }

    public void loadOnlineScammerList() {
        try {
            try(BufferedReader reader = Resources.asCharSource(new URL(getOnlineScammerURL()), StandardCharsets.UTF_8).openBufferedStream()) {

                JsonReader json = new JsonReader(reader);
                json.beginArray();

                getOnlineScammerListName().clear();
                getOnlineScammerListUUID().clear();

                while (json.hasNext()) {
                    json.beginObject();
                    while (json.hasNext()) {
                        switch(json.nextName()) {
                            case "name":
                                String name = json.nextString();
                                getOnlineScammerListName().add(name);
                                break;
                            case "uuid":
                                String uuid = json.nextString();
                                getOnlineScammerListUUID().add(uuid);
                                break;
                        }
                    }
                    json.endObject();
                }

                json.endArray();

                saveConfig();
            }
        } catch (IOException e) {
            System.out.println("[ScammerList] Could not load online scammer list: "+e.getLocalizedMessage());
        }
    }

    public void updateLists() {
        loadOnlineScammerList();

        getScammerListUUID().forEach(uuid -> {
            String name = getNamesFromUUID(uuid).get(0);
            if (name != null) getScammerListName().add(name);
        });
        saveConfig();
    }

    public String getPrefix() {
        return PREFIX;
    }
    public String getCommandPrefix() {
        return COMMAND_PREFIX;
    }
    public String getOnlineScammerURL() {
        return ONLINE_SCAMMER_URL;
    }
    public int getPlayersPerListPage() {
        return PLAYERS_PER_LIST_PAGE;
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

    public ArrayList<String> getClanMemberList() {
        return clanMemberList;
    }
    public void setClanMemberList(ArrayList<String> clanMemberList) {
        this.clanMemberList = clanMemberList;
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

    public void setClanMessage(boolean clanMessage) {
        this.clanMessage = clanMessage;
    }
    public boolean isClanMessage() {
        return clanMessage;
    }

    public boolean isConfirmClear() {
        return confirmClear;
    }
    public void setConfirmClear(boolean confirmClear) {
        this.confirmClear = confirmClear;
    }

    public String getClanName() {
        return clanName;
    }
    public void setClanName(String clanName) {
        this.clanName = clanName;
    }

    public long getNextUpdate() {
        return nextUpdate;
    }
    public void setNextUpdate(long nextUpdate) {
        this.nextUpdate = nextUpdate;
    }
}

