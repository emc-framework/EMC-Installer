package me.deftware.emc.installer.utils;

import java.io.File;

public class Utils {

	public static String getMinecraftRoot() throws Exception {
		if (OSUtils.isWindows()) {
			return System.getenv("APPDATA") + File.separator + ".minecraft" + File.separator;
		} else if (OSUtils.isLinux()) {
			return System.getProperty("user.home") + File.separator + ".minecraft" + File.separator;
		} else if (OSUtils.isMac()) {
			return System.getProperty("user.home") + File.separator + "Library" + File.separator + "Application Support"
					+ File.separator + "minecraft" + File.separator;
		}
		throw new RuntimeException("Unable to find Minecraft version");
	}

}
