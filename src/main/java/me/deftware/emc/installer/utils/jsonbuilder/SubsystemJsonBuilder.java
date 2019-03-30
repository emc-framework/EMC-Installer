package me.deftware.emc.installer.utils.jsonbuilder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.Date;

@SuppressWarnings("Duplicates")
public class SubsystemJsonBuilder extends AbstractJsonBuilder {

    public SubsystemJsonBuilder(JsonObject emcJson) {
        super(emcJson);
    }

    @Override
    public JsonObject build(String mcVersion, String emcVersion, String id, String mainClass, String inheritsFrom) {
        String mappings = emcJson.get("mappings").getAsString();
        String date = formatDate(new Date());
        JsonObject jsonObject = new JsonObject();
        // Properties
        jsonObject.add("inheritsFrom", new JsonPrimitive(inheritsFrom));
        jsonObject.add("id", new JsonPrimitive(mcVersion + "-" + id));
        jsonObject.add("time", new JsonPrimitive(date));
        jsonObject.add("releaseTime", new JsonPrimitive(date));
        jsonObject.add("type", new JsonPrimitive("release"));
        // Main class
        JsonObject arguments = new JsonObject();
        arguments.add("game", new JsonArray());
        jsonObject.add("arguments", arguments);
        jsonObject.add("mainClass", new JsonPrimitive(emcJson.get("mainClass").getAsString()));
        // Libraries
        JsonArray libsArray = new JsonArray();
        libsArray.add(generateMavenRepo("name", "net.fabricmc:sponge-mixin:0.7.11.16", "url",
                "https://maven.fabricmc.net/"));
        libsArray.add(generateMavenRepo("name", "net.fabricmc:tiny-mappings-parser:0.1.0.6", "url",
                "https://maven.fabricmc.net/"));
        libsArray.add(generateMavenRepo("name", "net.fabricmc:tiny-remapper:0.1.0.23", "url",
                "https://maven.fabricmc.net/"));
        libsArray.add(generateMavenRepo("name", "org.ow2.asm:asm:7.0", "url",
                "https://maven.fabricmc.net/"));
        libsArray.add(generateMavenRepo("name", "org.ow2.asm:asm-analysis:7.0", "url",
                "https://maven.fabricmc.net/"));
        libsArray.add(generateMavenRepo("name", "org.ow2.asm:asm-commons:7.0", "url",
                "https://maven.fabricmc.net/"));
        libsArray.add(generateMavenRepo("name", "org.ow2.asm:asm-tree:7.0", "url",
                "https://maven.fabricmc.net/"));
        libsArray.add(generateMavenRepo("name", "org.ow2.asm:asm-util:7.0", "url",
                "https://maven.fabricmc.net/"));
        libsArray.add(generateMavenRepo("name", "net.fabricmc:yarn:" + mappings, "url",
                "https://maven.fabricmc.net/"));
        libsArray.add(generateMavenRepo("name", "me.deftware:EMC-F:" + emcVersion, "url",
                "https://gitlab.com/EMC-Framework/maven/raw/master/"));
        jsonObject.add("libraries", libsArray);
        return jsonObject;
    }

}
