package de.neocraftr.scammerlist.utils;

import com.google.common.base.CharMatcher;
import com.google.common.io.Resources;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import de.neocraftr.scammerlist.ScammerList;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class Helper {

    private ScammerList sc = ScammerList.getScammerList();

    public List<String> getNamesFromUUID(String uuid) {
        List<String> names = new ArrayList<>();

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
            System.out.println(ScammerList.CONSOLE_PREFIX + "Could not get name from mojang api: "+e);
        }
        Collections.reverse(names);
        return names;
    }

    public String getUUIDFromName(String name) {
        if(name.startsWith("!")) {
            return name;
        }

        try {
            BufferedReader reader = Resources.asCharSource(new URL(String.format("https://api.mojang.com/users/profiles/minecraft/%s", name)), StandardCharsets.UTF_8).openBufferedStream();
            JsonObject json = sc.getGson().fromJson(reader, JsonObject.class);
            if(json == null || !json.has("id")) return null;
            String uuid = json.get("id").getAsString();
            return Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})").matcher(uuid).replaceAll("$1-$2-$3-$4-$5");
        } catch(IOException e) {
            System.out.println(ScammerList.CONSOLE_PREFIX + "Could not get uuid from mojang api: "+e);
        }
        return null;
    }

    public String colorize(String msg) {
        return msg.replace("&", "§");
    }

    public String replaceUrlWildcards(String msg) {
        switch(msg.toLowerCase()) {
            case "%scammer-radar%":
                return "https://coolertyp.scammer-radar.de/onlineScammer.json";
            default:
                return msg;
        }
    }
}
