package me.deftware.emc.installer.utils.jsonbuilder;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public abstract class AbstractJsonBuilder {

    public abstract JsonObject build(String mcVersion, String emcVersion, String id, String emcTweaker, String inheritsFrom);

    public static JsonObject generateLaunchProfile(String name, String mcVersion) {
        JsonObject json = new JsonObject();
        json.add("name", new JsonPrimitive(name));
        json.add("type", new JsonPrimitive("custom"));
        json.add("icon", new JsonPrimitive("Diamond_Block"));
        json.add("lastVersionId", new JsonPrimitive(mcVersion + "-" + name));
        return json;
    }

    public JsonObject generateMavenRepo(String name, String n, String url, String u) {
        JsonObject obj = new JsonObject();
        obj.addProperty(name, n);
        if (!url.equals("")) {
            obj.addProperty(url, u);
        }
        return obj;
    }


}
