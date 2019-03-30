package me.deftware.emc.installer.utils.jsonbuilder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.Date;

/**
 * Used to generate launcher profile json files for Minecraft 1.12.2 and below
 */
public class LegacyJsonBuilder extends AbstractJsonBuilder {

    public LegacyJsonBuilder(JsonObject emcJson) {
        super(emcJson);
    }

    @Override
    public JsonObject build(String mcVersion, String emcVersion, String id, String emcTweaker, String inheritsFrom) {
        String date = formatDate(new Date());
        JsonObject jsonObject = new JsonObject();
        String tweaker = "--username ${auth_player_name} " +
                "--version ${version_name} " +
                "--gameDir ${game_directory} " +
                "--assetsDir ${assets_root} " +
                "--assetIndex ${assets_index_name} " +
                "--uuid ${auth_uuid} " +
                "--accessToken ${auth_access_token} " +
                "--userType ${user_type} " +
                "--tweakClass " + emcTweaker;
        // Properties
        jsonObject.add("inheritsFrom", new JsonPrimitive(inheritsFrom));
        jsonObject.add("id", new JsonPrimitive(mcVersion + "-" + id));
        jsonObject.add("time", new JsonPrimitive(date));
        jsonObject.add("releaseTime", new JsonPrimitive(date));
        jsonObject.add("type", new JsonPrimitive("release"));
        jsonObject.add("minecraftArguments", new JsonPrimitive(tweaker));
        jsonObject.add("mainClass", new JsonPrimitive("net.minecraft.launchwrapper.Launch"));
        jsonObject.add("minimumLauncherVersion", new JsonPrimitive("0"));
        jsonObject.add("jar", new JsonPrimitive(mcVersion));
        jsonObject.add("downloads", new JsonObject());
        // Libraries
        JsonArray libsArray = new JsonArray();
        libsArray.add(generateMavenRepo("name", "net.minecraft:launchwrapper:1.12", "", ""));
        libsArray.add(generateMavenRepo("name", "me.deftware:EMC:" + emcVersion, "url",
                "https://gitlab.com/EMC-Framework/maven/raw/master/"));
        libsArray.add(generateMavenRepo("name", "org.spongepowered:mixin:0.7.1-SNAPSHOT", "url",
                "http://dl.liteloader.com/versions/"));
        libsArray.add(generateMavenRepo("name", "net.jodah:typetools:0.5.0", "url",
                "https://repo.maven.apache.org/maven2/"));
        jsonObject.add("libraries", libsArray);
        return jsonObject;

    }

}
