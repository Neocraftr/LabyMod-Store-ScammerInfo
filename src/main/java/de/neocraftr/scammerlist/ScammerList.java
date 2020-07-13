package de.neocraftr.scammerlist;

import com.google.common.base.CharMatcher;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import net.labymod.api.LabyModAddon;
import net.labymod.settings.elements.SettingsElement;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class ScammerList extends LabyModAddon {

    private static ScammerList scammerList;
    private Gson gson;
    private final String prefix = "§8[§4Scammerliste§8] §r", commandPrefix = ".";
    private ChatComponentText scammerMessage;

    private ArrayList<String> scammerListName, scammerListUUID;
    private Pattern chatRegex;
    private Pattern msgRegex;

    private Pattern msg2Regex;

    @Override
    public void onEnable() {
        setScammerList(this);
        setGson(new Gson());
        setChatRegex(Pattern.compile("^(?:.\\w+. \\w+|\\w+) ┃ (\\!?\\w{1,16}) »"));
        setMsgRegex(Pattern.compile("^\\[\\w+ ┃ (\\!?\\w{1,16}) -> mir]"));
        setMsg2Regex(Pattern.compile("^\\[mir -> \\w+ ┃ (\\!?\\w{1,16})]"));

        ChatComponentText scammerMessage = new ChatComponentText("§c§l[§4§l!§c§l] §r");
        scammerMessage.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("§4§lScammer")));
        setScammerMessage(scammerMessage);

        getApi().getEventManager().register(new ChatSendListener());
        getApi().getEventManager().register(new ModifyChatListener());
    }

    @Override
    public void loadConfig() {
        if(!getConfig().has("scammerListName")) {
            getConfig().add("scammerListName", getGson().toJsonTree(new ArrayList<String>()));
            saveConfig();
        }
        if(!getConfig().has("scammerListUUID")) {
            getConfig().add("scammerListUUID", getGson().toJsonTree(new ArrayList<String>()));
            saveConfig();
        }

        setScammerListName(getGson().fromJson(getConfig().get("scammerListName"), ArrayList.class));
        setScammerListUUID(getGson().fromJson(getConfig().get("scammerListUUID"), ArrayList.class));
    }

    @Override
    protected void fillSettings(List<SettingsElement> list) {}

    public void saveSettings() {
        getConfig().add("scammerListName", getGson().toJsonTree(getScammerListName()));
        getConfig().add("scammerListUUID", getGson().toJsonTree(getScammerListUUID()));
        saveConfig();
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

                String name = null;
                long when = 0;

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
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return null;
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

    public String getPrefix() {
        return this.prefix;
    }

    public String getCommandPrefix() {
        return this.commandPrefix;
    }

    public Pattern getChatRegex() {
        return chatRegex;
    }
    public void setChatRegex(Pattern chatRegex) {
        this.chatRegex = chatRegex;
    }

    public Pattern getMsgRegex() {
        return msgRegex;
    }
    public void setMsgRegex(Pattern msgRegex) {
        this.msgRegex = msgRegex;
    }

    public Pattern getMsg2Regex() {
        return msg2Regex;
    }
    public void setMsg2Regex(Pattern msg2Regex) {
        this.msg2Regex = msg2Regex;
    }

    public ChatComponentText getScammerMessage() {
        return scammerMessage;
    }
    public void setScammerMessage(ChatComponentText scammerMessage) {
        this.scammerMessage = scammerMessage;
    }
}

