package me.deftware.emc.installer.utils.jsonbuilder;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class AbstractJsonBuilder {

    protected JsonObject emcJson;

    public AbstractJsonBuilder(JsonObject emcJson) {
        this.emcJson = emcJson;
    }

    public static JsonObject generateLaunchProfile(String name, String mcVersion) {
        JsonObject json = new JsonObject();
        json.add("name", new JsonPrimitive(name));
        json.add("type", new JsonPrimitive("custom"));
        json.add("created", new JsonPrimitive(formatDateMs(new Date())));
        json.add("lastUsed", new JsonPrimitive(formatDateMs(new Date())));
        json.add("icon", new JsonPrimitive("Diamond_Block"));
        json.add("lastVersionId", new JsonPrimitive(mcVersion + "-" + name));
        return json;
    }

    public static String formatDateMs(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd'T'HH:mm:ss.SSS'Z'");
        return dateFormat.format(date);
    }

    public static String formatDate(Date date) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
            return dateFormat.format(date);
        } catch (Exception e) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            return dateFormat.format(date);
        }
    }

    public abstract JsonObject build(String mcVersion, String emcVersion, String id, String emcTweaker, String inheritsFrom);

    public JsonObject generateMavenRepo(String name, String n, String url, String u) {
        JsonObject obj = new JsonObject();
        obj.addProperty(name, n);
        if (!url.equals("")) {
            obj.addProperty(url, u);
        }
        return obj;
    }

}
