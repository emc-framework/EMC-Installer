package me.deftware.emc.installer.utils.jsonbuilder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Used to generate launcher profile json files for Minecraft 1.13 and higher
 */
public class ModernJsonBuilder extends AbstractJsonBuilder {

    @Override
    public JsonObject build(String mcVersion, String emcVersion, String id, String emcTweaker, String inheritsFrom) {
        // TODO: Generate date
        String date = "2018-07-12T08:51:45-05:00";
        JsonObject jsonObject = new JsonObject();
        // Properties
        jsonObject.add("inheritsFrom", new JsonPrimitive(inheritsFrom));
        jsonObject.add("id", new JsonPrimitive(mcVersion + "-" + id));
        jsonObject.add("time", new JsonPrimitive(date));
        jsonObject.add("releaseTime", new JsonPrimitive(date));
        jsonObject.add("type", new JsonPrimitive("release"));
        // Tweaker
        JsonObject arguments = new JsonObject();
        JsonArray tweakArray = new JsonArray();
        tweakArray.add("--tweakClass");
        tweakArray.add(emcTweaker);
        arguments.add("game", tweakArray);
        jsonObject.add("arguments", arguments);
        jsonObject.add("mainClass", new JsonPrimitive("net.minecraft.launchwrapper.Launch"));
        // Libraries
        JsonArray libsArray = new JsonArray();
        libsArray.add(generateMavenRepo("name", "net.minecraft:launchwrapper:1.12", "", ""));
        libsArray.add(generateMavenRepo("name", "me.deftware:EMC:" + emcVersion, "url",
                "https://gitlab.com/EMC-Framework/maven/raw/master/"));
        libsArray.add(generateMavenRepo("name", "org.dimdev:mixin:0.7.11-SNAPSHOT", "url",
                "https://www.dimdev.org/maven/"));
        libsArray.add(generateMavenRepo("name", "net.jodah:typetools:0.5.0", "url",
                "https://repo.maven.apache.org/maven2/"));
        jsonObject.add("libraries", libsArray);
        return jsonObject;
    }

}
